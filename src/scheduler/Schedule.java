package scheduler;

import java.util.ArrayList;

import requirements.Cover;
import requirements.CoverRequirements;

public class Schedule {
	String start = "";
	String end = "";
	int period = 0;
	
	ArrayList<String> skillsTypes = new ArrayList<String>();
	ArrayList<Shift> shifts = new ArrayList<Shift>();
	ArrayList<Contract> contracts = new ArrayList<Contract>();
	ArrayList<Nurse> nurses = new ArrayList<Nurse>();
	ArrayList<CoverRequirements> weeklyCoverRequirements = new ArrayList<CoverRequirements>() ;
	
	int nursesCount = nurses.size();
	
	public Schedule(String _start, String _end){
		start = _start;
		end = _end;
	}
	
	public void initialize(){
		
	}
	
	public void addSkill(String s){
		skillsTypes.add(s);
	}
	
	public void addNurse(Nurse n) {
		nurses.add(n);
	}
	
	public void addShift(Shift s){
		shifts.add(s);
	}
	
	public void addContract(Contract c){
		contracts.add(c);
	}
	
	public void addCoverRequirements(ArrayList<CoverRequirements> _week){
		weeklyCoverRequirements = _week;
	}
	
	public Contract getContract(int contractID){
		return contracts.get(contractID);
	}
	
	public Shift getShift(String type){
		for(Shift s: shifts){
			if(s.getType().equals(type)){
				return s;
			}
		}
		return null;
	}
	
}
