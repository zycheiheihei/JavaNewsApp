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

import java.util.AbstractMap;
import java.util.List;

public class ExpertAdapter extends RecyclerView.Adapter<ExpertAdapter.ViewHolder> {
    List<AbstractMap.SimpleEntry<String, String>> expertsList;
    Context context;

    @NonNull
    @Override
    public ExpertAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expert_layout,parent,false);
        ExpertAdapter.ViewHolder holder = new ExpertAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            AbstractMap.SimpleEntry<String,String> expert = expertsList.get(position);
            holder.nameString.setText(expert.getValue());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,ExpertProfileActivity.class);
                    intent.putExtra("id",expertsList.get(position).getKey());
                    context.startActivity(intent);
                }
            });
        }catch (IndexOutOfBoundsException e){
            Log.e("Error",e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return expertsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameString;

        public ViewHolder(View view) {
            super(view);
            nameString = (TextView) view.findViewById(R.id.name_string);
        }
    }

    public ExpertAdapter(Context context,List<AbstractMap.SimpleEntry<String, String>> expertsList) {
        this.context = context;
        this.expertsList = expertsList;
    }
}
