package com.shiku.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Decoder;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.alibaba.fastjson.JSON;


@WebServlet("/base64Manager")
public class Base64UploadController extends HttpServlet {

	/**
	 * 
	 */
	

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	
		  request.setCharacterEncoding("UTF-8");
		  response.setContentType("text/html; charset=UTF-8");
		 
		
		String imgStr = request.getParameter("imgStr");
		
		imgStr = imgStr.trim().replace(" ","+");
	
		if (imgStr.contains("data:image/png;base64,")) {
			imgStr = imgStr.replace("data:image/png;base64,", "");
		}
		if (imgStr.contains("data:image/jpg;base64,")) {
			imgStr = imgStr.replace("data:image/jpg;base64,", "");
		}
		if (imgStr.contains("data:image/jpeg;base64,")) {
			imgStr = imgStr.replace("data:image/jpeg;base64,", "");
		}
		if (imgStr.contains("data:image/gif;base64,")) {
			imgStr = imgStr.replace("data:image/gif;base64,", "");
		}
		
	
	    String random = System.currentTimeMillis()+"";
	    
		/*
		 * File f = new File("/data/www/resources/base64Pics/"+random+".txt") ;
		 * 
		 * Writer writer = new FileWriter(f);
		 * 
		 * writer.write(imgStr); writer.close();
		 */
	    @SuppressWarnings("restriction")
	    sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
        try {
            // Base64解码
            @SuppressWarnings("restriction")
			byte[] b = decoder.decodeBuffer(imgStr);
            
           // response.getWriter().write("blength = "+b.length);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream("/mnt/data/www/resources/base64Pics/"+random+".png");
            out.write(b);
            out.flush();
            out.close();
          
        } catch (Exception e) {
          response.getWriter().write(e.toString());
        }
        
		/*
		 * Map<String,String> map = new HashMap<String,String>(); map.put("url",
		 * "http://file.quyangapp.com:8089/base64Pics/"+random+".png");
		 */
		/*
		 * String json =
		 * JSON.toJSONStringWithDateFormat("file.quyangapp.com:8089/base64Pics/"+random+
		 * ".png", "yyyy-MM-dd HH:mm:ss"); response.getWriter().write(json);
		 * response.getWriter().flush(); response.getWriter().close();
		 */
		/*
		 * response.getWriter().write("file.quyangapp.com:8089/base64Pics/"+random+
		 * ".png"); response.getWriter().close();
		 */
        //cdn注意
        response.getWriter().write("file.xrbio.cn/base64Pics/"+random+".png");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doGet(request, response);

	}
	

}