package com.itkmitl59.foodbook.profile;

import java.io.Serializable;

public class User implements Serializable {

    private String displayName;
    private String email;
    private String phone;
    private String aboutme;


    public User(String displayName, String email, String phone, String aboutme) {
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
        this.aboutme = aboutme;
    }

    public User () {}


    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
