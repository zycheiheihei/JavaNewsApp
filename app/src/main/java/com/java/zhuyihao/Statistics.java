package com.java.zhuyihao;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;



public class Statistics implements Config {
	
	private static Statistics instance;
	JSONObject data;
	Date refreshTime;
	
	private Statistics() {}
	
	public static Statistics getInstance() {
		if(instance==null) {
			instance = new Statistics();
		}
		return instance;
	}
	
	static final HashMap<Integer, Integer> monthDays = new HashMap<Integer,Integer>() {
		  {
			    put(1,31);
			    put(2,29);
			    put(3,31);
			    put(4,30);
			    put(5,31);
			    put(6,30);
			    put(7,31);
			    put(8,31);
			    put(9,30);
			    put(10,31);
			    put(11,30);
			    put(12,31);
			  }
			};
	
	public static int CalculateDateIndex(String beginTime,String date) {
		if(beginTime.compareTo(date)>0) {
			return -1;
		}
		else {
			String[] beginStrings = beginTime.split("-");
			String[] dateStrings = date.split("-");
			int beginMonth = Integer.parseInt(beginStrings[1]);
			int beginDay = Integer.parseInt(beginStrings[2]);
			int dateMonth = Integer.parseInt(dateStrings[1]);
			int dateDay = Integer.parseInt(dateStrings[2]);
			int days=0;
			for(int i=beginMonth;i<dateMonth;i++) {
				days+=monthDays.get(i);
			}
			days-=beginDay;
			days+=dateDay;
			return days;
		}
	}
	
	
	
	public JSONArray getData(String place, String date) {
		JSONObject placeInfo = data.getJSONObject(place);
		if(placeInfo==null) {
			return null;
		}
		JSONArray placeData = placeInfo.getJSONArray("data");
		String beginTime = placeInfo.getString("begin");
		int daysIndex = CalculateDateIndex(beginTime, date);
		if(daysIndex<0 || daysIndex >= placeData.size()) {
			System.out.println("Information on "+date+" is not included in our database.");
			System.out.println("Now showing the latest data...");
			return placeData.getJSONArray(placeData.size()-1);
		}
		else {
			return placeData.getJSONArray(daysIndex);
		}
		
	}
	
	public void refresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Loading data from server...");
				JSONObject info = getJsonObject();
				Log.e("load","success");
				writeFile(info.toString());
				refreshTime = new Date();
				readFile();
				Log.e("get","success");
			}
		}).start();
	}
	
	private JSONObject getJsonObject() {
		JSONObject jsonObject = null;
		try {
			URL cs = new URL("https://covid-dashboard.aminer.cn/api/dist/epidemic.json");
		    URLConnection tc = cs.openConnection();
			Log.e("data get","ojbk1");
		    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
			StringBuilder builder = new StringBuilder();
			for (String s = in.readLine(); s != null; s = in.readLine()) {
				builder.append(s);
			}
			Log.e("data get","ojbk");
			jsonObject = (JSONObject)JSON.parse(builder.toString());
		}catch (Exception e) {
			e.printStackTrace();
			Log.e("fuck","what's up");
			jsonObject = null;
			return new JSONObject();
		}
		JSONObject newObject = new JSONObject();
		Set<String> keys = jsonObject.keySet();
		newObject.put("World",new JSONObject());
		newObject.getJSONObject("World").put("begin",jsonObject.getJSONObject("World").get("begin"));
		newObject.getJSONObject("World").put("data",jsonObject.getJSONObject("World").get("data"));
		newObject.getJSONObject("World").put("subarea",new JSONObject());
		JSONObject worldsubJSON = newObject.getJSONObject("World").getJSONObject("subarea");
		keys.remove("World");
//		int max=0;
//		for(String k:keys) {
//			int s = k.split("\\|").length;
//			if(s>max) {
//				max=s;
//			}
//		}
//		System.out.println(max);
		for(String k: keys) {
			if(!k.contains("|")) {
				worldsubJSON.put(k,new JSONObject());
				worldsubJSON.getJSONObject(k).put("begin",jsonObject.getJSONObject(k).get("begin"));
				worldsubJSON.getJSONObject(k).put("data",jsonObject.getJSONObject(k).get("data"));
				worldsubJSON.getJSONObject(k).put("subarea",new JSONObject());
			}
		}
		for(String k: keys) {
			String[] s = k.split("\\|");
			if(s.length==2) {
				String father = s[0];
				String subkey = s[1];
				JSONObject fathersubJSON = null;
				try {
					fathersubJSON = worldsubJSON.getJSONObject(father).getJSONObject("subarea");
				} catch (NullPointerException e) {
					// TODO: handle exception
					worldsubJSON.put(father, new JSONObject());
					worldsubJSON.getJSONObject(father).put("subarea",new JSONObject());
					worldsubJSON.getJSONObject(father).put("begin",jsonObject.getJSONObject(k).get("begin"));
					worldsubJSON.getJSONObject(father).put("data",jsonObject.getJSONObject(k).get("data"));
					fathersubJSON = worldsubJSON.getJSONObject(father).getJSONObject("subarea");
				}
				fathersubJSON.put(subkey,new JSONObject());
				fathersubJSON.getJSONObject(subkey).put("begin",jsonObject.getJSONObject(k).get("begin"));
				fathersubJSON.getJSONObject(subkey).put("data",jsonObject.getJSONObject(k).get("data"));
				fathersubJSON.getJSONObject(subkey).put("subarea",new JSONObject());
			}
		}
		for(String k: keys) {
			String[] s = k.split("\\|");
			if(s.length==3) {
				String grandfather = s[0];
				String father = s[1];
				String subkey = s[2];
				JSONObject fathersubJSON = worldsubJSON.getJSONObject(grandfather).getJSONObject("subarea").getJSONObject(father).getJSONObject("subarea");
				fathersubJSON.put(subkey,new JSONObject());
				fathersubJSON.getJSONObject(subkey).put("begin",jsonObject.getJSONObject(k).get("begin"));
				fathersubJSON.getJSONObject(subkey).put("data",jsonObject.getJSONObject(k).get("data"));
				fathersubJSON.getJSONObject(subkey).put("subarea",new JSONObject());
			}
		}
		return newObject;
	}
	
	private boolean writeFile(String sets) {
        FileWriter fw;
        try {
            fw = new FileWriter(MyCacheSta+"/my_new_structure.json");
            PrintWriter out = new PrintWriter(fw);
            out.write(sets);
            out.println();
            fw.close();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public void readFile() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(MyCacheSta+"/my_new_structure.json")));
			data = (JSONObject)JSON.parse(reader.readLine());
//			JSONObject data = getJsonObject();
			reader.close();
		}  catch (Exception e) {
			System.out.println("Load Data Failed.");
			System.out.println(e.getMessage());
//			logger.error(e.getMessage());
		} 
	}
	
	public JSONObject getData() {
		return data;
	}
	
	public Date getRefreshTime() {
		return refreshTime;
	}

}
