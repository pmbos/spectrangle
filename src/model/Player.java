package model;
import java.util.*;

import util.ColourCompare;
import util.Protocol;

import java.io.*;
import java.net.*;

/**
 * Abstract class for keeping a player in the Spectrangle game.
 * @author Alina Maximova, s2032074
 */
public abstract class Player {
	private final String name;
	private int score;
	private final List<Tile> hand;
    private BufferedReader in;
    private PrintWriter out;
    private Socket sock;
    private int preference;
    
	/**
	 * Constructor of the abstract class Player.
	 * @param name the name of the player.
	 * @param sock the connection socket.
	 * @param preference the player's preference.
	 * @throws IOException when IO streams cannot be created.
	 */
	/*@ensures this.name() == name
	  	&& this.getPreference() == preference
	  	&& getScore() == 0
	  	&& getInput() != null
	  	&& getOutput() != null;
	 */
	public Player(String name, Socket sock, int preference) throws IOException {
		this.hand = new ArrayList<>();
		this.score = 0;
		this.name = name;
        this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.out = new PrintWriter(sock.getOutputStream(), true);
        this.sock = sock;
        this.preference = preference;
	}
	/**
	 * Constructor of the abstract class Player.
	 * @param name name for creating a player with it
	 * @param preference the preference with which to make the player.
	 */
	/*@ensures this.name() == name
  		&& this.getPreference() == preference
  		&& getScore() == 0;
 */
	public Player(String name, int preference) {
		this.hand = new ArrayList<>();
		this.name = name;
		this.preference = preference;
		score = 0;
		this.in = new BufferedReader(new InputStreamReader(System.in));
        this.out = new PrintWriter(System.out, true);
	}
	
	/**
	 * Constructor of the abstract class Player.
	 * @param name name for creating a player with it
	 */
	/*@ensures this.name() == name
  		&& getScore() == 0;
  	*/
	public Player(String name) {
		this.hand = new ArrayList<>();
		this.name = name;
		score = 0;
	}
    
    /**
     * @return the preferred amount of players this player wants to play with.
     */
    /*@pure */public int getPreference() {
    	return this.preference;
    }
    
    /**
     * @return the socket associated with this player.
     */
    /*@pure */public Socket getSocket() {
    	return sock;
    }
    
    /**
     * Method for getting the player's score.
     * @return score
     */
    /*@pure */public int getScore() {
    	return score;
    }
    
    /**
     * @return the input stream for this player.
     */
    /*@pure */public BufferedReader getInput() {
    	return this.in;
    }
    
    /**
     * @return the output stream for this player.
     */
    /*@pure */public PrintWriter getOutput() {
    	return this.out;
    }
	/**
	 * Method for getting the name of the player.
	 * @return the name of the player
	 */
	/*@ pure */public String name() {
		return name;
	}
	/**
	 * Method for getting the list of tiles in the player's hand.
	 * @return the list of tiles in the player's hand
	 */
	/*@ pure */public List<Tile> getHand() {
		return hand;
	}

	/**
	 * Method for getting String description of tiles in the player's hand.
	 * @return the String representation of the player's hand
	 */
	/*@pure */public String toString() {
		String res = "";
		
		int f = 0;
		while (f < 5) {
			for (Tile tile : hand) {				
				String rep = tile.graphicRepresentation(0);

				switch (f) {
					case 0:
						res += rep.substring(0, 8) + "    ";
						break;
					case 1:
						res += rep.substring(10, 18) + "    ";
						break;
					case 2:
						res += rep.substring(20, 28) + "    ";
						break;
					case 3:
						res += rep.substring(30, 38) + "    ";
						break;
					case 4:
						res += rep.substring(40, 48) + "    ";
						break;
				}
			}
			res += "\n";
			f++;
		}
		return res;
	}
	/**
	 * Method for counting the number of tiles in the player's hand.
	 * @return the number of tiles in a player's hand.
	 */
	//@ensures \result == getHand().size();
	/*@pure */public int numInHand() {
		return getHand().size();
	}
	/**
	 * Method checks if there are any tiles in the player's hand.
	 * @return true if there are no tiles
	 */
	//@ensures \result == getHand().isEmpty();
	/*@pure */public boolean emptyHand() {
		return getHand().isEmpty();
	}
	/**
	 * Method for removing the tile with the specified index.
	 * @param index the index of the tile to remove.
	 */
	//@requires index >= 0 && index < getHand().size();
	//@ensures numInHand() == \old(numInHand()) - 1;
	public void removeTile(int index) {
		getHand().remove(index);
	}
	/**
	 * Method for removing the tile with the specified index.
	 * @param tile the tile to be removed 
	 */
	//@requires tile != null;
	//@ensures \old(getHand().contains(tile)) ==> !getHand().contains(tile); 
	public void removeTile(Tile tile) {
		hand.removeIf(tile1 -> tile1.equals(tile));
	}
	/**
	 * Method check if the specified tile is in the hand.
	 * @param tile the tile to check.
	 * @return true if the tile is in the player's hand
	 */
	//@requires tile != null;
	//@ensures \result == getHand().contains(tile);
	/*@pure */public boolean hasTileInHand(Tile tile) {
		for (Tile piece : hand) {
			if (piece.equals(tile)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if this <code>Player</code> has a play.
	 * 
	 * @param board a copy of the board to perform the check with.
	 * @return true if the <code>Player</code> can make a play, false otherwise.
	 */
	//@requires board != null;
	/*@pure */public boolean hasPlay(Board board) {
		if (board.isFull()) {
			return false;
		}

		for (int i = 0; i < Board.MAX_FIELDS; i++) {
			List<Character> neighbours = board.edges(i);

			if (board.isEmpty(i) && neighbours.size() > 0) {
				for (Tile tile : this.getHand()) {
					for (int j = 0; j < 3; j++) {
						if (ColourCompare.checkPermutation(tile, i, neighbours, board, j)) {
							return true;
						}
					}
					if (tile.equals(Protocol.JOKER)) {
						return true;
					}
				}

			} else if (board.isEmpty(i) && board.boardEmpty()) {
				return SpectrangleBoardPrinter.BONUS.get(i) == 1;
			}
		}
		return false;
	}
	
	/**
	 * Finds the indices of all the fields the player can put a tile on, per tile.
	 * 
	 * @param board a copy of the board to use in the checks.
	 * @return a map with tiles as keys and a list of their possible locations as
	 *         values.
	 */
	/*@pure */public Map<Tile, List<Integer>> getPlays(Board board) {
		Map<Tile, List<Integer>> result = new HashMap<>();
		if (!hasPlay(board)) {
			return result; 
		}

		for (Tile tile : this.getHand()) {
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < Board.MAX_FIELDS; i++) {
				List<Character> neighbours = board.edges(i);
				if (board.isEmpty(i) && neighbours.size() > 0) {
					ColourCompare.compare(tile, i, neighbours, board, list);
				} else if (board.isEmpty(i) && board.boardEmpty() && 
						SpectrangleBoardPrinter.BONUS.get(i) == 1) {
					list.add(i);
				}
			}
			result.put(tile, list);
		}
		return result;
	}
	
	 /**
     * Method for setting the player's score.
     * @param num the amount by which a score needs to be increased
     */
    //@ensures getScore() == \old(getScore()) + num;
    public void updateScore(int num) {
    	score += num;
    }
	
	/**
	 * Method for adding a tile to the player's hand.
	 * @param tile the tile which needs to be placed to the hand
	 */
	//@ensures numInHand() == \old(numInHand()) + 1;
	public void add(Tile tile) {
		hand.add(tile);
	}
	
	/**
	 * Method for calculating the total value of the player's current hand.
	 * @return the sum of values of the tiles
	 */
	//@ensures \forall int i; i < numInHand(); \result == 0; 
	//				\result += getHand().get(i).getValue());
	/*@pure */public int totalValue() {
		int counter = 0;
		for (Tile piece : hand) {
			counter += piece.getValue();
		}
		return counter;
	}
	/**
	 * Method for shutting down the socket.
	 */
	/*
	 * @ensures getInput().close() &&
	  			getOutput().close() &&
	  			getSocket().close();
	 */
    public void shutdown() {
        try {
        	in.close();
        	out.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
