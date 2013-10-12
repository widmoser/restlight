package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

/**
 * A tree that allows to decide who should handle the request.
 * 
 * @author Hannes Widmoser
 * 
 */
public class RouteTree {

	private class MethodNode extends AbstractRouteNode {

		private String method;

		public MethodNode(String method) {
			this.method = method;
		}

		@Override
		public boolean matches(PushbackReader reader) throws IOException, MatchException {
			return true;
		}

		@Override
		public String toString() {
			return method + ":";
		}

		@Override
		public boolean isPathEnd() {
			return false;
		}

		@Override
		public String getPathRepresentation() {
			return method + " /";
		}

	}

	private Map<String, RouteNode> methods = new HashMap<String, RouteNode>();

	public RouteTree() {
		methods.put("POST", new MethodNode("POST"));
		methods.put("PATCH", new MethodNode("PATCH"));
		methods.put("GET", new MethodNode("GET"));
		methods.put("DELETE", new MethodNode("DELETE"));
	}

	public RouteNode getRoot(String method) {
		return methods.get(method);
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
		for (RouteNode method : methods.values()) {
			print(output, 0, method);
		}
		return output.toString();
	}

	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException {
		for (RouteNode method : methods.values()) {
			method.initControllers();
		}
	}

	private void appendActions(String path, RouteNode node, List<String> actions) {
		path = path + node.getPathRepresentation();
		if (node.getControllerClassName() != null) {
			actions.add(path);
		} else {
			for (RouteNode n : node.getChildren()) {
				appendActions(path, n, actions);
			}
		}
	}

	public List<String> getActions() {
		List<String> result = new ArrayList<String>();
		for (RouteNode method : methods.values()) {
			appendActions("", method, result);
		}
		return result;
	}
}
