package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

public abstract class AbstractRouteNode implements RouteNode {

	protected String controllerClassName;
	protected Controller controller;
	protected List<RouteNode> children;

	public AbstractRouteNode() {
		children = new ArrayList<RouteNode>();
	}

	@Override
	public List<RouteNode> getChildren() {
		return children;
	}

	@Override
	public void addChild(RouteNode node) {
		children.add(node);
	}

	@Override
	public void removeChild(int index) {
		children.remove(index);
	}

	@Override
	public void removeAllChildren() {
		children.clear();
	}

	@Override
	public void addAllChildren(List<RouteNode> children) {
		this.children.addAll(children);
	}

	@Override
	public void setControllerClassName(String clazz) {
		controllerClassName = clazz;
	}

	@Override
	public String getControllerClassName() {
		return controllerClassName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initControllers() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			ServletException {
		if (controllerClassName != null) {
			Class<? extends Controller> c = (Class<? extends Controller>) Class.forName(controllerClassName);
			controller = c.newInstance();
			controller.init();
		}
		for (RouteNode n : children) {
			n.initControllers();
		}
	}

	@Override
	public Controller getController() {
		return controller;
	}

	public RouteNode findNode(PushbackReader reader) throws MatchException {
		try {
			if (controller != null) {
				return this;
			} else {
				for (RouteNode n : children) {
					if (n.matches(reader)) {
						if (n.isPathEnd() && reader.read() >= 0) {
							throw new MatchException();
						}
						return n.findNode(reader);
					}
				}
				throw new MatchException();
			}
		} catch (IOException e) {
			throw new MatchException("Unexpected error: " + e.getLocalizedMessage());
		}
	}

	public RouteNode findNode(String path) throws MatchException {
		try {
			PushbackReader reader = new PushbackReader(new StringReader(path), 256);
			if (reader.read() != '/') {
				throw new MatchException();
			}
			return findNode(reader);
		} catch (IOException e) {
			throw new MatchException("Unexpected error: " + e.getLocalizedMessage());
		}
	}
}
