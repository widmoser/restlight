package net.openvision.tools.restlight.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.openvision.tools.restlight.ParseException;
import net.openvision.tools.restlight.RouteTreeParser;
import net.openvision.tools.restlight.Routes;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String filename = "routes";
		try {
			RouteTreeParser parser = new RouteTreeParser();
			Routes routes = parser.parse(new FileReader(filename));
			System.out.println(routes.toString());

			for (String a : routes.getActions()) {
				System.out.println(a);
			}
		} catch (ParseException e) {
			System.err.println(filename + ":" + e.getLine() + " - " + e.getLocalizedMessage());
		}
	}

}
