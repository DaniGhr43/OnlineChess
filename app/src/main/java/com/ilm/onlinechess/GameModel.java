package com.ilm.onlinechess;

import androidx.lifecycle.MutableLiveData;

import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameModel {
    public  int gameId;
    private int winner;
    private String hostPlayer;
    private String guestPlayer;

    //private int turn;
    private String hostRank;
    private String guestRank;
    private int currentPlayer;
   private ArrayList<Integer> positions  ;
   private int GAME_STATUS ;




    public GameModel(){
        this.currentPlayer=0;
        this.gameId = -1;
        this.winner = -1;
        this.hostPlayer = "";
        this.guestPlayer = "";
        this.GAME_STATUS = 0;
        this.hostRank = "";
        this.guestPlayer = "";

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

}

