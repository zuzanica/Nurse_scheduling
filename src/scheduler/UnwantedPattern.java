package scheduler;

import java.util.ArrayList;

public class UnwantedPattern {

	int id;
	ArrayList<Pattern> patterns;
	
	public class Pattern{ 
		String shiftType = "";
		String day = ""; // -1 represents ANY day 
		
		public Pattern(String _st, String _day ){
			shiftType = _st;
			day = _day;
		}
		public String getShiftType() {
			return shiftType;
		}
		public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
		public void setShiftType(String shiftType) {
			this.shiftType = shiftType;
		}
	}
	
	public UnwantedPattern(int _id){
		 patterns = new ArrayList<Pattern>();
		 id = _id;
	}
	
	public void addPattern(Pattern p ){
		patterns.add(p);
	}
	
	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}
	
	public int getId() {
		return id;
	}
	
}
