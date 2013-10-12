package net.openvision.tools.restlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

public class PatternRoutes implements Routes {

	private List<Route> routesList;
	private Map<String, List<Route>> routes;
	private Map<String, Pattern> patterns;
	private Map<String, SortedMap<Integer, PathRegex>> groupMapping;

	public PatternRoutes() {
		patterns = null;
		routesList = new ArrayList<>();
		routes = new HashMap<>();
		groupMapping = new TreeMap<>();
	}

	public void appendRoute(Route route) {
		routesList.add(route);
		List<Route> methodRoutes = routes.get(route.getMethod());
		if (methodRoutes == null) {
			methodRoutes = new ArrayList<>();
			routes.put(route.getMethod(), methodRoutes);
			groupMapping.put(route.getMethod(), new TreeMap<Integer, PathRegex>());
		}
		methodRoutes.add(route);
	}

	private static class Parameter {

		private String name;

		public Parameter(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	private static class PathRegex {

		private Route route;
		private String regex;
		private List<Parameter> parameters;

		public PathRegex(Route route, String regex, List<Parameter> parameters) {
			super();
			this.route = route;
			this.regex = regex;
			this.parameters = parameters;
		}

	}

	private PathRegex processPathExpression(int index, Route route) {
		String[] pathElements = route.getPathExpression().split("/");
		StringBuilder s = new StringBuilder();
		List<Parameter> parameters = new ArrayList<>();
		for (String pathElement : pathElements) {
			if (pathElement.length() > 0) {
				s.append("/");
				if (pathElement.startsWith(":")) {
					s.append("(\\w+)");
					parameters.add(new Parameter(pathElement.substring(1)));
				} else {
					s.append(pathElement);
				}
			}
		}
		return new PathRegex(route, s.toString(), parameters);
	}

	public void compilePatterns() {
		this.patterns = new HashMap<String, Pattern>();
		for (String method : SupportedMethods) {
			List<Route> methodRoutes = routes.get(method);
			Map<Integer, PathRegex> mapping = groupMapping.get(method);
			if (methodRoutes != null) {
				StringBuilder patternString = new StringBuilder();
				patternString.append("^");
				int currentGroup = 1;
				for (int i = 0; i < methodRoutes.size(); ++i) {
					if (i > 0) {
						patternString.append("|");
					}
					Route r = methodRoutes.get(i);
					PathRegex pathRegex = processPathExpression(i, r);
					patternString.append("(").append(pathRegex.regex).append(")");
					mapping.put(currentGroup, pathRegex);
					currentGroup += pathRegex.parameters.size() + 1;
				}
				patternString.append("$");
				this.patterns.put(method, Pattern.compile(patternString.toString()));
			} else {
				this.patterns.put(method, Pattern.compile("^$"));
			}
		}

	}

	@Override
	public List<Route> getRoutes() {
		return routesList;
	}

	private PathRegex getRoute(String method, int matchingGroup) {
		return groupMapping.get(method).get(matchingGroup);
	}

	@Override
	public Action getAction(String method, String uri) throws UnsupportedMethodException {
		Pattern pattern = patterns.get(method);
		if (pattern == null)
			throw new UnsupportedMethodException(method);

		Matcher matcher = pattern.matcher(uri);
		Action action = null;
		PathRegex selectedRoute = null;
		if (matcher.matches()) {
			Map<String, String> params = new HashMap<String, String>();
			int paramCounter = 0;
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				String param = matcher.group(i);
				if (param != null) {
					if (selectedRoute == null) {
						// first group is route itself:
						selectedRoute = getRoute(method, i);
					} else {
						// param:
						params.put(selectedRoute.parameters.get(paramCounter++).getName(), param);
					}
				}
			}
			action = new Action(method, uri, selectedRoute.route.getController(), params);
		}
		return action;
	}

	@Override
	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException {
		for (Route r : routesList) {
			r.initController();
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Pattern> e : patterns.entrySet()) {
			s.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
		}
		return s.toString();
	}

}
