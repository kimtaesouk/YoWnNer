package com.example.yownner;

public class Item_time {
    private String totalSeconds;
    private String starttime;
    private String stoptime;
    private String pid;
    private String timername;
    private String color;
    private String colorhex;
    private String date;

    // 생성자
    public Item_time(String pid, String totalSeconds, String starttime, String stoptime, String timername, String color , String colorhex, String date) {
        this.totalSeconds = totalSeconds;
        this.starttime = starttime;
        this.stoptime = stoptime;
        this.pid = pid;
        this.timername = timername;
        this.color = color;
        this.colorhex = colorhex;
        this.date = date;
    }

    // 게터 메서드
    public String getTotalSeconds() {
        return totalSeconds;
    }

    public String getStartTime() {
        return starttime;
    }

    public String getStopTime() {
        return stoptime;
    }

    public String getpid() {
        return pid;
    }

    public String getTimername() {
        return timername;
    }

    public String getColor() {
        return color;
    }

    public String getColorHex() {
        return colorhex;
    }
    public String getDate() {
        return date;
    }

    // 세터 메서드
    public void setDate(String date) {
        this.date = date;
    }
    public void setTotalSeconds(String totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public void setStartTime(String starttime) {
        this.starttime = starttime;
    }

    public void setStopTime(String stoptime) {
        this.stoptime = stoptime;
    }

    public void setTpid(String pid) {
        this.pid = pid;
    }

    public void setTimername(String timername) {
        this.timername = timername;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setColorHex(String colorhex) {
        this.colorhex = colorhex;
    }
}

