package model;
/**
 * Interface for determining the next move in the game Spectrangle.
 * @author Alina Maximova, s2032074
 *
 */
public interface Strategy {
	/**
	 * Method for determining the next move.
	 * @param board the current game board
	 * @return string representation of the next move
	 */
	//@requires board != null;
	public String determineMove(Board board);
	
	/**
	 * @return the name of this strategy.
	 */
	/*@pure */public String getName();
	
	/**
	 * @return the description of this strategy.
	 */
	/*@pure */public String getDescription();
}
