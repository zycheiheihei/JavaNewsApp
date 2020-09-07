package com.java.zhuyihao;

import java.io.*;
import java.net.*;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class KnowledgeGraph {

    private static KnowledgeGraph instance;

    String entity;

    JSONArray entityInfo;

    private KnowledgeGraph() {}

    public static KnowledgeGraph getInstance() {
        if(instance == null) {
            instance = new KnowledgeGraph();
        }
        return instance;
    }

    private JSONObject getRawData(String entityName) {
        JSONObject jsonObject;
        try {
            URL cs = new URL("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity="+entityName);
            URLConnection tc = cs.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            StringBuilder builder = new StringBuilder();
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                builder.append(s);
            }
            jsonObject = (JSONObject)JSON.parse(builder.toString());
        }catch (Exception e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    private void handleRawData(JSONObject rawData) {
        if(rawData==null)
            entityInfo = new JSONArray();
        else
            entityInfo = rawData.getJSONArray("data");
    }

    public JSONArray getInfo(String entityName) {
        entity = entityName;
        handleRawData(getRawData(entityName));
        return entityInfo;
    }

}
