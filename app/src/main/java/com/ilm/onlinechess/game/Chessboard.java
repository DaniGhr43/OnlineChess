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

    final int KING =0, KING2 =6;
    final int QUEEN =1 , QUEEN2 =7;
    final int ROOK =2 , ROOK2 =8;
    final int BISHOP =3 , BISHOP2 =9;
    final int KNIGHT =4 , KNIGHT2 =10;
    final int PAWN =5 , PAWN2 =11;
    final int EMPTY =-1 ;

    public GridLayout grid;
    public Context context;
    public Cell[][]  cells ;
    public ArrayList<int[]> availableMoves = new ArrayList<>();

    // private int[] lastSelection;
    private ArrayList<int[]> lastSelections = new ArrayList<>();
    private Cell lastSelectedCell;

    private final int WHITE=0;
    private final int BLACK=1;
    public  Cell whiteKing,blackKing;
    private int[][] matriz = new int[8][8];
    FirebaseFirestore db ;
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


    public Chessboard(GridLayout grid, Context context, LifecycleOwner lifecycleOwner){
        this.lifecycleOwner = lifecycleOwner;
        this.grid=grid;
        this.context = context;
        gameModel = GameData.gameModel.getValue();
        this.gameId = gameId;

        GameData.gameModel.observe(lifecycleOwner, new Observer<GameModel>() {
            @Override
            public void onChanged(GameModel newGameModel) {
                Log.d("changed","CHANGE");
                gameModel = newGameModel;
                ArrayList<Integer> clickedPositions = gameModel.getPositions();
                if (clickedPositions != null && !clickedPositions.isEmpty()){


                    click(cells[clickedPositions.get(0)][clickedPositions.get(1)]);
                    click(cells[clickedPositions.get(2)][clickedPositions.get(3)]);

                }

            }
        });

        if(!GameData.isOffline){
            joinGame();
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
    public Chessboard(){

    }
    public void joinGame() {
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
                                if(serverResponse.equals("EXIT")){
                                    if(GameData.currentPlayer==GameData.BLACK){
                                        gameModel.setWinner(GameData.BLACK);
                                        try {
                                            out.close();
                                            socket.close();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }else if(GameData.currentPlayer==GameData.WHITE){
                                        gameModel.setWinner(GameData.WHITE);
                                        try {
                                            out.close();
                                            socket.close();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    gameModel.setGAME_STATUS(GameData.FINISHED);
                                    GameData.saveGameModel(gameModel,true);
                                    break;
                                }

                                //Add the positions of the move done by the other player
                                for (String pos : serverResponseArray) {
                                    positions.add(Integer.parseInt(pos));
                                }

                                gameModel.setPositions(positions);
                                GameData.saveGameModel(gameModel,false);
                            }
                            Log.d("FINN ","SIIS");

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


    public void createCells(){


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

        for (int x = 0; x < 8; x ++) {
            for (int y = 0; y < 8; y++) {
                int pieceType = initialPiecePositions.getOrDefault(y + "," + x, EMPTY);

                Cell cell = new Cell(this, y , x, bitmaps,pieceType);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                params.width = 0;
                params.height =0;
                params.columnSpec = GridLayout.spec(x, 1f);
                params.rowSpec = GridLayout.spec(y, 1f);

                cell.setLayoutParams(params);

                // Asigna colores alternados para las celdas

                if ((x+y) % 2 == 0) {
                    cell.setBackgroundColor(Color.WHITE);
                    cell.backgroudColor=0;
                } else {
                    cell.setBackgroundColor(Color.GRAY);
                    cell.backgroudColor=1;
                }
                final int  XX= x;
                final int  YY= y;


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

                            if(GameData.gameModel.getValue().getGAME_STATUS()==GameData.STARTED ){

                                if(GameData.currentPlayer == GameData.turn ){
                                    click(cell);
                                }

                            }
                        }
                });


                cells[x][y]=cell;

                grid.addView(cell);


            }
        }



    }


    private void changeTurn() {
        GameData.turn=((GameData.turn == WHITE) ? BLACK : WHITE);

        if(GameData.isOffline)
            GameData.currentPlayer=((GameData.currentPlayer == WHITE) ? BLACK : WHITE);

    }




    private boolean mate = true;
    public void click(Cell cell) {
         isBeingCaptured = false;
         canChangeTurn = false;


        turnCont++;

        Log.d("CLOCK", String.valueOf(GameData.turn));


        //Change the state of selected in the clicked and last clicked cell if it can be changed
        if ( (cell.pieceType < 6 && GameData.turn == WHITE )|| (cell.pieceType > 5 && GameData.turn == BLACK) || cell.pieceType == cell.EMPTY || cell.isShowingAvailableMove )
            cell.setSeleccionada(!cell.seleccionada);


        //To unselect cells if a piece move
        if (lastSelectedCell != null && lastSelectedCell != cell)
            lastSelectedCell.setSeleccionada(false);

        //if the cell is being selected
        if (cell.seleccionada) {

            //if the cell is a king

            availableMoves = cell.setMoves(false);


            //if the cell is showing an available move(the bitmap of the cell is the dot) and is clicked, move the piece
            if (cell.isShowingAvailableMove) {
                //If its its turn
                moveCell(cell);
            }
            //Hide the available moves that are being showed in the last selected cell
            for (int last[] : lastSelections) {
                cells[last[0]][last[1]].hideAvailableMoves();
            }

            //show the availables moves of the cell. this is only executed if this cell is not being captured
            showAllAvailableMoves(cell);

       //if the cell is being unselected, hide all the availables moves that are being showed
        } else {
            hideLastShownMoves(cell);
        }

        if (canChangeTurn)
            changeTurn();



        cells[cell.posX][cell.posY] = cell;

        lastSelectedCell = cell;
    }

    private void showAllAvailableMoves(Cell cell){
        if (!isBeingCaptured) {
            lastSelections.clear();

            for (int moves[] : availableMoves) {
                int X = moves[0];
                int Y = moves[1];
                //correct values inside the board
                if (X >= 0 && Y >= 0 && X < 8 && Y < 8) {
                    showSingleAvailableMove(cells[X][Y],cell);
                    lastSelections.add(new int[]{X, Y});
                }
            }
        }
    }
    public void showSingleAvailableMove(Cell checkedCell, Cell clickedCell){
        Log.d("showAvailableMove", "showAvailableMove");

        Bitmap overlayBitmap = checkedCell.bitmaps[6]; // Bitmap to overlay when a cell is clicked(a dot)

        //BLACKS. setImageBitmap will set a just dot
        if(checkedCell.checkKingIsSafe(clickedCell)){
            if((checkedCell.pieceType==checkedCell.EMPTY || checkedCell.pieceType > 5 ) && clickedCell.pieceType < 6){
                checkedCell.setImageBitmap(checkedCell.bitmaps[6]);
                checkedCell.isShowingAvailableMove = true;
            }
            //WHITES. setImageBitmap will set a just dot
            if((checkedCell.pieceType==checkedCell.EMPTY || checkedCell.pieceType < 6 ) && clickedCell.pieceType > 5 ){
                checkedCell.setImageBitmap(checkedCell.bitmaps[6]);
                checkedCell.isShowingAvailableMove = true;
            }
            //WHITES. setImageBitmap will set a dot combined with the piece
            if (checkedCell.pieceType > 5 && clickedCell.pieceType < 6 && checkedCell.pieceType!=checkedCell.EMPTY) {
                Bitmap combinedBitmap = overlayBitmaps(checkedCell.bitmap, overlayBitmap);
                checkedCell.setImageBitmap(combinedBitmap);
                checkedCell.isShowingAvailableMove = true;
            }
            //BLACKS. setImageBitmap will set a dot combined with the piece
            if ( checkedCell.pieceType < 6 && clickedCell.pieceType > 5 && checkedCell.pieceType!=checkedCell.EMPTY) {
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


    private void hideLastShownMoves(Cell cell){
        if (availableMoves!=null){
            for (  int moves[] : availableMoves) {
                int X = moves[0];
                int Y = moves[1];

                if (X >= 0 && Y >= 0 && X < 8 && Y < 8) {
                    cells[X][Y].hideAvailableMoves();
                }
            }
            cell.availableMoves=new ArrayList<>();
        }
    }
   private boolean checkMate(){
        //Change turn becuase chenkKingIsSafe need the cell to be the same type as turn
       changeTurn();

       for (int i = 0 ; i<8; i++) {
           for (int j = 0 ; j<8; j++) {

               if( (( GameData.turn == GameData.BLACK && cells[i][j].pieceType > 5) || ( GameData.turn == GameData.WHITE && cells[i][j].pieceType < 6  && cells[i][j].pieceType!=cells[i][j].EMPTY))) {

                   availableMoves = cells[i][j].setMoves(false);

                   //if any cell can move , it is not mate
                   for (int[] moves : availableMoves) {
                       if (cells[i][j].isValidPosition(moves[0],moves[1]) && cells[moves[0]][moves[1]].checkKingIsSafe(cells[i][j])){
                           Log.d("PIECETYPE", String.valueOf(cells[i][j].pieceType));
                           Log.d("PIECETYPE", "X: "+ moves[0] + ", Y: " + moves[1]);
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



    public void moveCell(Cell cell){
        if ((GameData.turn== WHITE && lastSelectedCell.pieceType < 6 && lastSelectedCell.pieceType != -1) || (GameData.turn == BLACK && lastSelectedCell.pieceType > 5)) {
            isBeingCaptured = cell.movePiece(lastSelectedCell);

            //Aux positions are the positions that changed in the board, and the arraylist that will be send to server
            ArrayList<Integer> auxPositions = new ArrayList<>();
            auxPositions.add(lastSelectedCell.posX);
            auxPositions.add(lastSelectedCell.posY);
            auxPositions.add(cell.posX);
            auxPositions.add(cell.posY);

            if(cell.pieceType==cell.KING)
                whiteKing = cell;
            if(cell.pieceType==cell.KING2)
                blackKing = cell;

            gameModel.setPositions(auxPositions);

            //Just let to the client that is moving write to the server
            String userInput = auxPositions.toString();
            if(GameData.currentPlayer == GameData.turn &&  !GameData.isOffline){
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

            //Check if the game has ended
            checkWin();

            GameData.saveGameModel(gameModel , false);
            canChangeTurn=true;

        }
    }

    //first it checks if the king is checked with checkCheckMate,then check if its mate with checkMate()
    private void checkWin(){
        if(whiteKing.checkCheckMate() || blackKing.checkCheckMate()){
            if(checkMate()){
                if(GameData.turn == GameData.BLACK){
                    gameModel.setWinner(GameData.BLACK);

                    if(!GameData.isOffline){
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }


                }else if(GameData.turn == GameData.WHITE){
                    Toast.makeText(context, "White wins", Toast.LENGTH_SHORT).show();
                    gameModel.setWinner(GameData.WHITE);
                    if(!GameData.isOffline) {
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
                GameData.saveGameModel(gameModel,true);
                gameModel.setGAME_STATUS(GameData.FINISHED);
                //updateStats();
                return;
            }
        }
    }

}