package net.openvision.tools.restlight;

import javax.servlet.ServletException;

public class Route {

	private String method;
	private String pathRegex;
	private String controllerClassName;

	private Controller controller;

	public Route(String method, String pathRegex, String controllerClassName) {
		super();
		this.method = method;
		this.pathRegex = pathRegex;
		this.controllerClassName = controllerClassName;
	}

	public String getMethod() {
		return method;
	}

	public String getPathExpression() {
		return pathRegex;
	}

	public String getControllerClassName() {
		return controllerClassName;
	}

	public Controller getController() {
		return controller;
	}

	@Override
	public String toString() {
		return method + " " + pathRegex + " " + controllerClassName;
	}

	@SuppressWarnings("unchecked")
	public void initController() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException {
		if (controllerClassName != null) {
			Class<? extends Controller> c = (Class<? extends Controller>) Class.forName(controllerClassName);
			controller = c.newInstance();
			controller.init();
		}
	}

}
