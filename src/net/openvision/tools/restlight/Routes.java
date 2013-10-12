package net.openvision.tools.restlight;

import java.util.List;

import javax.servlet.ServletException;

public interface Routes {
	
	public static String[] SupportedMethods = { "POST", "PUT", "PATCH", "GET", "DELETE", "OPTIONS", "TRACE" };

	public List<Route> getRoutes();

	public Action getAction(String method, String uri) throws UnsupportedMethodException;

	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException;

}
