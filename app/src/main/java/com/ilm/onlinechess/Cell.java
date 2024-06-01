package com.ilm.onlinechess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;


public class Cell extends androidx.appcompat.widget.AppCompatImageView implements Cloneable {

    final int KING =0, KING2 =6;
    final int QUEEN =1 , QUEEN2 =7;
    final int ROOK =2 , ROOK2 =8;
    final int BISHOP =3 , BISHOP2 =9;
    final int KNIGHT =4 , KNIGHT2 =10;
    final int PAWN =5 , PAWN2 =11;
    final int EMPTY =-1 ;
    int pieceType;
    Bitmap bitmap;
    Bitmap bitmaps[];
    int posX,posY;
    boolean seleccionada;
    public boolean isShowingAvailableMove=false;

    public int backgroudColor ;
    private final int WHITE=0;
    private final int BLACK=1;
    Cell[][] chessboard;
    public ArrayList<int[]> availableMoves = new ArrayList<>();
    private ArrayList<int[]> checkMateMoves;
    public ArrayList< ArrayList<int[]> > availableEnemyMoves = new ArrayList<>();

    private Context context;
    public Cell whiteKing,blackKing;


    //posX and posY are invertied because view and inserted from top to bottom, no from left to right
    public Cell(Context context, int posY, int posX, Bitmap[] bitmaps) {
        super(context);
        this.context=context;
        this.posX = posX;
        this.posY = posY;
        this.bitmaps = bitmaps;
        if(posY == 0 && posX == 0) {
            pieceType = ROOK;
        } else if (posY == 0 && posX == 1) {
            pieceType = KNIGHT;
        } else if (posY == 0 && posX == 2) {
            pieceType = BISHOP;
        } else if (posY == 0 && posX == 3) {
            pieceType = QUEEN;
        } else if (posY == 0 && posX == 4) {
            pieceType = KING;
        } else if (posY == 0 && posX == 5 ) {
            pieceType = BISHOP;
        }else if (posY == 0 && posX == 6 ) {
            pieceType = KNIGHT;
        }else if (posY == 0 && posX == 7 ) {
            pieceType = ROOK;
        } else if (posY == 1 ) {
            pieceType = PAWN;
        }else if (posY == 6 ) {
            pieceType = PAWN2;
        } else if (posY == 7 && posX == 0) {
            pieceType = ROOK2;
        } else if (posY == 7 && posX == 1) {
            pieceType = KNIGHT2;
        } else if (posY == 7 && posX == 2) {
            pieceType = BISHOP2;
        } else if (posY == 7 && posX == 3) {
            pieceType = QUEEN2;
        } else if (posY == 7 && posX == 4) {
            pieceType = KING2;
        } else if (posY == 7 && posX == 5) {
            pieceType = BISHOP2;
        } else if (posY == 7 && posX == 6) {
            pieceType = KNIGHT2;
        } else if (posY == 7 && posX == 7) {
            pieceType = ROOK2;
        } else {
            pieceType = EMPTY; // Si no hay pieza en la celda
        }

        changeBitmap();
        this.setImageBitmap(bitmap);

    }




    //To simulate the next move
    public Cell clone() {
        Log.d("Clone", "Cloned");
        try {
            Cell clonedCell = (Cell) super.clone();
            clonedCell.pieceType = this.pieceType;
            clonedCell.whiteKing = this.whiteKing;
            clonedCell.blackKing = this.blackKing;
            clonedCell.chessboard = this.chessboard;  // Añadir esta línea para asegurarse de que el tablero también se clona

            return clonedCell;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); // Manejar la excepción apropiadamente
        }
    }


    public void setSeleccionada(boolean seleccionada){
        this.seleccionada = seleccionada;
        Log.d("setSeleccionada", "setSeleccionada");

        if(pieceType == KING)
            whiteKing=this;
        if(pieceType==KING2)
            blackKing=this;

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
    public void refreshChessboard(Cell[][] chessboard){
        Log.d("refreshChessboard", "refreshChessboard");
        this.chessboard = chessboard;
        for(Cell[] cel : chessboard) {
            for (Cell c : cel) {
                if (c.pieceType == KING) {
                    whiteKing = c;
                }
                if (c.pieceType == KING2) {
                    blackKing = c;
                }
            }
        }

    }

    //refresh the value of the local chessboard variable
    //set the availables moves of the piece, if a piece of the other type is found the method stop adding available moves in that direction

    public ArrayList<int[]> setMoves() {
        Log.d("setMoves", "setMoves");
        availableMoves = new ArrayList<>();

        addCheckedKingMoves();
        return availableMoves;
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
                addKnightMoves2();
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
                addKnightMoves2();
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
    public ArrayList<int[]> setMoves3(boolean ignoreKing) {
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

                        if( (!ignoreKing && (chessboard[newX][newY].pieceType != KING || chessboard[newX][newY].pieceType != KING2)) )
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
               // if(chessboard[newX][newY].checkKingIsSafe(this)){
                    if (pieceAtNewPos == EMPTY) {
                        availableMoves.add(new int[]{newX, newY});
                    } else {
                        if (isOpponentPiece(pieceAtNewPos)) {
                            availableMoves.add(new int[]{newX, newY});
                            if((!ignoreKing && (chessboard[newX][newY].pieceType != KING || chessboard[newX][newY].pieceType != KING2)) )
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

    //set the moves of the king and sets check to true if needed
    private void addCheckedKingMoves() {
        Log.d("addCheckedKingMoves", "addCheckedKingMoves");

        int[][] moves = {
                {0, 0},{1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] move : moves) {

            int newX = posX + move[0];
            int newY = posY + move[1];

           //if (!isValidPosition(newX, newY)) {
           //   continue;
           // }
           // Integer pieceAtNewPos = getPieceAt(newX, newY);
           //if (pieceAtNewPos != EMPTY && !isOpponentPiece(pieceAtNewPos)) {
           //     continue;//}

            if (isValidPosition(newX,newY) && chessboard[newX][newY].checkKingIsSafe(this)) {
                availableMoves.add(new int[]{newX, newY});
                Log.d("guarda", "guarda");

            }

        }
    }


    private void addPawnMoves( boolean isWhite) {
        Log.d("addPawnMoves", "addPawnMoves");
        int direction = isWhite ? 1 : -1;
        int startRow = isWhite ? 1 : 6;


        // Movimiento hacia adelante
        if (getPieceAt(posX, posY + direction) == EMPTY && chessboard[posX][posY+direction].checkKingIsSafe(this)) {
            availableMoves.add(new int[]{posX, posY + direction});
            // Movimiento doble si está en la fila inicial

        }
        if (posY == startRow && getPieceAt(posX, posY + 2 * direction) == EMPTY) {
            if(isValidPosition(posX, posY + 2 * direction) && chessboard[posX][ posY + 2 * direction].checkKingIsSafe(this))
                availableMoves.add(new int[]{posX, posY + 2 * direction});
        }
        if (isOpponentPiece(getPieceAt(posX + 1, posY + direction))) {
            if(isValidPosition(posX + 1, posY + direction) && chessboard[posX + 1][posY + direction].checkKingIsSafe(this))
                availableMoves.add(new int[]{posX + 1, posY + direction});
        }
        if (isOpponentPiece(getPieceAt(posX - 1, posY + direction))) {
            if(isValidPosition(posX - 1, posY + direction) && chessboard[posX - 1][ posY + direction].checkKingIsSafe(this))
                availableMoves.add(new int[]{posX - 1, posY + direction});
        }
    }

    private void addPawnMoves2( boolean isWhite) {
        Log.d("addPawnMoves", "addPawnMoves");
        int direction = isWhite ? 1 : -1;
        int startRow = isWhite ? 1 : 6;



            if(isValidPosition(posX + 1, posY + direction) )
                availableMoves.add(new int[]{posX + 1, posY + direction});

            if(isValidPosition(posX - 1, posY + direction))
                availableMoves.add(new int[]{posX - 1, posY + direction});

    }

    //set all direction where an enemy can be found
    private ArrayList<int[]> setAllDirections() {
        Log.d("setAllDirections", "setAllDirections");
        checkMateMoves = new ArrayList<>();

        // Direcciones en línea y en diagonal para comprobar el jaque
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1} , {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        // Comprobar desde la posición actual de la casilla
        for (int[] direction : directions) {
            for (int i = 1; i < 8; i++) {
                int newX = posX + i * direction[0];
                int newY = posY + i * direction[1];

                //if its a horse newX and newY are calculated diferently
                if (direction[0] == 2 || direction[1] == 2) {
                    newX = posX + direction[0];
                    newY = posY + direction[1];

                }

                Integer pieceAtNewPos = getPieceAt(newX, newY);

                if (pieceAtNewPos == EMPTY) {
                    checkMateMoves.add(new int[]{newX, newY});
                } else {
                    if (isOpponentPiece(pieceAtNewPos)) {
                        checkMateMoves.add(new int[]{newX, newY});
                    }
                    break; // Parar cuando se encuentra una pieza
                }
            }
        }



        return checkMateMoves;
    }



    // Método auxiliar para verificar si una posición es válida dentro del tablero

    private Integer getPieceAt(int x, int y) {
        Log.d("getPieceAt", "getPieceAt");
        if(isValidPosition(x,y) ){
            return chessboard[x][y].pieceType;
        }else
            return EMPTY;

    }
    // COmpares actual position with the given one

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
    public void showAvailableMove(Cell clickedCell){
        Log.d("showAvailableMove", "showAvailableMove");

        Bitmap overlayBitmap = bitmaps[6]; // Bitmap to overlay when a cell is clicked(a dot)



        //BLACKS. setImageBitmap will set a just dot
        if(checkKingIsSafe(clickedCell)){
            if((pieceType==EMPTY || pieceType > 5 ) && clickedCell.pieceType < 6){
                setImageBitmap(bitmaps[6]);
                isShowingAvailableMove = true;
            }
            //WHITES. setImageBitmap will set a just dot
            if((pieceType==EMPTY || pieceType < 6 ) && clickedCell.pieceType > 5 ){
                setImageBitmap(bitmaps[6]);
                isShowingAvailableMove = true;
            }
            //WHITES. setImageBitmap will set a dot combined with the piece
            if (pieceType > 5 && clickedCell.pieceType < 6 && pieceType!=EMPTY) {
                Bitmap combinedBitmap = overlayBitmaps(bitmap, overlayBitmap);
                setImageBitmap(combinedBitmap);
                isShowingAvailableMove = true;
            }
            //BLACKS. setImageBitmap will set a dot combined with the piece
            if ( pieceType < 6 && clickedCell.pieceType > 5 && pieceType!=EMPTY) {
                Bitmap combinedBitmap = overlayBitmaps(bitmap, overlayBitmap);
                setImageBitmap(combinedBitmap);
                isShowingAvailableMove = true;
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
        showBitmap();
        lastSelectedCell.showBitmap();

        return returnn;
    }
// En la clase Cell

    public boolean checkKingIsSafe(Cell lastSelectedCell) {
        Log.d("checkKingIsSafe", "checkKingIsSafe");
        if(isOpponentPiece(lastSelectedCell.pieceType) || pieceType==EMPTY){
            // Crear copias de las celdas
            Cell currentCellCopy = this.clone();
            Cell[][] clonedChessboard = new Cell[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    clonedChessboard[i][j] = chessboard[i][j].clone();
                }
            }
            Cell lastSelectedCellCopy = lastSelectedCell.clone();
            currentCellCopy.whiteKing = whiteKing.clone();
            lastSelectedCellCopy.whiteKing = whiteKing.clone();
            currentCellCopy.blackKing = blackKing.clone();
            lastSelectedCellCopy.blackKing = blackKing.clone();
            currentCellCopy.refreshChessboard(clonedChessboard);
            lastSelectedCellCopy.refreshChessboard(clonedChessboard);
            //currentCellCopy.movePiece(lastSelectedCellCopy);


            Log.d("MAR",  "PiecaType current:" + currentCellCopy.pieceType);
            Log.d("MAR2", "PiecaType last:" + lastSelectedCellCopy.pieceType);
            int auxPiecetype = currentCellCopy.pieceType;
            currentCellCopy.pieceType=lastSelectedCellCopy.pieceType;
            lastSelectedCellCopy.pieceType = EMPTY;


            currentCellCopy.setSeleccionada(false);

            Log.d("MAR", currentCellCopy.posX + " posY " + currentCellCopy.posY);
            Log.d("MAR2", lastSelectedCellCopy.posX + " posY " + lastSelectedCellCopy.posY);

            //You have to refresh the value in the chessboard of the changed piece, and refresh the chessboard of the kings with that new chessboard
            currentCellCopy.chessboard[currentCellCopy.posX][currentCellCopy.posY] = currentCellCopy;
            lastSelectedCellCopy.chessboard[lastSelectedCellCopy.posX][lastSelectedCellCopy.posY] = lastSelectedCellCopy;
            currentCellCopy.blackKing.refreshChessboard(currentCellCopy.chessboard);
            currentCellCopy.whiteKing.refreshChessboard(currentCellCopy.chessboard);
            lastSelectedCellCopy.whiteKing.refreshChessboard(lastSelectedCellCopy.chessboard);
            lastSelectedCellCopy.blackKing.refreshChessboard(lastSelectedCellCopy.chessboard);




            if( ( GameData.turn == BLACK  && currentCellCopy.blackKing.checkCheckMate() ) || (GameData.turn == WHITE && currentCellCopy.whiteKing.checkCheckMate() ) && currentCellCopy.isOpponentPiece(auxPiecetype)  ){
                Log.d("REY22", "posX: " +currentCellCopy.blackKing.posX + "posY: " + currentCellCopy.blackKing.posY);

                return false;
            }
            lastSelectedCellCopy.pieceType= currentCellCopy.pieceType;
            currentCellCopy.pieceType=auxPiecetype;

            return true;
        }
        return false;
    }



    //CHECK THAN IN A CELL AN ENEMY CAN MOVE AND SET THE POSSIBLE MOVEMENTS OF THE ENEMY
    public boolean checkCheckMate(){
        Log.d("checkCheckMate", "checkCheckMate");

        boolean check = false;

        Log.d("ARRAYS", "---------------");
        Log.d("WHERECHEKING", "("+posX + ":" + posY+")");

        if(pieceType==KING2)
            Log.d("REY", "("+posX + ":" + posY+")");

        setAvailableEnemyMoves();

        for (ArrayList<int[]> arrays : availableEnemyMoves ) {
            for (int[] enemyMoves : arrays) {

                int enemyX =enemyMoves[0];
                int enemyY =enemyMoves[1];

                Log.d("ARRAYS", "("+enemyX + "," + enemyY+")");
                //But also an enemy
                if ((posX == enemyX && posY == enemyY)   ) {
                    check = true;
                }
            }
        }
       // availableEnemyMoves.clear();
        return check;

    }

    //CREAR OTRO TIPO DE SETMOVES PARA LOS AVAILABLE ENEMY MOVES
    private void setAvailableEnemyMoves(){
        Log.d("setAvailableEnemyMoves", "setAvailableEnemyMoves");
        checkMateMoves=setAllDirections();
        availableEnemyMoves.clear();


        for (int moves[] : checkMateMoves) {
            int X = moves[0];
            int Y = moves[1];

            //IF THE POSITION EXISTS
            if (isValidPosition(X, Y) ) {
                availableEnemyMoves.add(chessboard[X][Y].setEnemyMoves(true));
                for (ArrayList<int[]> moves3 : availableEnemyMoves) {
                    for(int [] moves4 :moves3){
                        Log.d("ESOE", Arrays.toString(moves4));
                    }
                }


                Log.d("ESOE", String.valueOf(pieceType));
            }
        }

    }

    public boolean isValidPosition(int posX, int posY) {
        return posX >= 0 && posX < 8 && posY >= 0 && posY < 8;
    }
    public boolean isLegitMove( Cell king){

        int cont = 0;
        if(checkKingIsSafe(king)){
            if((pieceType==EMPTY || pieceType > 5 ) && king.pieceType < 6){
                cont++;

            }
            //WHITES. setImageBitmap will set a just dot
            if((pieceType==EMPTY || pieceType < 6 ) && king.pieceType > 5 ){
                cont++;
            }
            //WHITES. setImageBitmap will set a dot combined with the piece
            if (pieceType > 5 && king.pieceType < 6 && pieceType!=EMPTY) {
                cont++;

            }
            //BLACKS. setImageBitmap will set a dot combined with the piece
            if ( pieceType < 6 && king.pieceType > 5 && pieceType!=EMPTY) {
                cont++;
            }


        }
        if(cont == 0){
            return true;
        }else
            return false;

    }

}