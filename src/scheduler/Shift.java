package scheduler;

import java.util.ArrayList;

public class Shift {
	String startTime;
	String endTime;
	String description;
	public String type;
	public ArrayList<String> requiredSkills = new ArrayList<String>();
	
	public Shift(String st, String et, String t, String s ){
		startTime = st;
		endTime = et;
		type = t;
		requiredSkills.add(s); 
	}
	
	public ArrayList<String> getSkills(){
		return requiredSkills;
	}
	
	public String getType(){
		return type;
	}
}
