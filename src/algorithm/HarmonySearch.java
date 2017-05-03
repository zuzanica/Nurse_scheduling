package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import algorithm.AllocationVector.Allocation;
import requirements.Cover;
import scheduler.Nurse;
import scheduler.Schedule;
import scheduler.Shift;

public class HarmonySearch {
	static final int HMS = 1;
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
			// generate feasible rooster and evaluate this rooster
			AllocationVector xi = new AllocationVector(schedule);
			xi.evaluateRooster();
			System.out.println(xi.toString());
			HM.add(xi);
		}
		//sort vector
		System.out.println("HM initialization finished.");
	}
	
	
}
