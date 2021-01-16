package client;

import java.io.Console;
import java.util.Observable;
import java.util.Observer;

import exceptions.EndOfProgram;
import util.Protocol;

/**
 * The thread responsible for user-client communication.
 * @author pmbos
 */
public class ConsoleInputThread extends Observable implements Runnable {
	private boolean run = false;
	private String result = null;
	boolean stop = false;
	
	/**
	 * Creates a new input handler with the given observer.
	 * @param observer the observer to add to this handler.
	 */
	public ConsoleInputThread(Observer observer) {
		this.addObserver(observer);
	}
	
	/**
	 * Reads and processes input.
	 */
	public synchronized void run() {
		Console console = System.console();
		while (!stop) {
			try {
				result = console.readLine();
				
				if (run) {
					this.setChanged();
					this.notifyObservers(result);
					run = false;
				}
				
				if (result.equalsIgnoreCase(Protocol.EXITCOMMAND)) {
					throw new EndOfProgram("Exit confirmed...");
				}
				
			} catch (EndOfProgram e) {
				stop = true;
			}
		}
	}
	
	/**
	 * Notifies that input should be sent to the observer.
	 */
	public void notifyMe() {
		run = true;
	}
	
	/**
	 * @return the result of the input request.
	 */
	public String getResult() {
		return result;
	}
}
