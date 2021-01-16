package server.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.*;
import server.GameMakerThread;
import server.ServerUI;
import strategies.StupidStrategy;

class GameMakerTest {
	//Was made when the game maker could still be run stand-alone. That is no longer possible
	//so the test will not complete.
	GameMakerThread gameMaker;
	ServerUI tui = new ServerUI();
	Player player;
	Player player2;
	Player player3;
	@BeforeEach
	void setUp() {
		gameMaker = new GameMakerThread(tui);
		player = new ComputerPlayer("Pascal", new StupidStrategy(player), 2);
		player2 = new ComputerPlayer("Alina", new StupidStrategy(player2), -1);
		player3 = new ComputerPlayer("Jan", new StupidStrategy(player3), -1);
		gameMaker.start();
	}

	@Test
	void testInitial() {
		assertEquals(gameMaker.getQueue().size(), 0);
		assertEquals(GameMakerThread.getGames().size(), 0);
		gameMaker.interrupt();
	}
	
	@Test
	void testAddPlayer() {
		gameMaker.addToQueue(player);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}

		assertEquals(GameMakerThread.getGames().size(), 1);
		assertEquals(GameMakerThread.getGames().get(0).getGame().getNumberOfPlayers(), 1);
		assertEquals(GameMakerThread.getGames().get(0).getGame().getPlayers().get(0).name(), 
				"Pascal");
		assertEquals(GameMakerThread.getGames().get(0).getGame().getPreference(), 2);
		gameMaker.interrupt();
	}
	
	@Test
	void testMultiplePlayers() {
		gameMaker.setName("GameMaker");
		gameMaker.addToQueue(player);
		gameMaker.addToQueue(player2);
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		assertEquals(gameMaker.getQueue().size(), 0);

		assertEquals(GameMakerThread.getGames().size(), 1);
		assertEquals(GameMakerThread.getGames().get(0).getGame().getNumberOfPlayers(), 2);
		assertEquals(GameMakerThread.getGames().get(0).getGame().getPreference(), 2);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		assertTrue(GameMakerThread.getGames().get(0).hasStarted());
		gameMaker.interrupt();
	}

	@Test
	void testMorePlayersThanPreference() {
		gameMaker.setName("GameMaker");
		gameMaker.addToQueue(player);
		gameMaker.addToQueue(player2);
		gameMaker.addToQueue(player3);
		
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		assertEquals(gameMaker.getQueue().size(), 0);
		assertEquals(GameMakerThread.getGames().size(), 2);
		assertEquals(GameMakerThread.getGames().get(0).getGame().getNumberOfPlayers(), 2);
		assertEquals(GameMakerThread.getGames().get(1).getGame().getNumberOfPlayers(), 1);
		gameMaker.interrupt();
	}
}
