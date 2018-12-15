package taxis;

import java.io.FileReader;



import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;



public class Graph {
	private long numberOfNodes;
	private long numberOfEdges;
	public static final String nodesPATH = "data/nodes.csv";
	public static final String taxisPATH = "data/taxis.csv";
	public static HashMap<Long, ArrayList<Connection>> adj = new HashMap<Long, ArrayList<Connection>>();
	public static HashMap<List<Double>, Node> hashNodes = new HashMap<List<Double>, Node>();
	public ArrayList<Node> nodes;
	public ArrayList<Node> allNodes = new ArrayList<Node>();
	public void createGraph() throws IOException {
	    Reader fd = new FileReader(nodesPATH);
	    CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
	    for (CSVRecord line : parser) {
	    	long key = line.getRecordNumber();
	    	double x = Double.parseDouble(line.get(0));
	    	double y = Double.parseDouble(line.get(1));
	    	long address = Long.parseLong(line.get(2));

	    	List<Double> keyList = new ArrayList<Double>();
	    	keyList.add(x);
	    	keyList.add(y);
	    	Node n = hashNodes.get(keyList);
	    	if (n == null) {
	    		n = new Node(x, y, key);
	    		n.addresses.add(address);
	    		hashNodes.put(keyList, n);
	    	}
	    	else {
	    		if (n.addresses.contains(address) == false) n.addresses.add(address);
	    	}
	    	allNodes.add(n);
	    }
	    fd.close();
	    System.out.println("Nodes parsing completed succesffully");
	    nodes = new ArrayList<Node>(hashNodes.values());
	    fd = new FileReader(nodesPATH);
	    parser = CSVParser.parse(fd, CSVFormat.RFC4180);
	    for (int i=0; i<allNodes.size()-1; i++) {
	    	Node node = allNodes.get(i);
	    	Node nextNode = allNodes.get(i+1);
	    	ArrayList<Connection> nextNodeConnectionList = adj.get(nextNode.key);
	    	ArrayList<Connection> connectionList = adj.get(node.key);

	    	if (connectionList ==  null) {
	    		connectionList = new ArrayList<Connection>();
	    	}
	    	if (nextNodeConnectionList == null) {
	    		nextNodeConnectionList = new ArrayList<Connection>();
	    	}
	    	Boolean flag = false;
	    	for (long address1 : node.addresses) {
	    		for (long address2 : nextNode.addresses) {
	    			if (address1 == address2) {
	    				flag = true;
	    			}
	    		}
	    	}
	    	if (flag) {
	    		node.numberOfNeighbors++;
	    		nextNode.numberOfNeighbors++;
	    		Connection connection_src = new Connection(node, nextNode);
	    		Connection connection_dst = new Connection(nextNode, node);
	    		connectionList.add(connection_src);
	    		nextNodeConnectionList.add(connection_dst);
	    	}
	    	
	    	adj.put(node.key, connectionList);
	    	adj.put(nextNode.key, nextNodeConnectionList);
	    }
//	    for (Node node: nodes) System.out.println(node.numberOfNeighbors);
	    
	}
	


}

class Node{
	public double x;
	public double y;
	public long key;
	public int numberOfNeighbors = 0;
	public ArrayList<Long> addresses = new ArrayList<Long>();
	Node(double x, double y, long key){
		this.x = x;
		this.y = y;
		this.key = key;
	}
}

class Connection{
	public Node node;
	public double cost;
	Connection(Node src, Node dst){
		this.node = dst;
		this.cost = computeDistance(src, dst);
	}
	
	private double computeDistance(Node src, Node dst) {
		double x1 = src.x;
		double x2 = dst.x;
		double y1 = src.y;
		double y2 = dst.y;
		return Math.sqrt( Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
	}
}

