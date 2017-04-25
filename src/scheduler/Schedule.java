package scheduler;

import java.util.ArrayList;

public class Schedule {
	String start = "";
	String end = "";
	int period = 0;
	
	ArrayList<String> skillsTypes = new ArrayList<String>();
	ArrayList<Shift> shifts = new ArrayList<Shift>();
	ArrayList<Nurse> nurses = new ArrayList<Nurse>();
	//ArrayList<Nurse> nurses = new ArrayList<Nurse>();
	
	int nursesCount = nurses.size();
	
	
	
	
	public Schedule(){
		
	}
}
