package scheduler;

import java.util.ArrayList;

public class Contract {
	public int id;
	public String description;
	boolean singleassignmentperdaytrue;
	
	int maxnumassignments;
	int minnumassignments;
	
	int maxconsecutiveworkingdays;
	int minconsecutiveworkingdays;
	
	int maxconsecutivefreedays;
	int minconsecutivefreedays;
	
	int maxconsecutiveworkingweekends;
	int minconsecutiveworkingweekends;
	
	int maxworkingweekendsinfourweeks;
	String weekenddefinition;
	
	boolean completeweekends;
	boolean identicalshifttypesduringweekend;
	boolean nonightshiftbeforefreeweekend;
	boolean alternativeskillcategory;

	//ArrayList<int> unwantedpatterns = new ArrayList<int>();
	
	public Contract(int id , String d){
		description = d;
		this.id = id; 
	}
	
	public String getDescription(){
		return description;
	}
}
