package net.openvision.tools.restlight;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class ViewController extends DefaultController {
	
	private String viewName;
	private String filename;
	
	protected ViewController(String viewName, String filename) {
		this.viewName = viewName;
		this.filename = filename;
	}
	
	protected ViewController(String filename) {
		this(filename, filename);
	}
	
	protected abstract Map<String, Object> getData(HttpServletRequest req);
	
	@Override
	public void action(HttpServletRequest req, HttpServletResponse resp, Action action) throws IOException {
		try {
			Template template = new Template(viewName, new FileReader(filename), getServlet().getTemplateConfiguration());
			resp.setContentType("text/html; charset=utf-8");
			template.process(getData(req), resp.getWriter());
		} catch (TemplateException e) {
			e.printStackTrace(resp.getWriter());
		}
	}

}
