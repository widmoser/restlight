package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;

public class CharacterNode extends AbstractRouteNode {

	private CharSequence label;
	private boolean isPathEnd;

	public CharacterNode(CharSequence label) {
		this.label = label;
		this.buffer = new char[label.length()];
		this.isPathEnd = true;
	}

	void delegateToChild(CharSequence match) {
		CharSequence delegateRemaining = label.subSequence(match.length(), label.length());
		RouteNode delegateNode;
		if (delegateRemaining.length() > 0)
			delegateNode = new CharacterNode(delegateRemaining);
		else
			delegateNode = new EmptyNode();
		delegateNode.addAllChildren(getChildren());
		removeAllChildren();
		label = label.subSequence(0, match.length());
		isPathEnd = false;
		if (label.length() == 0) {
			label = null;
			buffer = null;
		} else {
			this.buffer = new char[label.length()];
		}
		addChild(delegateNode);
	}

	public CharSequence getLabel() {
		return label;
	}

	private char[] buffer;

	@Override
	public boolean matches(PushbackReader reader) throws IOException, MatchException {
		int c = reader.read();
		if (label != null) {
			for (int i = 0; i < label.length(); ++i) {
				buffer[i] = (char) c;
				if (c != label.charAt(i)) {
					reader.unread(buffer, 0, i + 1);
					throw new MatchException(label.subSequence(0, i));
				}
				c = reader.read();
			}
		}
		if (!isPathEnd || (Character.isWhitespace(c) || c == '/' || c < 0)) {
			if (c >= 0)
				reader.unread(c);
			return true;
		} else {
			reader.unread(c);
			reader.unread(buffer, 0, buffer.length);
			throw new MatchException(label);
		}
	}

	@Override
	public String toString() {
		return (label != null ? label : "") + (isPathEnd ? "/" : "");
	}

	@Override
	public boolean isPathEnd() {
		return isPathEnd;
	}

	@Override
	public String getPathRepresentation() {
		return (label != null ? label : "").toString();
	}
}
