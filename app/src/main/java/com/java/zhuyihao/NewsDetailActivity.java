package com.java.zhuyihao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import rx.Subscriber;

public class NewsDetailActivity extends AppCompatActivity {
    TextView newsTitle;
    TextView newsInfo;
    TextView newsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        newsTitle = (TextView)findViewById(R.id.news_detail_title);
        newsInfo = (TextView)findViewById(R.id.news_detail_info);
        newsContent = (TextView)findViewById(R.id.news_detail_content);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        BottomNavigationViewHelper.removeNavigationShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.news_button:
                        Log.d("Bottom Clicked","news Clicked");
                        break;
                    case R.id.data_button:
                        Log.d("Bottom Clicked","data Clicked");
                        break;
                    case R.id.graph_button:
                        Log.d("Bottom Clicked","graph Clicked");
                        break;
                    case R.id.class_button:
                        Log.d("Bottom Clicked","class Clicked");
                        break;
                    case R.id.scholar_button:
                        Log.d("Bottom Clicked","scholar Clicked");
                        break;
                    default:
                }
                return true;
            }
        });
        Intent intent = getIntent();
        String news_id = intent.getStringExtra("news_id");
        if(NewsDataBaseServer.hasNews(news_id)){
            News news = NewsDataBaseServer.getNews(news_id);
            newsTitle.setText(news.getTitle());
            newsContent.setText(news.getContent());
            String info = news.getSource()+"  "+news.getDate();
            newsInfo.setText(info);
        }else{
            getNewsDetail(news_id);
        }
        Log.d("NEWS Detail",news_id);
    }

    private void getNewsDetail(String newsId){
        Subscriber<NewsDetail> subscriber = new Subscriber<NewsDetail>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("detail_shit", e.getMessage());
            }

            @Override
            public void onNext(NewsDetail newsDetail) {
                Log.d("GET NEWS DETAIL",newsDetail.getData().get_id());
                NewsDataBaseServer.insertNews(newsDetail.getData());
                newsTitle.setText(newsDetail.getData().getTitle());
                newsContent.setText(newsDetail.getData().getContent());
                String info = newsDetail.getData().getSource()+"  "+newsDetail.getData().getDate();
                newsInfo.setText(info);
            }
        };
        RetrofitUtil4Detail.getInstance().getNewsDetail(subscriber,newsId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.share){
            Toast.makeText(this,"You clicked Share",Toast.LENGTH_SHORT).show();
        }else if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }
}