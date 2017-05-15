package common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Common methods, like save file,etc.
 * @author Studena Zuzana
 *
 */
public final class Common {
	
	public static String getfileName(String inputFile){
		String[] tmp = inputFile.split("/");
		String outputFile = "out/" + tmp[2].substring(0, tmp[2].length() - 4);
		return outputFile;
	}
	
	public static void storeResult(String outputFile, int result, double averageViolation){
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		try {
		    fw = new FileWriter(outputFile+".txt", true);
		    bw = new BufferedWriter(fw);
		    out = new PrintWriter(bw);
		    out.println(Double.toString(averageViolation)+ ";" + Integer.toString(result));
		    out.close();
		} catch (IOException e) {
			System.err.println("Can not open file " + outputFile);
		}
		finally {
		    if(out != null)
			    out.close();
		    try {
		        if(bw != null)
		            bw.close();
		    } catch (IOException e) {
		        //exception handling left as an exercise for the reader
		    	System.err.println("Can not close file " + outputFile);
		    }
		}
	}

}	
