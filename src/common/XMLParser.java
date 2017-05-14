package common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import requirements.Cover;
import requirements.CoverRequirements;
import scheduler.Contract;
import scheduler.Nurse;
import scheduler.Schedule;
import scheduler.Shift;
import scheduler.UnwantedPattern;

public final class XMLParser {
	File fXmlFile; 
	DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;
	static Document doc;
	
	public XMLParser(String file){
		fXmlFile = new File(file);
		dbFactory = DocumentBuilderFactory.newInstance();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
	}
	
	public static Schedule parse(){
		Schedule schedule;
		try{
			//Get StartDate, EndDate
			NodeList startD = (NodeList) doc.getElementsByTagName("StartDate");
			NodeList endD = (NodeList) doc.getElementsByTagName("EndDate");
			schedule = new Schedule( startD.item(0).getTextContent(), endD.item(0).getTextContent());	

			parseSkills(schedule);
			parseShifts(schedule);
			parseUnwantedPatterns(schedule);
			parseContracts(schedule);
			parseNurses(schedule);
			parseCoverRequirements(schedule);
			patrseDayRequirements(schedule);
			patrseShiftRequirements(schedule);
			
		} catch (Exception e) {
			System.err.println("Problem in XML FILE READING");
			e.printStackTrace();
			return null;
		}
		
		return schedule;
	}
	
	static void parseSkills(Schedule schedule){
		NodeList skillNode = (NodeList) doc.getElementsByTagName("Skills");
		//System.out.println("Node node length " + skillNode.getLength());
		NodeList skills = (NodeList) skillNode.item(0);
		//System.out.println("Skills node length " + skills.getLength());
		for (int temp = 0; temp < skills.getLength(); temp++) {
			Node skill = (Node) skills.item(temp);			
			if (skill.getNodeType() == Node.ELEMENT_NODE) {
				//System.out.println(temp + ": skill type "+ skill.getTextContent());
				schedule.addSkill(skill.getTextContent());
			}
		}
	}
	
	static void parseShifts(Schedule schedule){
		NodeList shiftNode = (NodeList) doc.getElementsByTagName("ShiftTypes");
		//System.out.println("Node node length " + skillNode.getLength());
		NodeList shifts = (NodeList) shiftNode.item(0);
		//System.out.println("Skills node length " + shifts.getLength());
		for (int temp = 0; temp < shifts.getLength(); temp++) {
			Node shift = (Node) shifts.item(temp);

			if (shift.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) shift;

				//parse node with skill types
				ArrayList<String> RS = new ArrayList<String>();
				NodeList requiredSkills = (NodeList) eElement.getElementsByTagName("Skills").item(0);
				for (int i = 0; i < requiredSkills.getLength(); i++) {
					Node skill = (Node) requiredSkills.item(i);			
					if (skill.getNodeType() == Node.ELEMENT_NODE) {
						RS.add(skill.getTextContent());
					}
				}
				
				String type = eElement.getAttribute("ID");
				String st = eElement.getElementsByTagName("StartTime").item(0).getTextContent();
				String et = eElement.getElementsByTagName("EndTime").item(0).getTextContent();
				String desc = eElement.getElementsByTagName("Description").item(0).getTextContent();
				
				Shift s = new Shift(st, et, type, RS, desc);
				//System.out.println(s.toString());
				schedule.addShift(s);	
			}	
		}
		
	}
	
	static void parseUnwantedPatterns(Schedule schedule){
		NodeList patternsNode = (NodeList) doc.getElementsByTagName("Patterns");
		//System.out.println("Node node length " + skillNode.getLength());
		NodeList patterns = (NodeList) patternsNode.item(0);
		//System.out.println("Skills node length " + shifts.getLength());
		for (int temp = 0; temp < patterns.getLength(); temp++) {
			Node pattern = (Node) patterns.item(temp);

			if (pattern.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) pattern;
				int id = Integer.parseInt(eElement.getAttribute("ID"));
				
				//parse node with pattern entry
				NodeList pEntries = (NodeList) eElement.getElementsByTagName("PatternEntries").item(0);
				UnwantedPattern uwPattern = new UnwantedPattern();
				Boolean shiftPattern = true;
				for (int i = 0; i < pEntries.getLength(); i++) {
					Node pEntry = (Node) pEntries.item(i);	
					if (pEntry.getNodeType() == Node.ELEMENT_NODE) {
						Element elem = (Element) pEntry;
						String st = elem.getElementsByTagName("ShiftType").item(0).getTextContent();
						String day = elem.getElementsByTagName("Day").item(0).getTextContent();
						uwPattern.addPattern(uwPattern.new Pattern(st, day));
						if(day.equals("Any")){
							//System.out.println("add shift pattern!");
							shiftPattern = true;
						}else if(st.equals("None") || st.equals("Any")){
							shiftPattern = false;
							//System.out.println("add day pattern!");
						}else {
							System.err.println("Unknown patterns are dropped!");
						}
						
					}
				}
				if(shiftPattern){
					schedule.addShiftPattern(uwPattern);
				}else {
					schedule.addDayPattern(uwPattern);
				}
			}	
		}
	}
	
	static void parseContracts(Schedule schedule){
		NodeList contactNode = (NodeList) doc.getElementsByTagName("Contracts");
		NodeList contacts = (NodeList) contactNode.item(0);
		for (int temp = 0; temp < contacts.getLength(); temp++) {
			Node contact = (Node) contacts.item(temp);

			if (contact.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) contact;
				//TODO child nodeList unwanted patterns
				ArrayList<Integer> UP = new ArrayList<Integer>();
				NodeList unwantedPatterns = (NodeList) eElement.getElementsByTagName("Pattern").item(0);
				if(unwantedPatterns != null){
					for (int i = 0; i < unwantedPatterns.getLength(); i++) {
						Node pattern = (Node) unwantedPatterns.item(i);			
						if (pattern.getNodeType() == Node.ELEMENT_NODE) {
							//System.out.println(i + ": UP "+ skill.getTextContent());
							UP.add(Integer.parseInt(pattern.getTextContent()));
						}
					}
				}
				String id = eElement.getAttribute("ID");
				String desc = eElement.getElementsByTagName("Description").item(0).getTextContent();
				String assigmentPD = eElement.getElementsByTagName("SingleAssignmentPerDay").item(0).getTextContent();
				String maxNA = eElement.getElementsByTagName("MaxNumAssignments").item(0).getTextContent();
				String minNA = eElement.getElementsByTagName("MinNumAssignments").item(0).getTextContent();
				String maxWD = eElement.getElementsByTagName("MaxConsecutiveWorkingDays").item(0).getTextContent();
				String minWD = eElement.getElementsByTagName("MinConsecutiveWorkingDays").item(0).getTextContent();
				String maxFD = eElement.getElementsByTagName("MaxConsecutiveFreeDays").item(0).getTextContent();
				String minFD = eElement.getElementsByTagName("MinConsecutiveFreeDays").item(0).getTextContent();
				
				String maxCWW = eElement.getElementsByTagName("MaxConsecutiveWorkingWeekends").item(0).getTextContent();
				String minCWW = eElement.getElementsByTagName("MinConsecutiveWorkingWeekends").item(0).getTextContent();
				String MWWIFW = eElement.getElementsByTagName("MaxWorkingWeekendsInFourWeeks").item(0).getTextContent();
				String weekend = eElement.getElementsByTagName("WeekendDefinition").item(0).getTextContent();
				
				String compW = eElement.getElementsByTagName("CompleteWeekends").item(0).getTextContent();
				String ISTDW = eElement.getElementsByTagName("IdenticalShiftTypesDuringWeekend").item(0).getTextContent();
				String NNSBFW = eElement.getElementsByTagName("NoNightShiftBeforeFreeWeekend").item(0).getTextContent();
				String altSkill = eElement.getElementsByTagName("AlternativeSkillCategory").item(0).getTextContent();
				//TODO dorobit dalsie zavislosti z XML
				
				Contract c = new Contract(Integer.parseInt(id),desc);
				c.addContraints(assigmentPD, maxNA, minNA, maxWD, minWD, maxFD, minFD, maxCWW, minCWW, MWWIFW, weekend, compW ,ISTDW, NNSBFW, altSkill  );
				c.setUnwantedPatterns(UP);
				//System.out.println(c.toString());
				schedule.addContract(c);	
			}	
		}
	}
	
	static void parseNurses(Schedule schedule){
		NodeList nursesNode = (NodeList) doc.getElementsByTagName("Employees");
		NodeList nurses = (NodeList) nursesNode.item(0);
		for (int temp = 0; temp < nurses.getLength(); temp++) {
			Node nurse = (Node) nurses.item(temp);

			if (nurse.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nurse;

				//parse node with nurses skill types
				ArrayList<String> NS = new ArrayList<String>();
				NodeList nursesSkills = (NodeList) eElement.getElementsByTagName("Skills").item(0);
				for (int i = 0; i < nursesSkills.getLength(); i++) {
					Node skill = (Node) nursesSkills.item(i);			
					if (skill.getNodeType() == Node.ELEMENT_NODE) {
						NS.add(skill.getTextContent());
					}
				}
								
				int id = Integer.parseInt(eElement.getAttribute("ID"));
				int cId = Integer.parseInt(eElement.getElementsByTagName("ContractID").item(0).getTextContent());
				Nurse n = new Nurse(id, schedule.getContract(cId), NS );
				//System.out.println(n.toString());
				schedule.addNurse(n);	
			}	
		}
	}
	
	static void parseCoverRequirements(Schedule schedule){
		NodeList coverNode = (NodeList) doc.getElementsByTagName("CoverRequirements");
		NodeList weekCovers = (NodeList) coverNode.item(0);
		//create structure of the week cover requirements
		ArrayList<CoverRequirements> weekCR = new ArrayList<CoverRequirements>();
		for (int temp = 0; temp < weekCovers.getLength(); temp++) {
			Node dayCover = (Node) weekCovers.item(temp);

			CoverRequirements DC = null;
			if (dayCover.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) dayCover;
				//System.out.println(eElement.getTextContent());
				//get week day
				//System.out.println(eElement.getElementsByTagName("Day").item(0).getTextContent());
				String day = eElement.getElementsByTagName("Day").item(0).getTextContent();
				//list of four daily shit requirements
				ArrayList<Cover> shiftCoverList = new ArrayList<Cover>();
				//parse node with daily cover requirements skill types
				NodeList shiftCover = (NodeList) eElement.getElementsByTagName("Cover");
				for (int i = 0; i < shiftCover.getLength(); i++) {
					Node cover = (Node) shiftCover.item(i);			
					if (cover.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) cover;
						//get shift type
						String sType = e.getElementsByTagName("Shift").item(0).getTextContent();
						//get preferred nurses count
						int nursesCount = Integer.parseInt(e.getElementsByTagName("Preferred").item(0).getTextContent());
						//create Cover for one shift
						Cover c = new Cover(schedule.getShift(sType), nursesCount);
						shiftCoverList.add(c);
					}
				}
				//create structure of one day cover requirements
				DC = new CoverRequirements(day, shiftCoverList);
				weekCR.add(DC);
				//System.out.println(DC.toString());
			}
		}
		schedule.addCoverRequirements(weekCR);	
	}

	
	static void patrseDayRequirements(Schedule schedule){
		NodeList dayNode = (NodeList) doc.getElementsByTagName("DayOffRequests");
		NodeList days = (NodeList) dayNode.item(0);
		//create structure of the week cover requirements
		for (int temp = 0; temp < days.getLength(); temp++) {
			Node day = (Node) days.item(temp);

			if (day.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) day;
				//System.out.println(eElement.getTextContent());
				//System.out.println(eElement.getElementsByTagName("EmployeeID").item(0).getTextContent());
				int nurseId = Integer.parseInt(eElement.getElementsByTagName("EmployeeID").item(0).getTextContent());
				String d = eElement.getElementsByTagName("Date").item(0).getTextContent();
				schedule.addNurseFreeDay(nurseId, d);
				//System.out.println(DC.toString());
			}
		}
	}
	
	static  void patrseShiftRequirements(Schedule schedule){
		NodeList dayNode = (NodeList) doc.getElementsByTagName("ShiftOffRequests");
		NodeList days = (NodeList) dayNode.item(0);
		//create structure of the week cover requirements
		for (int temp = 0; temp < days.getLength(); temp++) {
			Node day = (Node) days.item(temp);

			if (day.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) day;
				//System.out.println(eElement.getTextContent());
				//System.out.println(eElement.getElementsByTagName("Day").item(0).getTextContent());
				int nurseId = Integer.parseInt(eElement.getElementsByTagName("EmployeeID").item(0).getTextContent());
				String st = eElement.getElementsByTagName("ShiftTypeID").item(0).getTextContent();
				String d = eElement.getElementsByTagName("Date").item(0).getTextContent();
				schedule.addNurseFreeShift(nurseId, d, st);
				//System.out.println(DC.toString());
			}
		}
	}
	
}
