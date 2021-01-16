package model.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exceptions.IllegalMove;
import model.*;
import server.Controller;
import server.ServerUI;
import strategies.StupidStrategy;
class GameTest {
	//May throw null pointer exceptions due to the handle console input thread not being 
	//initialized properly.
	ServerUI tui = new ServerUI();
	Game game;
	Player player1;
	Player player2;
	Player player3;
	Player player4;
	

	Tile tile = new Tile('R', 'R', 'R', 6); 
	Tile tile2 = new Tile('r', 'R', 'R', 6);
	@BeforeEach
	void setUp() {
		
		player1 = new ComputerPlayer("Pascal", -1);
		player2 = new ComputerPlayer("Alina", -1);
		player3 = new ComputerPlayer("Pascal", -1);
		player4 = new ComputerPlayer("Alina", -1);
		Strategy strategy = new StupidStrategy(player1);
		((ComputerPlayer) player1).setStrategy(strategy);
		((ComputerPlayer) player2).setStrategy(strategy);
		((ComputerPlayer) player3).setStrategy(strategy);
		((ComputerPlayer) player4).setStrategy(strategy);
		
		game = new Controller(0, -1, tui).getGame();
	}

	@Test
	void testInitial() {
		assertNotNull(game.getBag());
		assertNotNull(game.getBoard());
		assertEquals(game.getNumberOfPlayers(), 0);
	}
	
	@Test
	void testAddPlayer() {
		game.addPlayer(player1);
		assertEquals(game.getNumberOfPlayers(), 1);
	}
	
	@Test
	void setCurrentPlayer() {
		game.addPlayer(player1);
		game.setCurrentPlayer(player1);
		assertEquals(game.getCurrentPlayer(), player1);
	}
	
	@Test
	void setNextPlayer() {
		game.addPlayer(player1);
		game.addPlayer(player2);
		game.addPlayer(player3);
		game.addPlayer(player4);
		game.setCurrentPlayer(player1);
		for (int i = 0; i < game.getNumberOfPlayers() - 2; i++) {
			game.setCurrentPlayer(game.getNextPlayer());
		}
		
		assertEquals(game.getCurrentPlayer(), player3);
	}
	
	@Test
	void testValidMove() {
		assertTrue(game.getBoard().isValidMove(tile, 0, 0));
		assertFalse(game.getBoard().isValidMove(tile, 2, 0));
		try {
			game.attemptMove(tile, 0, 2);
			game.attemptMove(tile2, 2, 2);
			//game.attemptMove(tile, 0, 2);
		} catch (IllegalMove e) {
			System.out.println("illegal move");
		}
		
	}
	
	@Test
	void testGetVictor() {
		game.addPlayer(player1);
		game.addPlayer(player2);
		player1.updateScore(3);
		player2.updateScore(5);
		assertEquals(game.getVictor(), player2);
	}
	
	@Test
	void testExchangeTile() {
		game.addPlayer(player1);
		game.getBag().dealHand(player1, 4);
		game.setCurrentPlayer(player1);
		List<Tile> list = new ArrayList<>(game.getCurrentPlayer().getHand());
		game.exchangeTile(game.getCurrentPlayer().getHand().get(0));
		assertNotEquals(game.getCurrentPlayer().getHand(), list);
	}

}
