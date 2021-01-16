package exceptions;

public class CannotCreatePlayer extends Exception {
	/**
	 * An exception thrown when a player cannot be created.
	 */
	private static final long serialVersionUID = -3141558447025423690L;

	public CannotCreatePlayer(String msg) {
		super(msg);
	}
}
