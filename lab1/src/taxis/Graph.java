package taxis;

import java.io.BufferedReader;
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
        String line = "";
        public static final String nodesPATH = "taxis/nodes.csv";
        public static final String taxisPATH = "taxis/taxis.csv";
        public  HashMap<Long, ArrayList<Connection>> adj = new HashMap<Long, ArrayList<Connection>>();
        public static HashMap<List<Double>, Node> hashNodes = new HashMap<List<Double>, Node>();
        public ArrayList<Node> nodes;
        String [] temp = new String[3];
        public ArrayList<Node> allNodes = new ArrayList<Node>();
        long ai=0;
        public void createGraph() throws IOException {
            //FileReader fd = new FileReader(nodesPATH);
            //BufferedReader bufferedReader = new BufferedReader(fd);
            //bufferedReader.readLine();// try-catch omitted
            BufferedReader fd = new BufferedReader(new FileReader(nodesPATH));
            line=fd.readLine();
            // CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
            // for (CSVRecord line : parser) {
            //     long key = line.getRecordNumber();
            //     System.out.println(key);
            //     double x = Double.parseDouble(line.get(0));
            //     double y = Double.parseDouble(line.get(1));
            //     long address = Long.parseLong(line.get(2));
            while((line=fd.readLine())!=null) {
                ai=ai+1;
                //line=fd.readLine();
                temp=line.split(",");
                double x=Double.parseDouble(temp[0]);
                double y = Double.parseDouble(temp[1]);
                long address=Long.parseLong(temp[2]);
                List<Double> keyList = new ArrayList<Double>();

                keyList.add(x);
                keyList.add(y);
                Node n = hashNodes.get(keyList);
                if (n == null) {
                        n = new Node(x, y, ai);
                        n.addresses.add(address);
                        hashNodes.put(keyList, n);
                }
                else {
                        if (n.addresses.contains(address) == false) n.addresses.add(address);
                }
                allNodes.add(n);
            }
            fd.close();
            System.out.println("Nodes parsing completed succesfully");
            System.out.println(allNodes.size());
            nodes = new ArrayList<Node>(hashNodes.values());
            //fd = new FileReader(nodesPATH);
            //parser = CSVParser.parse(fd, CSVFormat.RFC4180);
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
//          for (Node node: nodes) System.out.println(node.numberOfNeighbors);


        }
        public void revert() {
            for (int j=0;j<allNodes.size();j++) {
                allNodes.get(j).distanceFromStart=Double.POSITIVE_INFINITY;
                allNodes.get(j).fscore=Double.POSITIVE_INFINITY;
            }
        }


}
class Node{
        public double x;
        public double y;
        public long key;
        public int numberOfNeighbors = 0;
        public double distanceFromStart;
        public double fscore;
        public ArrayList<Long> addresses = new ArrayList<Long>();
        Node(double x, double y, long key){
                this.x = x;
                this.y = y;
                this.key = key;
                this.distanceFromStart = Double.POSITIVE_INFINITY;
                this.fscore = Double.POSITIVE_INFINITY;
        }
        @Override
        public boolean equals(Object o) {
            if (o == this) { 
                return true; 
            } 
      
            /* Check if o is an instance of Mapnode or not */
            if (!(o instanceof Node)) { 
                return false; 
            } 
              
            // typecast o to Mapnode so that we can compare data members  
            Node m = (Node) o; 
              
            // Compare the data members and return accordingly 
            return (this.x == m.x && this.y == m.y); 
             
        
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

class Connection{
        public Node node;
        public double cost;
        Connection(Node src, Node dst){
                this.node = dst;
                this.cost = computeDistance(src, dst);
        }

        public double computeDistance(Node src, Node dst) {
            int R = 6371; // Radius of the earth
            double latDistance = Math.toRadians(dst.x - src.x);
            double lonDistance = Math.toRadians(dst.y - src.y);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) 
                    + Math.cos(Math.toRadians(src.x)) * Math.cos(Math.toRadians(dst.x))* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convert to meters
            return distance;
        }
    }



