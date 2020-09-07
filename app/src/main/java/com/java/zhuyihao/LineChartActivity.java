package com.java.zhuyihao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.jfree.data.time.Day;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LineChartActivity extends AppCompatActivity {

    LineChart lineChart;
    JSONObject info;
    String place;
    String date;
    boolean dateOutOfBound;
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
    }

    private void setLineChart(){
        if(date.equals("latest")) {
            date = dateToString(new Date());
        }
        int latest_index = Statistics.CalculateDateIndex(info.getString("begin"), date);
        String[] dateStrings = date.split("-");
        int dateMonth = Integer.parseInt(dateStrings[1]);
        int dateDay = Integer.parseInt(dateStrings[2]);
        JSONArray statistic = info.getJSONArray("data");
        firstDay = new Day(dateDay,dateMonth,2020);
        while(latest_index>=statistic.size()) {
            latest_index-=1;
            firstDay=(Day) firstDay.previous();
            dateOutOfBound = true;
        }
        if(latest_index<0){
            dateOutOfBound = true;
            date = dateToString(new Date());
            latest_index = Statistics.CalculateDateIndex(info.getString("begin"), date);
            dateStrings = date.split("-");
            dateMonth = Integer.parseInt(dateStrings[1]);
            dateDay = Integer.parseInt(dateStrings[2]);
            firstDay = new Day(dateDay,dateMonth,2020);
            while(latest_index>=statistic.size()) {
                latest_index-=1;
                firstDay=(Day) firstDay.previous();
                dateOutOfBound = true;
            }
        }
        Log.e("chart",firstDay.toString());

        List<Entry> entries1 = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();
        for(int i=Math.max(0,latest_index-9);i<=latest_index;i++) {
            JSONArray thisDay = statistic.getJSONArray(i);
            entries1.add(new Entry(i-Math.max(0,latest_index-9)+1,thisDay.getInteger(0)==null?0:thisDay.getInteger(0)));
            entries2.add(new Entry(i-Math.max(0,latest_index-9)+1,thisDay.getInteger(2)==null?0:thisDay.getInteger(2)));
            entries3.add(new Entry(i-Math.max(0,latest_index-9)+1,thisDay.getInteger(3)==null?0:thisDay.getInteger(3)));
//	   		System.out.println(firstDay);
        }

        final int maxXValue = latest_index - Math.max(0,latest_index-9)+1;


        LineDataSet confirmedDataSet = new LineDataSet(entries1, "确诊病例"); // add entries to dataset
        LineDataSet curedDataSet = new LineDataSet(entries2, "治愈病例");
        LineDataSet deathDataSet = new LineDataSet(entries3,"死亡病例");
        LineData lineData = new LineData(confirmedDataSet);
        lineData.setDrawValues(true);
        lineData.setValueTextSize(13f);
        curedDataSet.setValueTextSize(13f);
        deathDataSet.setValueTextSize(13f);
        lineChart.setData(lineData);
        lineChart.getLineData().addDataSet(curedDataSet);
        lineChart.getLineData().addDataSet(deathDataSet);
        Legend legend = lineChart.getLegend();
        legend.setTextSize(14f);
        lineChart.setBackgroundColor(Color.rgb(143,208,208));

        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);
        confirmedDataSet.setColor(Color.RED);
        confirmedDataSet.setCircleColor(Color.RED);
        curedDataSet.setColor(Color.GREEN);
        curedDataSet.setCircleColor(Color.GREEN);
        deathDataSet.setCircleColor(Color.DKGRAY);
        deathDataSet.setColor(Color.DKGRAY);
//        dataSet.setColor(Color.parseColor("#7d7d7d"));//线条颜色
//        dataSet.setCircleColor(Color.RED);//圆点颜色
//        dataSet1.setCircleColor(Color.GREEN);
//        dataSet.setLineWidth(2f);//线条宽度
//        dataSet1.setLineWidth(2f);//线条宽度
        //设置样式
        YAxis rightAxis = lineChart.getAxisRight();

        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(14f);
        leftAxis.setAxisLineWidth(3f);
        //设置图表左边的y轴禁用
        leftAxis.setEnabled(true);
        //设置x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        xAxis.setTextSize(14f);
        xAxis.setAxisLineWidth(3f);
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        Log.e("chart",firstDay.toString());
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int pushForward = maxXValue - (int)value;
                Day x = firstDay;
                while(pushForward>0){
                    x = (Day)x.previous();
                    pushForward--;
                }
                return x.getMonth()+"-"+x.getDayOfMonth();
            }
        });
        xAxis.setDrawGridLines(true);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(2f);//禁止放大后x轴标签重绘
        lineChart.invalidate(); // refresh
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);
        initInfo();
        lineChart = findViewById(R.id.line_chart);
        //1.设置x轴和y轴的点
        setLineChart();

    }


    private static String dateToString(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");//日期格式
        String time = sformat.format(date);

        return time;
    }

}