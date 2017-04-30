package algorithm;

import java.util.ArrayList;
import java.util.stream.IntStream;

import algorithm.AllocationVector.Allocation;
import requirements.Cover;
import scheduler.Schedule;
import scheduler.Shift;

public class HarmonySearch {
	static final int HMS = 5;
	static final double HMCR = 0.99;
	static final double PAR = 0.0;
	static final int NI = 1000;
	
	 ArrayList<AllocationVector> HM = new ArrayList<AllocationVector>(); 
	
	Schedule schedule;
	
	public HarmonySearch(Schedule _schedule){
		this.schedule = _schedule;
		// Step1. skipped, everything is initialized
		// Step2. 
		inirializeHM();
	}
		
	public void inirializeHM(){
		//get vector of feasible solutions (AllocationVector)
		
		for(int i = 0; i < HMS; i++){	
			AllocationVector xi = createAllocVector();
			System.out.println(xi.toString());
			//TODO count f(x)
			HM.add(xi);
		}
		//sort vector
		System.out.println("HM initialization finished.");
	}
	
	public AllocationVector createAllocVector(){
		//get vector of feasible solutions (AllocationVector)
		AllocationVector x = new AllocationVector();
		int vectorSum = 0;
		int period = schedule.period / 7; // week = 7
		for(int i = 0; i < period; i++){	
			for(int j = 0; j < 7; j++){
				ArrayList<Cover> coverList = schedule.getCoversList(j);
				int nurseId = 0;
				int validNurses = schedule.nursesCount;
				int[] randN = randNurses(schedule.getAllocCount(j), schedule.nursesCount);
				for(int k = 0; k < coverList.size(); k++){
					Cover c = coverList.get(k);
					for(int l = 0; l < c.getPrefNurses(); l++){
						int day = 7*i+j; // week = 7 * period + day of week 
						Allocation a = x.new Allocation(randN[nurseId], day, c.getShift().getType());
						// set weight for triplet(n,d,s)
						a.setWeight(setAllocationWeight(a,validNurses));
						x.addAllocation(a);
						nurseId++;
						vectorSum += a.weight;
						validNurses -= 1;
					}
				}
			}
		}
		x.setFxWeight(vectorSum);
		
		return x;
	}
	
	public int[] randNurses(int count, int max ){
		
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

}
