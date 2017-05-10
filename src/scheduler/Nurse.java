package scheduler;

import java.util.ArrayList;

public class Nurse {
	public int id;
	public Contract contract; 
	public Shift shift;
	ArrayList<String> skills;
	ArrayList<Integer> freeDayRequirements = new ArrayList<Integer>();
	ArrayList<FreeShift> freeShiftRequirements = new ArrayList<FreeShift>();
	String name; 
	
	public class FreeShift {
		int date;
		String shiftType;
		//int weight;
		
		public  FreeShift(int d, String st){
			date = d;
			shiftType = st;
		}
		
		public int getDate() {
			return date;
		}
		
		public String getShiftType() {
			return shiftType;
		}
		
		public boolean contains(int date, String shift){
			return date == this.date && shift.equals(this.shiftType) ? true : false;
		}
		
		public String toString() {
			String out = "(" + date + ", " + shiftType + ")";
			
			return out;
		}
		
		
		@Override
		public boolean equals(Object object)
		{
		    boolean isEqual= false;

		    if (object != null && object instanceof FreeShift)
		    {
		        isEqual = (this.date == ((FreeShift) object).date  && shiftType.equals(((FreeShift) object).shiftType) );
		    }

		    return isEqual;
		}

		@Override
		public int hashCode() {
		    return this.date;
		}
		
		
	}
	
	public Nurse(int _id, Contract c, ArrayList<String> _skills ){
		this.id = _id;
		contract = c;
		skills = _skills;
		
	}
	
	public void addFreeDay(int day) {
		freeDayRequirements.add(day);
	}
	
	public void addFreeShift(int d, String st) {
		freeShiftRequirements.add(new FreeShift(d, st));
	}
	
	public Contract getContract() {
		return contract;
	}
	
	public ArrayList<Integer> getFreeDayRequirements() {
		return freeDayRequirements;
	}
	public ArrayList<FreeShift> getFreeShiftRequirements() {
		return freeShiftRequirements;
	}

	@Override
	public String toString() {
		String out = "Nurse Id: " + this.id + "\n" +
					 "name : " + this.name + "\n" +
					 "contract id: " + this.contract.id + "\n" +
					 "skills : ";
					
		for(String s : this.skills ){
			out += s + " ";
		}
		out += "\n Free days: ";
		for(int d : this.freeDayRequirements ){
			out += d + " ";
		}
		out += "\n Free shifts: ";
		for(FreeShift fs : this.freeShiftRequirements ){
			out += fs.date + "-" + fs.shiftType + ", ";
		}
		
		return out+="\n";
	}
	
}