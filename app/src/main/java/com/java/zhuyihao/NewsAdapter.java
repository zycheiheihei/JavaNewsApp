package com.java.zhuyihao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    //TODO set news display way as requested
    private List<NewsListInfo.NewsInfo> newsList;
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            NewsListInfo.NewsInfo news = newsList.get(position);
            holder.newsTitle.setText(news.getTitle());
            holder.newsTime.setText(news.getDate());
            if(NewsDataBaseServer.hasNews(newsList.get(position).get_id())){
                holder.newsTitle.setTextColor(Color.rgb(128, 128, 128));
            }else{
                holder.newsTitle.setTextColor(Color.rgb(0, 0, 0));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,NewsDetailActivity.class);
                    intent.putExtra("news_id",newsList.get(position).get_id());
                    context.startActivity(intent);
                    holder.newsTitle.setTextColor(Color.rgb(128, 128, 128));
                }
            });
        }catch (IndexOutOfBoundsException e){
            Log.e("Error",e.getMessage());
        }
    }



    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView newsTitle;
        TextView newsTime;
        public ViewHolder(View view){
            super(view);
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsTime = (TextView) view.findViewById(R.id.news_time);
        }
    }

    public void addNews2Head(List<NewsListInfo.NewsInfo> newsInfoList){
        for(NewsListInfo.NewsInfo news:newsInfoList){
            newsList.add(0,news);
            this.notifyDataSetChanged();
        }
        //this.notifyDataSetChanged();
    }

    public void addNews2Tail(List<NewsListInfo.NewsInfo> newsInfoList){
        Collections.reverse(newsInfoList);
        newsList.addAll(newsInfoList);
        this.notifyDataSetChanged();
    }
    public NewsAdapter(Context context, List<NewsListInfo.NewsInfo> newsInfoList){
        this.context = context;
        newsList = newsInfoList;
    }
}
