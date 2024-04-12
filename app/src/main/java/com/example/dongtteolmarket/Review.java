package com.example.dongtteolmarket;

public class Review {
    private String sellerID;
    private String customerID;
    private String boardID;
    private String reviewID;

    private String content;
    private int star;
    private String writer;

    public Review(){}

    public Review(String boardID,String content,String customerID, String reviewID, String sellerID, int star, String writer){
        this.reviewID=reviewID;
        this.boardID=boardID;
        this.sellerID = sellerID;
        this.customerID =customerID;
        this.content=content;
        this.star=star;
        this.writer=writer;

    }
    public String getReviewID(){return reviewID;}

    public String getBoardID(){return boardID;}

    public String getSellerID(){return sellerID;}
    public String getCustomerID(){return customerID;}

    public String getContent(){return content;}
    public int getStar(){return star;}
    public String getWriter(){return writer;}
}
