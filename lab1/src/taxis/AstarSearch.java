package taxis;

import java.util.*;
import java.io.*;

class AstarSearch {
/**
     * //@return Priority Queue
     */
    // public PriorityQueue<Node> initQueue() {
    //     return new PriorityQueue<>(10, new Comparator<Node>() {
    //         public int compare(Node x, Node y) {
    //             if (x.fscore < y.fscore)                
    //             {
    //                 return 1;
    //             }
    //             return 0;
    //         };
    //     });
    // }


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




public AstarResult aStarSearch(Graph g1,Node startNode,Node endNode)
    { 
 
        // setup for A*
        HashMap<Long,ArrayList<Node>> parentsMap = new HashMap<Long,ArrayList<Node>>();
        HashSet<Node> visited = new HashSet<Node>();
 
        //Queue<Node> priorityQueue = initQueue();
        ArrayList <Node> frontier = new ArrayList<>();
 
        //  enque StartNode, with distance 0
        startNode.distanceFromStart=new Double(0);
        startNode.fscore=startNode.distance(endNode);
        //priorityQueue.add(startNode);
        frontier.clear();
        visited.clear();
        frontier.add(startNode);
        Node current = null;
         
        while (!frontier.isEmpty()) {
            current = frontier.get(0);
            frontier.remove(0);
            //System.out.println(current.fscore);
            if(!visited.contains(current) ) {
                visited.add(current);
                // if last element in PQ reached
                if (current.key==endNode.key) {
                    System.out.println("Found");
                    return new AstarResult(current.distanceFromStart,parentsMap);
                }
                ArrayList<Connection> neighbors = g1.adj.get(current.key);
                for (int j=0;j<neighbors.size();j++) {
                    Node neighbor=neighbors.get(j).node;
                    if (!visited.contains(neighbor) ){  
 
                        // calculate predicted distance to the end node
                        double predictedDistance = neighbor.distance(endNode);
 
                        // 1. calculate distance to neighbor. 2. calculate dist from start node 3.find total distance
                        double totalDistance =current.distanceFromStart +neighbors.get(j).cost+ predictedDistance;
 
                        // check if distance smaller
                        if(totalDistance < neighbor.fscore ){
                            // update n's distance
                            neighbor.fscore=totalDistance;
                            neighbor.distanceFromStart=current.distanceFromStart+neighbors.get(j).cost;
                            // set parent
                            if (parentsMap.containsKey(neighbor.key)) {
                            ArrayList<Node> parents=parentsMap.get(neighbor.key);
                            parents.add(current);
                            parentsMap.put(neighbor.key,parents);
                            }
                            else {
                                ArrayList<Node> parents=new ArrayList<Node>();
                                parents.add(current);
                                parentsMap.put(neighbor.key,parents);
                            }
                            // enqueue
                            frontier.add(neighbor);
                        }
                    }
                    // else {
                    //     ArrayList<Node> parents=parentsMap.get(neighbor.key);
                    //     parents.add(current);
                    //     parentsMap.put(neighbor.key,parents);
                    // }
                }
            }
        Collections.sort(frontier, new Comparator<Node>(){
                   public int compare(Node point1, Node point2){
                         if((point1.fscore) == (point2.fscore))
                             return 0;
                         return (point1.fscore ) < (point2.fscore)? -1 : 1;
                     }
                });

        }
        System.out.println("shit");
        return null;
    }

}
class AstarResult {
    public double score;
    public HashMap<Long,ArrayList<Node>> routes;
    AstarResult(double score,HashMap<Long,ArrayList<Node>> parentsMap){
        this.score=score;
        this.routes = new HashMap<Long,ArrayList<Node>>();
        for (Map.Entry<Long, ArrayList<Node>> entry : parentsMap.entrySet()) {
            Long key = entry.getKey();
            ArrayList<Node> path = new ArrayList<Node>(entry.getValue());
            this.routes.put(key,path);
            //System.out.println(this.routes.get(key).get(0));
        }
    }
}

