package server;

import java.io.Console;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import exceptions.EndOfProgram;
import util.Protocol;

/**
 * A class for handling console input.
 * @author pmbos
 */
public class HandleConsoleInputThread extends Observable implements Runnable {
	private String result = null;
	boolean stop = false;
	
	/**
	 * Creates a new instance of this class with the given observer.
	 * @param observer the observer to add to this class.
	 */
	public HandleConsoleInputThread(Observer observer) {
		this.addObserver(observer);
	}
	
	/**
	 * Runs the thread, waits until input is ready then reads it and passes it to the
	 * observer.
	 */
	public synchronized void run() {
		//Uses the system's native console to fetch input (NOTE: will not work in an IDE but will
		//in a terminal/command prompt) Tests were performed with a buffered reader.
		Console input = System.console();
		while (!stop) {
			try {
				String in = input.readLine();
				result = in;
				
				this.setChanged();
				this.notifyObservers(result);
				
				if (result.equalsIgnoreCase(Protocol.EXITCOMMAND)) {
					throw new EndOfProgram("Exit confirmed");
				}

				
			} catch (EndOfProgram e) {
				try {
					if (Server.getServerSocket() != null) {
						Server.getServerSocket().close();
					}
					break;
				} catch (IOException e1) {
					break;
				}
			}
		}
	}
	
	/**
	 * @return the result of the input query.
	 */
	public String getResult() {
		return result;
	}
}
