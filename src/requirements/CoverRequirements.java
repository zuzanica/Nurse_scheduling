package requirements;

import java.util.ArrayList;

/**
 * Represents all requests for all period.
 * @author Studena Zuzana
 *
 */
public class CoverRequirements {
	String day;
	ArrayList<Cover> cover;
	
	public CoverRequirements(String d, ArrayList<Cover> _cover){
		day = d;
		cover = _cover;
	}
	
	public String getDay(){
		return day;
	}
	public ArrayList<Cover> getCoversList(){
		return cover;
	}
	
	public int getDailyCoverCount(){
		int dayNurseCover = 0; 
		for(Cover c : cover ){
			dayNurseCover += c.getPrefNurses();
		}
		return dayNurseCover;
		
	}
	
	@Override
	public String toString() {
		String out = "Day : " + this.day + "\n";
		for(Cover c : this.cover ){
			out += "Shift " + c.getShift().getType() + ", require " + c.getPrefNurses()+" nurses \n";
		}
		return out+="\n";
	}
	
	
	
}
