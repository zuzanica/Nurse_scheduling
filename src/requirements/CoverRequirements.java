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
	
	@Override
	public String toString() {
		String out = "Day : " + this.day + "\n";
		for(Cover c : this.cover ){
			out += "Shift " + c.getShift().getType() + ", require " + c.getPrefNurses()+" nurses \n";
		}
		return out+="\n";
	}
	
	
	
}
