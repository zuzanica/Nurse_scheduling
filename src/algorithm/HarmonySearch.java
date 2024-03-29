package algorithm;

import java.util.ArrayList;
import java.util.Collections;

import algorithm.Allocation;
import scheduler.Schedule;

/**
 * Harmony search algorithm.
 * @author Studena Zuzana
 *
 */
public class HarmonySearch {
	// constants set according the study 
	static final int HMS = 10;
	static final int LOOPLIMIT = HMS*2;
	static final double HMCR = 0.99;
	static final int NI = 100000;
	static final double PAR = 0.01;
	static final double PAR1 = PAR/3;
	static final double PAR2 = 2*PAR/3;
	static final double PAR3 = PAR;
	
	ArrayList<AllocationVector> HM = new ArrayList<AllocationVector>(); 
	Schedule schedule;
	double avegageInitializeViolation = 0.0;
	AllocationVector solution = null;
	
	public HarmonySearch(Schedule _schedule){
		this.schedule = _schedule;
		AllocationVector xWorst = null;
		AllocationVector xNew = null;
		// Step1. skipped, everything is initialized
		// Step2-part1 
		initializeHM();
		Collections.sort(HM);
		
		int ni = 0;
		while(ni < NI){
			if(ni%10000 == 0)
				System.out.println("Round:" + ni);
			ni++;
			// Step2-part2
			Collections.sort(HM);
			xWorst = HM.get(HM.size()-1);
			// Step3. 
			xNew = improviseNewHarmony();
			// Step4.
			updateHM(xWorst, xNew);
		} // Step5. repeat
		
		Collections.sort(HM);
		solution = HM.get(0);
		schedule.setSolution(solution);
		System.out.println("Harmony algorithm finishned.");
		System.out.println("Best solution is: " + solution.getFxWeight());
		
		/*
		System.out.println("Expected Solution looks like: ");
		test();
		*/
	}

	public int getSolution(){
		return solution.getFxWeight();
	}
	
	public double getInitViolation(){
		return avegageInitializeViolation;
	}
	
	/**
	 * Initialize HM with Allcations vectors, witch represents feasible roosters.
	 */
	public void initializeHM(){
		// get vector of feasible solutions (AllocationVector)
		int violation = 0;
		for(int i = 0; i < HMS; i++){	
			// generate feasible rooster and evaluate this rooster
			AllocationVector xi = new AllocationVector(schedule);
			xi.createFeasubleRooster();
			xi.evaluateRooster();
			violation += xi.getFxWeight();
			HM.add(xi);
		}
		avegageInitializeViolation = (double) violation / HMS;
		System.out.println("HM initialization finished.");
		System.out.println("Average initialized violation: " + avegageInitializeViolation);
		return;
	}
	
	/**
	 * 3. Step of Harmony algorithm, finds new rooster.
	 * @return
	 */
	public AllocationVector improviseNewHarmony(){
		// x'
		AllocationVector newRooster = new AllocationVector(schedule);
		boolean skip;
		//do while don t have feasible rooster
		do{
			skip = false;
			//clear rooster from previous round
			newRooster.clear();
			for (int i = 0; i < schedule.allocationCount; i++) {
				double randHMCR = schedule.getRandNum();
				if(randHMCR < HMCR){
					// Memory Consideration 
					int controlVar = 0;
					Allocation a = null;
					do{
						controlVar++;
						int rooseterPos = schedule.getRandNum(0, HMS-1);
						AllocationVector historicalRooseterInHM = HM.get(rooseterPos);
						// select x from AllocationVector and Allocation i from x. 
						a = historicalRooseterInHM.getX().get(i).clone();
						if(controlVar > LOOPLIMIT){
							skip = true;
							break;
						}
					}while(!newRooster.willBeFeasible(a));
					
					if(skip){
						break;
					}	
					// add allocation a into new rooster
					newRooster.addAllocation(a);
					
					// Pitch adjustments
					double randPAR = schedule.getRandNum();
					// 0 <= U(0,1) < PAR1
					if(randPAR < PAR1){ 
						a = move(newRooster);
						if(!newRooster.checkFeasibility()){
							System.err.println("===============================================");
							System.err.println("ERROR PAR1");
							System.err.println("===============================================");
							break;
						}
					}
					// PAR1 <= U(0,1) < PAR2
					else if(randPAR < PAR2){
						a = swapNurses(newRooster);
						if(!newRooster.checkFeasibility()){
							System.err.println("===============================================");
							System.err.println("ERROR PAR2");
							System.err.println("===============================================");
							break;
						}
					}
					// PAR2 <= U(0,1) < PAR3
					
					else if(randPAR < 1){
						a = swapDays(newRooster);
						if(!newRooster.checkFeasibility()){
							System.err.println("===============================================");
							System.err.println("ERROR PAR3");
							System.err.println("===============================================");
							break;
						}
					}
					// PAR3 <= U(0,1) <= 1
					else{
						; // do nothing
					}
					
				} else{
					// Random Consideration
					Allocation a;
					do{
						// get feasible Allocation
						// find out Allocation position (day and shift);
						Allocation tmpAllocation = HM.get(0).getX().get(i);
						int day = tmpAllocation.d;
						String shift = tmpAllocation.s; 
						a = newRooster.getNextFeasibleAllocation(i, day, shift);
					}while(!newRooster.willBeFeasible(a));
					newRooster.addAllocation(a);
				}
			} // rooster created
		}while(!newRooster.checkFeasibility() || (newRooster.getX().size() != schedule.allocationCount));

		newRooster.evaluateRooster();
		return newRooster;
	}
	
	/**
	 * 4. Step of Harmony search algorithm. 
	 * @param xWorst
	 * @param xNew
	 */
	public void updateHM(AllocationVector xWorst, AllocationVector xNew){
		if(xNew.getFxWeight() < xWorst.getFxWeight()){
			HM.add(xNew);
			HM.remove(xWorst);
		}
		return;
	}
	
	/**
	 * Switch shift allocation.s at day allocation.d with any other feasible nurse. 
	 * New nurse is selected according actually formed rooster.   
	 * @param a, rooster
	 * @return allocation with new nurse 
	 */
	Allocation move(AllocationVector rooster){
		int newNurseId;
		Allocation a = rooster.getX().get(rooster.getX().size()-1);
		ArrayList<Integer> avaliableNurses  = rooster.getAvaliableNurseList(a.d);
		// get new random nurse from list of alloc nurses
		do{
			newNurseId = rooster.randNurse(avaliableNurses);
		} while(newNurseId == a.n); 
		a.n = newNurseId;
		return a;
	}
	
	/**
	 * Switch shift with another nurse at day a.d
	 * @param a
	 * @param rooster
	 * @return
	 */
	Allocation swapNurses(AllocationVector rooster){
		// get last added Allocation from rooster
		Allocation alloc = rooster.getX().get(rooster.getX().size()-1);
		int nurseId = alloc.n;
		ArrayList<Integer> availableNurses = new ArrayList<>();
		// except last value, what is allocation witch will be swapped
		for (int i = 0; i < rooster.x.size()-1; i++) {
			Allocation a = rooster.x.get(i);
			// find all working nurses at day a.d 
			if(a.d == alloc.d){
				availableNurses.add(a.n);
			}
		}
		// select random nurse from available
		if(availableNurses.size() > 0){
			nurseId= rooster.randNurse(availableNurses);
		}
		
		for (int i = 0; i < rooster.x.size(); i++) {
			Allocation a = rooster.x.get(i);
			// find allocation with new nurse and switch this nurse with alloc.n
			if(a.d == alloc.d && a.n == nurseId ){
				a.n = alloc.n;
			}
		}
		alloc.n = nurseId;
		return alloc;
	}
	
	/**
	 * Swap nurse in newly created allocation with nurse in allocation from random previous day,
	 * stored in rooster. 
	 * @param alloc
	 * @param rooster
	 * @return swapped allocation;
	 */
	Allocation swapDays(AllocationVector rooster){
		Allocation alloc = rooster.getX().get(rooster.getX().size()-1);
		int lastAllocationIndex = rooster.x.size();
		// check if there are enough allocation in rooster
		if(lastAllocationIndex <= schedule.getAllocCount(0)) 
			return alloc;
		
		// select random allocation with different day from rooster, 
		Allocation randAllocation = rooster.getX().get(0);
		int MAXTRY = 5; 
		int loop = 0;
		boolean again1 = true, again2 = true; 
		do{ 
			again1 = true;
			again2 = true; 
			loop++;
			randAllocation = rooster.getX().get(schedule.getRandNum(0, lastAllocationIndex-1));
			ArrayList<Integer> avaliableNursesD1 = rooster.getAvaliableNurseList(randAllocation.d);
			// check if Nurse1 is assigned at selected day
			for(int i=0; i < avaliableNursesD1.size();  i++){
				// if nurse is in available list 
				if(alloc.n == avaliableNursesD1.get(i) ){
					again1 = false;
					break;
				}
			}
			if(!again1){
				ArrayList<Integer> avaliableNursesD2 = rooster.getAvaliableNurseList(alloc.d);
				// check if Nurse2 is assigned at new day
				for(int i=0; i < avaliableNursesD2.size(); i++){
					if(randAllocation.n == avaliableNursesD2.get(i) ){
						again2 = false;
						break;
					}
				}
			}
			if(MAXTRY == loop) {
				randAllocation =  rooster.getX().get(rooster.getX().size()-1);
				break;
			}
			
		}while( randAllocation.d == alloc.d || again1 || again2 );
		
		// swap
		int nurseId = randAllocation.n;
		randAllocation.n = alloc.n;
		alloc.n = nurseId;
		return alloc; 
	}
	
	/**
	 * Test function with static rooster 
	 */
	public void test(){
		AllocationVector tstX = new AllocationVector(schedule);
		int k = 0;
		int[] n = {3,4,1,0,4,5,3,2,1,0,5,4,1,2,1,0,5,4,3,2,0,1,4,3,3,2,3,0,1,5,2,3,1,0,5};
		for(int i = 0; i < 7; i++){
			for(int j = 0; j < 2; j++){
				tstX.getX().add(new Allocation(n[k],i,"E"));
				k++;
			}
				
			for(int j = 0; j < 2; j++){
				tstX.getX().add(new Allocation(n[k],i,"L"));
				k++;
			}
			tstX.getX().add(new Allocation(n[k],i,"N"));
			k++;
		}
		
		tstX.evaluateRooster();
		System.out.println("Tested rooster: " + tstX.toString());		
	}
	
	
}
