package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import algorithm.AllocationVector.Allocation;
import requirements.Cover;
import scheduler.Nurse;
import scheduler.Schedule;
import scheduler.Shift;

public class HarmonySearch {
	static final int HMS = 10;
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
		improviseNewHarmony();
	}
		
	public void inirializeHM(){
		//get vector of feasible solutions (AllocationVector)
		
		for(int i = 0; i < HMS; i++){	
			// generate feasible rooster and evaluate this rooster
			AllocationVector xi = new AllocationVector(schedule);
			xi.createFeasubleRooster();
			xi.evaluateRooster();
			HM.add(xi);
		}
		
		//sort vector
		Collections.sort(HM);
		for(int i = 0; i < HMS; i++){	
			System.out.println(HM.get(i).toString());
		}
		
		System.out.println("HM initialization finished.");
	}
	
	public void improviseNewHarmony(){
		// x'
		AllocationVector newRooster = new AllocationVector(schedule);
		for (int i = 0; i < schedule.allocationCount; i++) {
			double randHMCR = schedule.getRandNum();
			if(randHMCR < HMCR){
				//while(rooster is feasible)
				// Memory Consideration 
				int rooseterPos = schedule.getRandNum(0, HMS-1);
				//System.out.println("Iteration "+ i +" HMS pos: " + rooseterPos);
				AllocationVector historicalRooseterInHM = HM.get(rooseterPos);
				// select x from AllocationVector and Allocation i from x. 
				Allocation a = historicalRooseterInHM.getX().get(i);
				newRooster.addAllocation(a);
				//System.out.println("Add HM Allocation "+ a.toString());
				// do TODO check feasibility
			} else{
				// Random Consideration
				// get feasible Allocation
				// find out Allocation position (day and shift);
				Allocation tmpAllocation = HM.get(0).getX().get(i);
				int day = tmpAllocation.d;
				String shift = tmpAllocation.s; 
				Allocation a = newRooster.getNextFeasibleAllocation(i, day, shift);
				newRooster.addAllocation(a);
				//System.out.println("Add new Allocation "+ a.toString());
			}
			// TODO check feasibility.
		}
		newRooster.evaluateRooster();
		System.out.println("New rooster:\n" + newRooster.toString());
		
	}
	
}
