package com.chaubacho.doyourlist2.data.model;

public class Project {
    private String color;
    private String name;

    public Project(String name) {
        color = "#FFFFFF";
        this.name = name;
    }

    public Project(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
