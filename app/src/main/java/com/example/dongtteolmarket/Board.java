package com.example.dongtteolmarket;

public class Board {
    private String userID;
    private String boardID;
    private String boardName;
    private String location;
    private int cost;
    private int count;
    private String note;
    private String startTime;
    private String endTime;
    private int likeCount;
    private String photo;
    private String now;

    public Board(){}

    public Board(String userID, String boardID, String boardName, String location, int cost, int count, String note, String startTime, String endTime, int likeCount, String photo, String now){
        this.userID = userID;
        this.boardID = boardID;
        this.location = location;
        this.boardName = boardName;
        this.cost = cost;
        this.count = count;
        this.note = note;
        this.startTime = startTime;
        this.endTime = endTime;
        this.likeCount = likeCount;
        this.photo = photo;
        this.now = now;
    }

    public String getUserID() {return userID;}
    public String getBoardName() {return boardName;}
    public int getCost() {return cost;}
    public int getCount() {return count;}
    public String getNote() {return note;}
    public String getStartTime() {return startTime;}
    public String getEndTime() {return endTime;}
    public int getLikeCount() {return likeCount;}
    public String getPhoto() {return photo;}
    public String getNow() {return now;}
    public String getLocation() {return location;}
    public String getBoardID() {return boardID;}

}
