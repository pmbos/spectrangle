package strategies;

import model.Player;
import model.Strategy;

/**
 * A class managing all the strategies.
 * @author pmbos
 */
public class Strategies {
	public static final Strategy[] STRATEGIES = {new StupidStrategy()};
	
	/**
	 * Fetches a specific strategy from the array.
	 * @param i the index of the strategy to fetch.
	 * @param player the player to add to the strategy.
	 * @return the strategy the user selected.
	 */
	//@requires player != null;
	public static Strategy get(int i, Player player) {
		if (STRATEGIES[i] instanceof StupidStrategy) {
			((StupidStrategy) STRATEGIES[i]).setPlayer(player);
		}
		
		return STRATEGIES[i];
	}
	
	/**
	 * @return a string displaying all available strategies.
	 */
	public static String display() {
		StringBuilder result = new StringBuilder();
		int i = 1;
		for (Strategy strategy : STRATEGIES) {
			result.append(i).append(": ").append(strategy.getName()).append(" | ")
					.append(strategy.getDescription()).append("\n");
			i++;
		}
		
		return result.toString();
	}
}
