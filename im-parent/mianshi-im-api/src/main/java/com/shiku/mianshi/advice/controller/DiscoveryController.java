package com.shiku.mianshi.advice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dicovery")
public class DiscoveryController {

	@RequestMapping("/getUrl")
	public Map<String, String> getUrl() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "http://www.baidu.com");
		return map;
	}

	@RequestMapping("/getUrl2")
	public Map<String, String> getUrl2() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", "http://www.hao123.com");
		return map;
	}

}
