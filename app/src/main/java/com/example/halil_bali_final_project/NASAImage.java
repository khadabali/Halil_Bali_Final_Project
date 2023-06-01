package com.example.halil_bali_final_project;

public class NASAImage  {

//    attributes of a NASAImage
    private String date;
    private String url;
    private String title;
    private long id;

//    constructor
    public NASAImage(String date, String url, String title, long id) {
        this.date = date;
        this.url = url;
        this.title = title;
        this.id = id;
    }

//    getter methods
    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }
}