package com.example.news_aggregator_jgb;

import java.util.List;
//This Class as it says NewsStats, will bring out various News sources and the its status
public class NewsStats {
    private List<newsSource> news_sources;
    private String news_status;

    public NewsStats(String news_status, List<newsSource> news_sources){
        this.news_sources=news_sources;
        this.news_status=news_status;

    }

    public List<newsSource> getNews_sources(){
        return news_sources;
    }
}
