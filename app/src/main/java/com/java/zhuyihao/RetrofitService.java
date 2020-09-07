package com.java.zhuyihao;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface RetrofitService {
    public String Base_URL = "https://covid-dashboard.aminer.cn/api/events/";

    @GET("list")
    Observable<NewsListInfo> getNews(@Query("type")String type,@Query("page")int page,@Query("size")int size);
}
