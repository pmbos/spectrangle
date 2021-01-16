package model;

import util.Protocol;

/**
 * This is the class describing a Tile in the Spectrangle game.
 * @author Alina Maximova, s2032074
 *
 */
public class Tile {
	private final int value;
	private char side1;
	private char side2;
	private char side3;
	private boolean flipped = false;
	
	/**
	 * Class Tile constructor.
	 * @param s1 the letter representing the color of 1/3 of the tile
	 * @param s2 the letter representing the color of 1/3 of the tile
	 * @param s3 the letter representing the color of 1/3 of the tile
	 * @param value the value of the tile
	 */
	public Tile(char s1, char s2, char s3, int value) {
		this.side1 = s1;
		this.side2 = s2;
		this.side3 = s3;
		this.value = value;
	}
	
	/**
	 * Method counts and returns the value of the tile.
	 * @return the value of the Tile
	 */
	/*@pure */public int getValue() {
		return value;
	}
	
	/**
	 * Method for flipping a tile.
	 */
	/*
	 * @ensures getColourL() == \old(getColourR()) &&
	 			getColourR() == \old(getColourL()) &&
	 			isFlipped() == !\old(isFlipped());
	 */
	public void flip() {
		char tmp;
		tmp = side1;
		side1 = side3;
		side3 = tmp;
		flipped = !flipped;
	}
	/**
	 * Method for getting whether the tile is flipped.
	 * @return true if the tile is flipped
	 */
	/*@pure */public boolean isFlipped() {
		return flipped;
	}
	/**
	 * Method for getting a graphical representation of the flipped tile.
	 * @param rotation the rotation with which to display the tile. 
	 * @return the string representation of a tile.
	 */
	//@ensures \result != null;
	/*@pure */public String graphicRepresentation(int rotation) {
		String result = "";
		if (flipped) {
			switch (rotation) {
				case 0:
					result += "-------- \n";
					result += "\\   " + getColourV() + "  / \n";
					result += " \\" + getColourL() + "  " + getColourR() + "/  \n";
					result += "  \\" + getValue() + " /   \n";
					result += "   \\/     \n";
					break;
				case 1:
					result += "-------- \n";
					result += "\\   " + getColourL() + "  / \n";
					result += " \\" + getColourR() + "  " + getColourV() + "/  \n";
					result += "  \\" + getValue() + " /   \n";
					result += "   \\/     \n";
					break;
				case 2:
					result += "-------- \n";
					result += "\\   " + getColourR() + "  / \n";
					result += " \\" + getColourV() + "  " + getColourL() + "/  \n";
					result += "  \\" + getValue() + " /   \n";
					result += "   \\/     \n";
					break;
			}

		} else {
			switch (rotation) {
				case 0:
					result += "   /\\    \n";
					result += "  / " + getValue() + "\\   \n";
					result += " /" + getColourL() + "  " + getColourR() + "\\  \n";
					result += "/   " + getColourV() + "  \\ \n";
					result += "-------- \n";
					break;
				case 1:
					result += "   /\\    \n";
					result += "  / " + getValue() + "\\   \n";
					result += " /" + getColourV() + "  " + getColourL() + "\\  \n";
					result += "/   " + getColourR() + "  \\ \n";
					result += "-------- \n";
					break;
				case 2:
					result += "   /\\    \n";
					result += "  / " + getValue() + "\\   \n";
					result += " /" + getColourR() + "  " + getColourV() + "\\  \n";
					result += "/   " + getColourL() + "  \\ \n";
					result += "-------- \n";
					break;
			}
			
		}
		return result;
	}
	
	/**
	 * Gets the color of the first side.
	 * @return the color of 1/3 of tile
	 */
	/*@pure */public char getColourL() {
		return side1;
	}
	/**
	 * Gets the color of the second side.
	 * @return the color of 1/3 of tile
	 */
	/*@pure */public char getColourV() {
		return side2;
	}
	/**
	 * Gets the color of the third side.
	 * @return the color of 1/3 of tile
	 */
	/*@pure */public char getColourR() {
		return side3;
	}
	/**
	 * Converts the tile to string for easier understanding of a user.
	 * @return the the String representation of the tile
	 */
	/*@pure */public String toString() {	
		return "The tile with 3 sides: " + side1 + side2 + side3 + getValue();
	}
	/**
	 * Checking if the tile exists.
	 * @return true if the tile is valid (has correct colors)
	 */
	//@ensures \result == true || \result == false;
	/*@pure */public boolean isValid() {
		for (Tile tile : Protocol.VALIDTILES) {
			if (tile.getColourL() == getColourL() &&
					tile.getColourR() == getColourR() &&
					tile.getColourV() == getColourV() &&
					tile.getValue() == getValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Checking if the tile is a Joker.
	 * @return true if the tile is Joker (has all sides white)
	 */
	/*@ensures \result == (getColourL() == Protocol.WHITE &&
				 getColourV() == Protocol.WHITE && getColourR() == Protocol.WHITE);
	*/
	/*@pure */public boolean isJoker() {
		return side1 == Protocol.WHITE && side2 == Protocol.WHITE && side3 == Protocol.WHITE;
	}
	
	/**
	 * Setting the color of the first side.
	 * @param s1 color of the 1/3 of the tile to set
	 */
	//@ensures getColourL() == s1;
	public void setColourL(char s1) {
		side1 = s1;
	}
	
	/**
	 * 
	 * Setting the color of the second side.
	 * @param s2 color of the 1/3 of the tile to set
	 */
	//@ensures getColourV() == s2;
	public void setColourV(char s2) {
		side2 = s2;
	}
	
	/**
	 * Setting the color of the third side.
	 * @param s3 color of the 1/3 of the tile to set
	 */
	//@ensures getColourR() == s3;
	public void setColourR(char s3) {
		side3 = s3;
	}

	@Override
	/*
	 * @requires o != null;
	 * @ensures o instanceof Tile ==> \result == this.getValue() == ((Tile) o).getValue() &
					this.getColourL() == ((Tile) o).getColourL() &
					this.getColourR() == ((Tile) o).getColourR() &
					this.getColourV() == ((Tile) o).getColourV();
	 */
	public boolean equals(Object o) {
		if (o instanceof Tile) {
			return this.getValue() == ((Tile) o).getValue() &
					this.getColourL() == ((Tile) o).getColourL() &
					this.getColourR() == ((Tile) o).getColourR() &
					this.getColourV() == ((Tile) o).getColourV();
		} else {
			return false;
		}
		
	}
}
