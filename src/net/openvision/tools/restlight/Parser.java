package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.Reader;

public interface Parser {
	
	public Routes parse(Reader input) throws IOException, ParseException;

}
