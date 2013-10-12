package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class PatternParser implements Parser {

	@Override
	public Routes parse(Reader input) throws IOException, ParseException {
		PatternRoutes result = new PatternRoutes();
		LineNumberReader reader = new LineNumberReader(input);
		String line = reader.readLine();
		while (line != null) {
			line.trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				String[] parts = line.split("\\s+");
				result.appendRoute(new Route(parts[0], parts[1], parts[2]));
			}
			line = reader.readLine();
		}
		result.compilePatterns();
		return result;
	}

}
