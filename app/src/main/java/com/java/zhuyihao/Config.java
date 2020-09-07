package com.java.zhuyihao;
import android.os.Environment;

import java.util.HashMap;

public interface Config {
    int PAGE_SIZE = 50;
    int SEARCH_SIZE = 1000;
    String[] TYPE_LIST = {"all","news","paper"};
    String[] STATUS = {"TEST_START","INIT_NEWS","REFRESH","GET_MORE","TEST_REFRESH"};
    String MyCacheSta = "/data/data/com.java.zhuyihao/cache";
    String[] NEWS_TAGS = {"ALL","NEWS","PAPER"};
    HashMap<String,Integer> TAGS2NUM = new HashMap<String,Integer>(){
        {
            put("ALL",0);
            put("NEWS",1);
            put("PAPER",2);
        }
    };
}
