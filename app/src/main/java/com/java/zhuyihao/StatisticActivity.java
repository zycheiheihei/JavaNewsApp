package com.java.zhuyihao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class StatisticActivity extends AppCompatActivity {
    private Spinner countrySpinner;
    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private ArrayList<String> countriesName = new ArrayList<>();
    private ArrayList<String> provincesName = new ArrayList<>();
    private ArrayList<String> citiesName = new ArrayList<>();
    private JSONObject countries;
    private JSONObject provinces;
    private JSONObject cities;
    private ArrayList<String> empty = new ArrayList<>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        context = this;
        empty.add(" ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("疫情数据");
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        countries = Statistics.getInstance().getData().getJSONObject("World").getJSONObject("subarea");
        countriesName = new ArrayList<String>(countries.keySet());
        countriesName.add(" ");
        countriesName.sort(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        provincesName.add(" ");
        citiesName.add(" ");
        countrySpinner = (Spinner)findViewById(R.id.country_spinner);
        provinceSpinner = (Spinner)findViewById(R.id.province_spinner);
        citySpinner = (Spinner) findViewById(R.id.city_spinner);

        final ArrayAdapter<String> arrayAdapterCountry = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,countriesName);
        arrayAdapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(arrayAdapterCountry);

        final ArrayAdapter<String> arrayAdapterProvince = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,provincesName);
        arrayAdapterProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(arrayAdapterProvince);

        Log.e("cities","length is "+citiesName.size());
        final ArrayAdapter<String> arrayAdapterCity = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,citiesName);
        arrayAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(arrayAdapterCity);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!countriesName.get(i).equals(" ")) {
                    provinces = countries.getJSONObject(countriesName.get(i)).getJSONObject("subarea");
                    substituteArray(provincesName, new ArrayList<String>(provinces.keySet()));
                    Log.d("select Country", countriesName.get(i) + " total provinces " + provincesName.size());
                    provincesName.add(" ");
                    provincesName.sort(new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                return s.compareTo(t1);
                            }
                        });
                }else{
                    substituteArray(provincesName,empty);
                }
                arrayAdapterProvince.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!provincesName.get(i).equals(" ")) {
                    Log.d("select Province", provincesName.get(i));
                    cities = provinces.getJSONObject(provincesName.get(i)).getJSONObject("subarea");
                    substituteArray(citiesName, new ArrayList<String>(cities.keySet()));
                    citiesName.add(" ");
                    citiesName.sort(new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                }else{
                    substituteArray(citiesName,empty);
                }
                arrayAdapterCity.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button button = (Button) findViewById(R.id.certain_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country = countriesName.get(countrySpinner.getSelectedItemPosition());
                String province = provincesName.get(provinceSpinner.getSelectedItemPosition());
                String city = citiesName.get(citySpinner.getSelectedItemPosition());
                String searchKey = formSearchKey(country,province,city);
                DatePicker datePicker = (DatePicker)findViewById(R.id.date_picker);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateSelected = datePicker.getYear()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
                Date date = null;
                try {
                    date = sdf.parse(dateSelected);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String dateString = sdf.format(date);
                RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_chart_mode);
                int chart_mode = radioGroup.getCheckedRadioButtonId();
                Log.e("MSG",searchKey+' '+dateString+" "+chart_mode);
                if(chart_mode==R.id.line_chart_button){
                    Intent intent = new Intent(context,LineChartActivity.class);
                    intent.putExtra("searchKey",searchKey);
                    intent.putExtra("date",dateString);
                    startActivity(intent);
                }else if(chart_mode==R.id.pie_chart_button){
                    Intent intent = new Intent(context,PieChartActivity.class);
                    intent.putExtra("searchKey",searchKey);
                    intent.putExtra("date",dateString);
                    startActivity(intent);
                }else{
                    Log.e("哥哥","你倒是选呀");
                }
            }
        });
    }

    private String formSearchKey(String country,String province,String city){
        StringBuilder sb = new StringBuilder();
        if(country.equals(" ")){
            return "World";
        }else{
            sb.append(country);
            if(province.equals(" ")){
                return sb.toString();
            }else{
                sb.append('|');
                sb.append(province);
                if(city.equals(" ")){
                    return sb.toString();
                }else{
                    sb.append('|');
                    sb.append(city);
                    return sb.toString();
                }
            }
        }
    }

    private void substituteArray(ArrayList<String> oldArray,ArrayList<String> newArray){
        oldArray.clear();
        oldArray.addAll(newArray);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar3,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

}