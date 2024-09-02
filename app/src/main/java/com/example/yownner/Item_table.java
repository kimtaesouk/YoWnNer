package com.example.yownner;

public class Item_table {
    private String startTime;
    private String stopTime;
    private int color;

    public Item_table(String startTime, String stopTime, int color) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.color = color;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
