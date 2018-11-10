package com.itkmitl59.foodbook;

public class Like {
    private String foodID;
    private String userID;

    public Like() {
    }

    public Like(String foodID, String userID) {
        this.foodID = foodID;
        this.userID = userID;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
