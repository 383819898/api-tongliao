package com.shiku.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import it.sauronsoftware.jave.Encoder;

public class GetVideoPath {

	public static String getVideoLength(String path) {
		File source = null;
		try {
			source = new File(path);
			Encoder encoder = new Encoder();

			String length = "";
			InputStream in = new FileInputStream(source);

			int b = 0;

			/*
			 * while((b=in.read()) != -1) { logger.info("=<<<<<<<<==stream>>>>>>>"+b); }
			 */
			it.sauronsoftware.jave.MultimediaInfo m = encoder.getInfo(source);

			long ls = m.getDuration() / 1000;
			int hour = (int) (ls / 3600);
			int minute = (int) (ls % 3600) / 60;
			int seconds = (int) (ls - hour * 3600 - minute * 60);
			length = "视频时长为：" + hour + "时" + minute + "分" + seconds + "秒";

			return converter(minute, seconds);
		} catch (Exception e) {
			e.printStackTrace();
			return "exception";
		} finally {

		}

	}

	// 分秒转换
	public static String converter(int minute, int seconds) {
		String time = "";
		if (minute < 10) {
			time += "0" + minute + ":";
		} else {
			time += minute + ":";
		}
		if (seconds < 10) {
			time += "0" + seconds;
		} else {
			time += seconds;
		}

		return time;

	}
}