package scheduler;

import java.util.ArrayList;

public class Nurse {
	public int id;
	public Contract contract; 
	public Shift shift;
	ArrayList<String> skills;
	String name; 
	
	public Nurse(int _id, Contract c, ArrayList<String> _skills ){
		this.id = _id;
		contract = c;
		skills = _skills;
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
		return out+="\n";
	}
	
}