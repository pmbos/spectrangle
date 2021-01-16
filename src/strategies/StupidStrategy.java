package strategies;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.Board;
import model.Player;
import model.Tile;
import util.Protocol;
import model.Strategy;

/**
 * A class implementing a strategy which will always 
 * select the move for the highest amount of points.
 * @author pmbos
 */
public class StupidStrategy implements Strategy {
	private Player player;
	
	/**
	 * Creates a new <code>StupidStrategy</code> without a player.
	 */
	public StupidStrategy() {
		player = null;
	}
	
	/**
	 * Creates a new <code>StupidStrategy</code> with a player.
	 * @param player the player to add to this strategy.
	 */
	public StupidStrategy(Player player) {
		this.player = player;
	}

	@Override
	public String determineMove(Board board) {
		//Checks if the player has a move.
		if (player.hasPlay(board)) {
			Map<Tile, List<Integer>> moves = player.getPlays(board);
			Tile tile = null;
			int index = 1;
			int score = 0;
			int rotation = -1;
			Board copy = board.getCopy();
			
			//Checks all moves.
			for (Entry<Tile, List<Integer>> entry : moves.entrySet()) {
				//Checks all possible indices per move.
				for (Integer i : entry.getValue()) {
					//Checks all possible rotations.
					for (int j = 0; j < 3; j++) {
						//Checks for validity of the move.
						if (entry.getKey().isValid() && board.isValidMove(entry.getKey(), i, j)) {
							copy.setTile(i, entry.getKey(), j);
							//Checks if the move yields a higher score than the current highest.
							if (copy.getScore(i) > score) {
								//Flips the tile back into standard position if necessary.
								if (entry.getKey().isFlipped()) {
									entry.getKey().flip();
								}
								//Sets the score to the new highest.
								score = copy.getScore(index);
								//Creates a tile.
								tile = new Tile(entry.getKey().getColourL(),
										entry.getKey().getColourV(), entry.getKey().getColourR(),
										entry.getKey().getValue());
								
								index = i;
								rotation = j;
							}
						}
						//Removes the attempt from the board.
						copy.remove(i);
					}
				}
			}
			//If for any reason, tile ends up as null, will select the first tile
			//it can play.
			if (tile == null) {
				for (Entry<Tile, List<Integer>> toPlay : moves.entrySet()) {
					tile = toPlay.getKey();
					index = toPlay.getValue().get(0);
					for (int g = 0; g < 3; g++) {
						if (board.isValidMove(tile, index, g)) {
							rotation = g;
							break;
						}
					}
					break;
				}
			}
			//Updates the player's score.
			player.updateScore(score);
			//Returns a move request.
			assert tile != null;
			return constructMove(tile, rotation, index);
		} else {
			//If no move can be made, finds the tile with the lowest value and replaces is.
			Tile toReplace = null;
			int lowest = 100000;
			for (Tile tile : player.getHand()) {
				if (tile.getValue() < lowest) {
					toReplace = tile;
				}
			}
			assert toReplace != null;
			return constructTilereplace(toReplace);
		}
	}
	
	/**
	 * @return the name of this strategy.
	 */
	/*@pure */public String getName() {
		return "Tournament strategy";
	}
	
	/**
	 * @return the player assiociated with this strategy.
	 */
	/*@pure */public Player getPlayer() {
		return player;
	}
	
	/**
	 * @return the description f this strategy.
	 */
	/*@pure */public String getDescription() {
		return "A strategy that will always make the move "
					+ "resulting in the highest amount of points.";
	}
	
	/**
	 * Constructs a string representation of a move request.
	 * @param tile the tile to place.
	 * @param rotation the rotation to use.
	 * @param index the index of the field to place the tile on.
	 * @return a move request.
	 */
	//@requires tile.isValid();
	/*@pure */private String constructMove(Tile tile, int rotation, int index) {
		return Protocol.MOVE + Protocol.DELIMITER + tile.getColourL() + tile.getColourV()
			+ tile.getColourR() + tile.getValue() + Protocol.DELIMITER
			+ rotation + Protocol.DELIMITER + index;
	}
	
	/**
	 * Constructs a string representation of a replace request.
	 * @param tile the tile to replace.
	 * @return a replace request.
	 */
	//@requires tile.isValid();
	/*@pure */private String constructTilereplace(Tile tile) {
		return Protocol.TILEREPLACE + Protocol.DELIMITER + tile.getColourL()
			+ tile.getColourV() + tile.getColourR() + tile.getValue();
	}
	
	/**
	 * Sets the given player as the player associated with this strategy.
	 * @param player the player to associate with this strategy.
	 */
	//@requires player != null;
	//@ensures getPlayer() == player;
	public void setPlayer(Player player) {
		this.player = player;
	}
}
