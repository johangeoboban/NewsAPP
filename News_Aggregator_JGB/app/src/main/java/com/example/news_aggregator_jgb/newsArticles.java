package com.example.news_aggregator_jgb;

public class newsArticles {
    private String news_headline, news_date, news_author, news_description, news_articleURL, news_imageURL;


    public String getNewsHeadline() {
        return news_headline;
    }

    public void setNewsHeadline(String headline) {
        this.news_headline = headline;
    }

    public String getNewsDate() {
        return news_date;
    }

    public void setNewsDate(String date) {
        this.news_date = date;
    }

    public String getNewsAuthor() {
        return news_author;
    }

    public void setNewsAuthor(String authorNames) {
        this.news_author = authorNames;
    }

    public String getNewsDescription() {
        return news_description;
    }

    public void setNewsDescription(String description) {
        this.news_description = description;
    }

    public String getNewsUrl() {
        return news_articleURL;
    }

    public void setNewsUrl(String URL) {
        this.news_articleURL = URL;
    }

    public String getNewsImageURL() {
        return news_imageURL;
    }

    public void setNewsImageURL(String ImageURL) {
        this.news_imageURL = ImageURL;
    }

}
