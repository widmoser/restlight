package net.openvision.tools.restlight;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRouteNode implements RouteNode {

	protected String controllerClassName;
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

}
