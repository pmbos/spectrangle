package exceptions;

import java.io.IOException;

public class CannotConnect extends IOException {
	/**
	 * An exception thrown when the server socket cannot be opened or a
	 * client socket is unable to connect.
	 */
	private static final long serialVersionUID = 5014642581063206190L;

	public CannotConnect(String msg) {
		super(msg);
	}
}
