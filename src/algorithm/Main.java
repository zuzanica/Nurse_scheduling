package algorithm;

import common.Common;
import common.XMLParser;
import scheduler.Schedule;
/**
 * 
 * @author Studena Zuzana
 *
 */
public class Main {
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
		XMLParser.storeSchedule(outputFile, nurseSchedule);
		Common.storeResult(outputFile, HSA.getSolution(), HSA.getInitViolation());
		System.out.println("Result saved in "+ outputFile); 
		
	}
}
