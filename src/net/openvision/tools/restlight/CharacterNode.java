package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;

public class CharacterNode extends AbstractRouteNode {

	private char character;
	private CharSequence remaining;
	private boolean isPathEnd;

	public CharacterNode(CharSequence seq) {
		this.character = seq.charAt(0);
		this.remaining = seq.subSequence(1, seq.length());
		this.buffer = new char[remaining.length()];
		this.isPathEnd = true;
	}

	public char getCharacter() {
		return character;
	}

	void delegateToChild(CharSequence match) {
		CharSequence delegateRemaining = remaining.subSequence(match.length(), remaining.length());
		RouteNode delegateNode;
		if (delegateRemaining.length() > 0)
			delegateNode = new CharacterNode(delegateRemaining);
		else
			delegateNode = new EmptyNode();
		delegateNode.addAllChildren(getChildren());
		removeAllChildren();
		remaining = remaining.subSequence(0, match.length());
		isPathEnd = false;
		if (remaining.length() == 0) {
			remaining = null;
			buffer = null;
		} else {
			this.buffer = new char[remaining.length()];
		}
		addChild(delegateNode);
	}

	public CharSequence getRemaining() {
		return remaining;
	}

	void clearRemaining() {
		remaining = null;
		buffer = null;
	}

	private char[] buffer;

	@Override
	public boolean matches(PushbackReader reader) throws IOException, MatchException {
		int c = reader.read();
		if (character == c) {
			if (remaining != null) {
				for (int i = 0; i < remaining.length(); ++i) {
					c = reader.read();
					buffer[i] = (char) c;
					if (c != remaining.charAt(i)) {
						reader.unread(buffer, 0, i + 1);
						reader.unread(character);
						throw new MatchException(remaining.subSequence(0, i));
					}
				}
			}
			c = reader.read();
			if (!isPathEnd || (Character.isWhitespace(c) || c == '/' || c < 0)) {
				if (c >= 0)
					reader.unread(c);
				return true;
			} else {
				reader.unread(c);
				reader.unread(buffer, 0, buffer.length);
				reader.unread(character);
				throw new MatchException(remaining);
			}
		} else {
			reader.unread(c);
			return false;
		}
	}

	@Override
	public String toString() {
		return "{" + character + "}" + (remaining != null ? remaining : "") + (isPathEnd ? "/" : "");
	}

	@Override
	public boolean isPathEnd() {
		return isPathEnd;
	}

	@Override
	public String getPathRepresentation() {
		return String.valueOf(character) + (remaining != null ? remaining : "");
	}
}
