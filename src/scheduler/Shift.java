package scheduler;

import java.util.ArrayList;
/**
 * Specify all shift parameters.
 * @author Studená Zuzana
 *
 */
public class Shift {
	int id;
	String startTime;
	String endTime;
	String description;
	public String type;
	public ArrayList<String> requiredSkills = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public Shift( String st, String et, String t,  ArrayList<String> s, String desc ){
		startTime = st;
		endTime = et;
		type = t;
		requiredSkills = (ArrayList<String>) s.clone();
		description = desc;
	}
	
	public ArrayList<String> getSkills(){
		return requiredSkills;
	}
	
	public String getType(){
		return type;
	}
	
	@Override
	public String toString() {
		String out = "Shift Type: " + this.type + "\n" +
					 "description : " + this.description + "\n" +
					 "start time: " + this.startTime + "\n" +
					 "end time: " + this.endTime + "\n" + 
					 "skills : ";
					
		for(String s : this.requiredSkills ){
			out += s + " ";
		}
		return out;
	}

}



