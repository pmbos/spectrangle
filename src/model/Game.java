package model;

import java.util.List;
import java.util.Observable;

import exceptions.IllegalMove;
import server.Controller;
import util.Protocol;

import java.util.ArrayList;

/**
 * A class modelling the Game controller of a game of spectrangle.
 * @author PMBos
 */
public class Game extends Observable {
	public static final int MAX_PLAYERS = 4; 
	public static final int MIN_PLAYERS = 2;
	private final int preference;
	private final List<Player> players;
	private Player currentPlayer;
	private final Board board;
	private final Bag bag;

	/**
	 * Creates a new <code>Game</code> instance with the given id. Creating a new
	 * <code>Board</code> and <code>Bag</code>.
	 * @param preference the preference of the entire game.
	 * @param cont the controller to sue as an observer.
	 */
	public Game(int preference, Controller cont) {
		this.addObserver(cont);
		players = new ArrayList<>();
		board = new Board();
		bag = new Bag();
		this.preference = preference;
	}

	// Queries
	
	/**
	 * @return the list of players in this game.
	 */
	/*@pure */public List<Player> getPlayers() {
		return this.players;
	}
	
	/**
	 * @return the preferred amount of players.
	 */
	/*@pure */public int getPreference() {
		return preference;
	}

	/**
	 * @return the number of players in a game.
	 */
	//@ensures \result == getPlayers().size(); 
	/*@pure */public int getNumberOfPlayers() {
		return players.size();
	}

	/**
	 * @return the <code>Player</code> whose turn it is now.
	 */
	/*@pure */public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Calculates which player is going next.
	 * 
	 * @return the <code>Player</code> that has the next turn.
	 */
	/* @requires getCurrentPlayer() != null;
	   @ensures \result == getPlayers().get((players.indexOf(getCurrentPlayer()) + 1) 
	 			% getNumberOfPlayers());
	 */
	/*@pure */public Player getNextPlayer() {
		if (getNumberOfPlayers() > 0) {
			return players.get((players.indexOf(getCurrentPlayer()) + 1) % getNumberOfPlayers());
		}
		return null;
	}

	/**
	 * @return a copy of the <code>Board</code> being used for the game.
	 */
	/*@pure */public Board getBoard() {
		return board.getCopy();
	}

	/**
	 * @return the <code>Bag</code> used in this game.
	 */
	/*@pure */public Bag getBag() {
		return bag;
	}

	/**
	 * Determines if a game is over. Conditions for a game over are: a) the board is
	 * full. b) the board is not full, but no player can make another move.
	 * 
	 * @return true if the game is over, false otherwise.
	 */
	/*@pure */public boolean gameOver() {
		for (Player player : players) {
			if (((player.hasPlay(getBoard().getCopy()) || bag.getSize() > 0) 
					&& !getBoard().isFull()) 
					|| getBoard().boardEmpty()) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Checks if the rotation specified is valid.
	 * @param rotation the rotation to check.
	 * @return true if the rotation is valid, false otherwise.
	 */
	/*@ensures \result == (rotation == Protocol.ROTATION1 
	   || rotation == Protocol.ROTATION2 
	   || rotation == Protocol.ROTATION3);
	*/
	/*@pure */public boolean isValidRotation(int rotation) {
		return rotation == Protocol.ROTATION1 
				|| rotation == Protocol.ROTATION2 
				|| rotation == Protocol.ROTATION3;
	}

	/**
	 * Finds the victor of the game.
	 * 
	 * @return the <code>Player</code> that won the game.
	 */
	//@requires getPlayers() != null;
	//@ensures \result != null;
	/*@pure */public Player getVictor() {
		Player victor = null;
		for (Player player : players) {
			if (victor == null) {
				victor = player;
			} else {
				if (player.getScore() > victor.getScore()) {
					victor = player;
				}
			}
		}
		return victor;
	}

	// Commands

	/**
	 * Sets the current player equal to the given player.
	 * 
	 * @param player the player to set as current.
	 */
	// @requires player != null;
	// @ensures getCurrentPlayer() == player;
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}

	/**
	 * Adds the given player to the list of players participating in this game.
	 * 
	 * @param player the player to add.
	 */
	// @requires player != null;
	// @ensures getNumberOfPlayers() == \old(getNumberOfPlayers() + 1);
	public void addPlayer(Player player) {
		players.add(player);
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Attempts to move the given tile to the given index. An attempt succeeds if:
	 * The index is valid. The tile is valid. The rotation is valid. The move is valid.
	 * 
	 * @param tile  the tile to move.
	 * @param index the index to move the tile to. Throws an Exception if the
	 *              attempt failed.
	 * @param rotation the rotation of the tile to attempt.
	 * @throws IllegalMove when the move is not legal.
	 */
	//@requires tile != null;
	public void attemptMove(Tile tile, int index, int rotation) throws IllegalMove {
		if (!board.isValidField(index)) {
			throw new IllegalMove("Error: invalid field!");
		}
		if (!board.isEmpty(index)) {
			throw new IllegalMove("field not empty");
		}
		if (!tile.isValid()) {
			throw new IllegalMove("Error: tile invalid");
		}
		if (!isValidRotation(rotation)) {
			throw new IllegalMove("rotation invalid: " + rotation);
		}
		if (!board.isValidMove(tile, index, rotation)) {
			throw new IllegalMove("Error: invalid move");
		}
		
		if (board.isValidField(index)
				&& board.isEmpty(index)
				&& tile.isValid()
				&& isValidRotation(rotation)
				&& board.isValidMove(tile, index, rotation)) {
			board.setTile(index, tile, rotation);
		} else {
			throw new IllegalMove("Error: cannot move " + tile + " to " + index);
		}
	}

	/**
	 * Removes the given tile from the players hand and gives him a new one.
	 * @param tile the tile to exchange.
	 */
	/* @requires isValidTile(tile);
	   @ensures \old(getCurrentPlayer().getHand().contains(tile)) ==>
				!getCurrentPlayer().getHand().contains(tile) &&
				getCurrentPlayer().numInHand() == \old(getCurrentPlayer().numInHand()) &&
				getBag().getSize() == \old(getBag().getSize()) + 1;
	 */
	public void exchangeTile(Tile tile) {
		getCurrentPlayer().removeTile(tile);
		getBag().dealHand(getCurrentPlayer(), 1);
		getBag().add(tile);
	}

}
