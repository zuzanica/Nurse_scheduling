package scheduler;

public class Nurse {
	public int id;
	public Contract contract; 
	public Shift shift;
	int skillId;
	String name; 
	
	public Nurse(int id, Shift s, Contract c, int skill ){
		this.id = id;
		shift = s;
		contract = c;
		skillId = skill;
	}
		
}
