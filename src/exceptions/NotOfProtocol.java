package exceptions;

public class NotOfProtocol extends Exception {
	/**
	 * An exception thrown when it is clear that the input does not correspond to the protocol.
	 */
	private static final long serialVersionUID = 8348504770092351996L;

	public NotOfProtocol(String msg) {
		super(msg);
	}
}
