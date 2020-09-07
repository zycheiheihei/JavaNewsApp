package com.java.zhuyihao;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitUtil {
    Retrofit retrofit;
    RetrofitService retrofitService;

    private RetrofitUtil() {
        retrofit = new Retrofit.Builder()
                .baseUrl(retrofitService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        retrofitService = retrofit.create(RetrofitService.class);
    }

    private static class SingletonHolder{
        private static final RetrofitUtil INSTANCE = new RetrofitUtil();
    }

    public static RetrofitUtil getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void getNews(Subscriber<NewsListInfo> newsSubscriber,String type,int page,int size){
        retrofitService.getNews(type,page,size)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsSubscriber);
    }
}