package net.openvision.tools.restlight;

public class UnsupportedMethodException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1820721316035947119L;

	public UnsupportedMethodException(String method) {
		super("The method " + method + " is not supported.");
	}

}
