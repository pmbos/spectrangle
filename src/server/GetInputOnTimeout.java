package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A class which reads input from the input stream and stops after a given amount of time.
 * @author constructed from the Internet.
 */
public class GetInputOnTimeout {
	private final int tries;
	private final int timeout;
	private final TimeUnit unit;
	private final BufferedReader in;
	
	public GetInputOnTimeout(int tries, int timeout, BufferedReader in,
			TimeUnit unit) {
	    this.tries = tries;
	    this.timeout = timeout;
	    this.in = in;
	    this.unit = unit;
    }
	
	/**
	 * Starts the timer and attempts to read from input.
	 * @return the input read over the given stream.
	 * @throws InterruptedException when the reading is interrupted.
	 * @throws IOException when a client disconnects while reading.
	 */
	public String readLine() throws InterruptedException, IOException {
	    ExecutorService ex = Executors.newSingleThreadExecutor();
	    String input = null;
	    try {
	        for (int i = 0; i < tries; i++) { 
	        	//Instructs a future to fetch a result.
	            Future<String> result = ex.submit(
	                new MakeMoveThread(in));
	            try { 
	            	//Fetches the result.
	                input = result.get(timeout, unit);
	                break;
	            } catch (ExecutionException e) {
	                throw new IOException("Client disconnected!");
	            } catch (TimeoutException e) {
	                result.cancel(true);
	            }
	        }
	    } finally {
	        ex.shutdownNow();
	    }
	    return input;
	}
}
