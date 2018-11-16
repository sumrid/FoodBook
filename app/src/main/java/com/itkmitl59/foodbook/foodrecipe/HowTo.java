package com.itkmitl59.foodbook.foodrecipe;

import java.io.Serializable;

public class HowTo implements Serializable {
    private String description;
    private String imageUrl;

    public HowTo() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
