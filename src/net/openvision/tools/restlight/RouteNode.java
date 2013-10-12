package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.List;

public interface RouteNode {

	public List<RouteNode> getChildren();

	void addChild(RouteNode node);

	void removeChild(int index);

	void removeAllChildren();

	void addAllChildren(List<RouteNode> children);

	public boolean matches(PushbackReader reader) throws IOException, MatchException;

}
