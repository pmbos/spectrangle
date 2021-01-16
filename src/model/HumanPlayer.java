package model;

import java.io.IOException;
import java.net.Socket;

/**
 * A class modelling a human player in a game of spectrangle.
 * @author Alina Maximova, s2032074 
 */
public class HumanPlayer extends Player {
	/**
	 * Creates a new HumanPlayer with the specified name, socket and preference.
	 * @param name the name of the player.
	 * @param sock the socket the player uses for its connection.
	 * @param preference the amount of players this player wants to play with.
	 * @throws IOException when the IO streams cannot be created.
	 */
	public HumanPlayer(String name, Socket sock, int preference) throws IOException {
		super(name, sock, preference); 
	}
	
	/**
	 * Creates a new HumanPlayer with the specified name.
	 * @param name the name of the player.
	 * @throws IOException when the default IO streams cannot be created.
	 */
	public HumanPlayer(String name) throws IOException {
		super(name); 
	}
	
	/**
	 * Creates a new HumanPlayer with the specified name and preference.
	 * @param name the name of the player
	 * @param preference the amount of players this player wants to play with.
	 * @throws IOException when the default IO streams cannot be created.
	 */
	public HumanPlayer(String name, int preference) throws IOException {
		super(name, preference); 
	}
}
