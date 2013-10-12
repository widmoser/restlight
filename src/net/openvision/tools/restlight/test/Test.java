package net.openvision.tools.restlight.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.openvision.tools.restlight.ParseException;
import net.openvision.tools.restlight.Parser;
import net.openvision.tools.restlight.RouteTree;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String filename = "routes";
		try {
			Parser parser = new Parser();
			RouteTree tree = parser.parse(new FileReader(filename));
			System.out.println(tree.toString());

			for (String a : tree.getActions()) {
				System.out.println(a);
			}
		} catch (ParseException e) {
			System.err.println(filename + ":" + e.getLine() + " - " + e.getLocalizedMessage());
		}
	}

}
