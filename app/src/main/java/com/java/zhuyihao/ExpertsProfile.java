package com.java.zhuyihao;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;



public class ExpertsProfile implements  Config{

    private static JSONObject expertsInfo;

    private static ExpertsProfile instance;

    private static JSONArray experts;

    private ExpertsProfile() {}

    public static ExpertsProfile getInstance() {
        if(instance == null) {
            instance = new ExpertsProfile();
        }
        return instance;
    }


    public void getExperts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                try {
                    URL cs = new URL("https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2");
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
                if(jsonObject==null){
                    experts = new JSONArray();
                }
                else{
                    experts = jsonObject.getJSONArray("data");
                }

                Log.e("experts","success");
            }
        }).start();
    }

    public List<SimpleEntry<String, String>> getExpertsList(){
        expertsInfo = new JSONObject();
        List<SimpleEntry<String, String>> expertName = new ArrayList<>();
        for(int i=0;i<experts.size();i++) {
            JSONObject oneExpert = experts.getJSONObject(i);
            String zhName = oneExpert.getString("name_zh");
            String name = oneExpert.getString("name");
            String id = oneExpert.getString("id");
            expertName.add(new SimpleEntry<String, String>(id,name+" "+zhName));
            expertsInfo.put(id, new JSONObject());
            JSONObject newExpert = expertsInfo.getJSONObject(id);
            newExpert.put("name", oneExpert.getString("name"));
            newExpert.put("name_zh", oneExpert.getString("name_zh"));
            newExpert.put("avatar", oneExpert.getString("avatar"));
            newExpert.put("indices", oneExpert.getJSONObject("indices"));
            newExpert.put("is_passedaway",oneExpert.getBoolean("is_passedaway"));
        }
        return expertName;
    }


    public JSONObject getSpecificExpert(String id) {
        JSONObject oneExpert = expertsInfo.getJSONObject(id);
        return oneExpert;
    }

}
