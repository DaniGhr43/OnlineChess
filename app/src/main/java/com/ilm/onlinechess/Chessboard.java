package com.ilm.onlinechess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewTreeLifecycleOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chessboard {

    public GridLayout grid;
    public Context context;
    public Cell[][] cells ;
    public ArrayList<int[]> availableMoves;

    // private int[] lastSelection;
    private ArrayList<int[]> lastSelections = new ArrayList<>();
    private Cell lastSelectedCell;

    private final int WHITE=0;
    private final int BLACK=1;
    private Cell whiteKing,blackKing;
    private int[][] matriz = new int[8][8];
    FirebaseFirestore db ;
    private GameModel gameModel = new GameModel();
    private LifecycleOwner lifecycleOwner;
    private int turnCont;

    public Chessboard(GridLayout grid, Context context, LifecycleOwner lifecycleOwner){
        this.lifecycleOwner = lifecycleOwner;
        this.grid=grid;
        this.context = context;
        gameModel = GameData.gameModel.getValue();
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

        for (int y = 0; y < 8; y ++) {
            for (int x = 0; x < 8; x++) {

                Cell cell = new Cell(context, y , x, bitmaps);
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




                    cell.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Log.d("dd", String.valueOf(GameData.gameModel.getValue().getGAME_STATUS()));
                            if(GameData.gameModel.getValue().getGAME_STATUS()==GameData.STARTED){
                                for(Cell[] cel : cells){
                                    for(Cell c : cel){
                                        c.refreshChessboard(cells);
                                    }
                                }

                                Log.d("CLOICK", String.valueOf(cell.pieceType));



                                cell.refreshChessboard(cells);
                                click(cell);

                            }
                        }

                    });

                cells[x][y]=cell;

                grid.addView(cell);


            }
        }
    }


    private void changeTurn() {
        GameData.turn = (GameData.turn == WHITE) ? BLACK : WHITE;
    }
    public void click(Cell cell) {
        boolean isBeingCaptured = false;
        boolean canChangeTurn = false;


        turnCont++;

        Log.d("CLOICK", String.valueOf(GameData.turn));

        for (Cell[] cel : cells) {
            for (Cell c : cel) {
                c.refreshChessboard(cells);
            }
        }


        //Change the state of selected in the clicked and last clicked cell if it can be changed


        if (cell.pieceType < 6 && GameData.turn == WHITE || (cell.pieceType > 5 && GameData.turn == BLACK) || cell.pieceType == cell.EMPTY || cell.isShowingAvailableMove)
            cell.setSeleccionada(!cell.seleccionada);


        //To unselect cells if a piece move
        if (lastSelectedCell != null && lastSelectedCell != cell)
            lastSelectedCell.setSeleccionada(false);

        //if the cell is being selected
        if (cell.seleccionada) {

            //if the cell is a king
            if ((cell.pieceType == cell.KING || cell.pieceType == cell.KING2))
                availableMoves = cell.setMoves();
            else
                availableMoves = cell.setMoves(false);


            //if the cell is showing an available move(the bitmap of the cell is the dot) and is clicked, move the piece
            if (cell.isShowingAvailableMove) {
                //If its its turn
                if ((GameData.turn == WHITE && lastSelectedCell.pieceType < 6 && lastSelectedCell.pieceType != -1) || (GameData.turn == BLACK && lastSelectedCell.pieceType > 5)) {
                    isBeingCaptured = cell.movePiece(lastSelectedCell);
                    ArrayList<Integer> auxPositions = new ArrayList<>();

                    auxPositions.add(lastSelectedCell.posX);
                    auxPositions.add(lastSelectedCell.posY);
                    auxPositions.add(cell.posX);
                    auxPositions.add(cell.posY);

                    gameModel.setPositions(auxPositions);
                    GameData.saveGameModel(gameModel);


                    canChangeTurn = true;
                }

            }
            //Hide the last availables moves
            for (int last[] : lastSelections) {
                cells[last[0]][last[1]].hideAvailableMoves();
            }

            //show the availables moves of the cell. this is only executed if this cell is not being captured
            if (!isBeingCaptured) {
                lastSelections.clear();
                //if the cell is a checked king

                for (int moves[] : availableMoves) {
                    int X = moves[0];
                    int Y = moves[1];
                    //correct values inside the board
                    if (X >= 0 && Y >= 0 && X < 8 && Y < 8) {
                        cells[X][Y].showAvailableMove(cell);
                        lastSelections.add(new int[]{X, Y});
                    }
                }


            }
            //if the cell is being unselected, hide all the last shown availables moves
        } else {

            for (int moves[] : availableMoves) {
                int X = moves[0];
                int Y = moves[1];

                if (X >= 0 && Y >= 0 && X < 8 && Y < 8) {
                    cells[X][Y].hideAvailableMoves();
                }
            }
        }

        //If the game is offline just change turn if available, if the game is online  change the turn when the click is finished in the client who is not moving
        if (canChangeTurn)
            changeTurn();


        //Agregar el caballo a la available enemy moves
        lastSelectedCell = cell;
    }
//AÑADIR A SET AVAILABLES MOVES EL MOVIMIENTO DE CABALLO
}