//KML Export class
package taxis;
import java.util.*;
import java.io.*;
import java.awt.Color;

class Colors{
	public static ArrayList<Color> colors = new ArrayList<Color>(Arrays.asList(Color.BLACK.darker(), Color.BLUE.darker()));
	public static Iterator<Color> it = colors.iterator();
}
class KmlExport {
	public void kmlCreate (aStarResult result, Node startNode, Node endNode, String fileName) {
		Color color = Color.GREEN.darker();
	    PrintWriter writer = null;
		try {
				// prints path of winner-taxi with alternative routes
				writer = new PrintWriter(fileName);
				writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	            writer.println("<kml xmlns=\"http://earth.google.com/kml/2.1\">");
	            writer.println("<Document>");
	            writer.println("<name>Taxi Routes</name>");
	            writer.println("<Style id=\"green\">");
	            writer.println("<LineStyle>");
	            writer.println("<color>" + Integer.toHexString(color.getRGB()) + "</color>");
	            writer.println("<width>4</width>");
	            writer.println("</LineStyle>");
	            writer.println("</Style>");
				
	            // this is to make the client point
			    writer.println("<Placemark>");
			    writer.println("<name>Client</name>");
			    writer.println("<Point>");
			    writer.println("<coordinates>");
			    writer.println(endNode.x + "," + endNode.y);
			    writer.println("</coordinates>");
			    writer.println("</Point>");
			    writer.println("</Placemark>");

			    writer.println("<Placemark>");
			    writer.println("<name> Line " + "</name>");
			    writer.println("<styleUrl>#green</styleUrl>");
			    writer.println("<LineString>");
			    writer.println("<altitudeMode>relative</altitudeMode>");
			    writer.println("<coordinates>");
			    printAllPaths(result, endNode, startNode, writer);
			    writer.println("</coordinates>");
				writer.println("</LineString>");
				writer.println("</Placemark>"); 
				writer.println("</Document>");
				writer.println("</kml>");
	
	} catch (FileNotFoundException e) {
	            System.out.println("File not found");
	        } finally {
	            if (writer != null)
	                writer.close();
	        }
		
		   
	}

	public void printAllPaths(aStarResult result, Node startNode, Node endNode, PrintWriter writer){ 
	        HashSet<Node> isVisited = new HashSet<Node>(); 
	        ArrayList<Node> pathList = new ArrayList<Node>(); 
	        pathList.add(startNode); 
	        printAllPathsUtil(result, startNode, endNode, isVisited, pathList, writer);
	} 
	    
	void printAllPathsUtil(aStarResult result, Node u, Node d, HashSet<Node> isVisited, ArrayList <Node> localPathList, PrintWriter writer) { 
	        isVisited.add(u);
	        if (u.key == d.key) { 
	        	for (int i=0; i<localPathList.size(); i++){
	        		writer.println(localPathList.get(i).x + "," + localPathList.get(i).y);
	        	}    	 
	            isVisited.remove(u); 
	            return ; 
	        } 

	        ArrayList<Node> parents =new ArrayList<Node>(result.routes.get(u.key));
	        for(int j=0;j<parents.size();j++)   
	        { 
	            if (!isVisited.contains(parents.get(j))) 
	            { 

	                localPathList.add(parents.get(j)); 
	                printAllPathsUtil(result,parents.get(j), d, isVisited, localPathList,writer); 
	                localPathList.remove(parents.get(j)); 
	            } 
	        } 

	        isVisited.remove(u); 
	    } 

}

