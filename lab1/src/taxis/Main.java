package taxis;
import java.io.IOException;


public class Main {
        private static Graph graph = new Graph(); 
        public static void main(String[] args) throws IOException {
        	graph.createGraph();
        	graph.createTaxis();
        	graph.run();
        }

}
