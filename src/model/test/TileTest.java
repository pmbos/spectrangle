package model.test;
import static org.junit.jupiter.api.Assertions.*;

import model.Tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Tile test.
 * @author Alina Maximova, s2032074
 *
 */
public class TileTest {
	Tile tile;
	Tile notValTile;
	Tile jokerTile;
	@BeforeEach
	void setUp() {
		tile = new Tile('R', 'R', 'R', 6);
		notValTile = new Tile('r', 'G', 'b', -1);
		jokerTile = new Tile('W', 'W', 'W', 1);
		
	}

	@Test
	void testGetValue() {
		assertEquals(tile.getValue(), 6);
		assertEquals(jokerTile.getValue(), 1);
	}
	@Test
	void testGetSide1() {
		assertEquals(tile.getColourL(), 'R');
	}
	@Test
	void testGetSide2() {
		assertEquals(tile.getColourV(), 'R');
	}
	@Test
	void testGetSide3() {
		assertEquals(tile.getColourR(), 'R');
	}
	@Test
	void testIsValid() {
		assertTrue(tile.isValid());
		assertFalse(notValTile.isValid());
	}
	@Test
	void testIsJocker() {
		assertTrue(jokerTile.isJoker());
		assertFalse(tile.isJoker());
	}
	@Test
	void testSetSide1() {
		tile.setColourL('W');
		assertEquals(tile.getColourL(), 'W');
	}
	@Test
	void testSetSide2() {
		tile.setColourV('W');
		assertEquals(tile.getColourV(), 'W');
	}
	@Test
	void testSetSide3() {
		tile.setColourR('W');
		assertEquals(tile.getColourR(), 'W');
	}
	
	@Test
	void testEquals() {
		assertNotEquals(jokerTile, tile);
	}
	
	@Test
	void testString() {
		System.out.println(tile.graphicRepresentation(0));
		System.out.println(tile.graphicRepresentation(1));
		tile.flip();
		System.out.println(tile.graphicRepresentation(0));
		System.out.println(tile.graphicRepresentation(1));
	}

}
