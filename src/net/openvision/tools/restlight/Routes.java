package net.openvision.tools.restlight;

import java.util.List;

import javax.servlet.ServletException;

/**
 * Represents a set of routes that are supported by a particular
 * {@link RestServlet}. The routes are defined in a routes file, which is parsed
 * by a {@link Parser} resulting in an instance of this class.
 * 
 * @author Hannes Widmoser
 * 
 */
public interface Routes {

	public static String[] SupportedMethods = { "POST", "PUT", "PATCH", "GET", "DELETE", "OPTIONS", "TRACE" };

	public List<Route> getRoutes();

	public Action getAction(String method, String uri) throws UnsupportedMethodException;

	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException;

}
