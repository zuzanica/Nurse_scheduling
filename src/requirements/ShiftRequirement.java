package requirements;

import scheduler.Nurse;
import scheduler.Shift;

/**
 * Specify shift requirement.
 * @author Studená Zuzana
 *
 */
public class ShiftRequirement extends Requirement {

	Shift shift;
	Nurse nurse;
	public ShiftRequirement(String date, Nurse nurse, Shift s){
		super(date, nurse);
		shift = s;
	}
}
