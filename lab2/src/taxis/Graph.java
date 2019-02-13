package taxis;

import java.io.FileReader;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;

class Point {
	public double x, y;

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof Node))
			return false;
		Node casted_other = (Node) other;
		return (this.x == casted_other.x && this.y == casted_other.y);
	}

	public Node findNearestNode(ArrayList<Node> nodes) {
		double minDist, dist;
		Node min;
		min = nodes.get(0);
		minDist = Distance.computeDistance(this, min);
		for (int j = 1; j < nodes.size(); j++) {
			dist = Distance.computeDistance(this, nodes.get(j));
			if (dist < minDist) {
				minDist = dist;
				min = nodes.get(j);
			}
		}
		return min;
	}
}

class Node extends Point {
	public long key;
	public long address;
	public int numberOfNeighbors = 0;
	public double distanceFromStart;
	public double fscore;
	public ArrayList<Long> addresses = new ArrayList<Long>();

	Node(double x, double y, long key,long address) {
		this.x = x;
		this.y = y;
		this.key = key;
		this.address=address;
		this.distanceFromStart = Double.POSITIVE_INFINITY;
		this.fscore = Double.POSITIVE_INFINITY;
	}
}

class Taxi extends Point {
	public JIPEngine jip = new JIPEngine();
	JIPTermParser JIPparser = jip.getTermParser();
	public long id;
	public Node nearestNode;

	Taxi(double x, double y, long id, ArrayList<Node> nodes) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.nearestNode = this.findNearestNode(nodes);
	}
	
	public boolean isELigible(long taxiId) throws IOException{
		jip.consultFile("./data/client_driver.pl");
		JIPTermParser JIPparser = jip.getTermParser();
		JIPQuery jipQuery; 
		JIPTerm term;
		jipQuery = jip.openSynchronousQuery(JIPparser.parseTerm("taxi(A" + ",B," + taxiId + ",C" + ",D" + ",E" + ",F" + ",G)."));
		int available=0,capacity=0,longDistanceTaxi=0,maxLuggage=0;
		int persons=0,luggage=0,longDistanceClient=0;
		String language="";
		ArrayList<String> languageList =new ArrayList<String>();
		term=jipQuery.nextSolution();
		if (term != null) {
			//System.out.println("got in");
			available = Integer.parseInt(term.getVariablesTable().get("C").toString());
			capacity = Integer.parseInt(term.getVariablesTable().get("D").toString());
			longDistanceTaxi = Integer.parseInt(term.getVariablesTable().get("F").toString());
			maxLuggage = Integer.parseInt(term.getVariablesTable().get("G").toString());
		}
		jipQuery = jip.openSynchronousQuery(JIPparser.parseTerm("languages(" + taxiId +",L)."));
		term=jipQuery.nextSolution();
		while(term!=null) {
			//System.out.println("got in");
			languageList.add(term.getVariablesTable().get("L").toString());
			term =jipQuery.nextSolution();
		}
		jipQuery = jip.openSynchronousQuery(JIPparser.parseTerm("client(23.733912,37.975687" + ",A1,B1,C1,D1,E1,F1)."));
		term=jipQuery.nextSolution();
		if (term!=null) {
			//System.out.println("got in");
			longDistanceClient=Integer.parseInt(term.getVariablesTable().get("C1").toString());
			persons= Integer.parseInt(term.getVariablesTable().get("D1").toString());
			language=term.getVariablesTable().get("E1").toString();
			luggage =Integer.parseInt(term.getVariablesTable().get("F1").toString());
		}
		if ((available==0) || (longDistanceClient==1 && longDistanceTaxi==0) || (!languageList.contains(language))) {
			return false;
		}
		else
		    return  (capacity>=persons) && (maxLuggage>=luggage);
		
	}
}

class aStar {
	
	public JIPEngine jip = new JIPEngine();
	JIPTermParser JIPparser = jip.getTermParser();
	
	public double heuristic(long key) throws JIPSyntaxErrorException,IOException {
		double h =1;
		long lineKey=0;
		jip.consultFile("data/lines.pl");
		//JIPTermParser JIPparser = jip.getTermParser();
		JIPQuery jipQuery;
		JIPTerm term;
		jipQuery = jip.openSynchronousQuery(JIPparser.parseTerm("line(" + lineKey  + ",A" + ",B" + ",C" + ",D" + ",E" + ",F" + ",G" + ")."));
		term = jipQuery.nextSolution();
		if (term != null) {
			int penaltylight;
			int lanebonus;
			int penaltyaccess;
			int highway =Integer.parseInt(term.getVariablesTable().get("A").toString());
			//System.out.println(highway);
			int lit = Integer.parseInt(term.getVariablesTable().get("C").toString());
			//System.out.println(lit);
			if (lit==0) {
				penaltylight=1;
			}
			else
				penaltylight=0;
			int lanes = Integer.parseInt(term.getVariablesTable().get("D").toString());
			//System.out.println(lanes);
			if (lanes > 2)
				lanebonus=1;
			else
				lanebonus=0;
			int access = Integer.parseInt(term.getVariablesTable().get("E").toString());
			//System.out.println(access);
			if (access==0) {
				penaltyaccess=1;
			}
			else
				penaltyaccess=0;
			int incline = Integer.parseInt(term.getVariablesTable().get("F").toString());
			//System.out.println(incline);
			int toll = Integer.parseInt(term.getVariablesTable().get("G").toString());
		
			h=h-0.1*highway+0.1*penaltylight-0.1*lanebonus+ penaltyaccess + incline*0.2+toll*0.2;
			System.out.println(h);
		}
		return h;
	}
	public aStarResult aStarSearch(Graph g1, Node startNode) throws JIPSyntaxErrorException,IOException{
		/*
		 * A* from start to end node. The idea is to take the smaller node from the
		 * frontier and go to the neighbor with the smallest f score. At the meantime,
		 * update all the neighbors scores.
		 */
		/* parentsMap takes a node key and returns a list of fathers for this node */
		double lambda = 0.5;
		ArrayList<Long> endKeys = new ArrayList<Long>();
		HashMap<Long, Long> taxiKeysMap = new HashMap<Long, Long>();
		HashMap<Long, HashSet<Node>> parentsMap = new HashMap<Long, HashSet<Node>>();
		HashSet<Node> visited = new HashSet<Node>();
		JIPEngine jip = new JIPEngine();
		jip.consultFile("data/input.pl");
		jip.consultFile("data/taxis.pl");
		JIPTermParser parser = jip.getTermParser();
		JIPQuery jipQuery; 
		JIPTerm term;
		Comparator<Node> comp = new Comparator<Node>() {
			public int compare(Node point1, Node point2) {
				if (point1.fscore == point2.fscore)
					return 0;
				return (point1.fscore) < (point2.fscore) ? -1 : 1;
			}
		};
		TreeSet<Node> frontier = new TreeSet<Node>(comp);
		startNode.distanceFromStart = 0;
		double minimumDistance = Double.POSITIVE_INFINITY;
		for (Taxi taxi : g1.allTaxis) {
			endKeys.add(taxi.nearestNode.key);
			taxiKeysMap.put(taxi.nearestNode.key, taxi.id);
			double distance = Distance.computeDistance(startNode, taxi.nearestNode);
			if (distance < minimumDistance) {
				minimumDistance = distance;
			}
		}
//	    System.out.println(endKeys.toString());
//	    System.out.println(taxiKeysMap.toString());
		startNode.fscore = minimumDistance;
		frontier.clear();
		visited.clear();
		parentsMap.clear();
		frontier.add(startNode);
		Node current = null;
		double tolerance = 200;
		while (!frontier.isEmpty()) {
			current = frontier.pollFirst();
			if (!visited.contains(current)) {
				visited.add(current);
				if (endKeys.contains(current.key)) {
					System.out.println(taxiKeysMap.get(current.key));
					System.out.println("Found");
					return new aStarResult(current, current.distanceFromStart, parentsMap);
				}
				jipQuery = jip.openSynchronousQuery(parser.parseTerm("canMoveFromTo(" + current.key + ",Y)."));
				term = jipQuery.nextSolution();
				while(term!=null) {
					BigDecimal bd = new BigDecimal(term.getVariablesTable().get("Y").toString());
					long nextKey = bd.longValue(); 
					Node neighbor = g1.hashNodes.get(nextKey);
					double h=heuristic(neighbor.address);
					//System.out.println(current.key);
					if (!visited.contains(neighbor)) {
						minimumDistance = Double.POSITIVE_INFINITY;
						//here h
						for (Taxi taxi : g1.allTaxis) {
							double distance = h*Distance.computeDistance(neighbor, taxi.nearestNode);
							if (distance < minimumDistance) {
								minimumDistance = distance;
							}
						}
						//lamda
						//neighbors.get(j).cost->compute
						double predictedDistance = minimumDistance;
						double cost=Distance.computeDistance(current,neighbor);
						double totalDistance = current.distanceFromStart + cost + predictedDistance;
						if (totalDistance < neighbor.fscore) {
							if (totalDistance + tolerance < neighbor.fscore) {
								HashSet<Node> parents = new HashSet<Node>();
								parents.add(current);
								parentsMap.put(neighbor.key, parents);
							} else {
								HashSet<Node> parents = parentsMap.get(neighbor.key);
								parents.add(current);
								parentsMap.put(neighbor.key, parents);
							}
							neighbor.fscore = totalDistance;
							neighbor.distanceFromStart = current.distanceFromStart + cost;
							frontier.add(neighbor);
						} else {
							/* maybe in the tolerance region ? */
							if (totalDistance < neighbor.fscore + tolerance) {
								HashSet<Node> parents = parentsMap.get(neighbor.key);
								parents.add(current);
								parentsMap.put(neighbor.key, parents);
							}
						}
					}
					term=jipQuery.nextSolution();
				}
				
			}
		}
		if (frontier.isEmpty()) {
			System.out.println("Not found");
		}
		return null;
	}
}

class aStarResult {
	public Node taxi;
	public double score;
	public HashMap<Long, HashSet<Node>> routes;

	aStarResult(Node taxi, double score, HashMap<Long, HashSet<Node>> parentsMap) {
		this.taxi = taxi;
		this.score = score;
		this.routes = new HashMap<Long, HashSet<Node>>();
		for (Map.Entry<Long, HashSet<Node>> entry : parentsMap.entrySet()) {
			Long key = entry.getKey();
			HashSet<Node> path = new HashSet<Node>(entry.getValue());
			this.routes.put(key, path);
		}
	}
}

public class Graph {
	public static final String clientPATH = "data/client.csv";
	public static final String nodesPATH = "data/nodes.csv";
	public static final String taxisPATH = "data/taxis.csv";
	public static KmlExport kml = new KmlExport();
	public HashMap<Long, ArrayList<Connection>> adj = new HashMap<Long, ArrayList<Connection>>();
	//public static HashMap<List<Double>, Node> hashNodes = new HashMap<List<Double>, Node>();
	public HashMap<Long, Node> hashNodes = new HashMap<Long, Node>();
	/* nodes are the discrete nodes of the graph */
	public ArrayList<Node> nodes;
	/* all noNodes are the nodes of the graph as read from the csv file */
	public ArrayList<Node> allNodes = new ArrayList<Node>();
	public ArrayList<Taxi> allTaxis = new ArrayList<Taxi>();
	private static aStar search = new aStar();
	

	public void createGraph() throws IOException,JIPSyntaxErrorException {
		FileReader fd = new FileReader(nodesPATH);
		CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
		for (CSVRecord line : parser) {
			//long key = line.getRecordNumber();
			double x = Double.parseDouble(line.get(0));
			double y = Double.parseDouble(line.get(1));
			long address = Long.parseLong(line.get(2));
			long key = Long.parseLong(line.get(3));
			List<Double> keyList = new ArrayList<Double>();
			keyList.add(x);
			keyList.add(y);
			//Node n = hashNodes.get(keyList);
			Node n= hashNodes.get(key);
			if (n == null) {
				n = new Node(x, y,key,address);
				//n.addresses.add(address);
				//hashNodes.put(keyList, n);
				hashNodes.put(key,n);
			} else {
//				if (n.addresses.contains(address) == false)
//					n.addresses.add(address);
			}
			allNodes.add(n);
		}
		fd.close();
//		
		System.out.println("Nodes created successfully");

	}
	

	public void createTaxis() throws IOException {
		FileReader fd = new FileReader(taxisPATH);
		CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
		for (CSVRecord line : parser) {
			double x = Double.parseDouble(line.get(0));
			double y = Double.parseDouble(line.get(1));
			long address = Long.parseLong(line.get(2));
			Taxi t1=new Taxi(x, y, address, allNodes);
			if (t1.isELigible(address)) {
				allTaxis.add(t1);
				System.out.println(address);
			}
		}
	}


	public void run() throws IOException {
		Reader fd = new FileReader(clientPATH);
		CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
		double x = 0.0;
		double y = 0.0;
//		for (CSVRecord line : parser) {
//			x = Double.parseDouble(line.get(0));
//			y = Double.parseDouble(line.get(1));
//		}
		x=23.733912;
		y=37.975687;
		Node clientNode = new Node(x, y, -1,-1);
		Node revisedClientNode = clientNode.findNearestNode(allNodes);
		aStarResult result = search.aStarSearch(this, revisedClientNode);
		kml.kmlCreate(result, revisedClientNode, result.taxi, "solution.kml");
		kml.visualizeTaxis(this);
		kml.visualizeClient(revisedClientNode);

	}
}

class Connection {
	public Node node;
	public double cost;

	Connection(Node src, Node dst) {
		this.node = dst;
		this.cost = Distance.computeDistance(src, dst);
	}
}