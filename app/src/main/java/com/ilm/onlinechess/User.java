package com.ilm.onlinechess;

public class User {
    private String username;
    private int rank;
    private String timespend;
    private int level;
    private int email;
    public int getEmail() {
        return email;
    }

    public void setEmail(int email) {
        this.email = email;
    }
    public User(String username, int rank, String timespend, int level,int email) {
        this.username = username;
        this.rank = rank;
        this.timespend = timespend;
        this.level = level;
        this.email = email;
    }
    public User() {}

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

}
