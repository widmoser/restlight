package net.openvision.tools.restlight;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {

	public void init() throws ServletException;

	public void action(HttpServletRequest req, HttpServletResponse resp, Action action) throws IOException;

}
