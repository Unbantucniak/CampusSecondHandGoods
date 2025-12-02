package com.example.campussecondhandgoods.entity;

public class User {
    private int id;
    private String username;
    private String password;
    private String contact;
    private String avatarUrl;

    public void setId(int id) {this.id = id;}
    public int getId() {return id;}
    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}
    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return password;}
    public void setContact(String contact) {this.contact = contact;}
    public String getContact() {return contact;}

    public void setAvatarUrl(String avatarUrl) {this.avatarUrl = avatarUrl;}
    public String getAvatarUrl() {return avatarUrl;}


}
