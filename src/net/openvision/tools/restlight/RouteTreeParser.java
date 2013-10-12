package net.openvision.tools.restlight;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * Parses a routes file and creates a routes tree.
 * 
 * @author Hannes Widmoser
 * 
 */
public class RouteTreeParser implements Parser {

	public static final char PATH_SEPARATOR = '/';

	private int line;

	private CharSequence readPathElement(PushbackReader input) throws IOException {
		StringBuilder s = new StringBuilder();
		int c = input.read();
		while (c != PATH_SEPARATOR && !Character.isWhitespace(c) && c >= 0) {
			s.append((char) c);
			c = input.read();
		}
		if (c >= 0)
			input.unread(c);
		return s;
	}

	@Override
	public Routes parse(Reader input) throws IOException, ParseException {
		PushbackReader reader = new PushbackReader(input, 16);
		RouteTree tree = new RouteTree();
		line = 1;
		while (parseRoute(tree, reader)) {
		}
		return tree;
	}

	private RouteNode parsePath(RouteTree tree, String method, PushbackReader reader) throws IOException,
			ParseException {
		int c = reader.read();
		RouteNode node = tree.getRoot(method);
		if (node == null) {
			throw new ParseException("Unsupported HTTP method: " + method, line);
		}

		// TODO: check also if character is a valid path character
		while (!Character.isWhitespace(c) && c != '\n' && c >= 0) {

			if (node.isPathEnd() && c != PATH_SEPARATOR) {
				throw new ParseException("Expected path separator character: " + PATH_SEPARATOR + " but found "
						+ (char) c, line);
			}

			boolean nodeProcessed = false;
			for (RouteNode childNode : node.getChildren()) {
				try {
					if (childNode.matches(reader)) {
						node = childNode;
						nodeProcessed = true;
						break;
					}
				} catch (MatchException e) {
					if (childNode instanceof CharacterNode) {
						CharacterNode cn = (CharacterNode) childNode;
						cn.delegateToChild(e.getMatch());
						reader.skip(1 + e.getMatch().length());

						CharSequence path = readPathElement(reader);
						RouteNode newNode;
						if (path.length() > 0)
							newNode = new CharacterNode(path);
						else
							newNode = new EmptyNode();
						childNode.addChild(newNode);
						node = newNode;
						nodeProcessed = true;
					}
				}
			}
			if (!nodeProcessed) {
				CharSequence path = readPathElement(reader);
				RouteNode childNode;
				if (path.length() > 0)
					childNode = new CharacterNode(path);
				else
					childNode = new EmptyNode();
				node.addChild(childNode);
				node = childNode;
			}

			c = reader.read();

		}

		if (c < 0 || c == '\n') {
			throw new ParseException("Missing controller statement", line);
		}

		return node;
	}

	private void skipWhitespaces(PushbackReader reader) throws IOException {
		int c = reader.read();
		while (Character.isWhitespace(c)) {
			if (c == '\n')
				line++;
			c = reader.read();
		}
		reader.unread(c);
	}

	private boolean skipLine(PushbackReader reader) throws IOException {
		int c = reader.read();
		while (c != '\n' && c >= 0)
			c = reader.read();
		if (c == '\n')
			line++;
		return c >= 0;
	}

	private int parseJavaIdentifier(StringBuilder output, PushbackReader reader) throws IOException, ParseException {
		int c = reader.read();
		if (!Character.isJavaIdentifierStart(c)) {
			throw new ParseException("Invalid controller class name", line);
		} else {
			output.append((char) c);
		}

		c = reader.read();
		while (Character.isJavaIdentifierPart(c)) {
			output.append((char) c);
			c = reader.read();
		}

		if (c == '.') {
			output.append((char) c);
			return c; // expect more
		} else if (c == '\n' || c == -1) {
			return c; // line finished
		} else {
			throw new ParseException("Invalid controller class name", line);
		}
	}

	private int parseController(RouteNode node, PushbackReader reader) throws IOException, ParseException {
		StringBuilder s = new StringBuilder();
		int c = parseJavaIdentifier(s, reader);
		while (c == '.')
			c = parseJavaIdentifier(s, reader);
		node.setControllerClassName(s.toString());
		if (c == '\n')
			line++;
		return c;
	}

	private String parseMethod(PushbackReader reader) throws IOException, ParseException {
		StringBuilder s = new StringBuilder();
		int c = reader.read();
		while (!Character.isWhitespace(c)) {
			s.append((char) c);
			c = reader.read();
		}
		return s.toString();
	}

	public boolean parseRoute(RouteTree tree, PushbackReader reader) throws IOException, ParseException {
		skipWhitespaces(reader);
		int c = reader.read();
		if (c == '#') {
			return skipLine(reader);
		} else {
			reader.unread(c);
			String method = parseMethod(reader);
			skipWhitespaces(reader);
			RouteNode node = parsePath(tree, method, reader);
			skipWhitespaces(reader);
			return parseController(node, reader) >= 0;
		}
	}
}
