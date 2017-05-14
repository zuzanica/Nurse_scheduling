package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

import algorithm.Allocation;
import requirements.Cover;
import scheduler.Nurse;
import scheduler.Schedule;
import scheduler.Shift;

public class HarmonySearch {
	// constatnts set accortidn the study 
	static final int HMS = 1;
	static final int LOOPLIMIT = HMS*2;
	static final double HMCR = 0.99;
	static final int NI = 150000;
	static final double PAR = 0.01;
	static final double PAR1 = PAR/3;
	static final double PAR2 = 2*PAR/3;
	static final double PAR3 = PAR;
	
	ArrayList<AllocationVector> HM = new ArrayList<AllocationVector>(); 
	Schedule schedule;
	AllocationVector solution = null;
	
	public HarmonySearch(Schedule _schedule){
		this.schedule = _schedule;
		AllocationVector xWorst = null;
		AllocationVector xNew = null;
		// Step1. skipped, everything is initialized
		// Step2-part1 
		
		inirializeHM();
		Collections.sort(HM);
		
		for(int i = 0; i < HMS; i++){	
			System.out.println(HM.get(i).toString());
		}
		
		/*
		System.out.println(HM.get(HM.size()-1).getFxWeight());
		
		int ni = 0;
		while(ni < NI){
			if(ni%10000 == 0) System.out.println("Round:" + ni);
			
			ni++;
			// Step2-part2
			Collections.sort(HM);
			xWorst = HM.get(HM.size()-1);
			//for(int i = 0; i < HMS; i++){	
			//	System.out.println(HM.get(i).toString());
			//}
			// Step3. 
			xNew = improviseNewHarmony();
			// Step4.
			updateHM(xWorst, xNew);
			//for(int i = 0; i < HMS; i++){	
			//	System.out.println(HM.get(i).toString());
			//}
		} // Step5. repeat
		Collections.sort(HM);
		System.out.println("Harmony algorithm finishned.");
		
		for(int i = 0; i < HMS; i++){	
			System.out.println(HM.get(i).toString());
		}
		
		solution = HM.get(0);
		System.out.println("Best solution is: " + solution.getFxWeight());
		//System.out.println(solution.toString());
		 */
		/*
		System.out.println("Expected Solution looks like: ");
		test();
		*/
	}

	public int getSolution(){
		return solution.getFxWeight();
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
		/*
		//sort vector
		Collections.sort(HM);
		for(int i = 0; i < HMS; i++){	
			System.out.println(HM.get(i).toString());
		}
		*/
		
		System.out.println("HM initialization finished.");
		return;
	}
	
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
					//System.out.println("Rooseter round "+ i + " : " + newRooster.toString());
					// Memory Consideration 
					int controlVar = 0;
					Allocation a = null;
					do{
						controlVar++;
						//System.out.println("Rooseter round "+ i + " : " + newRooster.toString());
						int rooseterPos = schedule.getRandNum(0, HMS-1);
						//System.out.println("Iteration "+ i +" HMS pos: " + rooseterPos);
						AllocationVector historicalRooseterInHM = HM.get(rooseterPos);
						// select x from AllocationVector and Allocation i from x. 
						a = historicalRooseterInHM.getX().get(i).clone();
						//System.out.println("Add HM Allocation "+ a.toString());
						if(controlVar > LOOPLIMIT){
							//System.out.println("Bad vector start new"+ a.toString());
							skip = true;
							break;
						}
					}while(!newRooster.willBeFeasible(a));
					
					if(skip){
						break;
					}	
					//System.out.println("Add allocation: " + a.toString());
					//System.out.println("===============================================");
					// add allocation a into new rooster
					newRooster.addAllocation(a);
					
					// Pitch adjustments
					double randPAR = schedule.getRandNum();
					// 0 <= U(0,1) < PAR1
					if(randPAR < PAR1){ 
						//System.out.println("Historical val "+ a.toString());
						a = move(newRooster);
						//System.out.println("New val "+ a.toString());
						if(!newRooster.checkFeasibility()){
							System.out.println("===============================================");
							System.out.println("ERROR PAR1");
							System.out.println("===============================================");
							break;
						}
					}
					// PAR1 <= U(0,1) < PAR2
					else if(randPAR < PAR2){
						a = swapNurses(newRooster);
						if(!newRooster.checkFeasibility()){
							System.out.println("===============================================");
							System.out.println("ERROR PAR2");
							System.out.println("===============================================");
							break;
						}
						//System.out.println("new alloc "+ a.toString());
						//System.out.println("=============================================== ");
					}
					// PAR2 <= U(0,1) < PAR3
					
					else if(randPAR < 1){
						a = swapDays(newRooster);
						//System.out.println("new alloc "+ a.toString());
						//System.out.println("=============================================== ");
						if(!newRooster.checkFeasibility()){
							System.out.println("===============================================");
							System.out.println("ERROR PAR3");
							System.out.println("===============================================");
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
						//System.out.println("Add new Allocation "+ a.toString());
					}while(!newRooster.willBeFeasible(a));
					//System.out.println("Add allocation from Random cons: " + a.toString());
					newRooster.addAllocation(a);
				}
			} // rooster created
		}while(!newRooster.checkFeasibility() || (newRooster.getX().size() != schedule.allocationCount));

		newRooster.evaluateRooster();
		//System.out.println("New rooster:\n" + newRooster.toString());
		return newRooster;
	}
	
	public void updateHM(AllocationVector xWorst, AllocationVector xNew){
		if(xNew.getFxWeight() < xWorst.getFxWeight()){
			HM.add(xNew);
			HM.remove(xWorst);
			//System.out.println("Memory updated: " + xWorst.getFxWeight());
			//System.out.println("replaced with: " + xNew.getFxWeight());
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
		//TODO optimalizace prechadzat od konca
		// bez posledneho prvku, to je ta alokacia ktoru swapujem
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
			//find allocation with new nurse and switch this nurse with alloc.n
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
			//System.out.println("Selected swap "+ randAllocation.toString());
			ArrayList<Integer> avaliableNursesD1 = rooster.getAvaliableNurseList(randAllocation.d);
			// check if Nurse1 is assigned at selected day
			for(int i=0; i < avaliableNursesD1.size();  i++){
				// ak je sestra medzi dostupnymi je to ok.
				if(alloc.n == avaliableNursesD1.get(i) ){
					//System.out.println("Pass1" );
					again1 = false;
					break;
				}
			}
			if(!again1){
				ArrayList<Integer> avaliableNursesD2 = rooster.getAvaliableNurseList(alloc.d);
				// check if Nurse2 is assigned at new day
				for(int i=0; i < avaliableNursesD2.size(); i++){
					if(randAllocation.n == avaliableNursesD2.get(i) ){
						//System.out.println("Pass2" );
						again2 = false;
						break;
					}
				}
			}
			// osetrenie pre pripad ze nieje mozne vykonat swap
			if(MAXTRY == loop) {
				randAllocation =  rooster.getX().get(rooster.getX().size()-1);
				break;
			}
			
		}while( randAllocation.d == alloc.d || again1 || again2 );
		
		// swap
		//System.out.println("SWAP "+ alloc.toString() + " with " + randAllocation.toString());
		int nurseId = randAllocation.n;
		randAllocation.n = alloc.n;
		alloc.n = nurseId;
		//System.out.println("changed alloc "+ randAllocation.toString());
		return alloc; 
	 }
	
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
	
	//System.out.println("Tested rooster: " + tstX.toString());
	tstX.evaluateRooster();
	System.out.println("Tested rooster: " + tstX.toString());		
	}
	
	
}
