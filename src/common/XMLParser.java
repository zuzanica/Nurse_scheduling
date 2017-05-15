package common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import algorithm.Allocation;
import requirements.Cover;
import requirements.CoverRequirements;
import scheduler.Contract;
import scheduler.Nurse;
import scheduler.Schedule;
import scheduler.Shift;
import scheduler.UnwantedPattern;
/**
 * Working with XML file.
 * @author Studená Zuzana
 *
 */
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
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		doc.getDocumentElement().normalize();
	}
	
	/**
	 * Save final schedule into xml.
	 * @param file
	 * @param schedule
	 */
	public static void storeSchedule(String file, Schedule schedule){
		String finalFile = file+ "_sol.xml";
		try{		
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			doc = docBuilder.newDocument();
			Element rootElement = (Element) doc.createElement("Solution");
			doc.appendChild(rootElement);
						
			Element period = doc.createElement("SchedulingPeriodID");
			period.appendChild(doc.createTextNode(file));
			rootElement.appendChild(period);
			Element competitor = doc.createElement("SchedulingPeriodID");
			competitor.appendChild(doc.createTextNode("Zuzana"));
			rootElement.appendChild(competitor);
			Element penalty = doc.createElement("SoftConstraintsPenalty");
			penalty.appendChild(doc.createTextNode(Integer.toString(schedule.getSolution().getFxWeight())));
			rootElement.appendChild(penalty);

			for(Allocation alloc : schedule.getSolution().getX()){
				Element assigment = doc.createElement("Assignment");
				
				Element date = doc.createElement("Date");
				String d = schedule.createDate(alloc.getD());
				date.appendChild(doc.createTextNode(d));				
				assigment.appendChild(date);
				
				Element nurse = doc.createElement("Employee");
				nurse.appendChild(doc.createTextNode(Integer.toString(alloc.getN())));				
				assigment.appendChild(nurse);
				
				Element shift = doc.createElement("ShiftType");
				shift.appendChild(doc.createTextNode(alloc.getS()));				
				assigment.appendChild(shift);

				rootElement.appendChild(assigment);
			}
			
			// write a content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(finalFile));
	
			transformer.transform(source, result);
	
		  } catch (ParserConfigurationException pce) {
			  pce.printStackTrace();
		  } catch (TransformerException tfe) {
			  tfe.printStackTrace();
		  }
	}
	
	/**
	 * Parse schedule information from XML. 
	 * @return
	 */
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
		NodeList skills = (NodeList) skillNode.item(0);
		for (int temp = 0; temp < skills.getLength(); temp++) {
			Node skill = (Node) skills.item(temp);			
			if (skill.getNodeType() == Node.ELEMENT_NODE) {
				schedule.addSkill(skill.getTextContent());
			}
		}
	}
	
	static void parseShifts(Schedule schedule){
		NodeList shiftNode = (NodeList) doc.getElementsByTagName("ShiftTypes");
		NodeList shifts = (NodeList) shiftNode.item(0);
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
				schedule.addShift(s);	
			}	
		}
	}
	
	static void parseUnwantedPatterns(Schedule schedule){
		NodeList patternsNode = (NodeList) doc.getElementsByTagName("Patterns");
		NodeList patterns = (NodeList) patternsNode.item(0);
		for (int temp = 0; temp < patterns.getLength(); temp++) {
			Node pattern = (Node) patterns.item(temp);

			if (pattern.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) pattern;
				
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
							shiftPattern = true;
						}else if(st.equals("None") || st.equals("Any")){
							shiftPattern = false;
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
				ArrayList<Integer> UP = new ArrayList<Integer>();
				NodeList unwantedPatterns = (NodeList) eElement.getElementsByTagName("Pattern").item(0);
				if(unwantedPatterns != null){
					for (int i = 0; i < unwantedPatterns.getLength(); i++) {
						Node pattern = (Node) unwantedPatterns.item(i);			
						if (pattern.getNodeType() == Node.ELEMENT_NODE) {
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
				
				Contract c = new Contract(Integer.parseInt(id),desc);
				c.addContraints(assigmentPD, maxNA, minNA, maxWD, minWD, maxFD, minFD, maxCWW, minCWW, MWWIFW, weekend, compW ,ISTDW, NNSBFW, altSkill  );
				c.setUnwantedPatterns(UP);
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
				schedule.addNurse(n);	
			}	
		}
	}
	
	static void parseCoverRequirements(Schedule schedule){
		NodeList coverNode = (NodeList) doc.getElementsByTagName("CoverRequirements");
		NodeList weekCovers = (NodeList) coverNode.item(0);
		ArrayList<CoverRequirements> weekCR = new ArrayList<CoverRequirements>();
		for (int temp = 0; temp < weekCovers.getLength(); temp++) {
			Node dayCover = (Node) weekCovers.item(temp);

			CoverRequirements DC = null;
			if (dayCover.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) dayCover;
				//get week day
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
				int nurseId = Integer.parseInt(eElement.getElementsByTagName("EmployeeID").item(0).getTextContent());
				String d = eElement.getElementsByTagName("Date").item(0).getTextContent();
				schedule.addNurseFreeDay(nurseId, d);
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
				int nurseId = Integer.parseInt(eElement.getElementsByTagName("EmployeeID").item(0).getTextContent());
				String st = eElement.getElementsByTagName("ShiftTypeID").item(0).getTextContent();
				String d = eElement.getElementsByTagName("Date").item(0).getTextContent();
				schedule.addNurseFreeShift(nurseId, d, st);
			}
		}
	}
}
