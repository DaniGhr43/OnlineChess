package com.ilm.onlinechess.game;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.onlinechess.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chessboard {

    final int KING = 0, KING2 = 6;
    final int QUEEN = 1, QUEEN2 = 7;
    final int ROOK = 2, ROOK2 = 8;
    final int BISHOP = 3, BISHOP2 = 9;
    final int KNIGHT = 4, KNIGHT2 = 10;
    final int PAWN = 5, PAWN2 = 11;
    final int EMPTY = -1;

    public GridLayout grid;
    public Context context;
    public Cell[][] cells;
    public ArrayList<int[]> availableMoves = new ArrayList<>();

    // private int[] lastSelection;
    private ArrayList<int[]> lastSelections = new ArrayList<>();
    private Cell lastSelectedCell;

    private final int WHITE = 0;
    private final int BLACK = 1;
    public Cell whiteKing, blackKing;
    private int[][] matriz = new int[8][8];
    FirebaseFirestore db;
    private GameModel gameModel = new GameModel();
    private LifecycleOwner lifecycleOwner;
    private int turnCont;
    public Socket socket;
    public OutputStream out;
    public BufferedReader in;
    private int gameId;
    private boolean isBeingCaptured = false;
    private boolean canChangeTurn = false;

    private final Map<String, Integer> initialPiecePositions = createInitialPositions();


    public Chessboard(GridLayout grid, Context context, LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        this.grid = grid;
        this.context = context;
        gameModel = GameData.gameModel.getValue();
        this.gameId = gameId;

        GameData.gameModel.observe(lifecycleOwner, new Observer<GameModel>() {
            @Override
            public void onChanged(GameModel newGameModel) {
                Log.d("changed", "CHANGE");
                gameModel = newGameModel;
                ArrayList<Integer> clickedPositions = gameModel.getPositions();
                if (clickedPositions != null && !clickedPositions.isEmpty()) {
                    click(cells[clickedPositions.get(0)][clickedPositions.get(1)]);
                    click(cells[clickedPositions.get(2)][clickedPositions.get(3)]);
                }
            }
        });

        if (!GameData.isOffline) {
            connectToServer();
        }

    }

    private Map<String, Integer> createInitialPositions() {
        Map<String, Integer> positions = new HashMap<>();
        positions.put("0,0", ROOK2);
        positions.put("0,1", KNIGHT2);
        positions.put("0,2", BISHOP2);
        positions.put("0,3", QUEEN2);
        positions.put("0,4", KING2);
        positions.put("0,5", BISHOP2);
        positions.put("0,6", KNIGHT2);
        positions.put("0,7", ROOK2);
        for (int i = 0; i < 8; i++) {
            positions.put("1," + i, PAWN2);
            positions.put("6," + i, PAWN);
        }
        positions.put("7,0", ROOK);
        positions.put("7,1", KNIGHT);
        positions.put("7,2", BISHOP);
        positions.put("7,3", QUEEN);
        positions.put("7,4", KING);
        positions.put("7,5", BISHOP);
        positions.put("7,6", KNIGHT);
        positions.put("7,7", ROOK);
        return positions;
    }

    public Chessboard() {

    }

    public void connectToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String serverAddress = "13.36.252.117"; // Dirección IP o nombre de host del servidor
                int serverPort = 8444; // Puerto del servidor

                try {
                    socket = new Socket(serverAddress, serverPort);


                    //BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

                    //To join the game, send the gameID to the server
                    out = socket.getOutputStream();
                    String gameId = String.valueOf(gameModel.getGameId());
                    out.write((gameId + "\n").getBytes());
                    out.flush();

                    // Thread for reading server messages
                    new Thread(() -> {
                        String serverResponse;
                        try {
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            while ((serverResponse = in.readLine()) != null) {
                                System.out.println(serverResponse);
                                String serverResponseStr = serverResponse.substring(1, serverResponse.length() - 1).replaceAll("\\s+", "");
                                String[] serverResponseArray = serverResponseStr.split(",");
                                ArrayList<Integer> positions = new ArrayList<>();

                                //If the server send us exit message, end the game
                                if (serverResponse.equals("EXIT")) {
                                    if (GameData.currentPlayer == GameData.BLACK) {
                                        gameModel.setWinner(GameData.BLACK);
                                        try {
                                            out.close();
                                            socket.close();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else if (GameData.currentPlayer == GameData.WHITE) {
                                        gameModel.setWinner(GameData.WHITE);
                                        try {
                                            out.close();
                                            socket.close();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    gameModel.setGAME_STATUS(GameData.FINISHED);
                                    GameData.saveGameModel(gameModel, true);
                                    break;
                                }

                                //Add the positions of the move done by the other player
                                for (String pos : serverResponseArray) {
                                    positions.add(Integer.parseInt(pos));
                                }

                                gameModel.setPositions(positions);
                                GameData.saveGameModel(gameModel, false);
                            }
                            Log.d("FINN ", "SIIS");

                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void createCells() {


        db = FirebaseFirestore.getInstance();


        //Create bitmap array to create bitmaps 1 time

        Bitmap[] bitmaps = new Bitmap[]{
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_king),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_queen),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_rook),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_knight),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_pawn),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_bishop),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.dot),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_king_black),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_queen_black),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_rook_black),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_knight_black),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_pawn_black),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_bishop_black)

        };
        cells = new Cell[8][8];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int pieceType = initialPiecePositions.getOrDefault(y + "," + x, EMPTY);

                Cell cell = new Cell(this, y, x, bitmaps, pieceType);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                params.width = 0;
                params.height = 0;
                params.columnSpec = GridLayout.spec(x, 1f);
                params.rowSpec = GridLayout.spec(y, 1f);

                cell.setLayoutParams(params);

                // Asigna colores alternados para las celdas

                if ((x + y) % 2 == 0) {
                    cell.setBackgroundColor(Color.WHITE);
                    cell.backgroudColor = 0;
                } else {
                    cell.setBackgroundColor(Color.GRAY);
                    cell.backgroudColor = 1;
                }
                final int XX = x;
                final int YY = y;


                if (cell.pieceType == cell.KING) {
                    whiteKing = cell;
                }
                if (cell.pieceType == cell.KING2) {
                    blackKing = cell;
                }
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("CLIICK", String.valueOf(GameData.turn));
                        Log.d("CLIICK3", String.valueOf(GameData.currentPlayer));
                        Log.d("CLIICK2", String.valueOf(GameData.gameModel.getValue().getGAME_STATUS()));

                        if (GameData.gameModel.getValue().getGAME_STATUS() == GameData.STARTED) {

                            if (GameData.currentPlayer == GameData.turn) {
                                click(cell);
                            }

                        }
                    }
                });


                cells[x][y] = cell;

                grid.addView(cell);


            }
        }


    }


    private void changeTurn() {
        GameData.turn = ((GameData.turn == WHITE) ? BLACK : WHITE);

        if (GameData.isOffline)
            GameData.currentPlayer = ((GameData.currentPlayer == WHITE) ? BLACK : WHITE);

    }


    private boolean mate = true;

    public void click(Cell cell) {
        isBeingCaptured = false;
        canChangeTurn = false;


        turnCont++;

        Log.d("CLOCK", String.valueOf(GameData.turn));


        //Change the state of selected in the clicked and last clicked cell if it can be changed
        if ((cell.pieceType < 6 && GameData.turn == WHITE) || (cell.pieceType > 5 && GameData.turn == BLACK) || cell.pieceType == cell.EMPTY || cell.isShowingAvailableMove)
            cell.setSeleccionada(!cell.seleccionada);


        //To unselect cells if a piece move
        if (lastSelectedCell != null && lastSelectedCell != cell)
            lastSelectedCell.setSeleccionada(false);

        //if the cell is being selected
        if (cell.seleccionada) {


            availableMoves = cell.setMoves(false);


            //if the cell is showing an available move(the bitmap of the cell is the dot) and is clicked, move the piece
            if (cell.isShowingAvailableMove) {
                moveCell(cell);
            }
            //Hide the available moves that are being showed in the last selected cell
            for (int last[] : lastSelections) {
                hideAvailableMoves(cells[last[0]][last[1]]);
            }

            //show the availables moves of the cell.
            if (!isBeingCaptured) {
                showAllAvailableMoves(cell);
            }

            //if the cell is being unselected, hide all the availables moves that are being showed
        } else {
            hideLastShownMoves(cell);
        }

        if (canChangeTurn)
            changeTurn();


        cells[cell.posX][cell.posY] = cell;

        lastSelectedCell = cell;
    }

    private void showAllAvailableMoves(Cell cell) {

        lastSelections.clear();

        for (int moves[] : availableMoves) {
            int X = moves[0];
            int Y = moves[1];
            //correct values inside the board
            if (X >= 0 && Y >= 0 && X < 8 && Y < 8) {
                showSingleAvailableMove(cells[X][Y], cell);
                lastSelections.add(new int[]{X, Y});
            }
        }

    }

    public void showSingleAvailableMove(Cell checkedCell, Cell clickedCell) {
        Log.d("showAvailableMove", "showAvailableMove");

        Bitmap overlayBitmap = checkedCell.bitmaps[6]; // Bitmap to overlay when a cell is clicked(a dot)

        //BLACKS. setImageBitmap will set a just dot
        if (checkKingIsSafe(checkedCell, clickedCell)) {
            if ((checkedCell.pieceType == checkedCell.EMPTY || checkedCell.pieceType > 5) && clickedCell.pieceType < 6) {
                checkedCell.setImageBitmap(checkedCell.bitmaps[6]);
                checkedCell.isShowingAvailableMove = true;
            }
            //WHITES. setImageBitmap will set a just dot
            if ((checkedCell.pieceType == checkedCell.EMPTY || checkedCell.pieceType < 6) && clickedCell.pieceType > 5) {
                checkedCell.setImageBitmap(checkedCell.bitmaps[6]);
                checkedCell.isShowingAvailableMove = true;
            }
            //WHITES. setImageBitmap will set a dot combined with the piece
            if (checkedCell.pieceType > 5 && clickedCell.pieceType < 6 && checkedCell.pieceType != checkedCell.EMPTY) {
                Bitmap combinedBitmap = overlayBitmaps(checkedCell.bitmap, overlayBitmap);
                checkedCell.setImageBitmap(combinedBitmap);
                checkedCell.isShowingAvailableMove = true;
            }
            //BLACKS. setImageBitmap will set a dot combined with the piece
            if (checkedCell.pieceType < 6 && clickedCell.pieceType > 5 && checkedCell.pieceType != checkedCell.EMPTY) {
                Bitmap combinedBitmap = overlayBitmaps(checkedCell.bitmap, overlayBitmap);
                checkedCell.setImageBitmap(combinedBitmap);
                checkedCell.isShowingAvailableMove = true;
            }
        }


    }

    private Bitmap overlayBitmaps(Bitmap base, Bitmap overlay) {
        Log.d("overlayBitmaps", "overlayBitmaps");
        Bitmap combined = Bitmap.createBitmap(base.getWidth(), base.getHeight(), base.getConfig());
        Canvas canvas = new Canvas(combined);
        canvas.drawBitmap(base, 0, 0, null);
        canvas.drawBitmap(overlay, 0, 0, null); // Ajusta la posición si es necesario
        return combined;
    }


    private void hideLastShownMoves(Cell cell) {
        if (availableMoves != null) {
            for (int moves[] : availableMoves) {
                int X = moves[0];
                int Y = moves[1];

                if (X >= 0 && Y >= 0 && X < 8 && Y < 8) {
                    hideAvailableMoves(cells[X][Y]);
                }
            }
            cell.availableMoves = new ArrayList<>();
        }
    }

    public void hideAvailableMoves(Cell cell) {
        Log.d("hideAvailableMoves", "hideAvailableMoves");
        if (cell.pieceType == EMPTY) {
            cell.setImageBitmap(null);
            cell.isShowingAvailableMove = false;
        } else {
            cell.setImageBitmap(cell.bitmap);
            cell.isShowingAvailableMove = false;
        }

    }

    private boolean checkMate() {
        //Change turn becuase chenkKingIsSafe need the cell to be the same type as turn
        changeTurn();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (((GameData.turn == GameData.BLACK && cells[i][j].pieceType > 5) || (GameData.turn == GameData.WHITE && cells[i][j].pieceType < 6 && cells[i][j].pieceType != cells[i][j].EMPTY))) {

                    availableMoves = cells[i][j].setMoves(false);

                    //if any cell can move , it is not mate
                    for (int[] moves : availableMoves) {
                        if (cells[i][j].isValidPosition(moves[0], moves[1]) && checkKingIsSafe(cells[moves[0]][moves[1]], cells[i][j])) {
                            Log.d("PIECETYPE", String.valueOf(cells[i][j].pieceType));
                            Log.d("PIECETYPE", "X: " + moves[0] + ", Y: " + moves[1]);
                            //Undo turn changes
                            changeTurn();
                            return false;
                        }

                    }

                }
            }
        }
        //Undo turn changes
        changeTurn();

        return true;
    }


    public void showBitmap(Cell cell) {
        Log.d("showBitmap", "showBitmap");
        cell.isShowingAvailableMove = false;
        if (cell.pieceType == EMPTY) {
            cell.setImageBitmap(null);
        } else {
            cell.changeBitmap();
            cell.setImageBitmap(cell.bitmap);
        }
    }

    public void moveCell(Cell cell) {
        if ((GameData.turn == WHITE && lastSelectedCell.pieceType < 6 && lastSelectedCell.pieceType != -1) || (GameData.turn == BLACK && lastSelectedCell.pieceType > 5)) {
            //if the cells moves


            isBeingCaptured = false;
            if (cell.pieceType != EMPTY && lastSelectedCell.pieceType != EMPTY) {
                Log.d("Comida", "ss");
                isBeingCaptured = true;
            }

            cell.pieceType = lastSelectedCell.pieceType;
            lastSelectedCell.pieceType = EMPTY;

            //if a pawn reach end line
            if (cell.pieceType == PAWN && cell.posY == 0)
                cell.pieceType = QUEEN;
            if (cell.pieceType == PAWN2 && cell.posY == 7)
                cell.pieceType = QUEEN2;

            showBitmap(cell);
            showBitmap(lastSelectedCell);


            //Updates data of the gameModel and sends it to the other client if its online
            sendData(cell);

            //Check if the game has ended
            checkWin();

            GameData.saveGameModel(gameModel, false);
            canChangeTurn = true;

        }
    }


    private void sendData(Cell cell) {
        //Aux positions are the positions that changed in the board, and the arraylist that will be send to server
        ArrayList<Integer> auxPositions = new ArrayList<>();
        auxPositions.add(lastSelectedCell.posX);
        auxPositions.add(lastSelectedCell.posY);
        auxPositions.add(cell.posX);
        auxPositions.add(cell.posY);

        if (cell.pieceType == cell.KING)
            whiteKing = cell;
        if (cell.pieceType == cell.KING2)
            blackKing = cell;

        gameModel.setPositions(auxPositions);

        //Just let to the client that is moving write to the server
        String userInput = auxPositions.toString();
        if (GameData.currentPlayer == GameData.turn && !GameData.isOffline) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        out.write((userInput + "\n").getBytes());
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //first it checks if the king is checked with checkCheckMate,then check if its mate with checkMate()
    private void checkWin() {
        if (checkCheckMate(whiteKing) || checkCheckMate(blackKing)) {
            if (checkMate()) {
                if (GameData.turn == GameData.BLACK) {
                    gameModel.setWinner(GameData.BLACK);

                    if (!GameData.isOffline) {
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }


                } else if (GameData.turn == GameData.WHITE) {
                    Toast.makeText(context, "White wins", Toast.LENGTH_SHORT).show();
                    gameModel.setWinner(GameData.WHITE);
                    if (!GameData.isOffline) {
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
                GameData.saveGameModel(gameModel, true);
                gameModel.setGAME_STATUS(GameData.FINISHED);
                //updateStats();
                return;
            }
        }
    }

    public boolean checkCheckMate(Cell king) {
        Log.d("checkCheckMate", "checkCheckMate");


        setAvailableEnemyMoves(king);

        for (ArrayList<int[]> arrays : king.availableEnemyMoves) {
            for (int[] enemyMoves : arrays) {
                //But also an enemy
                if ((king.posX == enemyMoves[0] && king.posY == enemyMoves[1]))
                    return true;

            }
        }

        return false;

    }


    public void setAllDirections(Cell cell) {
        Log.d("setAllDirections", "setAllDirections");
        cell.checkMateMoves.clear();


        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        //ALL DIRECTIONS
        for (int[] direction : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = cell.posX + i * direction[0];
                int newY = cell.posY + i * direction[1];

                if (!cell.isValidPosition(newX, newY)) break;

                int pieceAtNewPos = cell.getPieceAt(newX, newY);

                if (pieceAtNewPos == EMPTY) {
                    cell.checkMateMoves.add(new int[]{newX, newY});
                } else {
                    if (cell.isOpponentPiece(pieceAtNewPos)) {
                        cell.checkMateMoves.add(new int[]{newX, newY});
                    }
                    break; // Stop when a piece is found
                }
            }
        }
        //KNIGHT DIRECTIONS
        for (int[] move : knightMoves) {
            int newX = cell.posX + move[0];
            int newY = cell.posY + move[1];
            if (cell.isValidPosition(newX, newY)) {
                int pieceAtNewPos = cell.getPieceAt(newX, newY);
                if (pieceAtNewPos == EMPTY || cell.isOpponentPiece(pieceAtNewPos)) {
                    cell.checkMateMoves.add(new int[]{newX, newY});
                }
            }
        }
    }

    public void setAvailableEnemyMoves(Cell cell) {
        Log.d("setAvailableEnemyMoves", "setAvailableEnemyMoves");
        setAllDirections(cell);
        cell.availableEnemyMoves.clear();

        for (int[] move : cell.checkMateMoves) {
            int X = move[0];
            int Y = move[1];

            if (cell.isValidPosition(X, Y)) {
                cell.availableEnemyMoves.add(cell.chessboard.cells[X][Y].setEnemyMoves(true));
            }
        }
    }

    //Fakes a move from <selectedCell> to <underCheckCell> and checks if the king is safe after that
    public boolean checkKingIsSafe(Cell underCheckCell, Cell selectedCell) {
        Log.d("checkKingIsSafe", "checkKingIsSafe");
        if (underCheckCell.isOpponentPiece(selectedCell.pieceType) || underCheckCell.pieceType == EMPTY) {

            boolean changeWhiteKing = false;
            boolean changeBlackKing = false;

            //Saves the pieces types to undo the change later
            int currentOriginalPieceType = underCheckCell.pieceType;
            int lastOriginalPieceType = selectedCell.pieceType;

            //Fakes the move
            underCheckCell.pieceType = selectedCell.pieceType;
            selectedCell.pieceType = EMPTY;


            //If the king is the one being clicked, save correct kings positions to not change them after the simulation
            if (underCheckCell.pieceType == KING) {
                whiteKing = underCheckCell;
                changeWhiteKing = true;
            } else if (underCheckCell.pieceType == KING2) {
                blackKing = underCheckCell;
                changeBlackKing = true;
            }

            //Check that the king is in check after the move
            boolean kingIsSafe = true;
            if (GameData.turn == BLACK) {
                Log.d("dddds", blackKing.posX + " , " + blackKing.posY);
                if (checkCheckMate(blackKing)) {
                    kingIsSafe = false;
                }
            } else if (GameData.turn == WHITE) {
                Log.d("dddds", whiteKing.posX + " , " + whiteKing.posY);
                if (checkCheckMate(whiteKing)) {
                    kingIsSafe = false;
                }
            }

            //Undo changes
            underCheckCell.pieceType = currentOriginalPieceType;
            selectedCell.pieceType = lastOriginalPieceType;
            if (changeWhiteKing) {
                whiteKing = selectedCell;
            } else if (changeBlackKing) {
                blackKing = selectedCell;
            }


            return kingIsSafe;
        }
        return false;
    }

}