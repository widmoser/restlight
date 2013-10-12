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
public class RouteTree implements Routes {

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
			return "/";
		}

	}

	private Map<String, RouteNode> methods = new HashMap<String, RouteNode>();

	public RouteTree() {
		for (String method : SupportedMethods) {
			methods.put(method, new MethodNode(method));
		}
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

	@Override
	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException {
		for (RouteNode method : methods.values()) {
			method.initControllers();
		}
	}

	private void appendActions(String method, String path, RouteNode node, List<Route> actions) {
		path = path + node.getPathRepresentation();
		if (node.getControllerClassName() != null) {
			actions.add(new Route(method, path, node.getControllerClassName()));
		} else {
			for (RouteNode n : node.getChildren()) {
				appendActions(method, path, n, actions);
			}
		}
	}

	@Override
	public List<Route> getRoutes() {
		List<Route> result = new ArrayList<Route>();
		for (Map.Entry<String, RouteNode> e : methods.entrySet()) {
			appendActions(e.getKey(), "", e.getValue(), result);
		}
		return result;
	}

	@Override
	public Action getAction(String method, String uri) throws UnsupportedMethodException {
		try {
			Controller c = getRoot(method).findNode(uri).getController();
			return new Action(method, uri, c, null);
		} catch (MatchException e) {
			return null;
		}
	}
}
