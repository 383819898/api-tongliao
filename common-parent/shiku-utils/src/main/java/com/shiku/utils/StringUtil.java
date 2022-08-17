package com.shiku.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class StringUtil {

	public static String trim(String s) {
		StringBuilder sb = new StringBuilder();
		for (char ch : s.toCharArray())
			if (' ' != ch)
				sb.append(ch);
		s = sb.toString();

		return s.replaceAll("&nbsp;", "").replaceAll(" ", "").replaceAll("　", "").replaceAll("\t", "").replaceAll("\n", "");
	}

	private static final char[] charArray = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static String getExt(String filename) {
		return filename.substring(filename.lastIndexOf('.'));
	}
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
	}
	public static boolean isEmpty(String s) {
		return isNullOrEmpty(s);
	}

	public static boolean isNullOrEmpty(String s) {
		return null == s || 0 == s.trim().length();
	}

	public static String randomCode() {
		return "" + (new Random().nextInt(899999) + 100000);
	}

	public static String randomPassword() {
		return randomString(6);
	}

	public static String randomString(int length) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; i++) {
			int index = new Random().nextInt(36);
			sb.append(charArray[index]);
		}

		return sb.toString();
	}

	public static String randomUUID() {
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString().replace("-", "");

		return uuidStr;
	}

	public static String getFormatName(String fileName) {
		int index = fileName.lastIndexOf('.');

		return -1 == index ? "jpg" : fileName.substring(index + 1);
	}



	public static List<Integer> getRandom(int maxValue, int size){
		HashSet<Integer> hs = new HashSet();
		Random random = new Random();
		int j = 0;
		while (hs.size() < size) {
			int tmp = random.nextInt(maxValue);
			System.out.println("j:" + j + "tmp:" + tmp);
			j++;
			hs.add(tmp);
		}
		List<Integer> lsList = new ArrayList<>();
		for(Integer i : hs){
			lsList.add(i);
		}
		return lsList;
	}

	public static List<Integer> getRandom(int maxValue){
		List<Integer> hs = new ArrayList<>();
		Random random = new Random();
		int j = 0;
		while (hs.size() < maxValue) {
			int tmp = random.nextInt(maxValue);
			System.out.println("j:" + j + "tmp:" + tmp);
			j++;
			if (hs.contains(tmp)) {
				continue;
			}
			hs.add(tmp);
		}
		return hs;
	}

	public static void main(String... strings) {
		Object s = 24.0;
		System.out.println(s);
		System.out.println(new BigDecimal(s.toString()).longValue());
//		String redStrTmp = "0.63";
//		DecimalFormat df = new DecimalFormat("#.00");
//		Double money0 = Double.valueOf(redStrTmp);
//		money0 = Double.valueOf(df.format(money0));
//		System.out.println(money0);
//		Double money = 5d * 100;
//		String moneyStr = String.valueOf(money.intValue());
//		String sumStr = String.valueOf(34);
//		String grStr = "280";
//		int count = 4;
//
//		List<Integer> hsList = null;
//		if (count == grStr.length()) {
//			hsList = getRandom(count);
//		}else{
//			hsList = getRandom(count,grStr.length());
//		}
//		for(Integer i : hsList){
//			System.out.println(i);
//		}


		//增强for循环
//		for(Integer i : hs){
//			System.out.println(i);
//		}

////迭代器
//		Iterator<Integer> iterator = hs.iterator();
//		while(iterator.hasNext()){
//			Integer integer = iterator.next();
//			System.out.println(integer);
//		}
//		System.out.println(moneyStr);
//		System.out.println(moneyStr.substring(moneyStr.length() - 1));
//		System.out.println(sumStr.substring(sumStr.length() - 1));
//		String userGetRedPacket = "18516108779/0.2;18516108778/0.1;";
//
//		// 检测是否有红包策略 领取红包者为比例红包
//		if (userGetRedPacket.contains("18516108779")){
//
//			BigDecimal userPhoneAndAmount = BigDecimal.ZERO;
//			String[] users = userGetRedPacket.split(";");
//			for (String userPhoneAmount : users) {
//				String[] userPhoneAmounts = userPhoneAmount.split("/");
//				String amount = userPhoneAmounts[1];
//				String phoneUser = userPhoneAmounts[0];
//				if ("18516108779".equals(phoneUser)) {
//					userPhoneAndAmount = new BigDecimal(amount);
//					break;
//				}
//			}
//
//			// 比例红包剩余金额
//			Double surplusProportion = BigDecimal.valueOf(1).subtract(userPhoneAndAmount).doubleValue();
//
//			// 保留两位小数
//			DecimalFormat df = new DecimalFormat("0.00");
//
//			// 如果还有剩余金额，比例内的人直接领取全部
//			if (surplusProportion > 0 && userPhoneAndAmount.doubleValue() > 0) {
//				System.out.println(Double.valueOf(df.format(surplusProportion)));
//			}
//		}
//		System.out.println(getFormatName("试题1.xls"));
	}
}
