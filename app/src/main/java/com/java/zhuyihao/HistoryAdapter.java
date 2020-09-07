package com.java.zhuyihao;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    List<News> newsList;
    Context context;

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout,parent,false);
        HistoryAdapter.ViewHolder holder = new HistoryAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            News news = newsList.get(position);
            holder.historyTitle.setText(news.getTitle());
            holder.historyTime.setText(news.getDate());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,NewsDetailActivity.class);
                    intent.putExtra("news_id",newsList.get(position).getNews_id());
                    context.startActivity(intent);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyTitle;
        TextView historyTime;

        public ViewHolder(View view) {
            super(view);
            historyTitle = (TextView) view.findViewById(R.id.history_title);
            historyTime = (TextView) view.findViewById(R.id.history_time);
        }
    }

    public HistoryAdapter(Context context,List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }
}
