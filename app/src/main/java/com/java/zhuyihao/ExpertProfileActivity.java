package com.java.zhuyihao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.java.zhuyihao.Config.MyCacheSta;

public class ExpertProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        JSONObject thisExpert = ExpertsProfile.getInstance().getSpecificExpert(id);
        String avatarURL = thisExpert.getString("avatar");
        System.out.println(thisExpert);
        MyImageView myImageView = (MyImageView)findViewById(R.id.avatar);
        myImageView.setImageURL(avatarURL);
        actionBar.setTitle(thisExpert.getString("name")+" "+thisExpert.getString("name_zh"));
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        JSONObject indices = thisExpert.getJSONObject("indices");
        TextView activity = (TextView)findViewById(R.id.activity);
        activity.setText("学术活跃度："+indices.getString("activity"));
        TextView citations = (TextView)findViewById(R.id.citations);
        citations.setText("论文引用数："+indices.getString("citations"));
        TextView diversity = (TextView)findViewById(R.id.diversity);
        diversity.setText("研究多样性："+indices.getString("diversity"));
        TextView gindex = (TextView)findViewById(R.id.gindex);
        gindex.setText("g指数："+indices.getString("gindex"));
        TextView hindex = (TextView)findViewById(R.id.hindex);
        hindex.setText("h指数："+indices.getString("hindex"));
        TextView sociability = (TextView)findViewById(R.id.sociability);
        sociability.setText("社会影响力："+indices.getString("sociability"));
        TextView stars = (TextView)findViewById(R.id.star);
        stars.setText("学术合作：risingStar: "+indices.getString("risingStar")+" newStar: "+indices.getString("newStar"));
        TextView dead = (TextView)findViewById(R.id.passedaway);
        dead.setText("已经去世："+thisExpert.getBoolean("is_passedaway"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    private void loadImg(final String id,final String avatarURL){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (avatarURL!=null) {
                    try {
                        URL url1 = new URL(avatarURL);
                        URLConnection uc = url1.openConnection();
                        InputStream inputStream = uc.getInputStream();

                        FileOutputStream out = new FileOutputStream(MyCacheSta+"/"+id+".jpg");
                        int j = 0;
                        while ((j = inputStream.read()) != -1) {
                            out.write(j);
                        }
                        inputStream.close();
                        Log.e("expert","img_download success");
                        ImageView imageView = (ImageView) findViewById(R.id.avatar);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}