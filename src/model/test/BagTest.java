package model.test;

import static org.junit.jupiter.api.Assertions.*;
import model.Bag;
import model.ComputerPlayer;
import model.Player;
import model.Tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class BagTest {
	Bag list;
	Bag bag;
	Tile tile = new Tile('P', 'B', 'Y', 5);
	Tile tile1 = new Tile('Y', 'B', 'P', 4);
	@BeforeEach
	void setUp() throws Exception {
		list = new Bag();
		bag = new Bag();
	}
	@Test
	void testGetTiles() {
		boolean check = true;
		for (int i = 0; i < list.getSize(); i++) {
			if (!list.getTiles().get(i).equals(bag.getTiles().get(i))) {
				check = false;
				break;
			}
		}
		assertTrue(check);
	}

	@Test
	void testGetSize() {
		assertEquals(list.getSize(), 36);
	}
	@Test
	void testAdd() {
		list.getTiles().remove(0);
		list.add(tile);
		assertTrue(list.getTiles().contains(tile));
	}
	@Test
	void testShuffle() {
		Bag testBag = bag;
		list.shuffle();
		assertNotEquals(testBag, list);
	}
	
	@Test
	void testDealHand() {
		Player player = new ComputerPlayer("test", 2);
		bag.dealHand(player, 2);
		assertEquals(player.numInHand(), 2);
	}

}
