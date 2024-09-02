package com.example.yownner;

public class Item_memo {
    private String memo;
    private String pid;
    private String month;
    private String day;
    private boolean isChecked;
    private int status; // status 필드 추가

    public Item_memo(String memo, String pid, int status, String month, String day) {
        this.memo = memo;
        this.pid = pid;
        this.status = status;
        this.month = month;
        this.day = day;
    }

    // Getters and setters for memo and pid
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}


