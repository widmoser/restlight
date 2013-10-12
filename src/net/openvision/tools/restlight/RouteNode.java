package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.List;

import javax.servlet.ServletException;

public interface RouteNode {

	public List<RouteNode> getChildren();

	void addChild(RouteNode node);

	void removeChild(int index);

	void removeAllChildren();

	void addAllChildren(List<RouteNode> children);

	public boolean matches(PushbackReader reader) throws IOException, MatchException;

	public void setControllerClassName(String clazz);

	public String getControllerClassName();

	public Controller getController();

	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException;

	RouteNode findNode(PushbackReader reader) throws MatchException;

	RouteNode findNode(String path) throws MatchException;
	
	public boolean isPathEnd();
	
	public String getPathRepresentation();
}
