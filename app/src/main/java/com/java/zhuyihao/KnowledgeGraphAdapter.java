package com.java.zhuyihao;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.AbstractMap;
import java.util.List;

public class KnowledgeGraphAdapter extends RecyclerView.Adapter<KnowledgeGraphAdapter.ViewHolder> {

    JSONArray entityInfo;
    Context context;

    @NonNull
    @Override
    public KnowledgeGraphAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledgeitem_layout,parent,false);
        KnowledgeGraphAdapter.ViewHolder holder = new KnowledgeGraphAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        try {
            JSONObject item = entityInfo.getJSONObject(position);
            holder.nameString.setText(item.getString("label"));
            holder.initExpandView();
        }catch (IndexOutOfBoundsException e){
            Log.e("Error",e.getMessage());
        }
    }




    @Override
    public int getItemCount() {
        return entityInfo.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Button nameString;
        MyImageView imageView;
        ExpandLayout mExpandLayout;

        public void initExpandView() {
            mExpandLayout.initExpand(false);
            nameString.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    JSONObject item = entityInfo.getJSONObject(position);
                    mExpandLayout.toggleExpand();
                    String url = item.getString("img");
                    if(url!=null && mExpandLayout.isExpand()) {
                        Log.e("url", url);
                        imageView.setImageURL(item.getString("img"));
                    }
                }
            });
        }

        public ViewHolder(View view) {
            super(view);
            nameString = (Button) view.findViewById(R.id.header_title);
            mExpandLayout = (ExpandLayout) view.findViewById(R.id.expandLayout);
            imageView = (MyImageView) view.findViewById(R.id.image);
        }
    }

    public KnowledgeGraphAdapter(Context context,JSONArray entityInfo) {
        this.context = context;
        this.entityInfo = entityInfo;
    }
}
