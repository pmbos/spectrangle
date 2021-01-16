package model.test;
import model.*;
import strategies.StupidStrategy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {
	Player player;
	Bag bag;
	Board board;
	@BeforeEach
	void setUp() {
		player = new ComputerPlayer("Pascal", new StupidStrategy(player), -1);
		bag = new Bag();
		board = new Board();
	}

	@Test
	void testInitial() {
		assertEquals(player.name(), "Pascal");
		assertTrue(player.emptyHand());
	}
	
	@Test
	void testTotalValue() {
		player.add(new Tile('R', 'R', 'R', 6));
		player.add(new Tile('W', 'W', 'W', 1));
		assertEquals(player.totalValue(), 7);
	}
	
	@Test
	void testAdd() {
		bag.dealHand(player, 4);
		assertEquals(player.numInHand(), 4);
		assertFalse(player.emptyHand());
	}
	@Test
	void testSetGetScore() {
		player.updateScore(4);
		player.updateScore(5);
		assertEquals(player.getScore(), 9);
	}
	
	@Test
	void testRemove() {
		bag.dealHand(player, 2);
		Tile tile = player.getHand().get(1);
		player.removeTile(0);
		assertEquals(player.getHand().get(0), tile);
		assertEquals(player.numInHand(), 1);
	}
	@Test
	void testRemoveTile() {
		bag.dealHand(player, 2);
		Tile tile = player.getHand().get(0);
		Tile tile1 = player.getHand().get(1);
		player.removeTile(tile);
		assertEquals(player.getHand().get(0), tile1);
		assertEquals(player.numInHand(), 1);
	}
	
	@Test
	void testHasInHand() {
		bag.dealHand(player, 3);
		player.add(new Tile('R', 'R', 'R', 6));
		Tile tile = new Tile('R', 'R', 'R', 6);
		assertTrue(player.hasTileInHand(tile));
		System.out.print(player.toString());
	}
	
	@Test
	void testHasPlay() {
		bag.dealHand(player, 4);
		assertTrue(player.hasPlay(board.getCopy()));
		player.removeTile(0);
		player.add(new Tile('G', 'B', 'R', 2));
		board.setTile(11, new Tile('R', 'R', 'R', 6), 0);
		assertTrue(player.hasPlay(board.getCopy()));
	}
	
	@Test
	void testGetPlay() {
		bag.dealHand(player, 4);
		player.removeTile(0);
		player.add(new Tile('R', 'B', 'G', 2));
		board.setTile(0, new Tile('R', 'R', 'R', 6), 0);
		board.setTile(10, new Tile('B', 'Y', 'R', 3), 0);
		System.out.println(board.toString());
		Map<Tile, List<Integer>> map = player.getPlays(board.getCopy());
		assertTrue(map.size() > 0);
		
		for (Entry<Tile, List<Integer>> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
}
