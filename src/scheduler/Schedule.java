package scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import requirements.Cover;
import requirements.CoverRequirements;

public class Schedule {
	String start = "";
	String end = "";
	
	ArrayList<String> skillsTypes = new ArrayList<String>();
	ArrayList<Shift> shiftsTypes = new ArrayList<Shift>();
	ArrayList<UnwantedPattern> unwantedPatterns = new ArrayList<UnwantedPattern>();
	ArrayList<Contract> contractsTypes = new ArrayList<Contract>();
	
	ArrayList<Nurse> nurses = new ArrayList<Nurse>();
	ArrayList<CoverRequirements> weeklyCoverRequirements = new ArrayList<CoverRequirements>() ;
	
	// schedule variables	
	public int nursesCount = 0;
	public int period = 0;
	public int allocationCount = 0;
	
	public Schedule(String _start, String _end){
		start = _start;
		end = _end;
	}
	
	public void initialize(){
		nursesCount = nurses.size();
		period = covertDateToInt(start, end); 
		allocationCount = getAllocCount();
	}
	
	public int covertDateToInt(String sDate, String eDate){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
        Date startDate = null;
        Date endDate = null;
		try {
			startDate = df.parse(sDate);
			endDate = df.parse(eDate);
			int days = (int) Math.abs((startDate.getTime()-endDate.getTime())/86400000);
			return days;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void addSkill(String s){
		skillsTypes.add(s);
	}
	
	public void addNurse(Nurse n) {
		nurses.add(n);
	}
	
	public void addShift(Shift s){
		shiftsTypes.add(s);
	}
	
	public void addContract(Contract c){
		contractsTypes.add(c);
	}
	
	public void addUnwantedPattern(UnwantedPattern up){
		unwantedPatterns.add(up);
	}
	
	public void addCoverRequirements(ArrayList<CoverRequirements> _week){
		weeklyCoverRequirements = _week;
	}
	
	public Contract getContract(int contractID){
		return contractsTypes.get(contractID);
	}
	
	public Shift getShift(String type){
		for(Shift s: shiftsTypes){
			if(s.getType().equals(type)){
				return s;
			}
		}
		return null;
	}
	
	public Nurse getNurse(int index) {
		return nurses.get(index);
	}
	
	public void addNurseFreeDay(int nurseId, String day){
		nurses.get(nurseId).addFreeDay(covertDateToInt(start, day));
	}
	
	public void addNurseFreeShift(int nurseId, String day, String st){
		nurses.get(nurseId).addFreeShift(covertDateToInt(start, day), st);
	}
	
	/**
	 * Returns total nurses count for all period.
	 * */
	public int getAllocCount(){
		int ac = 0;
		//get count for period selected by xml Cover requirements
		for(CoverRequirements cr: weeklyCoverRequirements){
			ac += cr.getDailyCoverCount();
		}
		// count full period interval
		int i = period / weeklyCoverRequirements.size(); // prediod/7 || 28/7 = 4 
		ac *= i; 
		//System.out.println("Alocation Count:" + ac);
		return ac;
	}
	
	public int getAllocCount(String day){
		int ac = 0;
		//get count for period selected by xml Cover requirements
		for(CoverRequirements cr: weeklyCoverRequirements){
			if(cr.getDay().equals(day)){
				ac += cr.getDailyCoverCount();
				break;
			}
		}
		//System.out.println("Day " + day + " allocation count:" + ac);
		return ac;
	}	
	
	public int getAllocCount(int i){
		int ac = 0;
		//get count for day i
		CoverRequirements cr = weeklyCoverRequirements.get(i);	
		ac += cr.getDailyCoverCount();
		
		//System.out.println("Day " + cr.getDay() + " allocation count:" + ac);
		return ac;
	}
	
	public ArrayList<Cover>  getCoversList(int i){
		//get covers for day i
		CoverRequirements cr = weeklyCoverRequirements.get(i);	
		return cr.getCoversList();
	}
}
