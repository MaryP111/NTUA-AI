package search;

import java.io.IOException;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;

public class PrologExample {

	public static void main(String[] args) throws JIPSyntaxErrorException, IOException {
		
		JIPEngine jip = new JIPEngine();
		jip.consultFile("prolog.pl");
		
		JIPTermParser parser = jip.getTermParser();
	
		String x = "mary";
		String y = "cheese";
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		System.out.println("CASE 1");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("likes(" + x + "," + y + ")."));
		if (jipQuery.nextSolution() != null) {
			System.out.println("Yes. " + x + " likes " + y + ".");
		} else {
			System.out.println("No. " + x + " doesn't like " + y + ".");
		}
		
		System.out.println("CASE 2");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("likes(" + x + ",Y)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " likes " + term.getVariablesTable().get("Y").toString());
			term = jipQuery.nextSolution();
		}

		System.out.println("CASE 3");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("agree(X,Y)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(term.getVariablesTable().get("X").toString() + " agrees with " + term.getVariablesTable().get("Y").toString());
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 4");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("age(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " is " + term.getVariablesTable().get("Z").toString() + " years old.");
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 5");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("prefers(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " prefers " + term.getVariablesTable().get("Z").toString());
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 6");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("prefersHealthy(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " prefers " + term.getVariablesTable().get("Z").toString());
			term = jipQuery.nextSolution();
		}
		
		System.out.println("CASE 7");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("prefersMost(" + x + ",Z)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(x + " prefers " + term.getVariablesTable().get("Z").toString());
			term = jipQuery.nextSolution();
		}

	}
}
