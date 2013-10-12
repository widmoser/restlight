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

public class PerformanceTest {

	private static Routes process(String filename, Parser parser) throws FileNotFoundException, IOException {
		try {
			return parser.parse(new FileReader(filename));
		} catch (ParseException e) {
			System.err.println(filename + ":" + e.getLine() + " - " + e.getLocalizedMessage());
		}
		return null;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, UnsupportedMethodException, MatchException {
		String filename = "routes";
		// process(filename, new RouteTreeParser());
		long time = System.nanoTime();
		for (int i = 0; i < 1000; ++i) {
			process(filename, new PatternParser());
		}
		System.out.println("Parse time: " + (double) (System.nanoTime() - time) * 0.00000001);

		String[] testStrings = new String[1000];
		for (int j = 0; j < 1000; ++j) {
			StringBuilder s = new StringBuilder();
			int length = (int) Math.random() * 30;
			for (int i = 0; i < length; ++i) {
				s.append((char) Math.random() * 127);
			}
			testStrings[j] = s.toString();
		}

		Parser parser = new PatternParser();
		Routes routes = parser.parse(new FileReader("routes"));
		time = System.nanoTime();
		for (int i = 0; i < 1000; ++i) {
			routes.getAction("GET", "/adapter/123325/history/32948329489");
		}
		System.out.println("Match time: " + (double) (System.nanoTime() - time) * 0.000001);
	}
}
