package com.itkmitl59.foodbook.foodrecipe;

import java.util.ArrayList;

public class FoodRecipe {
    private String name;
    private String description;
    private String mainImageUrl;
    private String howTo;
    private String ingredients; // ส่วนผสม
    private String category;
    private ArrayList<Double> score;

    public FoodRecipe() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getHowTo() {
        return howTo;
    }

    public void setHowTo(String howTo) {
        this.howTo = howTo;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Double> getScore() {
        return score;
    }

    public void setScore(ArrayList<Double> score) {
        this.score = score;
    }
}
