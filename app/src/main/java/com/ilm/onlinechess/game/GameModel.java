package com.ilm.onlinechess.game;

import java.util.ArrayList;

public class GameModel {
    public  int gameId;
    private int winner;
    private String hostPlayer;
    private String guestPlayer;

    //private int turn;
    private String hostRank;
    private String guestRank;
    private String hostUri;
    private String guestUri;
    private int currentPlayer;
   private ArrayList<Integer> positions  ;
   private int GAME_STATUS ;

    public GameModel(){
        this.currentPlayer=0;
        this.gameId = -1;
        this.winner = -1;
        this.hostPlayer = "";
        this.GAME_STATUS = 0;
        this.hostRank = "";
        this.guestPlayer = "";
        this.guestUri="";
        this.hostUri="";
        positions = new ArrayList<>();
    }

    public String getHostPlayer() {
        return hostPlayer;
    }

    public  void setHostPlayer(String hostPlayer) {
        this.hostPlayer = hostPlayer;
    }
    public  String getGuestPlayer() {
        return guestPlayer;
    }

    public  void setGuestPlayer(String guestPlayer) {
        this.guestPlayer = guestPlayer;
    }

    public  int getGameId() {
        return gameId;
    }

    public  void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Integer> positions) {
        this.positions = positions;
    }

    public void setGAME_STATUS(int gameStatus){
        this.GAME_STATUS=gameStatus;
    }
    public int getGAME_STATUS(){
        return GAME_STATUS;
    }

    public void setHostRank(String hostRank) {
        this.hostRank = hostRank;
    }
    public void setGuestRank(String guestRank) {
        this.guestRank = guestRank;
    }

    public String getHostRank() {
        return hostRank;
    }

    public String getGuestRank() {
        return guestRank;
    }
    public String getHostUri() {
        return hostUri;
    }

    public String getGuestUri() {
        return guestUri;
    }

    public void setHostUri(String hostUri) {
        this.hostUri = hostUri;
    }

    public void setGuestUri(String guestUri) {
        this.guestUri = guestUri;
    }
}

