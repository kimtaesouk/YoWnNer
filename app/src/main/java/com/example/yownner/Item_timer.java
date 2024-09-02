package com.example.yownner;

public class Item_timer {
    private String name;
    private String pid;
    private String upid;
    private String color;
    private String colorhex;
    private String date;
    private String totalSeconds;
    private String starttime;
    private String stoptime;
    private int status; // status 필드 추가

    public Item_timer(String name, String pid, String upid, String color, String colorhex, String date, int status, String totalSeconds) {
        this.name = name;
        this.pid = pid;
        this.status = status;
        this.color = color;
        this.colorhex = colorhex;
        this.date = date;
        this.upid = upid;
        this.totalSeconds = totalSeconds;
        this.starttime = starttime;
        this.stoptime = stoptime;
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }
    public String getUpid() {
        return upid;
    }

    public String getColor() {
        return color;
    }

    public String getColorhex() {
        return colorhex;
    }

    public String getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }
    public String getTotalSeconds() {
        return totalSeconds;
    }
    public String getStarttime() {
        return starttime;
    }
    public String getStoptime() {
        return stoptime;
    }

    // 세터 메서드
    public void setName(String name) {
        this.name = name;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setUpid(String upid) {
        this.upid = upid;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setColorhex(String colorhex) {
        this.colorhex = colorhex;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public void setTotalSeconds(String totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }
    public void setStoptime(String stoptime) {
        this.stoptime = stoptime;
    }

}


