package model;

public class Field {
	/**
	 * A class modelling a Field on the spectrangle board.
	 * @author P.M. Bos
	 */
	private int value = 0;
	private char colourV;
	private char colourL;
	private char colourR;

	// Commands
	/**
	 * Sets the value of this field equal to the given value.
	 * @param value the value of this field.
	 */
	//@requires value >= 0;
	//@ensures this.value() == value;
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Sets <code>colourV</code> equal to the given colour.
	 * @param colour the colour to set.
	 */
	/*@ensures this.getColourV() == colour; */
	public void setColourV(char colour) {
		this.colourV = colour;
	}
	
	/**
	 * Sets <code>colourL</code> equal to the given colour.
	 * @param colour colour the colour to set.
	 */
	/*@ensures this.getColourL() == colour; */
	public void setColourL(char colour) {
		this.colourL = colour;
	}

	/**
	 * Sets <code>colourR</code> equal to the given colour.
	 * @param colour colour the colour to set.
	 */
	/*@ensures this.getColourR() == colour; */
	public void setColourR(char colour) {
		this.colourR = colour;
	}

	// Queries
	/**
	 * @return the current integer value held by <code>value</code>
	 */
	//@ensures \result == this.value();
	/*@pure */public int value() {
		return value;
	}

	/**
	 * @return the current character value held by <code>colourV</code>
	 */
	//@ensures \result == this.getColourV();
	/*@pure */public char getColourV() {
		return this.colourV;
	}

	/**
	 * @return the current character value held by <code>colourL</code>
	 */
	//@ensures \result == this.getColourL();
	/*@pure */public char getColourL() {
		return this.colourL;
	}

	/**
	 * @return the current character value held by <code>colourR</code>
	 */
	//@ensures \result == this.getColourR();
	/*@pure */public char getColourR() {
		return this.colourR;
	}

	@Override
	public String toString() {
		return "Field value: " + value() + ", left colour: " + getColourL() 
				+ ", vertical colour: " + getColourV()
				+ ", right colour: " + getColourR();
	}
}
