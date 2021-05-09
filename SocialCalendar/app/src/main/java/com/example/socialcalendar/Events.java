package com.example.socialcalendar;

public class Events {
    public String uid, profileimage, username, event, time, date, month, year;

    public Events(){

    }

    public Events(String uid, String profileimage, String username, String event, String time, String date, String month, String year) {
        this.uid = uid;
        this.profileimage = profileimage;
        this.username = username;
        this.event = event;
        this.time = time;
        this.date = date;
        this.month = month;
        this.year = year;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

