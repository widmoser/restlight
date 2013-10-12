package net.openvision.tools.restlight;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3619582143214803843L;

	private RouteTree tree;

	@Override
	public void init(ServletConfig config) throws ServletException {
		String filename = config.getInitParameter("routes");
		try {
			Parser parser = new Parser();
			tree = parser.parse(new FileReader(filename));
			tree.initControllers();
		} catch (ParseException e) {
			throw new ServletException(filename + ":" + e.getLine() + " - " + e.getLocalizedMessage(), e);
		} catch (FileNotFoundException e) {
			throw new ServletException(e);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		RouteNode root = tree.getRoot(request.getMethod());
		if (root == null) {
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		} else {
			try {
				root.findNode(new PushbackReader(new StringReader(request.getServletPath()))).getController()
						.action(request, response);
			} catch (MatchException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(e.getLocalizedMessage());
				e.printStackTrace(response.getWriter());
			}
		}
	}

}
