package com.xtreme.doctorapp.domain;

import java.util.HashMap;

public class TimingUtil {

	static HashMap<String, String> timings;
	static HashMap<String, String> timingsValues ;

	public static HashMap<String, String> initialise() {
		timings = new HashMap<String, String>();
		timings.put("1", "10.00 AM - 10.30 AM");
		timings.put("2", "10.30 AM - 11.00 AM");
		timings.put("3", "11.00 AM - 11.30 AM");
		timings.put("4", "11.30 AM - 12.00 AM");

		return timings;
	}

	public static HashMap<String, String> initialiseValues() {
		timingsValues = new HashMap<String, String>();
		timingsValues.put("10.00 AM - 10.30 AM", "1");
		timingsValues.put("10.30 AM - 11.00 AM", "2");
		timingsValues.put("11.00 AM - 11.30 AM", "3");
		timingsValues.put("11.30 AM - 12.00 AM", "4");
		return timingsValues;
	}
	
	
	public static String getLabelbasedOnId(String id){
		HashMap<String, String>  hashMap= initialise();
		return hashMap.get(id);
	}

}
