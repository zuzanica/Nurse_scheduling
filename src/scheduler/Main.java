package scheduler;

import common.XMLParser;

public class Main {
	public static String inputFile  = "data/sprint01.xml" ;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
		new XMLParser(inputFile);
		Schedule nurseSchedule = XMLParser.parse();
		
	}

}
