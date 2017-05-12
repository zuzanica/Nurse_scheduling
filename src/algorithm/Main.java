package algorithm;

import common.XMLParser;
import scheduler.Schedule;

public class Main {
	//public static String inputFile  = "data/example.xml" ;
	//public static String inputFile  = "data/sprint01.xml" ;
	//public static String inputFile  = "data/sprint_late01.xml" ;
	public static String inputFile;
	public static String outputFile;

	public void getfileName(){
		String[] tmp = inputFile.split("/");
		outputFile = tmp[1].split(".")[0];

		System.out.println("Loading outputFile : "+ outputFile); 
	}

	public static void main(String[] args) {
		
		if(args.length <= 0 ){
			inputFile = "data/toy1.xml";
		} else{
			inputFile = args[0];
		}		
		System.out.println("Loading input file: "+ inputFile); 
		
		new XMLParser(inputFile);
		Schedule nurseSchedule = XMLParser.parse();
		nurseSchedule.initialize();
		HarmonySearch HSA = new HarmonySearch(nurseSchedule);
		

		//getfileName();
		String[] tmp = inputFile.split("/");
		outputFile = tmp[2].substring(0, tmp[2].length() - 4) + ".txt";
		System.out.println("Loading outputFile : "+ outputFile); 

		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		try {
		    fw = new FileWriter(outputFile, true);
		    bw = new BufferedWriter(fw);
		    out = new PrintWriter(bw);
		    
		    out.println(HSA.getSolution());
		    
		    out.close();
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		finally {
		    try {
		        if(out != null)
		            out.close();
		    } catch (IOException e) {
		        //exception handling left as an exercise for the reader
		    }
		    try {
		        if(bw != null)
		            bw.close();
		    } catch (IOException e) {
		        //exception handling left as an exercise for the reader
		    }
		    try {
		        if(fw != null)
		            fw.close();
		    } catch (IOException e) {
		        //exception handling left as an exercise for the reader
		    }
		}


	}

}
