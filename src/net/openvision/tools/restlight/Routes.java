package net.openvision.tools.restlight;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;

/**
 * Represents a set of routes that are supported by a particular
 * {@link RestServlet}. The routes are defined in a routes file, which is parsed
 * by a {@link Parser} resulting in an instance of this class.
 * 
 * @author Hannes Widmoser
 * 
 */
public abstract class Routes {

	public static String[] SupportedMethods = { "POST", "PUT", "PATCH", "GET", "DELETE", "OPTIONS", "TRACE" };

	public abstract List<Route> getRoutes();

	public abstract Action getAction(String method, String uri) throws UnsupportedMethodException;

	public String getLink(Class<? extends Controller> controllerClass, Object... params) {
		for (Route r : getRoutes()) {
			if (r.getControllerClassName().equals(controllerClass.getName())) {
				return r.getLink(params);
			}
		}
		throw new NoSuchElementException();
	}
	
	public void initControllers(RestServlet servlet) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		for (Route r : getRoutes()) {
			r.initController(this, servlet);
		}
	}

}
