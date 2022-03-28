package com.example.courseproject_new.model;

import java.io.Serializable;

public class Event implements Serializable {
    private int event_id;
    private  String title;
    private  String description;
    private  String date;
    private  String time;
    private  String endTime;
    private  String endDate;
    private  byte [] picture;
    private  String adress;
    private double price;

    public Event(int event_id, String title, String description, String date, String time,String endDate, String endTime, byte[] picture, String adress, double price) {
        this.event_id = event_id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.picture = picture;
        this.adress = adress;
        this.endTime = endTime;
        this.endDate = endDate;
        this.price = price;
    }




    public Event(String title, String description,double price, String date, String time,String endDate, String endTime, byte [] picture, String adress) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.picture = picture;
        this.adress = adress;
        this.endTime = endTime;
        this.endDate = endDate;
        this.price = price;
    }

    public Event(int event_id){
        this.event_id = event_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte [] getPicture() {
        return picture;
    }

    public void setPicture(byte [] picture) {
        this.picture = picture;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
