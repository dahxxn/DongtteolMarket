package com.example.dongtteolmarket;

public class OrderList {
    private String sellerID;
    private String customerID;
    private String boardID;
    private String state;
    private String pickupTime;
    private String callNum;
    private String count;
    private String request;
    private String orderID;
//5f5XiqcJ1bQ6bTbl46DygK9mIyn2 구매자
//            UK3iEYZAYlbOX6DujDQNb4mcdiZ2 판매자
//    B06T5rzXE0D7RNLa4RDu
    public OrderList(){}

    public OrderList(String boardID, String callNum, String count, String customerID,String orderID, String pickupTime,String request, String sellerID, String state){
        this.boardID = boardID;
        this.callNum = callNum;
        this.count=count;
        this.customerID=customerID;
        this.pickupTime=pickupTime;
        this.request=request;
        this.sellerID=sellerID;
        this.state=state;
        this.orderID=orderID;
    }

    public String getBoardID(){return boardID;}
    public String getCallNum() {return callNum; }
    public String getCount() {return count; }
    public String getCustomerID() {return customerID; }
    public String getPickupTime() {return pickupTime; }
    public String getRequest() {return request; }
    public String getSellerID() {return sellerID; }
    public String getOrderID() {return orderID; }
    public String getState() {return state; }

}
