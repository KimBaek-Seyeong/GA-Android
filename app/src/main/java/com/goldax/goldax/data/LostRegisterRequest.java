package com.goldax.goldax.data;

public class LostRegisterRequest {
    public String title;
    public String desc;
    public String category;
    public String location1;
    public String location2;
    public String date;
    public String money;

    public LostRegisterRequest(String title, String desc, String category, String location1, String location2, String date, String money) {
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.location1 = location1;
        this.location2 = location2;
        this.date = date;
        this.money = money;
    }

    @Override
    public String toString() {
        return "LostRegisterRequest{" +
                "title=" + title +
                ", desc=" + desc +
                ", category=" + category +
                ", location1=" + location1 +
                ", location2=" + location2 +
                ", date=" + date +
                ", money=" + money +
                '}';
    }
}
