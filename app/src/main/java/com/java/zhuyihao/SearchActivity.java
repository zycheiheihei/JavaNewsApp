package com.java.zhuyihao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import rx.Subscriber;

public class SearchActivity extends AppCompatActivity implements Config {

    private EditText editText;
    private ImageButton backButton;
    private Button searchButton;
    private String currentKey;
    private List<NewsListInfo.NewsInfo> newsList;
    private List<NewsListInfo.NewsInfo> resultList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SearchAdapter searchAdapter;
    private RefreshLayout refreshLayout;
    private int getTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        resultList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_search);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchAdapter(this, resultList);
        recyclerView.setAdapter(searchAdapter);
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getMoreNews();
            }
        });

        editText = (EditText)findViewById(R.id.search_et_input);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i==EditorInfo.IME_ACTION_SEARCH){
                    String searchKey = editText.getText().toString();
                    Search(searchKey);
                }
                return true;
            }
        });
        backButton = (ImageButton)findViewById(R.id.search_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchButton = (Button)findViewById(R.id.search_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                currentKey = editText.getText().toString();
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                Search(currentKey);
            }
        });
        searchButton.setClickable(false);
        init();
    }

    private void Search(final String key){
        resultList.clear();
        searchButton.setClickable(false);
        for(NewsListInfo.NewsInfo news:newsList){
            if(news.getTitle().contains(key)){
                resultList.add(news);
            }
        }
        searchAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
        searchButton.setClickable(true);
        Log.d("Search",key+"getting news firstly: "+resultList.size());
    }

    private void SearchMore(final String key){
        searchButton.setClickable(false);
        for(int i=getTimes*SEARCH_SIZE;i<(getTimes+1)*SEARCH_SIZE;i++){
            if(newsList.get(i).getTitle().contains(key)){
                resultList.add(newsList.get(i));
            }
        }
        searchAdapter.notifyDataSetChanged();
        searchButton.setClickable(true);
        Log.d("Search",key+"getting news totally: "+resultList.size());
        refreshLayout.finishLoadMore();
    }

    private void init(){
        Log.d("Search","init Started");
        getInitNews();
    }

    private void getInitNews(){
        Subscriber<NewsListInfo> subscriber = new Subscriber<NewsListInfo>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {
                Log.e("fuck_off", e.getMessage());
            }
            @Override
            public void onNext(NewsListInfo newsListInfo) {
                newsList = newsListInfo.getData();
                Log.d("Search","init Finished");
                searchButton.setClickable(true);
                getTimes++;
            }
        };
        RetrofitUtil.getInstance().getNews(subscriber, "all", 1, SEARCH_SIZE);
    }

    private void getMoreNews() {
        Subscriber<NewsListInfo> subscriber = new Subscriber<NewsListInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("fuck_off", e.getMessage());
            }

            @Override
            public void onNext(NewsListInfo newsListInfo) {
                newsList.addAll(newsListInfo.getData());
                SearchMore(currentKey);
                getTimes++;
            }
        };
        RetrofitUtil.getInstance().getNews(subscriber, "all", getTimes+1, SEARCH_SIZE);
    }
}