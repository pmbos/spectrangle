package model;
import java.util.*;

import util.Protocol;

/**
 * This is the class describing a bag of tiles in the Spectrangle game.
 * @author Alina Maximova, s2032074
 *
 */
public class Bag {
	private List<Tile> bag;
	public static final int SIZE = 36;
	/**
	 * Constructor for class Bag.
	 */
	public Bag() {
		bag = new ArrayList<Tile>();
		for (Tile tile : Protocol.VALIDTILES) {
			bag.add(tile);
		}
	}

	/**
	 * Method for getting tiles left in the bag.
	 * @return all tiles that are currently in the bag
	 */
	/*@pure */public List<Tile> getTiles() {
		return bag;
	}
	/**
	 * Method for getting the number of tiles left in the bag.
	 * @return the current size of the bag
	 */
	/*@pure */public int getSize() {
		return bag.size();
	}
	/**
	 * Method that allows to see tiles left in the bag.
	 * @return the String representation of the Bag
	 */
	/*@pure */public String toString() {
		String res = "";
		for (int i = 0; i < bag.size(); i++) {
			res = res + bag.get(i).toString() + "\n";
		}
		return res;
	}
	/**
	 * Method for shuffling the bag with tiles.
	 */
	public void shuffle() {
		Collections.shuffle(bag);
	}
	/**
	 * Method for removing a given number of tiles
	 * from the bag and for placing it to a specific player.
	 * @param player a specific player
	 * @param num number of tiles for moving
	 */
	//@requires player != null;
	//@requires num >= 0 && num <= getSize();
	//@ensures getSize() == \old(getSize()) - num;
	public void dealHand(Player player, int num) {
		shuffle();
		if (bag.size() > 0) {
			if (num >= bag.size()) {
				for (int i = 0; i < bag.size(); i++) {
					player.add(bag.get(i));
					bag.remove(i);
				}
			} else {
				for (int i = 0; i < num; i++) {
					player.add(bag.get(i));
					bag.remove(i);
				}
			}
			
		} 
		
	}
	/**
	 * Method for adding a tile to the bag.
	 * @param tile the tile which needs to be placed to the bag
	 */
	//@requires getSize() < 36;
	//@requires getTiles().contains(tile) == false;
	//@ensures getSize() == \old(getSize()) + 1;
	public void add(Tile tile) {
		bag.add(tile);
	}
}
