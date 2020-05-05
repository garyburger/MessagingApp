package com.example.finalproject;

public class User {

    private String image;
    private String name;
    private String status;

    public User(){
        image = "";
        name = "";
        status = "";
    }

    public User(String name, String status, String image){
        this.image = image;
        this.name = name;
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
