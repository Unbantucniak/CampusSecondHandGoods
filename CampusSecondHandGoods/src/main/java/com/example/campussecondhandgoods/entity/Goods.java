package com.example.campussecondhandgoods.entity;

public class Goods {
    private int id;
    private String title;
    private double price;
    private String description;
    private String imgUrl;
    private int category;
    private int userId;
    private String contact;
    private String sellerName;
    private String sellerAvatar;
    private int status; // 0:在售, 1:已售出
    private int viewCount;

    public void setId(int id) {this.id = id;}
    public int getId() {return id;}
    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return title;}
    public void setPrice(double price) {this.price = price;}
    public double getPrice() {return price;}
    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}
    public void setImgUrl(String imgUrl) {this.imgUrl= imgUrl;}
    public String getImgUrl() {return imgUrl;}
    public void setCategory(int category) {this.category = category;}
    public int getCategory() {return category;}
    public void setUserId(int userId) {this.userId = userId;}
    public int getUserId() {return userId;}

    public void setContact(String contact) {this.contact = contact;}
    public String getContact() {return contact;}

    public void setSellerName(String sellerName) {this.sellerName = sellerName;}
    public String getSellerName() {return sellerName;}

    public void setSellerAvatar(String sellerAvatar) {this.sellerAvatar = sellerAvatar;}
    public String getSellerAvatar() {return sellerAvatar;}

    public void setStatus(int status) {this.status = status;}
    public int getStatus() {return status;}

    public void setViewCount(int viewCount) {this.viewCount = viewCount;}
    public int getViewCount() {return viewCount;}
}
