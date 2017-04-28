package requirements;

import java.util.ArrayList;

import scheduler.Nurse;
import scheduler.Shift;

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
	
	/*public Cover getCover(String shitType){
		for(Cover c : cover ){
			if(c.getShift().getType().equals(shitType)){
				return c;
			}
		}
		return null;
	}*/
	
	
	@Override
	public String toString() {
		String out = "Day : " + this.day + "\n";
		for(Cover c : this.cover ){
			out += "Shift " + c.getShift().getType() + ", require " + c.getPrefNurses()+" nurses \n";
		}
		return out+="\n";
	}
	
	
	
}
