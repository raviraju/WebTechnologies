package com.elasticbeanstalk.us_west_2.ravirajustocksearch.stockmarketapplication;

public class News {
    private String url;
    private String title;
    private String description;
    private String publisher;
    private String date;

    public News(String url, String title, String description, String publisher, String date) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDate() {
        return date;
    }
}
