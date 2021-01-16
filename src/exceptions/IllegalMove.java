package exceptions;

public class IllegalMove extends Exception {
	/**
	 * An exception thrown when an Illegal move is attempted.
	 */
	private static final long serialVersionUID = -425423037454433861L;

	public IllegalMove(String msg) {
		super(msg);
	}
}
