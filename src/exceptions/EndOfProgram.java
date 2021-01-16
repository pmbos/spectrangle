package exceptions;

public class EndOfProgram extends Exception {
	private static final long serialVersionUID = 8231254858439699643L;

	/**
	 * An exception thrown when the program terminates through the
	 * exit command.
	 */
	/**
	 * Constructs a new EOP exception.
	 * @param msg the message to use.
	 */
	public EndOfProgram(String msg) {
		super(msg);
	}
}
