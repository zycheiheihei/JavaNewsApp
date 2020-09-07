package com.java.zhuyihao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.jfree.data.time.Day;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class PieChartActivity extends AppCompatActivity {

    PieChart confirmedChart;
    PieChart curedChart;
    PieChart deathChart;
    JSONObject info;
    String place;
    String date;
    boolean dateOutOfBound;
    List<Map.Entry<String,Integer>> sortedConfirmedInfo;
    List<Map.Entry<String,Integer>> sortedCuredInfo;
    List<Map.Entry<String,Integer>> sortedDeathInfo;
    Day firstDay;

    private void initInfo(){
        Intent intent = getIntent();
        place = intent.getStringExtra("searchKey");
        date = intent.getStringExtra("date");
        JSONObject data = Statistics.getInstance().getData();
        if(place.equals("World")) {
            info = data.getJSONObject("World");
        }
        else {
            String[] places = place.split("\\|");
            if(places.length==1) {
                info = data.getJSONObject("World").getJSONObject("subarea").getJSONObject(places[0]);
            }
            else if(places.length==2) {
                JSONObject fatherJSON = data.getJSONObject("World").getJSONObject("subarea").getJSONObject(places[0]);
                info = fatherJSON.getJSONObject("subarea").getJSONObject(places[1]);
            }
            else {
                JSONObject grandfatherJSON = data.getJSONObject("World").getJSONObject("subarea").getJSONObject(places[0]);
                JSONObject fatherJSON = grandfatherJSON.getJSONObject("subarea").getJSONObject(places[1]);
                info = fatherJSON.getJSONObject("subarea").getJSONObject(places[2]);
            }
        }
        dateOutOfBound = false;
        Log.e("chart","load success");
        if(date.equals("latest")) {
            date = dateToString(new Date());
        }
        getDataset();
    }

    void setPieChart(PieChart whichChart,List<Map.Entry<String,Integer>> sortedList,String title ){
        Log.e("piechart",sortedList.toString());
//        Log.e("piechart",sortedCuredInfo.toString());
//        Log.e("piechart",sortedDeathInfo.toString());


        List<PieEntry> Entry = new ArrayList<>();
        if(sortedList.size()>5){
            for(int i=0;i<5;i++){
                Entry.add(new PieEntry(sortedList.get(i).getValue(),sortedList.get(i).getKey()));
            }
            int Rest=0;
            for(int i=5;i<sortedList.size();i++){
                Rest += sortedList.get(i).getValue();
            }
            Entry.add(new PieEntry(Rest,"Rest"));
        }
        else{
            for(int i=0;i<sortedList.size();i++){
                Entry.add(new PieEntry(sortedList.get(i).getValue(),sortedList.get(i).getKey()));
            }
        }


        PieDataSet dataSet1 = new PieDataSet(Entry,place+" 各地区"+title);
        Random random = new Random();

        ArrayList<Integer> colors = new ArrayList<>();
        for(int i=0;i<Entry.size();i++){
            colors.add(Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256)));
        }
        dataSet1.setColors(colors);
        dataSet1.setValueTextSize(13f);
        dataSet1.setValueTextColor(Color.WHITE);


        PieData pieData = new PieData(dataSet1);
        pieData.setDrawValues(true);
        Legend legend = whichChart.getLegend();
        legend.setEnabled(false);
        Description description = whichChart.getDescription();
        description.setEnabled(false);
        whichChart.setCenterText(title);
        whichChart.setBackgroundColor(Color.rgb(143,208,208));
        whichChart.setCenterTextSize(20f);
        whichChart.setData(pieData);
        whichChart.invalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);
        confirmedChart = findViewById(R.id.confiremed_chart);
        curedChart = findViewById(R.id.cured_chart);
        deathChart = findViewById(R.id.death_chart);
        initInfo();
        //1.设置x轴和y轴的点
        setPieChart(confirmedChart,sortedConfirmedInfo,"确诊病例");
        setPieChart(curedChart,sortedCuredInfo,"治愈病例");
        setPieChart(deathChart,sortedDeathInfo,"死亡病例");

    }


    private static String dateToString(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");//日期格式
        String time = sformat.format(date);

        return time;
    }

    private void getDataset(){
        dateOutOfBound = false;
        String[] dateStrings = date.split("-");
        int dateMonth = Integer.parseInt(dateStrings[1]);
        int dateDay = Integer.parseInt(dateStrings[2]);
        Day firstDay = new Day(dateDay,dateMonth,2020);
        HashMap<String, Integer> LegalConfirmedData=new HashMap<>();
        HashMap<String, Integer> LegalCuredData=new HashMap<>();
        HashMap<String, Integer> LegalDeathData=new HashMap<>();
        JSONObject subarea = info.getJSONObject("subarea");
        Set<String> keys = subarea.keySet();
        for(String k:keys) {
            JSONObject placeInfo = subarea.getJSONObject(k);
            String beginTime = placeInfo.getString("begin");
            JSONArray allData = placeInfo.getJSONArray("data");
            int index = Statistics.CalculateDateIndex(beginTime, date);
            if(index>=allData.size()||index<0) {
                index=allData.size()-1;
                dateOutOfBound=true;
            }
            LegalConfirmedData.put(k, allData.getJSONArray(index).getInteger(0));
            LegalCuredData.put(k, allData.getJSONArray(index).getInteger(2));
            LegalDeathData.put(k, allData.getJSONArray(index).getInteger(3));
        }
        if(dateOutOfBound) {
            for(String k:keys) {
                JSONObject placeInfo = subarea.getJSONObject(k);
                JSONArray allData = placeInfo.getJSONArray("data");
                int index = allData.size()-1;
                LegalConfirmedData.put(k, allData.getJSONArray(index).getInteger(0));
                LegalCuredData.put(k, allData.getJSONArray(index).getInteger(2));
                LegalDeathData.put(k, allData.getJSONArray(index).getInteger(3));
            }
        }
        sortedConfirmedInfo = sort(LegalConfirmedData);
        sortedCuredInfo = sort(LegalCuredData);
        sortedDeathInfo = sort(LegalDeathData);
    }


    private static  List<Map.Entry<String,Integer>> sort(Map<String, Integer> unsortedMap){
        List<Map.Entry<String, Integer>> list = new ArrayList<>(unsortedMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> m1,Map.Entry<String, Integer> m2) {
                return m2.getValue().compareTo(m1.getValue());
            }
        });
        return list;
    }

}