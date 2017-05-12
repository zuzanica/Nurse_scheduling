package algorithm;

import common.XMLParser;
import scheduler.Schedule;

public class Main {
	//public static String inputFile  = "data/example.xml" ;
	public static String inputFile  = "data/sprint01.xml" ;
	//public static String inputFile  = "data/sprint_late01.xml" ;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
		new XMLParser(inputFile);
		Schedule nurseSchedule = XMLParser.parse();
		nurseSchedule.initialize();
		HarmonySearch HSA = new HarmonySearch(nurseSchedule);
		
	}

}
