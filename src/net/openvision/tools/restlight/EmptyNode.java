package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;

public class EmptyNode extends AbstractRouteNode {

	@Override
	public boolean matches(PushbackReader reader) throws IOException, MatchException {
		reader.unread('/');
		return true;
	}

	@Override
	public String toString() {
		return "{}";
	}

	@Override
	public boolean isPathEnd() {
		return true;
	}

	@Override
	public String getPathRepresentation() {
		return "/";
	}

}
