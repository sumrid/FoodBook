package com.itkmitl59.foodbook.foodrecipe;

import java.util.ArrayList;
import java.util.List;

public class FoodRecipe {
    private String uid;
    private String name;
    private String description;
    private String mainImageUrl;
    private List<HowTo> howTos;
    private String ingredients; // ส่วนผสม
    private String category;
    private int like;
    private String postDate;
    private String owner;

    public FoodRecipe() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<HowTo> getHowTos() {
        return howTos;
    }

    public void setHowTos(List<HowTo> howTos) {
        this.howTos = howTos;
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

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
