package com.example.news_aggregator_jgb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    //For debugging purposes
    private static final String TAG = "CheckPointMainActivity";

    private static ViewPager2 viewpage;
    private DrawerLayout drawer_layout;
    private ListView drawer_list;
    //Array for news article
    private static List<newsArticles> articlesList = new ArrayList<>();
    HashMap<String, String> color;
    private static HashMap<String, String> hashMap = new HashMap<String, String>();
    List<newsSource> list;
    //News Adapter
    private static NewsAdapter articles_Adapter;
    private ArrayAdapter<newsSource> arrayAdapter;
    // News stats and Sources
    NewsStats news;
    List<newsSource> sourceList;
    private ActionBarDrawerToggle drawerToggle;

    TextView menuItem;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewpage = findViewById(R.id.view_page);
        drawer_layout = findViewById(R.id.drawer_main);
        drawer_list = findViewById(R.id.drawer_layout);

        boolean hasNetwork = hasNetworkConnection();
        if (hasNetwork) {
            Log.d(TAG, "hasNetworkConnected: Connection is Live!");
            //System.out.println(TAG + "LIVE");
            NewsAPI.getNewsAPI(this); //Calling the News API
            Log.d(TAG, "Fetched News Data!");

        } else {
            Log.d(TAG, "hasNetworkConnected: Failed To Connect!");
//            //System.out.println(TAG + "OFFLINE");
            Log.d(TAG, "Unsuccessful in fetching the API!");
            errorHandler();
        }
        drawer_list.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String setSourceTitle = list.get(position).getNewsName();
                    setTitle(setSourceTitle);
                    articlesList = NewsAPI.getNewsArticles(list.get(position).getNewsId(), this);
                    viewpage.setCurrentItem(position);
                    drawer_layout.setBackgroundResource(0);
                    viewpage.setBackground(null);
                    drawer_layout.closeDrawer(drawer_list);
                });
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open, R.string.drawer_close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        Log.d(TAG, "\nonCreate successful! ");
        //System.out.println(TAG + "logging!");
    }

    public Boolean hasNetworkConnection() {
        ConnectivityManager cManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    void errorHandler() {
        Toast.makeText(this, "Connection Failed! Try Again!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "errorHandler: Error handled Successfully! ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Creating menu
        this.menu = menu;
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration config) {
        super.onConfigurationChanged(config);
        drawerToggle.onConfigurationChanged(config);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem menu_item) {

        if (drawerToggle.onOptionsItemSelected(menu_item)) {
            Log.d(TAG, "onOptionsItemSelected: Item Selected!");
            return true;
        }
        if(menu_item.getTitle().equals("all")){
            updateDrawerNewsItems(sourceList, menu_item.getTitle().toString());
            Log.d(TAG, "onOptionsItemSelected: All by default, Success!");
        }
        else if(menu_item.getTitle() != null){
            List<newsSource> updatedList =  NewsAPI.updateDrawerLayoutData(menu_item.getTitle().toString());
            Log.d(TAG, "onOptionsItemSelected: Selected updated option");
            String str = menu_item.getTitle().toString();
            updateDrawerNewsItems(updatedList, str);
        }
        else {
            Log.d(TAG, "onOptionsItemSelected: NothingSelected!");
        }
        boolean prev_state=super.onOptionsItemSelected(menu_item);
        return prev_state;
    }

    public void updateSourceData(NewsStats news) {
        this.news = news;
        sourceList = news.getNews_sources();
        list = sourceList;

        arrayAdapter = new ArrayAdapter<newsSource>(this, android.R.layout.simple_list_item_1, this.sourceList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup viewGroup) {
                newsSource source = sourceList.get(position);

                View view = super.getView(position, convertView, viewGroup);
                menuItem = (TextView) view.findViewById(android.R.id.text1);
                //Setting up of News source name
                menuItem.setText(source.getNewsName());

                int source_len = sourceList.size();
                String news_title = "News Gateway ";
                setTitle(news_title + "(" + source_len + ")"); //number of sources

                menuItem.setTextColor(Color.parseColor(color.get(source.getNewsCategory())));
                return view;
            }
        };
        drawer_list.setAdapter(arrayAdapter);
    }

    public void updateNewsMenuItem(HashSet<String> dynamic_menu) {
        //We update the menu items with different colors according to the category
        color = sourceNewsColors(dynamic_menu);
        for (String menu: dynamic_menu) {
            if(!menu.equals("all")){
                SpannableString s_str = new SpannableString(menu);
                s_str.setSpan(new ForegroundColorSpan(Color.parseColor(color.get(menu))), 0, s_str.length(), 0);
                this.menu.add(s_str);
                Log.d(TAG, "updateNewsMenuItem: Selected Menu Updated!");
            }
            else {
                this.menu.add(menu);
                Log.d(TAG, "updateNewsMenuItem: Successfully added context to the menu!");
            }
        }
    }


    private void updateDrawerNewsItems(List<newsSource> updateDrawerNewsItems, String title) {
        //Updating drawer sources based on selection
        list = updateDrawerNewsItems;
        arrayAdapter = new ArrayAdapter<newsSource>(this, android.R.layout.simple_list_item_1, updateDrawerNewsItems){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup viewGroup) {
                newsSource newsSource = updateDrawerNewsItems.get(position);
                View view = super.getView(position, convertView, viewGroup);
                menuItem = (TextView) view.findViewById(android.R.id.text1);
                menuItem.setText(newsSource.getNewsName());
                if(title.equals("all")){
                    int allListLength = updateDrawerNewsItems.size();
                    String title = "News Gateway";
                    setTitle(title + " " + "(" + allListLength + ")" ); //length of sources
                    menuItem.setTextColor(Color.parseColor(color.get(newsSource.getNewsCategory())));
                    Log.d(TAG, "getView: Showing all news sources");
                }
                else {
                    int len = updateDrawerNewsItems.size();
                    setTitle(updateDrawerNewsItems.get(0).getNewsCategory() + " (" + len + ")");
                    Log.d(TAG, "getView: Showing selected options size");
                }
                menuItem.setTextColor(Color.parseColor(color.get(newsSource.getNewsCategory()))); //Coloring the sources
                return view;
            }
        };
        drawer_list.setAdapter(arrayAdapter);
    }

    //Using hashMap to store the colors and use it according to the sources and categories
    public static HashMap<String, String> sourceNewsColors(HashSet<String> dynamic_menu){

        List<String> list = new ArrayList<String>();
        list.add("#B50000FF");
        list.add("#DDA52A2B");
        list.add("#DC8A2BE3");
        list.add("#F2008B8C");
        list.add("#DC6495EE");
        list.add("#EBDC143D");
        list.add("#D8FF7F51");
        list.add("#E97FFF00");
        list.add("#EBDEB888");
        list.add("#F5D2691D");
        list.add("#EB5F9EA1");

        int index_val = 0;
        for(String str : dynamic_menu){
            hashMap.put(str, list.get(index_val));
            index_val++;
        }
        Log.d(TAG, "sourceNewsColors: Added colors to the Sources with respect to Categories");
        return hashMap;
    }
    public void updateViewPager(List<newsArticles> articleDetails) {
        //Updating the View Pager Articles
        articles_Adapter = new NewsAdapter(this, articleDetails);
        viewpage.setAdapter(articles_Adapter);
    }
}