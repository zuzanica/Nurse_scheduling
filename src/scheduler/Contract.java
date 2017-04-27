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
	
	public void addHardContraints(String maxNA, String minNA, String maxWD, String minWD,String maxFD,String minFD ){
		maxnumassignments = Integer.parseInt(maxNA);
		minnumassignments = Integer.parseInt(minNA);
		
		maxconsecutiveworkingdays = Integer.parseInt(maxWD);
		minconsecutiveworkingdays = Integer.parseInt(minWD);
		
		maxconsecutivefreedays = Integer.parseInt(maxFD);
		minconsecutivefreedays = Integer.parseInt(minFD);
	}
	
	public String getDescription(){
		return description;
	}
	
	
	@Override
	public String toString() {
		String out = "Contract ID : " + this.id + "\n" +
					 "description : " + this.description + "\n" +
					 "maxnumassignments : " + this.maxnumassignments + "\n" +
					 "minnumassignments : " + this.minnumassignments + "\n" + 
					 "maxconsecutiveworkingdays : " + this.maxconsecutiveworkingdays + "\n" + 
					 "minconsecutiveworkingdays : " + this.minconsecutiveworkingdays + "\n" + 
					 "maxconsecutivefreedays : " + this.maxconsecutivefreedays + "\n" + 
					 "minconsecutivefreedays : " + this.minconsecutivefreedays + "\n"
					 ;
		return out;
	}
	
	
}
