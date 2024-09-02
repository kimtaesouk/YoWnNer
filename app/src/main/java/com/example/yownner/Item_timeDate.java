package com.example.yownner;

public class Item_timeDate {
        private String date;
        private int seconds;

        public Item_timeDate(String date, int seconds) {
            this.date = date;
            this.seconds = seconds;
        }

        public String getDate() {
            return date;
        }

        public int getSeconds() {
            return seconds;
        }

}
