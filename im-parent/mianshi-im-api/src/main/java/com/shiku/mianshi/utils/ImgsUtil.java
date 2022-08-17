package com.shiku.mianshi.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ImgsUtil {

	public static String getCommonFormat(String original) {
		
		String pics = "";
		JSONArray json = JSONArray.parseArray(original);
	
		if(original != null && !original.equals("")) {
			for (int i = 0; i < json.size(); i++) {
				//log.info("	json.get(i) " + json.get(i));
				JSONObject obj = JSONObject.parseObject(json.get(i).toString());
				System.out.println(obj.get("oUrl"));
				if(i != (json.size()-1)) {
					pics += obj.get("oUrl")+",";
				}else {
					pics += obj.get("oUrl");
				}
			}
		}
		
		return pics;
	}
	

}
