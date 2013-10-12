package net.openvision.tools.restlight;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {

	public void action(HttpServletRequest req, HttpServletResponse resp) throws IOException;
	
}
