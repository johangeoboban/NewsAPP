package com.example.news_aggregator_jgb;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAggrHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "NewsAggrHolderCheck";
    ImageView image;
    TextView headlines;
    TextView date_time;
    TextView authors;
    TextView description;
    TextView page_numbers;

    public NewsAggrHolder(@NonNull View view) {
        super(view);
        headlines = view.findViewById(R.id.news_headlines);
        date_time = view.findViewById(R.id.news_date_time);
        authors = view.findViewById(R.id.news_author);
        image = view.findViewById(R.id.news_image);
        description = view.findViewById(R.id.news_description);
        page_numbers = view.findViewById(R.id.news_page_number);



        this.description.setMovementMethod(new ScrollingMovementMethod());
    }

}
