package com.ilm.onlinechess.game;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.ilm.onlinechess.R;

import java.util.ArrayList;


public class Cell extends androidx.appcompat.widget.AppCompatImageView  {

    final int KING =0, KING2 =6;
    final int QUEEN =1 , QUEEN2 =7;
    final int ROOK =2 , ROOK2 =8;
    final int BISHOP =3 , BISHOP2 =9;
    final int KNIGHT =4 , KNIGHT2 =10;
    final int PAWN =5 , PAWN2 =11;
    final int EMPTY =-1 ;
    int pieceType;
    public Bitmap bitmap;
    public Bitmap bitmaps[];
    int posX,posY;
    boolean seleccionada;
    public boolean isShowingAvailableMove=false;

    public int backgroudColor ;
    private final int WHITE=0;
    private final int BLACK=1;
    public Chessboard chessboard;
    public ArrayList<int[]> availableMoves = new ArrayList<>();
    private ArrayList<int[]> checkMateMoves= new ArrayList<>();
    public ArrayList< ArrayList<int[]> > availableEnemyMoves = new ArrayList<>();


    //posX and posY are invertied because view and inserted from top to bottom, no from left to right
    public Cell(Chessboard chessboard, int posY, int posX, Bitmap[] bitmaps,int pieceType) {
        super(chessboard.context);
        this.posX = posX;
        this.posY = posY;
        this.bitmaps = bitmaps;
        this.chessboard = chessboard;
        this.pieceType = pieceType;


        changeBitmap();
        this.setImageBitmap(bitmap);

    }






    //To simulate the next move



    public void setSeleccionada(boolean seleccionada){
        this.seleccionada = seleccionada;
        Log.d("setSeleccionada", "setSeleccionada");

        if(seleccionada )
            setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        else{
            if(backgroudColor==WHITE)
                setBackgroundColor((Color.WHITE));
            else
                setBackgroundColor((Color.GRAY));
        }

    }

    //Refresh the chessboard and the whiteKing and blackKing references

    //REVISAR QUE EL BLACKKING Y EL WHITE KING SON LO QUE DICEN SER




    //refresh the value of the local chessboard variable
    //set the availables moves of the piece, if a piece of the other type is found the method stop adding available moves in that direction


    public ArrayList<int[]> setMoves(boolean ignoreKing) {
        Log.d("setMovesPARAM", "setMovesPARAM");
        availableMoves = new ArrayList<>();

        switch (pieceType) {
            case PAWN:
                addPawnMoves(true);
                break;
            case ROOK:
                addRookMoves(ignoreKing);
                break;
            case KNIGHT:
                addKnightMoves();
                break;
            case BISHOP:
                addBishopMoves(ignoreKing);
                break;
            case QUEEN:
                addRookMoves(ignoreKing);
                addBishopMoves(ignoreKing);
                break;
            case KING:
                addKingMoves();
                break;
            case PAWN2:
                addPawnMoves(false);
                break;
            case ROOK2:
                addRookMoves(ignoreKing);
                break;
            case KNIGHT2:
                addKnightMoves();
                break;
            case BISHOP2:
                addBishopMoves(ignoreKing);
                break;
            case QUEEN2:
                addRookMoves(ignoreKing);
                addBishopMoves(ignoreKing);
                break;
            case KING2:
                addKingMoves();
                break;
        }
        return availableMoves;
    }
    public ArrayList<int[]> setEnemyMoves(boolean ignoreKing) {
        Log.d("setMovesPARAM", "setMovesPARAM");
        availableMoves = new ArrayList<>();

        switch (pieceType) {
            case PAWN:
                addPawnMoves2(true);
                break;
            case ROOK:
                addRookMoves(ignoreKing);
                break;
            case KNIGHT:
                addKnightMoves();
                break;
            case BISHOP:
                addBishopMoves(ignoreKing);
                break;
            case QUEEN:
                addRookMoves(ignoreKing);
                addBishopMoves(ignoreKing);
                break;
            case KING:
                addKingMoves();
                break;
            case PAWN2:
                addPawnMoves2(false);
                break;
            case ROOK2:
                addRookMoves(ignoreKing);
                break;
            case KNIGHT2:
                addKnightMoves();
                break;
            case BISHOP2:
                addBishopMoves(ignoreKing);
                break;
            case QUEEN2:
                addRookMoves(ignoreKing);
                addBishopMoves(ignoreKing);
                break;
            case KING2:
                addKingMoves();
                break;
        }
        return availableMoves;
    }

    private void addRookMoves(boolean ignoreKing) {
        Log.d("addRookMoves", "addRookMoves");

        // Movimiento horizontal y vertical
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] direction : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = posX + i * direction[0];
                int newY = posY + i * direction[1];

                if(!isValidPosition(newX,newY))
                    break;

                Integer pieceAtNewPos = getPieceAt(newX, newY);


                if (pieceAtNewPos == EMPTY) {
                        availableMoves.add(new int[]{newX, newY});
                } else {
                    if (isOpponentPiece(pieceAtNewPos)) {
                        availableMoves.add(new int[]{newX, newY});

                        if( (!ignoreKing && (chessboard.cells[newX][newY].pieceType != KING || chessboard.cells[newX][newY].pieceType != KING2)) )
                            break;
                    } else {
                        availableMoves.add(new int[]{newX, newY});

                        break; // Detenerse al encontrar una pieza del mismo color
                    }
                }
            }
        }
    }

    private void addBishopMoves(boolean ignoreKing) {
        Log.d("addBishopMoves", "addBishopMoves");
        // Movimiento diagonal
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        for (int[] direction : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = posX + i * direction[0];
                int newY = posY + i * direction[1];

                if(!isValidPosition(newX,newY))
                    break;

                Integer pieceAtNewPos = getPieceAt(newX, newY);

                    if (pieceAtNewPos == EMPTY) {
                        availableMoves.add(new int[]{newX, newY});
                    } else {
                        if (isOpponentPiece(pieceAtNewPos)) {
                            availableMoves.add(new int[]{newX, newY});
                            if((!ignoreKing && (chessboard.cells[newX][newY].pieceType != KING || chessboard.cells[newX][newY].pieceType != KING2)) )
                                break;

                        } else {
                            availableMoves.add(new int[]{newX, newY});
                            break; // Detenerse al encontrar una pieza del mismo color
                        }
                    }
                //}

            }
        }
    }

    private void addKnightMoves() {
        Log.d("addKnightMoves", "addKnightMoves");

        // Movimiento del caballo

        int[][] moves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : moves) {
            int newX = posX + move[0];
            int newY = posY + move[1];

            Integer pieceAtNewPos = getPieceAt(newX, newY);

            if ( (pieceAtNewPos == EMPTY || isOpponentPiece(pieceAtNewPos)) && isValidPosition(newX,newY) ) {
                availableMoves.add(new int[]{newX, newY});
            }
        }
    }
    private void addKnightMoves2() {
        Log.d("addKnightMoves", "addKnightMoves");

        // Movimiento del caballo

        int[][] moves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : moves) {
            int newX = posX + move[0];
            int newY = posY + move[1];


            if ( isValidPosition(newX,newY)  ) {
                availableMoves.add(new int[]{newX, newY});
            }
        }
    }
    private void addKingMoves() {
        // Movimiento del rey
        int[][] moves = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] move : moves) {
            int newX = posX + move[0];
            int newY = posY + move[1];

            Integer pieceAtNewPos = getPieceAt(newX, newY);

            if (pieceAtNewPos == EMPTY || isOpponentPiece(pieceAtNewPos)) {
                availableMoves.add(new int[]{newX, newY});
            }
        }
    }




    private void addPawnMoves( boolean isWhite) {
        Log.d("addPawnMoves", "addPawnMoves");
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;


        // Movimiento hacia adelante
        if (getPieceAt(posX, posY + direction) == EMPTY ) {
            availableMoves.add(new int[]{posX, posY + direction});
            // Movimiento doble si está en la fila inicial
        }
        if (posY == startRow && getPieceAt(posX, posY + 2 * direction) == EMPTY) {
            if(isValidPosition(posX, posY + 2 * direction))
                availableMoves.add(new int[]{posX, posY + 2 * direction});
        }
        if (isOpponentPiece(getPieceAt(posX + 1, posY + direction))) {
            if(isValidPosition(posX + 1, posY + direction))
                availableMoves.add(new int[]{posX + 1, posY + direction});
        }
        if (isOpponentPiece(getPieceAt(posX - 1, posY + direction))) {
            if(isValidPosition(posX - 1, posY + direction) )
                availableMoves.add(new int[]{posX - 1, posY + direction});
        }
    }

    private void addPawnMoves2( boolean isWhite) {
        Log.d("addPawnMoves", "addPawnMoves");
        int direction = isWhite ?-1 : 1;



        if(isValidPosition(posX + 1, posY + direction) )
            availableMoves.add(new int[]{posX + 1, posY + direction});

        if(isValidPosition(posX - 1, posY + direction))
            availableMoves.add(new int[]{posX - 1, posY + direction});

    }

    //set all direction where an enemy can be found
    private void setAllDirections() {
        Log.d("setAllDirections", "setAllDirections");
        checkMateMoves.clear();


        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        //ALL DIRECTIONS
        for (int[] direction : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = posX + i * direction[0];
                int newY = posY + i * direction[1];

                if (!isValidPosition(newX, newY)) break;

                int pieceAtNewPos = getPieceAt(newX, newY);

                if (pieceAtNewPos == EMPTY) {
                    checkMateMoves.add(new int[]{newX, newY});
                } else {
                    if (isOpponentPiece(pieceAtNewPos)) {
                        checkMateMoves.add(new int[]{newX, newY});
                    }
                    break; // Stop when a piece is found
                }
            }
        }
        //KNIGHT DIRECTIONS
        for (int[] move : knightMoves) {
            int newX = posX + move[0];
            int newY = posY + move[1];
            if (isValidPosition(newX, newY)) {
                int pieceAtNewPos = getPieceAt(newX, newY);
                if (pieceAtNewPos == EMPTY || isOpponentPiece(pieceAtNewPos)) {
                    checkMateMoves.add(new int[]{newX, newY});
                }
            }
        }
    }




    private Integer getPieceAt(int x, int y) {
        Log.d("getPieceAt", "getPieceAt");
        if(isValidPosition(x,y) ){
            return chessboard.cells[x][y].pieceType;
        }else
            return EMPTY;

    }


    public boolean isOpponentPiece(Integer pieceAtNewPos) {
        Log.d("isOpponentPiece", "isOpponentPiece");
        if (pieceAtNewPos == EMPTY) {
            return false;
        }
        if (pieceType < 6 && pieceAtNewPos > 5 && pieceType > -1 ){
            return true;
        }
        if (pieceType > 5 && pieceAtNewPos < 6 && pieceAtNewPos > -1) {
            return true;
        }
        return false;
    }


    private void changeBitmap(){
        Log.d("changeBitmap", "changeBitmap");
        switch (pieceType) {
            case KING:
                bitmap= bitmaps[0];
                break;
            case QUEEN:
                bitmap= bitmaps[1];
                break;
            case ROOK:
                bitmap= bitmaps[2];
                break;
            case KNIGHT:
                bitmap= bitmaps[3];
                break;
            case PAWN:
                bitmap= bitmaps[4];
                break;
            case BISHOP:
                bitmap= bitmaps[5];
                break;
            case KING2:
                bitmap= bitmaps[7];
                break;
            case QUEEN2:
                bitmap= bitmaps[8];
                break;
            case ROOK2:
                bitmap= bitmaps[9];
                break;
            case KNIGHT2:
                bitmap= bitmaps[10];
                break;
            case PAWN2:
                bitmap= bitmaps[11];
                break;
            case BISHOP2:
                bitmap= bitmaps[12];
                break;
        }
    }

    //Show a dot in the cell if its empty or if the clickedCell can eat this cell
    public void showBitmap(){
        Log.d("showBitmap", "showBitmap");
        isShowingAvailableMove = false;
        if(pieceType==EMPTY){
            setImageBitmap(null);
        }else{
            changeBitmap();
            setImageBitmap(bitmap);
        }
    }

    public void hideAvailableMoves(){
        Log.d("hideAvailableMoves", "hideAvailableMoves");
        if(pieceType==EMPTY){
            setImageBitmap(null);
            isShowingAvailableMove = false;
        }else{
            setImageBitmap(bitmap);
            isShowingAvailableMove = false;
        }

    }



    //Set <seleccionada> to not show <availableMoves> after moving
    //return true if a piece has being eaten
    //<lastSelectedCell> is the cell where movement comes. <cell> is the cell where the piece will be
    public boolean movePiece(Cell lastSelectedCell){
        Log.d("movePiece", "movePiece");

        boolean returnn=false;
        if(pieceType!=EMPTY && lastSelectedCell.pieceType!=EMPTY){
            Log.d("Comida", "ss");
            returnn= true;
        }

        pieceType=lastSelectedCell.pieceType;
        lastSelectedCell.pieceType = EMPTY;

        //if a pawn reach end line
        if(pieceType==PAWN && posY==0)
            pieceType=QUEEN;
        if(pieceType==PAWN2 && posY==7)
            pieceType=QUEEN2;

        showBitmap();
        lastSelectedCell.showBitmap();

        return returnn;
    }
// En la clase Cell



    //Simulates a move from lastSelectedCell to this
    public boolean checkKingIsSafe(Cell lastSelectedCell) {
        Log.d("checkKingIsSafe", "checkKingIsSafe");
        if (isOpponentPiece(lastSelectedCell.pieceType) || pieceType == EMPTY) {

            boolean changeWhiteKing = false;
            boolean changeBlackKing = false;

            //Saves the pieces types to undo the change later
            int currentOriginalPieceType = this.pieceType;
            int lastOriginalPieceType = lastSelectedCell.pieceType;

            //Fakes the move
            this.pieceType = lastSelectedCell.pieceType;
            lastSelectedCell.pieceType = EMPTY;


            //If the king is the one being clicked, save correct kings positions to not change them after the simulation
            if (this.pieceType == KING) {
                chessboard.whiteKing = this;
                changeWhiteKing = true;
            } else if (this.pieceType == KING2) {
                chessboard.blackKing = this;
                changeBlackKing = true;
            }

            //Check that the king is in check after the move
            boolean kingIsSafe = true;
            if (GameData.turn == BLACK) {
                Log.d("dddds", chessboard.blackKing.posX + " , " + chessboard.blackKing.posY);
                if (chessboard.blackKing.checkCheckMate()) {
                    kingIsSafe = false;
                }
            } else if (GameData.turn == WHITE) {
                Log.d("dddds", chessboard.whiteKing.posX + " , " + chessboard.whiteKing.posY);
                if (chessboard.whiteKing.checkCheckMate()) {
                    kingIsSafe = false;
                }
            }

            //Undo changes
            this.pieceType = currentOriginalPieceType;
            lastSelectedCell.pieceType = lastOriginalPieceType;
            if (changeWhiteKing) {
                chessboard.whiteKing = lastSelectedCell;
            } else if (changeBlackKing) {
                chessboard.blackKing = lastSelectedCell;
            }




            return kingIsSafe;
        }
        return false;
    }




    //CHECK THAN IN A CELL AN ENEMY CAN MOVE AND SET THE POSSIBLE MOVEMENTS OF THE ENEMY

    public boolean checkCheckMate(){
        Log.d("checkCheckMate", "checkCheckMate");



        setAvailableEnemyMoves();


        for (ArrayList<int[]> arrays : availableEnemyMoves ) {
            for (int[] enemyMoves : arrays) {
                //But also an enemy
                if ((posX == enemyMoves[0] && posY == enemyMoves[1])   )
                    return true;

            }
        }

        return false;

    }


    //CREAR OTRO TIPO DE SETMOVES PARA LOS AVAILABLE ENEMY MOVES
    private void setAvailableEnemyMoves() {
        Log.d("setAvailableEnemyMoves", "setAvailableEnemyMoves");
        setAllDirections();
        availableEnemyMoves.clear();

        for (int[] move : checkMateMoves) {
            int X = move[0];
            int Y = move[1];

            if (isValidPosition(X, Y)) {
                availableEnemyMoves.add(chessboard.cells[X][Y].setEnemyMoves(true));
            }
        }
    }



    public boolean isValidPosition(int posX, int posY) {
        return posX >= 0 && posX < 8 && posY >= 0 && posY < 8;
    }


}