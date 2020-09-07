package com.java.zhuyihao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<NewsListInfo.NewsInfo> newsInfoList;
    private Context context;

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout,parent,false);
        SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.ViewHolder holder, final int position) {
        try {
            NewsListInfo.NewsInfo news = newsInfoList.get(position);
            holder.searchTitle.setText(news.getTitle());
            holder.searchTime.setText(news.getDate());
            if(NewsDataBaseServer.hasNews(newsInfoList.get(position).get_id())){
                holder.searchTitle.setTextColor(Color.rgb(128, 128, 128));
            }else{
                holder.searchTitle.setTextColor(Color.rgb(0, 0, 0));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,NewsDetailActivity.class);
                    intent.putExtra("news_id",newsInfoList.get(position).get_id());
                    context.startActivity(intent);
                    holder.searchTitle.setTextColor(Color.rgb(128, 128, 128));
                }
            });
        }catch (IndexOutOfBoundsException e){
            Log.e("Error",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return newsInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView searchTitle;
        TextView searchTime;

        public ViewHolder(View view) {
            super(view);
            searchTitle = (TextView) view.findViewById(R.id.search_title);
            searchTime = (TextView) view.findViewById(R.id.search_time);
        }
    }

    public SearchAdapter(Context context,List<NewsListInfo.NewsInfo> newsList){
        this.context = context;
        this.newsInfoList = newsList;
    }

}
