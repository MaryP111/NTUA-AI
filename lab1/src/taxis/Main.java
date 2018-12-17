package taxis;

import java.io.IOException;
import java.util.*;
import java.io.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class Main {
        private static Graph graph = new Graph(); 
        private static Taxis taxis=new Taxis();
        private static AstarSearch astar=new AstarSearch();
        public static final String clientPATH = "taxis/client.csv";
        public static void main(String[] args) {
                try {
                        
                      
                        //Reader fd = new FileReader(clientPATH);
                        // CSVParser parser = CSVParser.parse(fd, CSVFormat.RFC4180);
                        // double x=0;
                        // double y=0;
                        // for (CSVRecord line : parser) {
                        
                        //         x = Double.parseDouble(line.get(0));
                        //         y = Double.parseDouble(line.get(1));
                        // }


                        //node =new Taxi(x,y,id);
                        //Node startNode=node.nearestNode;

                        BufferedReader fd = new BufferedReader(new FileReader(clientPATH));
                        String line="";
                        String [] temp = new String[2];
                        line=fd.readLine();
                        line=fd.readLine();
                        temp=line.split(",");
                        double x=Double.parseDouble(temp[0]);
                        double y = Double.parseDouble(temp[1]);

                        double min_score=Double.POSITIVE_INFINITY;
                        long taxi_id=0;
                        graph.createGraph();
                        Node n1=new Node(x,y,-1);
                        Node endNode=n1.find_nearest_node(graph.allNodes);
                        taxis.initTaxisArray(graph.allNodes);
                        HashMap<Long,AstarResult> taxiroutes =new HashMap<Long,AstarResult>();
                        for(int j=0;j<taxis.allTaxis.size();j++) {
                                AstarResult result=astar.aStarSearch(graph,taxis.allTaxis.get(j).nearestNode,endNode);
                                double score=result.score;
                                taxiroutes.put(taxis.allTaxis.get(j).id,result);
                                if (score<min_score) {
                                        taxi_id=taxis.allTaxis.get(j).id;
                                        min_score=score;
                                        

                                }
                                graph.revert();
                        }
                        System.out.print(taxi_id);
                        System.out.print(min_score+ " ");
                        AstarResult finalResult=taxiroutes.get(taxi_id);
                        System.out.print(finalResult.routes.get(endNode.key).get(0).id);

                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.print(e.toString());
                        e.printStackTrace();
                }

        }

}
