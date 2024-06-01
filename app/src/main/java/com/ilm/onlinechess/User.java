package com.ilm.onlinechess;

public class User {
    private String username;
    private int rank=100;
    private String timespend;
    private int level=0;
    private String email;
    private boolean isHost;

    public User(String username, int rank, String timespend, int level,String email) {
        this.username = username;
        this.rank = rank;
        this.timespend = timespend;
        this.level = level;
        this.email = email;
    }
    public User() {
        this.username = "Guest";
        this.rank = 100;
        this.timespend = "";
        this.level = 1;
        this.email = "";
        this.isHost = false;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setTimespend(String timespend) {
        this.timespend = timespend;
    }
    public String getUsername() {
        return username;
    }
    public void setLevel(int level){
        this.level=level;
    }
    public int getRank() {
        return rank;
    }

    public String getTimespend() {
        return timespend;
    }

    public int getLevel(){
        return level;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
