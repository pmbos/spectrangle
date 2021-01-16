package model.test;
import model.Board;
import model.SpectrangleBoardPrinter;
import model.Tile;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {
	Board board;
	@BeforeEach
	void setUp() {
		board = new Board();
	}

	@Test
	void testInitial() {
		for (int i = 0; i < Board.MAX_FIELDS; i++) {
			assertTrue(board.isEmpty(i));
		}
		
		assertFalse(board.isFull());
	}
	
	@Test
	void testGetField() {
		for (int i = 0; i < Board.MAX_FIELDS; i++) {
			assertNotNull(board.getField(i));
		}
	}
	
	@Test
	void testValidFieldWithValids() {
		for (int i = 0; i < Board.MAX_FIELDS; i++) {
			assertTrue(board.isValidField(i));
		}
	}
	
	@Test
	void testValidFieldWithInvalids() {
		for (int i = Board.MAX_FIELDS; i < 2 * Board.MAX_FIELDS; i++) {
			assertFalse(board.isValidField(i));
		}
	}
	
	@Test
	void testGetIndex() {
		assertEquals(Board.getIndex(3, 1), 13);
		assertEquals(Board.getIndex(4, 2), 22);
	}

	@Test
	void testGetCopy() {
		Board copy = this.board.getCopy();
		assertNotNull(copy);
		for (int i = 0; i < Board.MAX_FIELDS; i++) {
			assertEquals(copy.getField(i), this.board.getField(i));
		}
	}
	
	@Test
	void testSetTile() {
		Tile tile1 = new Tile('B', 'G', 'G', 3);
		board.setTile(4, tile1, 0);
		assertEquals(board.getField(4).value(), tile1.getValue());
		assertEquals(board.getField(4).getColourL(), tile1.getColourL());
		assertEquals(board.getField(4).getColourR(), tile1.getColourR());
		assertEquals(board.getField(4).getColourV(), tile1.getColourV());
	}
	
	@Test
	void testGetScore() {
		Tile tile1 = new Tile('B', 'G', 'G', 3);
		board.setTile(11, tile1, 0);
		assertEquals(board.getScore(11), tile1.getValue() * 
				Math.max(1, board.edges(11).size()) * 
				SpectrangleBoardPrinter.BONUS.get(11));
	}
	
	@Test
	void testReset() {
		assertFalse(board.isFull());
		Tile tile1 = new Tile('B', 'G', 'G', 3);
		board.setTile(4, tile1, 0);
		board.reset();
		assertEquals(board.getField(4).value(), 0);
	}
	
	@Test
	void testGetNeighbour() {
		assertEquals(board.getNeighbour(11, 'V'), 19);
	}
	
	@Test
	void testToString() {
		System.out.println(board);
	}
}
