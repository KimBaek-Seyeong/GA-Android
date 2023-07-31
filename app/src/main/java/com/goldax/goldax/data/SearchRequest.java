package com.goldax.goldax.data;

public class SearchRequest {
    public String title;
    public String category;
    public String date;
    public String location;
    public String type;

    public SearchRequest(String title, String category, String date, String location, String type) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.location = location;
        this.type = type;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
