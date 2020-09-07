package com.java.zhuyihao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.library.AutoFlowLayout;
import com.example.library.FlowAdapter;
import com.google.android.material.internal.FlowLayout;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements Config{
    List<String> startUpTags;
    List<String> backUpTags;
    AutoFlowLayout flowLayoutStartUp;
    AutoFlowLayout flowLayoutBackUp;
    Context context;
    boolean onEditMode = false;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("编辑分类");
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        final Button editButton = (Button)findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editButton.getText()=="编辑"){
                    editButton.setText("完成");
                    startEditMode();
                }else{
                    editButton.setText("编辑");
                    endEditMode();
                }
            }
        });
        Intent mainIntent = getIntent();
        parseTagsCondition(mainIntent.getStringExtra("currentTagCondition"));
        flowLayoutStartUp = (AutoFlowLayout)findViewById(R.id.flow_layout_startup);
        flowLayoutBackUp = (AutoFlowLayout)findViewById(R.id.flow_layout_backup);
        flowLayoutStartUp.setAdapter(new FlowAdapter(startUpTags) {
            @Override
            public View getView(int i) {
                View item = LayoutInflater.from(context).inflate(R.layout.tag_layout,null);
                TextView tag = (TextView)item.findViewById(R.id.attr_tag);
                tag.setClickable(false);
                tag.setText(startUpTags.get(i));
                return item;
            }
        });
        flowLayoutBackUp.setAdapter(new FlowAdapter(backUpTags){
            @Override
            public View getView(int i) {
                View item = LayoutInflater.from(context).inflate(R.layout.tag_layout,null);
                TextView tag = (TextView)item.findViewById(R.id.attr_tag);
                tag.setText(backUpTags.get(i));
                return item;
            }
        });
        flowLayoutStartUp.setOnItemClickListener(new AutoFlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int i, View view) {
                //Log.e("CLICKED REMOVE","to backup "+startUpTags.get(i)+" ok "+onEditMode);
                if(onEditMode){
                    //flowLayoutBackUp.addView(view);

                    TextView clickedText=(TextView) view.findViewById(R.id.attr_tag);
                    String text = clickedText.getText().toString();
                    View item = LayoutInflater.from(context).inflate(R.layout.tag_layout,null);
                    TextView tag = (TextView)item.findViewById(R.id.attr_tag);
                    tag.setText(text);
                    flowLayoutBackUp.addView(item);
                    backUpTags.add(text);
                    flowLayoutStartUp.removeView(view);
                    startUpTags.remove(text);
                }
            }
        });
        flowLayoutBackUp.setOnItemClickListener(new AutoFlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int i, View view) {
                Log.e("CLICKED ADD","to startup "+i+" ok ");
                TextView clickedText= (TextView) view.findViewById(R.id.attr_tag);
                String text =clickedText.getText().toString();
                View item = LayoutInflater.from(context).inflate(R.layout.tag_layout,null);
                TextView tag = (TextView)item.findViewById(R.id.attr_tag);
                tag.setText(text);
                flowLayoutStartUp.addView(item);
                startUpTags.add(text);
                flowLayoutBackUp.removeView(view);
                backUpTags.remove(text);
            }
        });
    }

    private void startEditMode(){
        onEditMode = true;
    }

    private void endEditMode(){
        onEditMode = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar3,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Log.e("Stop",formEditString());
            Intent intent = new Intent();
            intent.putExtra("changedTag",formEditString());
            setResult(2,intent);
            finish();
        }
        return true;
    }

    private void parseTagsCondition(String condition){
        Log.e("get message:",condition);
        startUpTags = new ArrayList<>();
        backUpTags = new ArrayList<>();
        for(int i=0;i<condition.length();i++){
            startUpTags.add(NEWS_TAGS[condition.charAt(i)-'0']);
        }
        for(int value:TAGS2NUM.values()){
            if(!condition.contains(String.valueOf(value))){
                backUpTags.add(NEWS_TAGS[condition.charAt(value)-'0']);
            }
        }
    }

    private String formEditString(){
        StringBuilder sb =  new StringBuilder();
        for(String tag:startUpTags){
            sb.append(TAGS2NUM.get(tag));
        }
        return sb.toString();
    }
}