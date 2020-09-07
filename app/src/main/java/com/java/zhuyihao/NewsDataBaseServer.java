package com.java.zhuyihao;

import android.util.Log;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.HashSet;
import java.util.List;

public class NewsDataBaseServer {
    private static HashSet<String> idHash;
    public static void init(){
        LitePal.getDatabase();
        idHash = new HashSet<>();
        List<News> history = getAllHistory();
        for(News news:history){
            idHash.add(news.getNews_id());
        }
        Log.e("DATABASE","INITTED");
    }
    public static boolean hasNews(String newsId){
        return idHash.contains(newsId);
    }

    public static void insertNews(NewsDetail.Data data){
            News news = new News();
            news.setContent(data.getContent());
            news.setDate(data.getDate());
            news.setNews_id(data.get_id());
            news.setSource(data.getSource());
            news.setTitle(data.getTitle());
            news.setType(data.getType());
            news.save();
            idHash.add(news.getNews_id());
    }
    public static News getNews(String newsId){
        return DataSupport.where("news_id = ?",newsId).find(News.class).get(0);
    }
    public static List<News> getAllHistory(){
        return DataSupport.findAll(News.class);
    }
    public static List<News> searchNews(String key){
        List<News> newsList = DataSupport.where("title like ? or content like ?",key,key).find(News.class);
        return newsList;
    }

    public static void clear(){
        DataSupport.deleteAll(News.class);
    }
}
