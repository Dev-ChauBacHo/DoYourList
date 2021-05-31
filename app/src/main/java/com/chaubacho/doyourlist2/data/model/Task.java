package com.chaubacho.doyourlist2.data.model;

public class Task {
    private String name;
    private boolean isCompleted;
    private String date;
    private String time;
    private String coordinate;

    {
        this.isCompleted = false;
        this.date = "";
        this.time = "";
        this.coordinate = "";
    }

    public Task() {
    }

    public Task(String name) {
        this.name = name;

    }

    public Task(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public Task(String name, String date, String time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public Task(String name, boolean isCompleted, String date, String time, String coordinate) {
        this.name = name;
        this.isCompleted = isCompleted;
        this.date = date;
        this.time = time;
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
