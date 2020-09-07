package com.java.zhuyihao;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitService4Detail {
    public String News_Detail_URL = "https://covid-dashboard.aminer.cn/api/";

    @GET("event/{newsId}")
    Observable<NewsDetail> getNewsDetail(@Path("newsId")String newsId);
}
