package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

import exceptions.EndOfProgram;
import util.Protocol;

/**
 * The UI class for the server.
 * @author pmbos
 */
public class ServerUI implements Observer {
	PrintWriter console = new PrintWriter(System.out, true);
	HandleConsoleInputThread consoleHandler;
	Thread inputHandler;
	String received = null;
	
	/**
	 * Creates a new instance of this class.
	 */
	public ServerUI() {
		consoleHandler = new HandleConsoleInputThread(this);
		inputHandler = new Thread(consoleHandler);
		inputHandler.setName("Console input handler");
		inputHandler.start();
	}
	
	/**
	 * Shows the user that the server is started.
	 * @param location the location this message gets sent from.
	 */
	public void showServerStarted(String location) {
		write("Ready for connections", location);		
	}
	
	/**
	 * Writes the specified message and location to standard output.
	 * @param msg the message to write to standard output.
	 * @param location the location from where the call is made.
	 */
	public void write(String msg, String location) {
		console.println("(" + location + ") " + msg);
	}
	
	/**
	 * Reads an integer from standard input.
	 * @param msg the message to write before reading.
	 * @return the integer the user entered.
	 * @throws NumberFormatException when the user entered an invalid integer.
	 * @throws IOException when the server cannot read from the input stream.
	 * @throws EndOfProgram when the program ended.
	 */
	public int readInt(String msg) throws NumberFormatException, IOException, EndOfProgram {
		int result = Integer.parseInt(readString(msg));
		received = null;
		return result;
	}
	
	/**
	 * Writes an error message to standard output.
	 * @param e the exception to write.
	 * @param location the location where the call is made.
	 */
	public void writeError(Exception e, String location) {
		write(e.getMessage(), location);
	}
	
	/**
	 * Shows the compatible server extensions.
	 */
	public void showExtensions() {
		for (char extension : Server.EXTENSIONS) {
			write("" + extension, "HandleConsoleInputThread");
		}
	}
	
	/**
	 * Asks the user for the port on which to open the server.
	 * @return the port number the user entered.
	 * @throws NumberFormatException when the user entered an invalid integer.
	 * @throws IOException when the server cannot read from the input stream.
	 * @throws EndOfProgram when the program ended.
	 */
	public int askPort() throws IllegalArgumentException, IOException, EndOfProgram {
		try {
			return readInt("Please enter the port number on which the server will listen: ");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Enter a valid port number");
		}
		
	}
	
	/**
	 * Reads a string from standard input.
	 * @param msg the message to write before reading the input.
	 * @return the user's input.
	 * @throws EndOfProgram when the program ended.
	 */
	public String readString(String msg) throws EndOfProgram {
		write(msg, "UI");
		getInput();
		return received;
	}
	
	/**
	 * Tells the input handler to start sending input.
	 * @throws EndOfProgram when the program ended.
	 */
	private synchronized void getInput() throws EndOfProgram {
		try {
			wait();
			if (received.equalsIgnoreCase(Protocol.EXITCOMMAND)) {
				throw new EndOfProgram("Exit confirmed...");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if (arg instanceof String) {
			received = consoleHandler.getResult();
			this.notify();
		} else {
			write("Error: argument not of type string", "UI");
		}
	}
	
	
}
