package com.example.news_aggregator_jgb;
//This Class is for News Sources, Only consists of Id, name and category
public class newsSource {
    private String source_id, source_name, source_category;

    public newsSource(String source_id, String source_name, String source_category) {
        this.source_id = source_id;
        this.source_name = source_name;
        this.source_category = source_category;
    }

    public String getNewsId() {
        return source_id;
    }

    public String getNewsName() {
        return source_name;
    }

    public String getNewsCategory() {
        return source_category;
    }

}
