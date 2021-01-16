package exceptions;

public class IllegalBoot extends IllegalArgumentException {
	/**
	 * An exception thrown when the parameters of the server boot don't 
	 * comply with the standard.
	 */
	private static final long serialVersionUID = 7359369483958789020L;

	public IllegalBoot(String arg) {
		super(arg);
	}
}
