package com.shiku.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shiku.commons.SystemConfig;
import com.shiku.commons.utils.ConfigUtils;
import com.shiku.utils.GetVideoPath;


import it.sauronsoftware.jave.Encoder;

/**
 * @Description: TODO(用一句话描述该文件做什么)
 * @author lidaye
 * @date 2018年7月27日
 */
@Controller
public class UploadController {
	
	
	public SystemConfig getSystemConfig(){
		
		return ConfigUtils.getSystemConfig();
	}

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping("/getUrl")
	@ResponseBody
	public Map<String, String> getUrl() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "https://www.baidu.com");
		return map;
	}

	@RequestMapping("/getUrl2")
	@ResponseBody
	public Map<String, String> getUrl2() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "https://www.hao123.com");
		return map;
	}

	@RequestMapping("/getVideoLength")
	@ResponseBody
	public String getVideoLength(String path) {

  return GetVideoPath.getVideoLength("/data/www/resources/u/212/10000212/201912/d26bdacdf0914647a8809f87cb66b392.mp4");
	}
	
	@RequestMapping(value = "/config")
	@ResponseBody
	public String getConfig(HttpServletRequest request,HttpServletResponse response) {
		return "http://api.jiujiuim.com/";
	}
	

}
