package com.example.news_aggregator_jgb;

import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NewsAPI {
    private MainActivity mainActivity;
    private static NewsAdapter articles_Adapter;
    private ArrayAdapter<newsSource> arrayAdapter;
    private static ViewPager2 viewpage;
    private DrawerLayout drawer_layout;
    private ListView drawer_list;
    private static final String TAG = "NewsAPI";
    //API Key
    private static final String APIKey = "ddc00b9b8e3a426985f87382ce0710fe";
    //API News source URL
    private static final String NEWS_SOURCE_URL = "https://newsapi.org/v2/sources";
    //Top News URL
    private static final String TOP_NEWS_API_URL = "https://newsapi.org/v2/top-headlines";
    private static List<newsArticles> articleDetails;
    private static List<newsSource> sources;
    private static NewsStats newsStats;
    //Volley RequestQueue
    private static RequestQueue main_queue;
    private static RequestQueue articles_Queue;
    private static HashSet<String> Menu_Items_Set = new HashSet<String>();
    public static NewsStats getNewsAPI(MainActivity mainActivity) {
        main_queue=Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(NEWS_SOURCE_URL).buildUpon(); //change buildURL
        buildURL.appendQueryParameter("apiKey", APIKey);
        String res_URL = buildURL.build().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, res_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        newsStats = parseJSON_News_Source(response);
                        mainActivity.updateSourceData(newsStats);

                        Menu_Items_Set.add("all");
                        sources = newsStats.getNews_sources();

                        for (newsSource s : sources) {
                            Menu_Items_Set.add(s.getNewsCategory());
                        }
                        mainActivity.updateNewsMenuItem(Menu_Items_Set);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "\nVolley Error\t"+error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "ua");
                return headers;
            }
        };
        main_queue.add(jsonObjectRequest);
        return newsStats;
    }

    public static List<newsArticles> getNewsArticles(String source, MainActivity mainActivity){

        Uri.Builder buildURL = Uri.parse(TOP_NEWS_API_URL).buildUpon();
        buildURL.appendQueryParameter("sources", source);
        buildURL.appendQueryParameter("apiKey", APIKey);
        articles_Queue = Volley.newRequestQueue(mainActivity);
        JsonObjectRequest JSONRequest = new JsonObjectRequest
                (Request.Method.GET, buildURL.build().toString(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        articleDetails = parseArticlesDetails(response);
                        mainActivity.updateViewPager(articleDetails);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "ua");
                return headers;
            }
        };
        articles_Queue.add(JSONRequest);
        return articleDetails;
    }

    private static NewsStats parseJSON_News_Source(JSONObject response) {
        String source_ID;
        String source_Name;
        String source_Description;
        try{
            List<newsSource> sources = new ArrayList<newsSource>();
            String status  = response.getString("status");

            if(response.getJSONArray("sources") != null){
                JSONArray jsonArray = response.getJSONArray("sources");
                int source_len = jsonArray.length();
                int ival;
                for (ival = 0; ival < source_len; ival++) {
                    JSONObject item = jsonArray.getJSONObject(ival);
                    source_ID = item.getString("id");
                    source_Name = item.getString("name");
                    source_Description = item.getString("category");
                    sources.add(new newsSource(source_ID, source_Name, source_Description));
                }
            }
            newsStats = new NewsStats(status, sources);
        }
        catch (Exception exception){
            Log.d(TAG, "parseJSON_News_Source: Error in parsing News Json Source API");
            exception.printStackTrace();
        }
        return newsStats;
    }

    private static List<newsArticles> parseArticlesDetails(JSONObject response) {
        List<newsArticles> parsedList = new ArrayList<newsArticles>();
        try{
            JSONArray jsonArray = response.getJSONArray("articles");
            int ival;
            for(ival = 0; ival < jsonArray.length(); ival++){
                newsArticles news_articles = new newsArticles();
                JSONObject jsonItem = jsonArray.getJSONObject(ival);

                String news_headline = jsonItem.getString("title");
                String news_date = jsonItem.getString("publishedAt");
                String news_author = jsonItem.getString("author");
                String news_description = jsonItem.getString("description");
                String news_URL = jsonItem.getString("url");
                String news_URLToImage = jsonItem.getString("urlToImage");


                news_articles.setNewsHeadline(news_headline);
                news_articles.setNewsDate(news_date);
                news_articles.setNewsAuthor(news_author);
                news_articles.setNewsDescription(news_description);
                news_articles.setNewsUrl(news_URL);
                news_articles.setNewsImageURL(news_URLToImage);

                parsedList.add(news_articles);
            }
        }
        catch (Exception exception){
            Log.d(TAG, "parseArticlesDetails: Error in PArsing APIarticles");
            exception.printStackTrace();
        }
        return parsedList;
    }
    public static List<newsSource> updateDrawerLayoutData(String title) {
        List<newsSource> updateSourceList = new ArrayList<newsSource>();
        int length = sources.size();
        int ival;
        for (ival = 0 ; ival < length; ival++){
            String matchCategory = sources.get(ival).getNewsCategory();
            if(matchCategory.equalsIgnoreCase(title)){
                updateSourceList.add(sources.get(ival));
            }
        }
        return updateSourceList;
    }

}
