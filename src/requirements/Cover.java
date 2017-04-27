package requirements;

import scheduler.Shift;

public class Cover {
	
	Shift shift;
	int nursesCount = 0;
	public Cover(Shift _shift, int nCount){
		shift = _shift;
		nursesCount = nCount;		
	}
	
	public int getPrefNurses(){
		return nursesCount;
	}
	
	public Shift getShift(){
		return shift;
	}
}
