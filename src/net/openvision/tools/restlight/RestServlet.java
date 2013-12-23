package net.openvision.tools.restlight;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.appengine.api.utils.SystemProperty;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class RestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3619582143214803843L;

	private String routesMD5;
	private String routesFilename;
	private Routes routes;

	private Configuration cfg;

	private void initTemplateEngine(ServletConfig config) throws ServletException {

		cfg = new Configuration();

		// Specify the data source where the template files come from. Here I
		// set a
		// plain directory for it, but non-file-system are possible too:
		cfg.setServletContextForTemplateLoading(config.getServletContext(), "/");

		// Specify how templates will see the data-model. This is an advanced
		// topic...
		// for now just use this:
		cfg.setObjectWrapper(new DefaultObjectWrapper());

		// Set your preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear. Here we assume we are developing HTML
		// pages.
		// For production systems TemplateExceptionHandler.RETHROW_HANDLER is
		// better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

		// At least in new projects, specify that you want the fixes that aren't
		// 100% backward compatible too (these are very low-risk changes as far
		// as the
		// 1st and 2nd version number remains):
		cfg.setIncompatibleImprovements(new Version(2, 3, 20)); // FreeMarker
																// 2.3.20
	}

	private String getRoutesMD5() throws IOException, ServletException {
		return DigestUtils.md5Hex(new FileInputStream(routesFilename));
	}

	private void loadRoutes() throws ServletException {
		try {
			Parser parser = new PatternParser();
			routes = parser.parse(new FileReader(routesFilename));
			routes.initControllers();
			routesMD5 = getRoutesMD5();
		} catch (ParseException e) {
			throw new ServletException(routesFilename + ":" + e.getLine() + " - " + e.getLocalizedMessage(), e);
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
		} catch (IllegalArgumentException e) {
			throw new ServletException(e);
		} catch (InvocationTargetException e) {
			throw new ServletException(e);
		} catch (NoSuchMethodException e) {
			throw new ServletException(e);
		} catch (SecurityException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		initTemplateEngine(config);
		routesFilename = config.getInitParameter("routes");
		loadRoutes();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development && (routesMD5 == null || !routesMD5.equals(getRoutesMD5()))) {
				loadRoutes();
			}

			Action action = routes.getAction(request.getMethod(), request.getRequestURI());
			if (action != null) {
				action.execute(request, response);
			} else {
				try {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

					Template template = new Template("ActionNotFound", new InputStreamReader(getClass().getResourceAsStream("actionNotFound.ftl")), cfg);
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("actions", routes.getRoutes());
					template.process(data, response.getWriter());
				} catch (TemplateException e1) {
					e1.printStackTrace(response.getWriter());
				}
			}
		} catch (UnsupportedMethodException e) {
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}

	}

}
