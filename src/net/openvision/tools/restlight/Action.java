package net.openvision.tools.restlight;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Action {

	private String method;
	private String uri;
	private Controller controller;
	private Map<String, String> parameters;

	Action(String method, String uri, Controller controller, Map<String, String> parameters) {
		super();
		this.method = method;
		this.uri = uri;
		this.controller = controller;
		this.parameters = parameters;
	}

	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public Controller getController() {
		return controller;
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	public boolean hasParameter(String name) {
		return parameters.containsKey(name);
	}

	public void execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		controller.action(req, resp, this);
	}

}
