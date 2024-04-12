package com.example.dongtteolmarket;

//임의로 user 디비 작성
public class User {
    private String userID;
    private String email;
    private String password;
    private String userName;
    private String location;
    private String type;
    private int star;

    public User(){}

    public User(String userID, String email, String password, String userName, String location, String type, int star){
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.location = location;
        this.type = type;
        this.star = star;
    }

    public String getUserID() {return userID; }
    public String getEmail(){return email;}
    public String getPassword() {return password; }
    public String getUserName() {return userName; }
    public String getLocation() {return location; }
    public String getType() {return type; }
    public int getStar() {return star;}

}
