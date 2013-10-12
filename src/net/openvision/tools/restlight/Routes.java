package net.openvision.tools.restlight;

import java.util.List;

import javax.servlet.ServletException;

public interface Routes {

	public List<String> getActions();

	public Controller getController(String method, String uri) throws UnsupportedMethodException, MatchException;

	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException;

}
