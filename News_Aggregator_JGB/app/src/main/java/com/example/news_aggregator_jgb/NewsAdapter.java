package com.example.news_aggregator_jgb;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAggrHolder> {

    private static final String TAG = "NewsAdapter";
    private final MainActivity mainActivity;
    private final List<newsArticles> list;

    public NewsAdapter(MainActivity mainActivity, List<newsArticles> articleDetailsList) {
        this.mainActivity = mainActivity;
        this.list = articleDetailsList;
    }

    @NonNull
    @Override
    public NewsAggrHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new NewsAggrHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.articles_viewer, viewGroup, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NewsAggrHolder holder, int position) {
        newsArticles article = list.get(position);

        String checkHeadline = article.getNewsHeadline();
        if (checkHeadline != null){
            String headlineText = article.getNewsHeadline();
            holder.headlines.setText(headlineText);

            holder.headlines.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String thisURL = article.getNewsUrl();
                    intent.setData(Uri.parse(thisURL));
                    mainActivity.startActivity(intent);
                }
            });
        }

        boolean flag = article.getNewsAuthor().isEmpty();
        String nullString = "null";
        if(flag || article.getNewsAuthor().equals(nullString)) {
            holder.authors.setVisibility(View.GONE);
        }
        else {
            String authorNames = article.getNewsAuthor();
            holder.authors.setText(authorNames);
        }

        String checkArticle = article.getNewsDate();
        if(checkArticle != null) {
            String date = getArticleDate(article.getNewsDate());
            holder.date_time.setText(date);
        }

        String checkURL = article.getNewsUrl();
        if (checkURL == null) {
            holder.image.setImageResource(R.drawable.noimage);
        }
        else {
            Picasso picasso = Picasso.with(mainActivity);
            picasso.setLoggingEnabled(true);
            picasso.load(article.getNewsImageURL())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.brokenimage)
                    .into(holder.image);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(article.getNewsUrl()));
                    mainActivity.startActivity(intent);
                }
            });
        }

        boolean descriptionFlag = article.getNewsAuthor().isEmpty();
        String articleDescription = article.getNewsDescription();
        if(descriptionFlag || articleDescription.equals("null")){
            holder.description.setVisibility(View.GONE);
        }
        else {
            holder.description.setText(articleDescription);
            holder.description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(article.getNewsUrl()));
                    mainActivity.startActivity(intent);
                }
            });
        }
        int  startPage = position + 1;
        int lastPage = list.size();
        String pageNumbers = String.format("%d of %d", startPage, lastPage);
        holder.page_numbers.setText(pageNumbers);
    }

    @Override
    public int getItemCount() {
        int len = list.size();
        return len;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getArticleDate(String date) {
        String API_DATE = "";
        try{
            DateTimeFormatter tf = DateTimeFormatter.ISO_INSTANT;
            TemporalAccessor accessor = tf.parse(date);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
            API_DATE = ldt.format(dateTimeFormatter);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        boolean check = API_DATE.isEmpty();
        if(check){
            try{
                DateTimeFormatter tf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                TemporalAccessor accessor = tf.parse(date);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("LLL dd, yyyy kk:mm");
                LocalDateTime ldt = LocalDateTime.ofInstant(Instant.from(accessor), ZoneId.systemDefault());
                API_DATE = ldt.format(dtf);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        String formattedDate = API_DATE;
        return formattedDate;
    }

}
