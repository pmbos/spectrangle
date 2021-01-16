package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ColourCompare;

/** A class modelling the board of a game of spectrangle.
 * @author P.M. Bos
 */
public class Board {
	//@invariant getFields().size() == MAX_FIELDS;
	public static final int MAX_FIELDS = 36;
	public static final Map<Integer, Integer[]> FIELD_MAPPING = getMapping(); 
	private List<Field> fields;
	private boolean firstPlay;
	
	/**
	 * Creates a new <code>Board</code> instance with all 36 fields initialised to a new field.
	 */
	public Board() {
		fields = new ArrayList<>();
		this.firstPlay = true;
		for (int i = 0; i < MAX_FIELDS; i++) {
			fields.add(new Field());
		}
	}
	
	/**
	 * Creates a new <code>Board</code> instance with its <code>fields</code>
	 * initialised to the given list of fields.
	 * @param fields a list of <code>Fields</code>.
	 * @param first whether or not first play is still true.
	 */
	/*@pure */public Board(List<Field> fields, boolean first) {
		this.fields = new ArrayList<>();
		this.fields = fields;
		firstPlay = first;
	}
	
	//Queries
	/**
	 * Finds the <code>Field</code> on the <code>Board</code> 
	 * identified by the given <code>index</code>
	 * @param index a valid index of a <code>Field</code> on the <code>Board</code>
	 * @return the <code>Field</code> identified by the given <code>index</code>
	 */
	/*
	 * @requires isValidField(index);
	 * @ensures \result == getField(index);
	 */
	/*@pure */public Field getField(int index) {
		return fields.get(index);
	}
	
	/**
	 * @return true if this is the first play made in the game, false otherwise.
	 */
	/*@pure */public boolean isFirstPlay() {
		return firstPlay;
	}
	
	/*@pure */public List<Field> getFields() {
		return fields;
	}
	
	/**
	 * Determines if the given <code>index</code> identifies a valid.
	 * <code>Field</code> on the <code>Board</code>
	 * @param index the index to validate
	 * @return true if the given <code>index</code> identifies a valid <code>Field</code>, 
	 * false otherwise
	 */
	//@ensures \result == (0 <= index && index < 36);
	/*@pure */public boolean isValidField(int index) {
		return 0 <= index && index < 36;
	}
	
	
	
	/**
	 * Determines if the given move is valid. A move is valid when: The tile is
	 * placed in an empty Field. The tile has at least the number of neighbours
	 * adjoining edges with said neighbours. The tile would have at least one
	 * neighbour.
	 * @param rotation the rotation to check.
	 * @param tile  a Tile to be moved.
	 * @param index the index to move to.
	 * @return true if the given move is valid, false otherwise.
	 */
	/*@requires tile != null;
	  @requires isValidField(index);
	  @ensures edges(index).size() > 0 ==> 
	  	\result == ColourCompare.checkPermutation(tile, index, edges(index), getCopy(), rotation);
	  @ensures edges(index).size() == 0 ==> 
	  			\result == (boardEmpty() && SpectrangleBoardPrinter.BONUS.get(index) == 1); 
	 */
	/* @pure */public boolean isValidMove(Tile tile, int index, int rotation) {
		List<Character> neighbours = edges(index); 
		
		if (neighbours.size() >= 1) {
			Board copy = getCopy();
			return ColourCompare.checkPermutation(tile, index, neighbours, copy, rotation);
		} else {
			return boardEmpty() && SpectrangleBoardPrinter.BONUS.get(index) == 1;
		}
	}

	
	/**
	 * Identifies the index given a row- and column index.
	 * @param row the row index.
	 * @param column the column index.
	 * @return ,as an integer the, the index identified
	 * by the given <code>row</code> and <code>column</code> 
	 */
	//@ensures \result == (int)Math.pow(row, 2) + row + column;
	/*@pure */public static int getIndex(int row, int column) {
		return (int) Math.pow(row, 2) + row + column;
	}
	
	/**
	 * Finds out if a <code>Field</code> identified by the given <code>index</code> is empty.
	 * @param index the index to verify.
	 * @return true if the <code>Field</code> does have a value of 0
	 */
	/*
	 * @requires isValidField(index);
	 * @ensures \result == (fields.get(index).value() == 0);
	 */
	/*@pure */public boolean isEmpty(int index) {
		return getField(index).value() == 0;
	}
	
	/**
	 * Finds out if the <code>Board</code> is full.
	 * @return true if all fields of the <code>Board</code> have a value of 0 or less,
	 * false otherwise.
	 */
	//@ensures \result == (\forall int i; isValidField(i); getField(i).value() <= 0);
	/*@pure */public boolean isFull() {
		for (Field field : fields) {
			if (field.value() <= 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return a copy of the current state of the <code>Board</code>
	 */
	//@ensures \result == new Board(getFields(), isFirstPlay());
	/*@pure */public Board getCopy() {
		return new Board(this.fields, this.firstPlay);
	}
	
	/**
	 * Calculates the score of a <code>Field</code> identified by the given <code>index</code>.
	 * @param index the index of the field to check.
	 * @return the score of the <code>Field</code> as an integer. 
	 */
	/*
	 * @requires isValidIndex(index);
	 * @ensures \result == getField(index).value() * 
				Math.max(1, edges(index).size()) * 
				SpectrangleBoardPrinter.BONUS.get(index);
	 */
	/*@pure */public int getScore(int index) {
		return getField(index).value() * 
				Math.max(1, edges(index).size()) * 
				SpectrangleBoardPrinter.BONUS.get(index);
	}
	
	/**
	 * Finds the all the connecting edges of the <code>Field</code>
	 * identified by the given <code>index</code>.
	 * An edge is connecting when:
	 * A neighbour is occupied.
	 * @param index the index of the <code>Field</code> to check.
	 * @return A list containing the single character representations of all connecting edges.
	 */
	/*
	 * @requires isValidIndex(index) && getField(index).value > 0;
	 */
	/*@pure */public List<Character> edges(int index) {
		List<Character> neighbours = new ArrayList<>();
		if (getNeighbour(index, 'L') != -1 && getField(getNeighbour(index, 'L')).value() > 0) {
			neighbours.add('L');
		}
		if (getNeighbour(index, 'V') != -1 && getField(getNeighbour(index, 'V')).value() > 0) {
			neighbours.add('V');
		}
		if (getNeighbour(index, 'R') != -1 && getField(getNeighbour(index, 'R')).value() > 0) {
			neighbours.add('R');
		}
		return neighbours;
	}
	
	/**
	 * Checks whether the given <code>neighbour</code> is valid.
	 * @param neighbour the character to check.
	 * @return true if <code>neighbour</code> is valid, false otherwise.
	 */
	//@ensures \result == (neighbour == 'V' || neighbour == 'L' || neighbour == 'R');
	/*@pure */public boolean isValidNeighbour(char neighbour) {
		return neighbour == 'V' || neighbour == 'L' || neighbour == 'R';
	}
	
	/**
	 * Finds the neighbour of the <code>Field</code> identified by 
	 * the given <code>index</code> and <code>neighbour</code>.
	 * @param index the index of the <code>Field</code> to check.
	 * @param neighbour specifies which neighbour to find. 
	 * 'V' for vertical, 'L' for left, 'R' for right.
	 * @return the index of the neighbour, -1 if no such neighbour exists.
	 */
	/*
	 * @requires isValidIndex(index) && isValidNeighbour(neighbour);
	 * @ensures isValidIndex(\result) || \result == -1;
	 */
	/*@pure */public int getNeighbour(int index, char neighbour) {
		Integer[] indices = FIELD_MAPPING.get(index);
		int row = indices[0];
		int column = indices[1];
		switch (neighbour) {
			case 'V':
				if (isEven(row + column) && row + 1 <= 5) {
					return getIndex(row + 1, column);
				} else if (!isEven(row + column) && row - 1 > -1) {
					return getIndex(row - 1, column);
				}
				break;
			case 'L':
				if (column - 1 >= -row) {
					return getIndex(row, column - 1);
				}
				break;
			case 'R':
				if (column + 1 <= row) {
					return getIndex(row, column + 1);
				}
				break;
		}
		return -1;
	}
	
	/**
	 * Checks if a given integer is even.
	 * @param n the integer to check.
	 * @return true, if the given integer is even, false otherwise.
	 */
	//@ensures \result == (n % 2 == 0);
	/*@pure */public static boolean isEven(int n) {
		return n % 2 == 0;
	}
	
	/**
	 * Checks if the field belonging to the given index has an
	 * upward- or downward facing point.
	 * @param index the index to check.
	 * @return true if the point faces upwards, false otherwise.
	 */
	//@requires isValidField(index);
	//@ensures \result == isEven(FIELD_MAPPING.get(index)[0] + FIELD_MAPPING.get(index)[1]);
	public boolean isUp(int index) {
		Integer[] rc = FIELD_MAPPING.get(index);
		return isEven(rc[0] + rc[1]);
	}
	
	/**
	 * @return true if the board is empty, false otherwise.
	 */
	//@ensures \result == (\forall int i; i < MAX_FIELDS; getField(i).value() == 0);
	/*@pure */public boolean boardEmpty() {
		for (Field field : fields) {
			if (field.value() != 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return a string representation of the board.
	 */
	public String toString() {
		List<Integer> values = new ArrayList<>();
		List<Character> left = new ArrayList<>();
		List<Character> vertical = new ArrayList<>();
		List<Character> right = new ArrayList<>();
		
		for (int i = 0; i < Board.MAX_FIELDS; i++) {
			Field field = getField(i);
			values.add(field.value());
			vertical.add(field.getColourV());
			left.add(field.getColourL());
			right.add(field.getColourR());
		}
		
		return SpectrangleBoardPrinter.getBoardString(values, vertical, left, right);
	}
	
	/**
	 * Creates a new map and fills it with the standard field mapping of spectrangle.
	 * @return a map mapping the indices of <code>Fields</code> to their column- and row indices.
	 */
	/*@pure */public static Map<Integer, Integer[]> getMapping() {
		Map<Integer, Integer[]> map = new HashMap<>();
		fillMap(map);
		return map;
	}
	
	//Commands
	/**
	 * Lets the given <code>tile</code> occupy 
	 * the <code>Field</code> identified by the given <code>index</code>.
	 * @param index the index of the <code>Field</code> to occupy.
	 * @param tile the tile to occupy.
	 * @param rotation the rotation to use.
	 */
	/*
	 * @requires isValidIndex(index) && tile != null && isValidRotation(rotation);
	 * @ensures
	    getField(index).getValue() == tile.value();
	 */
	public void setTile(int index, Tile tile, int rotation) {
		if (firstPlay) {
			firstPlay = false;
		}
		
		if (!isUp(index) && !tile.isFlipped()) {
			tile.flip();
		} else if (isUp(index) && tile.isFlipped()) {
			tile.flip();
		}
		
		Field field = getField(index);
		field.setValue(tile.getValue());
		switch (rotation) {
			case 0:
				field.setColourL(tile.getColourL());
				field.setColourR(tile.getColourR());
				field.setColourV(tile.getColourV());
				break;
			case 1:
				if (tile.isFlipped()) {
					field.setColourL(tile.getColourR());
					field.setColourR(tile.getColourV());
					field.setColourV(tile.getColourL());
				} else {
					field.setColourL(tile.getColourV());
					field.setColourR(tile.getColourL());
					field.setColourV(tile.getColourR());
				}
				break;
			case 2:
				if (tile.isFlipped()) {
					field.setColourL(tile.getColourV());
					field.setColourR(tile.getColourL());
					field.setColourV(tile.getColourR());
				} else {
					field.setColourL(tile.getColourR());
					field.setColourR(tile.getColourV());
					field.setColourV(tile.getColourL());
				}
				break;
		}		
	}
	
	/**
	 * Resets the <code>Board</code> to empty.
	 */
	/*@ensures \forall int i; isValidField(i); 
	   getField(i).value() == 0 &&
	   getField(i).getColourL() == '\u0000' &&
	   getField(i).getColourL() == '\u0000' &&
	   getField(i).getColourL() == '\u0000';
	 */
	public void reset() {
		for (Field field : fields) {
			field.setValue(0);
			field.setColourL('\u0000');
			field.setColourV('\u0000');
			field.setColourR('\u0000');
		}
		firstPlay = true;
	}
	
	/**
	 * Removes the specified tile from the field.
	 * @param index which field to empty.
	 */
	/* @requires isValidField(index);
	 * @ensures getField(index).getValue() == 0 &&
	 		getField(index).getColourL() == '\u0000' &&
	 		getField(index).getColourV() == '\u0000' &&
	 		getField(index).getColourR() == '\u0000';
	 */
	public void remove(int index) {
		Field field = getField(index);
		field.setValue(0);
		field.setColourL('\u0000');
		field.setColourV('\u0000');
		field.setColourR('\u0000');
	}
	
	/**
	 * Fills the given <code>map</code> with the field mapping of the <code>Board</code>.
	 * @param map the map to fill.
	 */
	//@requires map != null;
	private static void fillMap(Map<Integer, Integer[]> map) {
		for (int r = 0; r <= 5; r++) {
			int c =  -r;	
			while (-r <= c && c <= r) {
				Integer[] array = {r, c};
				map.put(getIndex(r, c), array);
				c++;
			}
		}
	}
}

