package scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import requirements.Cover;
import requirements.CoverRequirements;

public class Schedule {
	public static final int SOFTCONTRAINTS = 15; 
	private Random rand; 	
	
	String start = "";
	String end = "";
	
	ArrayList<String> skillsTypes = new ArrayList<String>();
	ArrayList<Shift> shiftsTypes = new ArrayList<Shift>();
	ArrayList<UnwantedPattern> unwantedPatterns = new ArrayList<UnwantedPattern>();
	ArrayList<Contract> contractsTypes = new ArrayList<Contract>();
	
	ArrayList<Nurse> nurses = new ArrayList<Nurse>();
	// nurse requirements for specific day or shift
	ArrayList<CoverRequirements> weeklyCoverRequirements = new ArrayList<CoverRequirements>() ;
	
	// schedule variables	
	public int nursesCount = 0;
	// number of differenet shifts
	public int shiftTypeCount = 0;
	// scheduling period in days (example 0-28)
	public int period = 0;
	// total shifts type in period 
	public int allocationCount = 0;
	
	public Schedule(String _start, String _end){
		start = _start;
		end = _end;
	}
	
	public void initialize(){
		nursesCount = nurses.size();
		shiftTypeCount = shiftsTypes.size();
		//period = 7;
		period = covertDateToInt(start, end); 
		allocationCount = getAllocCount();
		
		// create Random generator
		long currentTime = System.currentTimeMillis();
	    rand = new Random(currentTime);
		
	}
	
	public int covertDateToInt(String sDate, String eDate){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
        Date startDate = null;
        Date endDate = null;
		try {
			startDate = df.parse(sDate);
			endDate = df.parse(eDate);
			int days = (int) Math.abs((startDate.getTime()-endDate.getTime())/86400000);
			return days+1;
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
	
	/**
	 * 
	 * @param day
	 * @return Allocation count at all days day
	 */
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
	
	/**
	 * 
	 * @param i
	 * @return Allocation count at day i
	 */
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
	
	public int getRandNum(int min, int max){
        int result = rand.nextInt((max - min) + 1) + min;
        return result;
	}
	
	public double getRandNum(){
        double result = rand.nextDouble();
        return result;
	}
	
}
