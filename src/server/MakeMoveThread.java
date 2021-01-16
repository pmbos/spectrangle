package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.Callable;

import util.Protocol;

/**
 * The actual reading class of the timed reader.
 * @author constructed from the Internet.
 */
public class MakeMoveThread implements Callable<String> {
	private final BufferedReader input;
	
	public MakeMoveThread(BufferedReader input) {
		this.input = input;
	}
	
	/**
	 * Reads data from the specified input stream.
	 */
	public synchronized String call() throws IOException {
	    String in;
	    do {
		    try {
		      // wait until we have data to complete a readLine()
		    	int first = ' ';
		        while (!input.ready()) { 
		            Thread.sleep(Protocol.INPUTCHECKINTERVAL);
		            first = input.read();
		            if (first == -1) {
		            	throw new IOException("Client disconnected!");
		            }
		        }
		        //Reads from input and reconstructs the message if needed.
		        in = input.readLine();
		        if (first != ' ') {
		        	in = (char) first + in;
		        }
		        
		        break;
		    } catch (InterruptedException e) {
		        return null;
		    } catch (SocketException e) {
		    	throw new IOException("Client disconnected!");
		    }
	    } while ("".equals(in));
	    return in;
	}
}
