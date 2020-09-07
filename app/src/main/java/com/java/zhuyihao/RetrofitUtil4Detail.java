package com.java.zhuyihao;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitUtil4Detail {
    Retrofit retrofit;
    RetrofitService4Detail retrofitService;

    private RetrofitUtil4Detail() {
        retrofit = new Retrofit.Builder()
                .baseUrl(retrofitService.News_Detail_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        retrofitService = retrofit.create(RetrofitService4Detail.class);
    }

    private static class SingletonHolder{
        private static final RetrofitUtil4Detail INSTANCE = new RetrofitUtil4Detail();
    }

    public static RetrofitUtil4Detail getInstance(){
        return RetrofitUtil4Detail.SingletonHolder.INSTANCE;
    }

    public void getNewsDetail(Subscriber<NewsDetail> newsSubscriber, String newsId){
        retrofitService.getNewsDetail(newsId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsSubscriber);
    }
}
