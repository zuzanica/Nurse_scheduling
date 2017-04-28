package requirements;

import java.util.ArrayList;
import java.util.stream.IntStream;

import requirements.Allocation;
import scheduler.Schedule;

public class HarmonySearch {
	static final int HMS = 10;
	static final double HMCR = 0.99;
	static final double PAR = 0.0;
	static final int NI = 1000;
	
	 ArrayList<ArrayList<Allocation>> HM = new ArrayList<ArrayList<Allocation>>(); 
	
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
			ArrayList<Allocation> xi = createAllocVector();
			HM.add(xi);
		}
		System.out.println("HM initialization finished.");
		
		//TODO count f(x) and sort vector
	}
	
	public ArrayList<Allocation> createAllocVector(){
		//get vector of feasible solutions (AllocationVector)
		ArrayList<Allocation> x = new ArrayList<Allocation>();
		int period = schedule.period / 7; // week = 7
		for(int i = 0; i < period; i++){	
			for(int j = 0; j < 7; j++){
				ArrayList<Cover> coverList = schedule.getCoversList(j);
				int nurseId = 0;
				int[] randN = randNurses(schedule.getAllocCount(j), schedule.nursesCount);
				for(int k = 0; k < coverList.size(); k++){
					Cover c = coverList.get(k);
					for(int l = 0; l < c.getPrefNurses(); l++){
						int day = 7*i+j; // week = 7 * period + day of week 
						Allocation a = new Allocation(randN[nurseId], day, c.getShift().getType());
						x.add(a);
						nurseId++;
					}
				}	
			}
		}
		System.out.print("[");
		for(int j = 0; j < schedule.allocationCount; j++){
			System.out.print(x.get(j).toString());
		}
		System.out.println("]");
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
	


}
