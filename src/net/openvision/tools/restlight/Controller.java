package net.openvision.tools.restlight;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Controller {

	private Routes routes;
	
	void setRoutes(Routes routes) {
		this.routes = routes;
	}
	
	public abstract void init() throws ServletException;

	public abstract void action(HttpServletRequest req, HttpServletResponse resp, Action action) throws IOException;
	
	protected Routes getRoutes() {
		return routes;
	}
	
}
