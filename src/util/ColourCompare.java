package util;

import java.util.ArrayList;
import java.util.List;

import model.Board;
import model.Field;
import model.Tile;

/**
 * @author pmbos
 */
public class ColourCompare {
	/**
	 * Adds all valid indices to the given list.
	 * @param tile the tile to check.
	 * @param index the index to check.
	 * @param neighbours the neighbours to check.
	 * @param copy a copy of the current state of the board.
	 * @param list the list to add the indices to.
	 */
	/*
	 * @requires tile != null 
	 			&& copy.isValidIndex(index)
	 			&& \forall(int i = 0; i < neighbours.size(); 
	 				copy.isValidNeighbour(neighbours.get(i)));
	 			&& copy != null;
	 */
	public static void compare(Tile tile, int index, List<Character> neighbours, 
			Board copy, List<Integer> list) {
		if (tile.equals(Protocol.JOKER)) {
			list.add(index);
		} else {
			for (int j = 0; j < 3; j++) {
				if (checkPermutation(tile, index, neighbours, copy, j)) {
					list.add(index);
				}
			}
		}
		
	}
	
	/**
	 * Checks the permutation identified by the given tile, index and rotation to see
	 * if it is valid.
	 * @param tile the tile to check.
	 * @param index the index to check.
	 * @param neighbours the neighbours to check.
	 * @param copy a copy of the current state of the board.
	 * @param rotation the rotation to check.
	 * @return true if the permuation is valid, false otherwise.
	 */
	/*
	 * @requires tile != null 
	 			&& copy.isValidIndex(index)
	 			&& \forall(int i = 0; i < neighbours.size(); 
	 				copy.isValidNeighbour(neighbours.get(i)));
	 			&& copy != null
	 			&& copy.isValidRotation(rotation);
	 */
	/*@pure */public static boolean checkPermutation(
				Tile tile, int index, List<Character> neighbours, 
			Board copy, int rotation) {
		List<Boolean> list = new ArrayList<>();

		//Checks if the supplied tile is a joker.
		if (tile.equals(Protocol.JOKER)) {
			return true;
		}
		//Creates a copy of the tile.
		Tile copyofTile = new Tile(tile.getColourL(), tile.getColourV(), 
				tile.getColourR(), tile.getValue());
		//Flips the tile if needed.
		if (copy.isUp(index) && copyofTile.isFlipped()) {
			copyofTile.flip();
		} else if (!copy.isUp(index) && !copyofTile.isFlipped()) {
			copyofTile.flip();
		}
		
		for (Character neighbour : neighbours) {
			Field field = copy.getField(copy.getNeighbour(index, neighbour));
			switch (neighbour) {
			//For each neighbour, checks if the adjoining colours match.
				case 'L':
					if (rotation == 0) {
						if (field.getColourR() == copyofTile.getColourL()
								|| field.getColourR() == Protocol.WHITE) {
							list.add(true);
						} else {
							list.add(false);
						}
					} else if (rotation == 1) {
						if (copyofTile.isFlipped()) {
							if (field.getColourR() == copyofTile.getColourR()
									|| field.getColourR() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						} else {
							if (field.getColourR() == copyofTile.getColourV()
									|| field.getColourR() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						}
						
					} else if (rotation == 2) {
						if (copyofTile.isFlipped()) {
							if (field.getColourR() == copyofTile.getColourV()
									|| field.getColourR() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						} else {
							if (field.getColourR() == copyofTile.getColourR()
									|| field.getColourR() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						}
						
					} else {
						return false;
					}
					
					break;
				case 'V':
					if (rotation == 0) {
						if (field.getColourV() == copyofTile.getColourV()
								|| field.getColourV() == Protocol.WHITE) {
							list.add(true);
						} else {
							list.add(false);
						}
					} else if (rotation == 1) {
						if (copyofTile.isFlipped()) {
							if (field.getColourV() == copyofTile.getColourL()
									|| field.getColourV() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						} else {
							if (field.getColourV() == copyofTile.getColourR()
									|| field.getColourV() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						}
						
					} else if (rotation == 2) {
						if (copyofTile.isFlipped()) {
							if (field.getColourV() == copyofTile.getColourR()
									|| field.getColourV() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						} else {
							if (field.getColourV() == copyofTile.getColourL()
									|| field.getColourV() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						}
						
					} else {
						return false;
					}
					break;
				case 'R':
					if (rotation == 0) {
						if (field.getColourL() == copyofTile.getColourR()
								|| field.getColourL() == Protocol.WHITE) {
							list.add(true);
						} else {
							list.add(false);
						}
					} else if (rotation == 1) {
						if (copyofTile.isFlipped()) {
							if (field.getColourL() == copyofTile.getColourV()
									|| field.getColourL() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						} else {
							if (field.getColourL() == copyofTile.getColourL()
									|| field.getColourL() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						}
						
					} else if (rotation == 2) {
						if (copyofTile.isFlipped()) {
							if (field.getColourL() == copyofTile.getColourL()
									|| field.getColourL() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						} else {
							if (field.getColourL() == copyofTile.getColourV()
									|| field.getColourL() == Protocol.WHITE) {
								list.add(true);
							} else {
								list.add(false);
							}
						}
						
					} else {
						return false;
					}
					break;
			}
		}
		//If all adjoining colours match, returns true.
		return !list.contains(false);
	}
}
