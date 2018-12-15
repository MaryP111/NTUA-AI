package taxis;

import java.io.IOException;

public class Main {
	private static Graph graph = new Graph();
	public static void main(String[] args) {
		try {
			graph.createGraph();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print(e.toString());
			e.printStackTrace();
		}
	}
}
