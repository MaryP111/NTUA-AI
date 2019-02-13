package taxis;
import java.io.IOException;


public class Main {
        private static Graph graph = new Graph(); 
        private static PrologConverter converter= new PrologConverter();
        public static void main(String[] args) throws IOException {
        	converter.createPredicates();
        	graph.createGraph();
        	graph.createTaxis();
        	System.out.println("Taxis created");
        	graph.run();
        }

}
