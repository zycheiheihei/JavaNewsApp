package com.java.zhuyihao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.EntityIterator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.AbstractMap;
import java.util.Date;
import java.util.List;

public class KnowledgeGraphActivity extends AppCompatActivity {

    private Button searchButton;
    private String currentItem;
    private EditText editText;
    private JSONArray Entities;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private KnowledgeGraphAdapter knowledgeGraphAdapter;
    public static final int GET_DATA_SUCCESS = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    knowledgeGraphAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    searchButton.setClickable(true);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("疫情知识图谱");
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        Entities = new JSONArray();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_items);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        editText = (EditText) findViewById(R.id.search_et_input) ;
        knowledgeGraphAdapter = new KnowledgeGraphAdapter(this,Entities);
        recyclerView.setAdapter(knowledgeGraphAdapter);
        searchButton = (Button)findViewById(R.id.search_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                currentItem = editText.getText().toString();
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                searchButton.setClickable(false);
                Search(currentItem);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    private void resetUI(){
        knowledgeGraphAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
        searchButton.setClickable(true);
    }

    private void Search(final String currentItem){
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Loading data from server...");
                JSONArray tempJSON = KnowledgeGraph.getInstance().getInfo(currentItem);
                Log.e("load",tempJSON.toString());
                Entities.clear();
                for(int i=0;i<tempJSON.size();i++){
                    Entities.add(tempJSON.getJSONObject(i));
                }
                Message msg = Message.obtain();
                msg.what = GET_DATA_SUCCESS;
                handler.sendMessage(msg);
                Log.e("get","success");
            }
        }).start();

    }
}