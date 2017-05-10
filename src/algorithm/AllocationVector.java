package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import algorithm.Allocation;
import requirements.Cover;
import scheduler.Nurse;
import scheduler.Schedule;

public class AllocationVector implements Comparable<Object>{
	
	Schedule schedule;
	ArrayList<Allocation> x ;
	int fx;
	int[] softContraintsVolation;
	
	public AllocationVector(Schedule schedule){
		this.schedule = schedule;
		softContraintsVolation = new int[Schedule.SOFTCONTRAINTS];
		Arrays.fill(softContraintsVolation,new Integer(0));
		x = new ArrayList<Allocation>();
		fx = 0;
		
		//createFeasubleRooster();
	}
	
	public void addAllocation(int _n, int _d, String _s){
		x.add(new Allocation(_n, _d,_s));
	}
	
	public void addAllocation(Allocation a){
		x.add(a);
	}
	
	public void setFxWeight(int _fx){
		fx = _fx;
	}
	
	public ArrayList<Allocation> getX(){
		return x;
	}
	
	public int getFxWeight(){
		return fx;
	}
	
	public void clear(){
		this.fx = 0;
		this.x.clear();
	}
	
	@Override
	public String toString(){
		String out = "[ ";
		for(int j = 0; j < x.size(); j++){
			
			out += x.get(j).toString() + " , ";
			if(j > 0 && j % 5 == 4){
				;//out += "\n";
			}
		}
		
		out += " | " + fx + " ]";
		return out;
	}
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof AllocationVector) {
			AllocationVector a = ((AllocationVector)o);
			if (this.fx == a.fx)
			{
			    return 0;
			}
			else if (this.fx > a.fx)
			{
			    return 1;
			}
			else
			{
			    return -1;
			}
                }
		return 0;
	}
	
	public void createFeasubleRooster(){
		//get vector of feasible solutions (AllocationVector)
		//int vectorSum = 0;
		int period = schedule.period / 7; // week = 7
		for(int i = 0; i < period; i++){	
			for(int j = 0; j < 7; j++){
				ArrayList<Cover> coverList = schedule.getCoversList(j);
				int nurseId = 0;
				//int validNurses = schedule.nursesCount;
				int[] randN = randNurses(schedule.getAllocCount(j), schedule.nursesCount);
				for(int k = 0; k < coverList.size(); k++){
					Cover c = coverList.get(k);
					for(int l = 0; l < c.getPrefNurses(); l++){
						int day = 7*i+j; // week = 7 * period + day of week 
						Allocation a = new Allocation(randN[nurseId], day, c.getShift().getType());
						// set weight for triplet(n,d,s)
						//a.setWeight(setAllocationWeight(a,validNurses));
						x.add(a);
						nurseId++;
						//vectorSum += a.weight;
						//validNurses -= 1;
					}
				}
			}
		}
		//x.setFxWeight(vectorSum);
		
		return;
	}
	
	public Allocation getNextFeasibleAllocation(int allocationPosition, int day, String shift){
		ArrayList<Integer> possibleNurses = new ArrayList<>();
		possibleNurses = initArray(possibleNurses, schedule.nursesCount);
		
		// iterate actual rooster x, and find all nurses scheduled at day "day"
		for(Allocation a : x){
			if(day == a.d ){
				possibleNurses.remove(Integer.valueOf(a.n));
			}
		}
		// get random nurse from array of possible nurses
		int randNurse = schedule.getRandNum(0, possibleNurses.size()-1);
		int nurseId = possibleNurses.get(randNurse);
		// return new allocation
		return new Allocation(nurseId, day, shift);
	}
	
	/**
	 * Check hard constraint H2 (one nurse has assigned only one shift per day).
	 * H1 is assumed as feasible.
	 * @return true/false
	 */
	public boolean checkFeasibility(){
		
		// sort
		Collections.sort(x);
		//System.out.println("Rooster: \n" + x.toString());
		ArrayList<Integer> assignedNurses = new ArrayList<>();
		
		int day = 0;
		for(int i = 0; i < x.size(); i++){	
			Allocation a = x.get(i);
			// new day starts, clear assigned nurses
			if(a.d != day){
				assignedNurses.clear();
				day = a.d;
			}
			//System.out.println( assignedNurses.toString());
			//check if nurse is already assigned at some day.  
			
			if(assignedNurses.contains(a.n)){
				System.out.println("Incorrcet rooster, nurse " + a.n + " in already assigned at day " + day);
				return false;
			}else{
				assignedNurses.add(a.n);
			}
		}
		
		//System.out.println("IS FEASIBLE.");
		//System.out.println("===============================================");
		return true;
	}
	
	/**
	 * Check if new allocation can be add into rooster
	 * @return true/false
	 */
	public boolean willBeFeasible(Allocation alloc){
		// sort
		Collections.sort(x);
		//System.out.println("Check allocation: " + alloc.toString());
		for(int i = 0; i < x.size(); i++){	
			Allocation a = x.get(i);
			// find same nurse assigned at same day
			if(a.d == alloc.d && alloc.n == a.n){
				//System.out.println("Incorrcet rooster, nurse " + a.n + " in already assigned at day " + a.d);
				return false;
			}
		}
		
		//System.out.println("ROOSTER WILL BE FEASIBLE.");
		return true;
	}
	
	/**
	 * Fot all Soft contraint coutn Gs(x). From Gs(x) count total evaluate function fx.
	 * @param x : feasible rooster
	 * @return fx value
	 */
	protected int evaluateRooster(){
		// check S1,S2 constraint	
		getS1S2violations();
		
		// check S3,S4 constraint
		getS3S4violations();
		
		// check S5,S6 constraint
		getMaxMinFreeDayVolations();
		
		//TODO S7 S9 S10 S12 
		
		for (int i = 0; i < softContraintsVolation.length; i++) {
			//System.out.println("Total S" + i + " violation " + softContraintsVolation[i]);
			fx +=  softContraintsVolation[i];
		}
		
		return 0;
	}
	
	/**
	 * Count soft constraints S1, S2.
	 */
	private void getS1S2violations(){
		// count Max,Min number of assigments
		// loop all nurses		
		for (int i = 0; i < schedule.nursesCount; i++) {
			Nurse n = schedule.getNurse(i);
			int maxAssigmentns = n.getContract().getMaxnumassignments();
			int minAssigmentns = n.getContract().getMinnumassignments();
			
			int scheduledAssigments = 0;
			
			//Count nurse assigned shifts
			for(Allocation a: x){
				if(a.n == n.id){
					scheduledAssigments++;
				}
			}
			//System.out.println("Nurse " + n.id + " S1 violation " +  (scheduledAssigments - maxAssigmentns));
			//System.out.println("Nurse " + n.id + " S2 violation " +  (minAssigmentns - scheduledAssigments));
			// total cost for S1 constraint
			softContraintsVolation[0] += (n.getContract().getWeight1()* Math.max( scheduledAssigments - maxAssigmentns, 0));
			//total cost for S2 constraint
			softContraintsVolation[1] += (n.getContract().getWeight2()* Math.max( minAssigmentns - scheduledAssigments, 0));
		}
	}
	
	private void getS3S4violations(){			
		int[] consWorkDayOld = new int[schedule.nursesCount];
		Arrays.fill(consWorkDayOld,new Integer(0));
		int[] consWorkDayNew = new int[schedule.nursesCount];
		Arrays.fill(consWorkDayNew,new Integer(0));
		
		// vector of nurses total consecutive day 
		int[] consWorkTotal = new int[schedule.nursesCount];
		Arrays.fill(consWorkTotal,new Integer(0));
		// vector of nurses total consecutive day 
		int[] minConsWorkTotal = new int[schedule.nursesCount];
		Arrays.fill(minConsWorkTotal,new Integer(0));
		
		//TODO sort x by day value
		int day = 0;
		for (int i = 0; i < x.size(); i++) {
			Allocation a = x.get(i);
			
			if(a.d != day ){
				day = a.d;
				// at start of the day find minimal consecutive day violation
				// System.out.println("Day " +  day);
				for (int j = 0; j < minConsWorkTotal.length; j++) {
					int mincwd = schedule.getNurse(j).getContract().getMinconsecutiveworkingdays();
					if((consWorkDayNew[j] == 0) && (consWorkDayOld[j] < mincwd )){
						minConsWorkTotal[j] += consWorkDayOld[j];
					}
					//System.out.println("Nurse " +j + " S4 violation-" +  minConsWorkTotal[j] +"   new val-" + consWorkDayNew[j] + "    Old val-" +  consWorkDayOld[j] + "   min " + mincwd);
				}
				//count consecutive work days
				consWorkDayOld = consWorkDayNew.clone();
				Arrays.fill(consWorkDayNew,new Integer(0));
			}
			
			// add next working dayt to nurse a.n
			consWorkDayNew[a.n] = consWorkDayOld[a.n] + 1;
			int maxcwd = schedule.getNurse(a.n).getContract().getMaxconsecutiveworkingdays();
			if(consWorkDayNew[a.n] > maxcwd){
				consWorkTotal[a.n]++;
				//System.out.println("Nurse " +a.n + " S3 violation incerased at day" + day + " to " +  consWorkTotal[a.n]);
			}
		}
		
		//count total soft constraints
		for (int j = 0; j < minConsWorkTotal.length; j++) {
			softContraintsVolation[2] += consWorkTotal[j]; 
			softContraintsVolation[3] += minConsWorkTotal[j]; 
			//System.out.println("Nurse " +j+ " S4 violation-" +  minConsWorkTotal[j]);
		}
	}
	
	
	private void getMaxMinFreeDayVolations(){
		int[] freeWorkDaysOld = new int[schedule.nursesCount];
		Arrays.fill(freeWorkDaysOld,new Integer(0));
		int[] freeWorkingDaysNew = new int[schedule.nursesCount];
		Arrays.fill(freeWorkingDaysNew,new Integer(0));
		
		// vector of nurses with maximal and minimal free days in row
		int[] maxFreeDays = new int[schedule.nursesCount];
		Arrays.fill(maxFreeDays,new Integer(0));
		// vector of nurses total consecutive day 
		int[] minFreeDays = new int[schedule.nursesCount];
		Arrays.fill(minFreeDays,new Integer(0));
		
		ArrayList<Integer> freeNurses = new ArrayList<>();
		freeNurses = initArray(freeNurses, schedule.nursesCount);
		
		// TODO sort x by day value
		int day = 0;
		// loop feasible rooster represented like Allocation vector x
		for (int i = 0; i < x.size(); i++) {
			Allocation a = x.get(i);
			// at startof new day starts
			if(a.d != day){
				day = a.d;
				//System.out.println("Day " + day);
				// iterate all nurses that does not work previous day
				for (int j = 0; j < freeNurses.size(); j++) {
					int nurse = freeNurses.get(j);
					freeWorkingDaysNew[nurse] = freeWorkDaysOld[nurse] + 1;
					
					// count maximum consecutive free days
					int maxcfd = schedule.getNurse(nurse).getContract().getMaxconsecutivefreedays();
					if(freeWorkingDaysNew[nurse] > maxcfd){
						maxFreeDays[nurse]++;
						//System.out.println("Nurse " + nurse + " has max incerased to  " +  maxFreeDays[nurse]);
					}
					//System.out.println("Nurse " + nurse + " at day " + day + " has max consequiteve free days " +  maxFreeDays[nurse]);
				}
				//for all nurses
				for (int j = 0; j < minFreeDays.length; j++) {
					//System.out.println("Nurse " + j + " has new value  " +  freeWorkingDaysNew[j] + "    and  old value " + freeWorkDaysOld[j] );
					// count minimum consecutive free days
					int mincfd = schedule.getNurse(j).getContract().getMinconsecutivefreedays();
					if((freeWorkingDaysNew[j] == 0) && (freeWorkDaysOld[j] < mincfd )){
						minFreeDays[j] += freeWorkDaysOld[j];
						//System.out.println("Nurse " + j + " has min incerased to  " +  minFreeDays[j]);
					}
				}
				// reinitialize nurses array
				freeNurses = initArray(freeNurses, schedule.nursesCount);
				// copy new array into old
				freeWorkDaysOld = freeWorkingDaysNew.clone();
				Arrays.fill(freeWorkingDaysNew,new Integer(0));
				/*for (int j = 0; j < minFreeDays.length; j++) {
					System.out.println("Nurse " + j  + " has min consequiteve free days " +  minFreeDays[j]);
				}*/
			}
			//remove nurse if has assigned shift
			freeNurses.remove(Integer.valueOf(a.n));
		}
		// count S4 S5 violation for all nurses
		for (int j = 0; j < maxFreeDays.length; j++) {
			softContraintsVolation[4] += maxFreeDays[j]; 
			softContraintsVolation[5] += minFreeDays[j]; 
			//System.out.println("Nurse " +j+ " S4 violation-" +  minConsWorkTotal[j]);
		}
	}	
	
	private ArrayList<Integer> initArray(ArrayList<Integer> list, int size){
		
		list.clear();
		for (int i = 0; i < size; i++) {
			list.add(i);
		}
		return list;
	}
	
	private int[] randNurses(int count, int max ){
		
		ArrayList<Integer> fullList = new ArrayList<>();
		for (int i = 0; i < max; ++i) {
			fullList.add(i);
		}

		int[] newfiled = new int[count]; 
		for (int i = 0; i < count; ++i) {
			int r = (int) (Math.random() * fullList.size());
			//System.out.println("rabd index " + r+" ");
			newfiled[i] = fullList.remove(r);
			//System.out.print(newfiled[i]+" ");
		}
		//System.out.println();
		return newfiled;
	}
	
	public int randNurse(ArrayList<Integer> avaliableNurses){
		int n, index;
		index = schedule.getRandNum(0, avaliableNurses.size()-1);
		return avaliableNurses.get(index);
	}
	
	ArrayList<Integer> getAvaliableNurseList(int day){
		ArrayList<Integer> avaliableNurses = new ArrayList<>();
		avaliableNurses = initArray(avaliableNurses, schedule.nursesCount);
		
		for (int i = 0; i < x.size(); i++) {
			Allocation a = x.get(i);
			// find working nurses at required day 
			if(a.d == day){
				avaliableNurses.remove(Integer.valueOf(a.n));
			}
		}
		//System.out.println("Avaliable nurses at day "+day+ " are "+ avaliableNurses.toString());
		return avaliableNurses;
	}
	 
		double setAllocationWeight(Allocation a,int validNurses){
			double weight=0;
			// Night shift is alwais type N, and has weight 100
			if(a.s.equals("N")){
				weight += 100; 
				//System.out.println("Sum " + weight);
			}
			// Weekend shift is alwais 5th or 6th day (week is 0..6) 
			if((a.d % 7 == 5) || (a.d % 7 == 6)){
				weight += 50;
				//System.out.println("Sum " + weight);
			}
			// (Number of valid Nurses / Total nurses) * 70 
			double s = (double)validNurses /(schedule.nursesCount);
			weight += s * 70;
			//System.out.println("Sum "+ weight);
			// (schedule end data - shift date) * 20
			weight += (schedule.period - a.d) * 20; 
			//System.out.println("Sum " + weight);
			return weight;
		
		}	 
	 
	
	//if schedule pass soft constraints
	/*if(minConsWorkDayNew[a.n] > mcfd){
		System.out.println(" MCFD " +mcfd);
		System.out.println("Nurse " +a.n + " S4 violation passed at day " + day +  "  from: " +  minConsWorkTotal[a.n]);
		minConsWorkTotal[a.n] -= (minConsWorkDayNew[a.n]-1);
		if(minConsWorkTotal[a.n] < 0) minConsWorkTotal[a.n] = 0;
		System.out.println(" to " +  minConsWorkTotal[a.n] + " by " + (minConsWorkDayNew[a.n]-1));
	}else{
		//System.out.println("Nurse " +a.n + " S4 violation passed at day" + day + " to " +  consWorkTotal[a.n]);
		minConsWorkTotal[a.n]++;
	}*/
	
	
}
