package scheduler;

import java.util.ArrayList;
/**
 * Specifies the employment contract and all its requirements.
 * @author Studena Zuzana
 *
 */
public class Contract {
	int id;
	String description;
	boolean singleassignmentperdaytrue;
	int weight1 = 1;
	int weight2 = 0;
	
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

	ArrayList<Integer> unwantedPatterns;
	
	public Contract(int id , String d){
		description = d;
		this.id = id; 
	}
	
	public void addContraints(String assigmentPD, String maxNA, String minNA, String maxWD, String minWD,String maxFD,
			String minFD, String maxCWW, String minCWW, String MWWIFW, String weekend, String compW ,
			String ISTDW, String NNSBFW, String altSkill ){
		
		singleassignmentperdaytrue = Boolean.valueOf(assigmentPD);
		maxnumassignments = Integer.parseInt(maxNA);
		minnumassignments = Integer.parseInt(minNA);
		
		maxconsecutiveworkingdays = Integer.parseInt(maxWD);
		minconsecutiveworkingdays = Integer.parseInt(minWD);
		
		maxconsecutivefreedays = Integer.parseInt(maxFD);
		minconsecutivefreedays = Integer.parseInt(minFD);
		
		maxconsecutiveworkingweekends = Integer.parseInt(maxCWW);;
		minconsecutiveworkingweekends = Integer.parseInt(minCWW);
		
		maxworkingweekendsinfourweeks = Integer.parseInt(MWWIFW); 
		weekenddefinition = weekend;
		
		completeweekends = Boolean.valueOf(compW);
		identicalshifttypesduringweekend = Boolean.valueOf(ISTDW);
		nonightshiftbeforefreeweekend = Boolean.valueOf(NNSBFW);
		alternativeskillcategory = Boolean.valueOf(altSkill);
	}
	
	public void setUnwantedPatterns(ArrayList<Integer> unwantedPatterns) {
		this.unwantedPatterns = unwantedPatterns;
	}
	
	public String getDescription(){
		return description;
	}
	
	public int getMaxconsecutivefreedays() {
		return maxconsecutivefreedays;
	}
	
	public int getMaxconsecutiveworkingweekends() {
		return maxconsecutiveworkingweekends;
	}
	
	public int getMaxworkingweekendsinfourweeks() {
		return maxworkingweekendsinfourweeks;
	}
	
	public int getMaxnumassignments() {
		return maxnumassignments;
	}
	
	public int getId() {
		return id;
	}
	
	public int getMaxconsecutiveworkingdays() {
		return maxconsecutiveworkingdays;
	}
	
	public int getMinconsecutivefreedays() {
		return minconsecutivefreedays;
	}
	
	public int getMinconsecutiveworkingdays() {
		return minconsecutiveworkingdays;
	}
	
	public int getMinconsecutiveworkingweekends() {
		return minconsecutiveworkingweekends;
	}
	
	public int getMinnumassignments() {
		return minnumassignments;
	}
	
	public ArrayList<Integer> getUnwantedPatterns() {
		return unwantedPatterns;
	}
	
	public String getWeekenddefinition() {
		return weekenddefinition;
	}
	
	public int getWeight1() {
		return weight1;
	}
	
	public int getWeight2() {
		return weight2;
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
