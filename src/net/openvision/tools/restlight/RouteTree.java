package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;

/**
 * A tree that allows to decide who should handle the request.
 * 
 * @author Hannes Widmoser
 * 
 */
public class RouteTree {

	private class RootNodeType extends AbstractRouteNode {

		@Override
		public boolean matches(PushbackReader reader) throws IOException, MatchException {
			return false;
		}

		@Override
		public String toString() {
			return "RouteTree:";
		}

	}

	private RouteNode root = new RootNodeType();

	public RouteTree() {
	}

	public RouteNode getRoot() {
		return root;
	}

	private static CharSequence spaces = "                                                              ";

	private void print(StringBuilder output, int indent, RouteNode node) {
		output.append(spaces, 0, indent);
		String nodeStr = node.toString();
		output.append(nodeStr);
		if (node.getControllerClassName() != null) {
			output.append(spaces, 0, 60 - indent - nodeStr.length());
			output.append(node.getControllerClassName());
		}
		output.append('\n');
		for (RouteNode c : node.getChildren()) {
			print(output, indent + 2, c);
		}
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		print(output, 0, root);
		return output.toString();
	}

}
