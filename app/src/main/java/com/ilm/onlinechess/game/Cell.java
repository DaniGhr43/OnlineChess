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
    public Chessboard chessboard;
    public ArrayList<int[]> availableMoves = new ArrayList<>();
    public ArrayList<int[]> checkMateMoves= new ArrayList<>();
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


    public void setSeleccionada(boolean seleccionada){
        this.seleccionada = seleccionada;
        Log.d("setSeleccionada", "setSeleccionada");

        if(seleccionada )
            setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        else{
            if(backgroudColor==GameData.WHITE)
                setBackgroundColor((Color.WHITE));
            else
                setBackgroundColor((Color.GRAY));
        }

    }

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

    private void addKingMoves() {

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



        if (getPieceAt(posX, posY + direction) == EMPTY ) {
            availableMoves.add(new int[]{posX, posY + direction});
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
    public Integer getPieceAt(int x, int y) {
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


    public void changeBitmap(){
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


    public boolean isValidPosition(int posX, int posY) {
        return posX >= 0 && posX < 8 && posY >= 0 && posY < 8;
    }


}