package com.shiku.mianshi.advice.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.vo.JSONMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api(value="DiceController",tags="色子接口")
@RestController
@RequestMapping("/dice")
public class DiceController {

	     //好友分组列表
		@ApiOperation("获取色子点数")
		@RequestMapping("/getDiceNO")
		public Object getDice() {

			System.out.println("请求到了---------------------------");
			Object data=null;
			try {

				Random ran = new Random();

				data=ran.nextInt(6)+1;
				return data;
			} catch (Exception e) {
				return JSONMessage.failureByException(e);
			}


		}
}
