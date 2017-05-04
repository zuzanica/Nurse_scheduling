package algorithm;

import java.util.ArrayList;
import java.util.Arrays;

import algorithm.AllocationVector.Allocation;
import requirements.Cover;
import scheduler.Nurse;
import scheduler.Schedule;

public class AllocationVector {
	
	Schedule schedule;
	ArrayList<Allocation> x ;
	int fx;
	int[] softContraintsVolation;
	
	public class Allocation {	
		int n,d; // nurse, date
		String s; // shift type
		double weight = 0.0;
		
		public Allocation(int _n, int _d, String _s ){
			n = _n;
			d = _d;
			s = _s;
		}
		 
		public void setWeight(double _w){
			weight = _w;
		}
		
		@Override
	    public String toString(){
			return "("+this.n+", "+this.d+ ", "+ this.s + " | "+ this.weight +")";
		}
	}
	
	public AllocationVector(Schedule schedule){
		this.schedule = schedule;
		softContraintsVolation = new int[Schedule.SOFTCONTRAINTS];
		Arrays.fill(softContraintsVolation,new Integer(0));
		x = new ArrayList<Allocation>();
		fx = 0;
		
		createFeasubleRooster();
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
	
	public String toString(){
		String out = "[ ";
		for(int j = 0; j < x.size(); j++){
			
			out += x.get(j).toString() + " , ";
			if(j > 0 && j % 5 == 4){
				out += "\n";
			}
		}
		
		out += " | " + fx + " ]";
		return out;
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
		
		return ;
	}
	
	/**
	 * Fot all Soft contraint coutn Gs(x). From Gs(x) count total evaluate function fx.
	 * @param x : feasible rooster
	 * @return fx value
	 */
	protected int evaluateRooster(){
			
		getS1S2volations();
		
		getS3S4volations();
		
		// check S5,S6 constraint
		getMaxMinFreeDayVolations();
		
		for (int i = 0; i < softContraintsVolation.length; i++) {
			System.out.println("Total S" + i + " volation " + softContraintsVolation[i]);
		}
		
		return 0;
	}
	
	/**
	 * Count soft constraints S1, S2.
	 */
	private void getS1S2volations(){
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
			//System.out.println("Nurse " + n.id + " S1 volation " +  (scheduledAssigments - maxAssigmentns));
			//System.out.println("Nurse " + n.id + " S2 volation " +  (minAssigmentns - scheduledAssigments));
			// total cost for S1 constraint
			softContraintsVolation[0] += (n.getContract().getWeight1()* Math.max( scheduledAssigments - maxAssigmentns, 0));
			//total cost for S2 constraint
			softContraintsVolation[1] += (n.getContract().getWeight2()* Math.max( minAssigmentns - scheduledAssigments, 0));
		}
	}
	
	private void getS3S4volations(){			
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
				// at start of the day find minimal consecutive day volation
				// System.out.println("Day " +  day);
				for (int j = 0; j < minConsWorkTotal.length; j++) {
					int mincwd = schedule.getNurse(j).getContract().getMinconsecutiveworkingdays();
					if((consWorkDayNew[j] == 0) && (consWorkDayOld[j] < mincwd )){
						minConsWorkTotal[j] += consWorkDayOld[j];
					}
					//System.out.println("Nurse " +j + " S4 volation-" +  minConsWorkTotal[j] +"   new val-" + consWorkDayNew[j] + "    Old val-" +  consWorkDayOld[j] + "   min " + mincwd);
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
				//System.out.println("Nurse " +a.n + " S3 volation incerased at day" + day + " to " +  consWorkTotal[a.n]);
			}
		}
		
		//count total soft constraints
		for (int j = 0; j < minConsWorkTotal.length; j++) {
			softContraintsVolation[2] += consWorkTotal[j]; 
			softContraintsVolation[3] += minConsWorkTotal[j]; 
			//System.out.println("Nurse " +j+ " S4 volation-" +  minConsWorkTotal[j]);
		}
	}
	
	
	private void getMaxMinFreeDayVolations(){
		
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
		System.out.println("Nurse " +a.n + " S4 volation passed at day " + day +  "  from: " +  minConsWorkTotal[a.n]);
		minConsWorkTotal[a.n] -= (minConsWorkDayNew[a.n]-1);
		if(minConsWorkTotal[a.n] < 0) minConsWorkTotal[a.n] = 0;
		System.out.println(" to " +  minConsWorkTotal[a.n] + " by " + (minConsWorkDayNew[a.n]-1));
	}else{
		//System.out.println("Nurse " +a.n + " S4 volation passed at day" + day + " to " +  consWorkTotal[a.n]);
		minConsWorkTotal[a.n]++;
	}*/
	
	
}
