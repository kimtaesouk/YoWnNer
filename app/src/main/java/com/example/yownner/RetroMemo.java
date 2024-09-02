package com.example.yownner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetroMemo {
    @Expose
    @SerializedName("memo") private String memo;
    public RetroMemo(String memo) {
        this.memo = memo;
    }
    public String getMemo() {
        return memo;
    }
    public void setMemo(String title) {
        this.memo = title;
    }
}
