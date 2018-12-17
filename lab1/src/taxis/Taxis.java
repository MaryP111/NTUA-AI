
package taxis;

import java.util.*;
import java.io.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Taxis {

	public ArrayList<Taxi> allTaxis = new ArrayList<Taxi>();
	public static final String taxisPATH = "taxis/taxis.csv";
    public void initTaxisArray(ArrayList<Node> nodes) throws IOException {
    		String line ="";
    		String [] temp = new String[3];
            BufferedReader fd = new BufferedReader(new FileReader(taxisPATH));
            line=fd.readLine();
            //CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
            //BufferedReader fd = new BufferedReader(new FileReader(taxisPATH));
            // for (CSVRecord line : parser) {
            //     long key = line.getRecordNumber();
            //     double x = Double.parseDouble(line.get(0));
            //     double y = Double.parseDouble(line.get(1));
            //     long address = Long.parseLong(line.get(2));
            while((line=fd.readLine())!=null) {
                temp=line.split(",");
                double x=Double.parseDouble(temp[0]);
                double y = Double.parseDouble(temp[1]);
                long key=Long.parseLong(temp[2]);
      
                allTaxis.add(new Taxi(x,y,key,nodes));
			}
		}




class Taxi {
	public double x;
	public double y;
	public long id;
	public Node nearestNode;
	
	Taxi (double x,double y,long id,ArrayList<Node> nodes) {
		this.x = x;
		this.y = y;
		this.id = id;				
		this.nearestNode = find_nearest_node(nodes);
	}
	
	public double distance(Node m) {
	    final int R = 6371; // Radius of the earth
	    double latDistance = Math.toRadians(m.x - this.x);
	    double lonDistance = Math.toRadians(m.y - this.y);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) 
	    		+ Math.cos(Math.toRadians(this.x)) * Math.cos(Math.toRadians(m.x))* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters
	    return distance;
	}
	
	public Node find_nearest_node (ArrayList <Node> nodes ) {
		double min_dist, dist;
		Node min;
		min=nodes.get(0);
		min_dist = this.distance(min);
			for (int j=1;j<nodes.size();j++) {
				dist = this.distance(nodes.get(j));
				if (dist < min_dist) {
					min_dist = dist;
					min = nodes.get(j);
			}
		}
		return min;
	}
}
}