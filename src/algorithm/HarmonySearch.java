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
	// constatnts set accortidn the study 
	static final int HMS = 10;
	static final double HMCR = 0.99;
	static final int NI = 1000;
	static final double PAR = 0.01;
	static final double PAR1 = PAR/3;
	static final double PAR2 = 2*PAR/3;
	static final double PAR3 = PAR;
	
	
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
				System.out.println("Rooseter round "+ i + " : " + newRooster.toString());
				//while(rooster is feasible)
				// Memory Consideration 
					int rooseterPos = schedule.getRandNum(0, HMS-1);
					//System.out.println("Iteration "+ i +" HMS pos: " + rooseterPos);
					AllocationVector historicalRooseterInHM = HM.get(rooseterPos);
					// select x from AllocationVector and Allocation i from x. 
					Allocation a = historicalRooseterInHM.getX().get(i);
					
					//System.out.println("Add HM Allocation "+ a.toString());
				// do TODO check feasibility
				// Pitch adjustments
				double randPAR = schedule.getRandNum();
				// 0 <= U(0,1) < PAR1
				if(randPAR < PAR1){ 
					//System.out.println("Historical val "+ a.toString());
					a = move(a, newRooster);
					//System.out.println("New val "+ a.toString());
				}
				// PAR1 <= U(0,1) < PAR2
				else if(randPAR < PAR2){
					a = swapNurses(a, newRooster); //TODO overit spravnot s fesible roosterom
					//System.out.println("new alloc "+ a.toString());
					//System.out.println("=============================================== ");
				}
				// PAR2 <= U(0,1) < PAR3
				else if(randPAR < 1){
					a = swapDays(a, newRooster);
					//System.out.println("new alloc "+ a.toString());
					//System.out.println("=============================================== ");
				}
				// PAR3 <= U(0,1) <= 1
				else{
					; // do nothing
				}
				
				// add allocation a into new rooster
				newRooster.addAllocation(a);
				
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
	
	/**
	 * Switch shift allocation.s at day allocation.d with any other feasible nurse. 
	 * New nurse is selected according actually formed rooster.   
	 * @param a, rooster
	 * @return allocation with new nurse 
	 */
	Allocation move(Allocation a, AllocationVector rooster){
		int newNurseId;
		ArrayList<Integer> avaliableNurses  = rooster.getAvaliableNurseList(a.d);
		// get new random nurse from list of alloc nurses
		do{
			newNurseId = rooster.randNurse(avaliableNurses);
		} while(newNurseId == a.n); 
		return rooster.new Allocation(newNurseId, a.d, a.s);
	}
	
	/**
	 * Switch shift with another nurse at day a.d
	 * @param a
	 * @param rooster
	 * @return
	 */
	Allocation swapNurses(Allocation alloc, AllocationVector rooster){
		int nurseId = alloc.n;
		ArrayList<Integer> availableNurses = new ArrayList<>();
		for (int i = 0; i < rooster.x.size(); i++) {
			Allocation a = rooster.x.get(i);
			// find all working nurses at day a.d 
			if(a.d == alloc.d){
				availableNurses.add(a.n);
			}
		}
		// select random nurse from available
		do{
			if(availableNurses.size() > 0){
				nurseId= rooster.randNurse(availableNurses);
			}else{
				break;
			}		
		} while( nurseId == alloc.n );
		
		for (int i = 0; i < rooster.x.size(); i++) {
			Allocation a = rooster.x.get(i);
			//find allocation with new nurse and switch this nurse with alloc.n
			if(a.d == alloc.d && a.n == nurseId ){
				//TODO overit spravnost na feasible roostery
				//System.out.println("SWAP "+ alloc.toString() + " with " + a.toString());
				a.n = alloc.n;
				//System.out.println("changed alloc "+ a.toString());
			}
		}
		return rooster.new Allocation(nurseId, alloc.d, alloc.s);
	}
	
	/**
	 * Swap nurse in newly created allocation with nurse in allocation from random previous day,
	 * stored in rooster. 
	 * @param alloc
	 * @param rooster
	 * @return swapped allocation;
	 */
	Allocation swapDays(Allocation alloc, AllocationVector rooster){
		int lastAllocation = rooster.x.size();
		// check if there are enough allocation in rooster
		if(lastAllocation < schedule.getAllocCount(0)) 
			return rooster.new Allocation(alloc.n, alloc.d, alloc.s); 
		
		// select random allocation with different day from rooster, 
		Allocation randAllocation = rooster.getX().get(0);
		do 
			randAllocation = rooster.getX().get(schedule.getRandNum(0, lastAllocation-1));
		while(randAllocation.d == alloc.d);
		
		// swap
		//System.out.println("SWAP "+ alloc.toString() + " with " + randAllocation.toString());
		int nurseId = randAllocation.n;
		randAllocation.n = alloc.n;
		//System.out.println("changed alloc "+ randAllocation.toString());
		return rooster.new Allocation(nurseId, alloc.d, alloc.s); 
	 }
	
}
