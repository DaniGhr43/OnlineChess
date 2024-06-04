package com.ilm.onlinechess;




public class User {
    private String username;
    private int rank=100;
    private String timespend;
    private double level=0;
    private String email;
    private boolean isHost;
    private String urlImage;


    private int type;

    public User() {
        this.username = "Guest";
        this.rank = 100;
        this.timespend = "";
        this.level = 1;
        this.email = "";
        this.isHost = false;
        this.type = -1;
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
    public void setLevel(double level){
        this.level=level;
    }
    public int getRank() {
        return rank;
    }

    public String getTimespend() {
        return timespend;
    }

    public double getLevel(){
        return level;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUrlImage(String url){
        this.urlImage=url;
    }
    public String getUrlImage(){
        return urlImage;
    }








}
