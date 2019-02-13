package taxis;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class PrologConverter {
	public static final String clientPATH = "data/client.csv";
	public static final String nodesPATH = "data/nodes.csv";
	public static final String taxisPATH = "data/taxis.csv";
	public static final String linesPATH = "data/lines.csv";
	public static final String trafficPATH = "data/traffic.csv";
	


	public void createPredicates() throws IOException {
		HashMap<Long, String> lines = new HashMap<Long, String>();
		FileReader fd = new FileReader(nodesPATH);
		CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
		PrintWriter writer = null;
		PrintWriter writerLines= null;
		PrintWriter writerClientDriver= null;
		File file = new File("./data/input.pl");
		File file2 = new File("./data/lines.pl");
		File file3 = new File("./data/client_driver.pl");
		writer = new PrintWriter(file);
		writerLines = new PrintWriter(file2);
		writerClientDriver = new PrintWriter(file3);
		
		/* read lines */
		FileReader fd4 = new FileReader(linesPATH);
		CSVParser parser4 = CSVParser.parse(fd4, CSVFormat.RFC4180);
		for (CSVRecord line : parser4) {
			long id = Long.parseLong(line.get(0));
			String highway = line.get(1);
			int priority;
			if (highway.equals(""))
				priority=0;
			else if (highway.equals("primary") || highway.equals("primary_link") || highway.equals("motorway_link"))
				priority=2;
			else if (highway.equals("secondary") || highway.equals("secondary_link"))
				priority=1;
			else if (highway.equals("residential"))
				priority=-1;
			else
				priority=0;
			String isOneway = line.get(3);
			int oneway;
			if (isOneway.equals("yes")) {
				oneway=1;
				lines.put(id, "yes");
			}
			else if (isOneway.equals("no")) {
				oneway=0;
				lines.put(id, "no");
			}
			else{
				oneway=-1;
				lines.put(id, "-1");
			}
			String isLit = line.get(4);
			int lit;
			if (isLit.equals("no")) 
				lit=0;
			else
				lit=1;
			
			String lanes =line.get(5);
			int nolanes=1;
			if (!lanes.equals("")) {
				nolanes = Integer.parseInt(lanes);
				System.out.println(nolanes);
			}
			else
				nolanes=1;

			String hasAccess = line.get(9);
			int access;
			if (hasAccess.equals("no")) 
				access=0;
			else
				access=1;
			
			String isInclined = line.get(14);
			int incline;
			if (isInclined.equals("yes")) 
				incline=1;
			else
				incline=0;
			
			String hasTolls = line.get(17);
			int tolls;
			if (hasTolls.equals("yes")) 
				tolls=1;
			else
				tolls=0;
			writerLines.println("line(" + id + "," + priority + "," + oneway +"," + lit + ","  + nolanes + "," + access +  "," + incline + "," + tolls  +").");
		}
		
		
		/* read nodes */
		long prevLineId=0;
		long prevNodeId=0;
		int i=0;
		for (CSVRecord line : parser) {
			double x = Double.parseDouble(line.get(0));
			double y = Double.parseDouble(line.get(1));
			long lineId = Long.parseLong(line.get(2));
			long nodeId = Long.parseLong(line.get(3));
			writer.println("node(" + x + "," + y + ","+ lineId + "," + nodeId + ").");
			if (lines.get(lineId) == "yes") {
				/* one way line */
				writer.println("neighbor(" + prevNodeId + "," + nodeId + ").");
			}
			else if (lines.get(lineId) == "no") {
				/* two way line */
				writer.println("neighbor(" + nodeId + "," + prevNodeId + ").");
				writer.println("neighbor(" + prevNodeId + "," + nodeId + ").");
			}
			else {
				/* reverse line */
				writer.println("neighbor(" + nodeId + "," + prevNodeId + ").");
			}
			prevLineId = lineId;
			prevNodeId = nodeId;
			i=i+1;
		}

		// read input for clients

//		FileReader fd2 = new FileReader(clientPATH);
//		CSVParser parser2 = CSVParser.parse(fd2, CSVFormat.RFC4180);
//		for (CSVRecord line : parser2) {
//			double x = Double.parseDouble(line.get(0));
//			double y = Double.parseDouble(line.get(1));
//			double x_dest = Double.parseDouble(line.get(2));
//			double y_dest = Double.parseDouble(line.get(3));
//			int persons  = Integer.parseInt(line.get(4));
//			String language = line.get(5);
//			int luggage = Integer.parseInt(line.get(6));
			writer.println("client(" + "23.733912" + "," + "37.975687" + "," + "23.772518" +"," + "38.012301" + "0" + "," + 0 + "," + 3 + "," + "greek" + "," + "1" + ").");
			writerClientDriver.println("client(" + "23.733912" + "," + "37.975687" + "," + "23.772518" +"," + "38.012301" + "0" + "," + 0 + "," + 3 + "," + "greek" + "," + "1" + ").");
//		}

			
		/* read input for taxis */
		FileReader fd3 = new FileReader(taxisPATH);
		CSVParser parser3 = CSVParser.parse(fd3, CSVFormat.RFC4180);
		for (CSVRecord line : parser3) {
			double x = Double.parseDouble(line.get(0));
			double y = Double.parseDouble(line.get(1));
			long id  = Long.parseLong(line.get(2));
			String available = line.get(3);
			int free;
			if (available.equals("yes")) 
				free=1;
			else
				free=0;
			String hasCapacity = line.get(4);
			List<String> capacities = new ArrayList<String>(Arrays.asList(hasCapacity.split("-")));
			int capacity = Integer.parseInt(capacities.get(1));
			String languages = line.get(5);
			//String[] languageList = languages.split(" | ");
			double rating =Double.parseDouble(line.get(6));
			String longDistance = line.get(7);
			int dLong;
			if (longDistance.equals("yes")) {
				dLong=1;
			}
			else
				dLong=0;
			String vehicleType = line.get(8);
			String[] parts = vehicleType.split("");
			int maxLuggage;
			if (parts[0].equals("s")) 
				maxLuggage=2;
			else if(parts[0].equals("c"))
				maxLuggage=3;
			else if (parts[0].equals("l"))
				maxLuggage=5;
			else
				maxLuggage=3;
			writerClientDriver.println("taxi(" + x + "," + y + "," + id +"," + free + ","  + capacity + "," +rating + ","+ dLong + "," + maxLuggage +").");
			if(languages.equals("greek|english")) {
				writerClientDriver.println("languages("+ id + ",greek).");
				writerClientDriver.println("languages("+ id + ",english).");
			}
			else if(languages.equals("greek"))
				writerClientDriver.println("languages("+ id + ",greek).");
			else
				writerClientDriver.println("languages("+ id + ",english).");
				
		}




		// read traffic data
		FileReader fd5 = new FileReader(trafficPATH);
		CSVParser parser5 = CSVParser.parse(fd5, CSVFormat.RFC4180);
		int j=1;
		for (CSVRecord line : parser5) {
			long id = Long.parseLong(line.get(0));
			if (!line.get(1).equals("")) {
					String traffic=line.get(2);
					if (!traffic.equals("")) {
						writer.println("traffic(" + id + ",1,2).");
						writer.println("traffic(" + id + ",2,1).");
						writer.println("traffic(" + id + ",3,2).");
				
					}
			}
		}
		writer.close();
		writerLines.close();
		writerClientDriver.close();
	}
}
