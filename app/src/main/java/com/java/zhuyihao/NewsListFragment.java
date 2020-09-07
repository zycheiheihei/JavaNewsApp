package com.java.zhuyihao;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.Collections;
import java.util.List;

import rx.Subscriber;

public class NewsListFragment extends Fragment implements Config{
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private NewsAdapter newsAdapter;
    private RefreshLayout refreshLayout;
    private int TOTAL_NEWS;
    private int LAST_NEWS_RANK;
    private int newsClass;

    private void trialStep(){
        getNews(TYPE_LIST[newsClass],1,1,4);
    }

    private void getNews(String type, final int page, final int size, final int status){
        //0 for test,1 for init,2 for get Newest,3 for get More,4 for refresh test
        Log.d("GET",type+' '+page+' '+size);
        if(page>0) {
            Subscriber<NewsListInfo> subscriber = new Subscriber<NewsListInfo>() {
                @Override
                public void onCompleted() {

                }
                //出现异常回调
                @Override
                public void onError(Throwable e) {
                    Log.e("fuck_off", e.getMessage());
                }

                //获取数据成功后回调
                @Override
                public void onNext(NewsListInfo news) {
                    Log.d("GET", STATUS[status]);
                    switch(status){
                        case 1:
                            LAST_NEWS_RANK = PAGE_SIZE;
                            TOTAL_NEWS = news.getPagination().getTotal();
                            List<NewsListInfo.NewsInfo> newsInfoList = news.getData();
                            Collections.reverse(newsInfoList);
                            newsAdapter = new NewsAdapter(getActivity(),newsInfoList);
                            recyclerView.setAdapter(newsAdapter);
                            refreshLayout.finishRefresh();
                            break;
                        case 2:
                            newsAdapter.addNews2Head(news.getData().subList(0,news.getPagination().getTotal()-TOTAL_NEWS));
                            refreshLayout.finishRefresh();
                            LAST_NEWS_RANK+=(news.getPagination().getTotal()-TOTAL_NEWS);
                            TOTAL_NEWS = news.getPagination().getTotal();
                            break;
                        case 3:
                            newsAdapter.addNews2Tail(news.getData().subList(LAST_NEWS_RANK%PAGE_SIZE,PAGE_SIZE));
                            refreshLayout.finishLoadMore();
                            LAST_NEWS_RANK = ((LAST_NEWS_RANK/PAGE_SIZE)+1)*PAGE_SIZE;
                            break;
                        case 4:
                            if(news.getPagination().getTotal()>TOTAL_NEWS){
                                Log.d("Refreshing","getting new data " + (news.getPagination().getTotal()-TOTAL_NEWS));
                                getNews(TYPE_LIST[newsClass],1,news.getPagination().getTotal()-TOTAL_NEWS,2);
                            }else{
                                Log.d("Refreshing","No new news");
                                refreshLayout.finishRefresh();
                            }
                            break;
                        default:
                            Log.d("Current Condition","LAST_NEWS "+LAST_NEWS_RANK+" /TOTAL NEWS "+TOTAL_NEWS);
                    }

                }
            };
            RetrofitUtil.getInstance().getNews(subscriber, type, page, size);
        }
    }


    public NewsListFragment(int newsClass) {
        this.newsClass = newsClass;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.news_list_layout,container,false);
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_news);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        getNews(TYPE_LIST[newsClass],1,PAGE_SIZE,1);
        refreshLayout = (RefreshLayout)view.findViewById(R.id.refreshLayout);
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                trialStep();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                Log.e("MORE","lnr: "+LAST_NEWS_RANK+" PAGE SIZE: "+PAGE_SIZE);
               getNews(TYPE_LIST[newsClass],(LAST_NEWS_RANK/PAGE_SIZE)+1,PAGE_SIZE,3);
            }
        });

    }
}
