package net.openvision.tools.restlight.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.openvision.tools.restlight.MatchException;
import net.openvision.tools.restlight.ParseException;
import net.openvision.tools.restlight.Parser;
import net.openvision.tools.restlight.PatternParser;
import net.openvision.tools.restlight.Routes;
import net.openvision.tools.restlight.UnsupportedMethodException;

public class Test {
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, UnsupportedMethodException, MatchException {
		Parser parser = new PatternParser();
		Routes routes = parser.parse(new FileReader("routes"));
		System.out.println(routes.toString());
		routes.getAction("GET", "/adapter/123325/history/32948329489");
	}

}
