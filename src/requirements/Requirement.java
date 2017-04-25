package requirements;

import scheduler.Nurse;

public class Requirement {
	String date;
	Nurse nurse;
	int weight;
	
	public Requirement(String d, Nurse n){
		date = d;
		nurse = n;
	}
}
