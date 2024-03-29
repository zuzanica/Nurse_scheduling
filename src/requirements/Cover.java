package requirements;

import scheduler.Shift;

/**
 * Represents one request on shift and day. 
 * @author Studena Zuzana
 *
 */
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
