package scheduler;

import java.util.ArrayList;

public class UnwantedPattern{ 

	ArrayList<Pattern> patterns;
	
	public class Pattern{ 
		String shiftType = "";
		String day = "";
		
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
		
		@Override
		public String toString() {
			return "[ " + this.day + ", "+ this.shiftType + " ]";
		}
	}
	
	public UnwantedPattern() {
		patterns = new ArrayList<Pattern>();
	}
	
	public void addPattern(Pattern p){
		patterns.add(p);
	}
	
	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}
}
