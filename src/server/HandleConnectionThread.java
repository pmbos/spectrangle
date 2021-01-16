package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import model.HumanPlayer;
import util.Protocol;

public class HandleConnectionThread extends Thread {
	//@invariant getID() > 0 && getSocket() != null && getGameMaker() != null;
	private final int id;
	private BufferedReader input;
	private PrintWriter output;
	private final Socket socket;
	private final GameMakerThread gameMaker;
	private final ServerUI tui;
	
	/**
	 * Creates a connection handler with the specified id, socket and gameMaker.
	 * @param id the client's unique id.
	 * @param socket the socket through which the client communicates with the server.
	 * @param gameMaker the thread responsible for finding a game.
	 * @param tui the user interface to write to.
	 */
	public HandleConnectionThread(int id, Socket socket, GameMakerThread gameMaker, ServerUI tui) {
		this.id = id;
		this.socket = socket;
		this.gameMaker = gameMaker;
		this.tui = tui;
	}
	
	/**
	 * Retrieves the name, extensions and preference of the client.
	 * Then generates a <code>Player</code> and adds it to the game maker's queue.
	 */
	public void run() {
		//Sets up all necessary data for a game to be played.
		String name = "";
		char[] extensionsClient;
		List<Character> matchingExtensions = new ArrayList<>();
		int playerPreference = -1;
		boolean okay = true;
		
		try {
			//Attempts to create IO streams.
			createIO();
		} catch (IOException e) {
			tui.writeError(e, "HandleConnectionThread");
			okay = false;
		}
		
		try {
			//Waits for a connect request, then sends a connect accept response.
			String[] connectRequest = waitForConnect(); 
			name = connectRequest[1];
			if (connectRequest.length == 3) {
				extensionsClient = connectRequest[2].toCharArray();
				for (char extension : extensionsClient) {
					for (char ext : Server.EXTENSIONS) {
						if (ext == extension) {
							matchingExtensions.add(extension);
						}
					}
				}
			}
			
		} catch (IOException e) {
			tui.writeError(e, "HandleConnectionThread");
			okay = false;
		}
		
		try {
			output.println(constructAccept(matchingExtensions));
		
			//Waits for a join request and responds by adding the player to the 
			//game maker queue.
			String[] joinRequest = waitForJoin();
			playerPreference = Integer.parseInt(joinRequest[1]);
		} catch (IOException e) {
			tui.writeError(e, "HandleConnectionThread");
			okay = false;
		} catch (NumberFormatException e) {
			tui.writeError(e, "HandleConnectionThread");
		}
		
		try {
			if (okay) {
				gameMaker.addToQueue(new HumanPlayer(name, socket, playerPreference));
			}
		} catch (IOException e) {
			tui.writeError(e, "HandleConnectionThread");
		}
		
	}
	
	//Queries
	/*@pure */public int getID() {
		return this.id;
	}
	
	/*@pure */public Socket getSocket() {
		return this.socket;
	}
	
	/*@pure */public GameMakerThread getGameMaker() {
		return this.gameMaker;
	}
	
	/*@pure */public BufferedReader getInput() {
		return this.input;
	}
	
	/**
	 * Waits for a Protocol.Command.CONNECTIONREQUEST.
	 * @return a <code>String[]</code> containing the information of the request. 
	 * @throws IOException when data cannot be read from the input stream.
	 */
	
	//@requires getInput() != null;
	/*@pure */private String[] waitForConnect() throws IOException {
		String in;
		while (true) {
			in = input.readLine();
			String[] parts = in.split(Protocol.DELIMITER);
			if (parts[0].equals(Protocol.CONNECTREQUEST)) {
				return parts;
			}
		}
	}
	
	/**
	 * Waits for a Protocol.Command.JOINGAME request.
	 * @return a <code>String[]</code> containing the information of the request.
	 * @throws IOException when data cannot be read from the input stream.
	 */
	//@requires getInput() != null;
	/*@pure */private String[] waitForJoin() throws IOException {
		String in;
		while (true) {
			in = input.readLine();
			String[] parts = in.split(Protocol.DELIMITER);
			if (parts[0].equals(Protocol.JOINGAME)) {
				return parts;
			}
		}
	}
	
	/**
	 * Constructs a <code>String</code> to send a Protocol.Command.CONNECTIONACCEPT.
	 * @param extensions a list of the matching server-client extensions.
	 * @return a <code>String</code> to be send to the client 
	 * as a Protocol.Command.CONNECTIONACCEPT.
	 */
	//@requires extensions != null;
	/*@pure */private String constructAccept(List<Character> extensions) {
		StringBuilder extension = new StringBuilder();
		while (extensions.iterator().hasNext()) {
			extension.append(extensions.iterator().next());
		}
		return Protocol.CONNECTACCEPT + Protocol.DELIMITER + extension.toString() + "\n";
	}
	
	//Commands
	/**
	 * Creates the IO channels for server-client communication.
	 * @throws IOException when it fails to create IO streams.
	 */
	/*
	 * @ensures getInput() != null;
	 */
	private void createIO() throws IOException {
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(), true);
	}
	
	
}
