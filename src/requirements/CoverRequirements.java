package requirements;

import scheduler.Nurse;
import scheduler.Shift;

public class CoverRequirements {
	String day;
	Shift shift;
	int prefered;
	public CoverRequirements(String d, Shift s, int p){
		day = d;
		shift = s;
		prefered = p;
	}
	
}
