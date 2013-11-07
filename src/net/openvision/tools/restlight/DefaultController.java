package net.openvision.tools.restlight;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultController implements Controller {

	@Override
	public void init() throws ServletException {
	}

	@Override
	public void action(HttpServletRequest req, HttpServletResponse resp, Action action) throws IOException {
	}

}
