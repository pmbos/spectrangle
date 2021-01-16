package model;

/**
 * @author PMBos 
 * A class modelling a computer player in a game of spectrangle.
 */
public class ComputerPlayer extends Player {
	Strategy strategy;

	/**
	 * Creates a computer player with the specified name, strategy and preference.
	 * @param name the name of the player.
	 * @param strategy the strategy the player should use.
	 * @param preference the amount of players the player wants to play with.
	 */
	public ComputerPlayer(String name, Strategy strategy, int preference) {
		super(name, preference);
		this.strategy = strategy;
	}
	
	/**
	 * Creates a computer player with the specified name and preference.
	 * @param name the name of the player.
	 * @param preference the amount of players the player wants to play with.
	 */
	public ComputerPlayer(String name, int preference) {
		super(name, preference);
	}
	
	/**
	 * @return the strategy of the player.
	 */
	/*@pure */public Strategy getStrategy() {
		return strategy;
	}
	
	/**
	 * Sets the strategy of the player to the specified strategy.
	 * @param strategy the strategy to play with.
	 */
	/*
	 * @requires strategy != null;
	 * @ensures getStrategy() == strategy;
	 */
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Determines a move according to the current state of the board.
	 * @param board a copy of the current state of the board.
	 * @return a string representing the player's move.
	 */
	//@requires board != null;
	public String determineMove(Board board) {
		return strategy.determineMove(board);
	}
}
