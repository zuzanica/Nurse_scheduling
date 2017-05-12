package algorithm;

import common.Common;
import common.XMLParser;
import scheduler.Schedule;

public class Main {
	//public static String inputFile  = "data/example.xml" ;
	//public static String inputFile  = "data/sprint01.xml" ;
	//public static String inputFile  = "data/sprint_late01.xml" ;
	public static String inputFile;
	public static String outputFile;

	public static void main(String[] args) {
		
		if(args.length <= 0 ){
			inputFile = "./data/toy1.xml";
		} else{
			inputFile = args[0];
		}		
		System.out.println("Loading input file: "+ inputFile); 
		
		new XMLParser(inputFile);
		Schedule nurseSchedule = XMLParser.parse();
		System.out.println("Data sucesfully loaded.");
		nurseSchedule.initialize();
		HarmonySearch HSA = new HarmonySearch(nurseSchedule);
		
		
		outputFile = Common.getfileName(inputFile);
		Common.storeResult(outputFile, HSA.getSolution());
		System.out.println("Result saved in "+ outputFile); 
	}
}
