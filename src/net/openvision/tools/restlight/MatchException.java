package net.openvision.tools.restlight;

public class MatchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4409316070239593381L;
	private CharSequence match;

	public MatchException(CharSequence match) {
		super("Action not found");
		this.match = match;
	}

	public MatchException() {
		this("");
	}

	public CharSequence getMatch() {
		return match;
	}

}
