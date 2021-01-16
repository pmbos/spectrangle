package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import model.Game;
import model.Player;

public class GameMakerThread extends Thread {
	//@invariant getQueue() != null && getGames() != null;
	private List<Player> queue;
	private static List<Controller> games;
	private static final Lock LOCK = new ReentrantLock();
	private static final Condition PLAYERADDED = LOCK.newCondition();
	private final ServerUI tui;

	/**
	 * Creates a GameMakerThread initialising the queue and games to 
	 * an empty list.
	 * @param tui the user interface to write to.
	 */
	public GameMakerThread(ServerUI tui) {
		this.setName("GameMaker");
		queue = new ArrayList<>();
		games = new ArrayList<>();
		this.tui = tui;
	}
	
	//Queries
	
	/**
	 * @return a list of players currently in the queue.
	 */
	/*@pure */public List<Player> getQueue() {
		return queue;
	}
	
	
	/**
	 * @return a list of currently active, unstarted games.
	 */
	/*@pure */public static List<Controller> getGames() {
		return games;
	}
	
	//Commands
	
	/**
	 * Permanently starts waiting for a <code>Player</code> to be added to the queue.
	 * When it is, attempts to find a <code>Game</code> for each <code>Player</code> in the
	 * list at that moment.
	 * If it cannot, creates a new <code>Game</code> and adds to <code>Player</code> to 
	 * that <code>Game</code>.
	 */
	public void run() {
		//Runs until instructed to stop.
		while (true) {
			//Locks because there is a possibility for multiple game makers to exist,
			//not implemented.
			LOCK.lock();
			try {
				while (queue.size() == 0) {
					//Waits for a player to be added to the queue.
					PLAYERADDED.await();
				}
			} catch (InterruptedException e) {
				break;
			}
			
			//Processes the queue.
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				boolean found = false;
				for (Controller game : games) {
					//Checks if a game corresponds with the user's wishes.
					if ((game.getGame().getNumberOfPlayers() < queue.get(i).getPreference() ||
							queue.get(i).getPreference() == -1) && (
							game.getGame().getNumberOfPlayers() + 1 <=
							game.getGame().getPreference() ||
							game.getGame().getPreference() == -1) &&
							!game.hasStarted() &&
							game.getGame().getNumberOfPlayers() <= Game.MAX_PLAYERS &&
							game.getGame().getNumberOfPlayers() >= 0) {
						//Adds the player to the game if a game is found.
						game.getGame().addPlayer(queue.get(i));
						found = true;
					}
				}
				if (!found) {
					//If a game is not found, creates a new one.
					Controller controller = new Controller(games.size(),
							queue.get(i).getPreference(), tui);
					games.add(controller);
					controller.start();
					controller.getGame().addPlayer(queue.get(i));
					tui.write("Player added", "GameMakerThread");
				}
			}
			//Clears the queue.
			queue.clear();
			LOCK.unlock();
		}
	}
	
	/**
	 * Adds the given player to the queue and signals the gameMaker.
	 * @param player the <code>Player</code> to be added to the queue.
	 */
	//@requires player != null;
	public void addToQueue(Player player) {
		try {
			LOCK.lock();
			queue.add(player);
			PLAYERADDED.signal();
		} finally {
			LOCK.unlock();
		}		
	}
	
	/**
	 * Removes the given controller from the games queue.
	 * @param controller the controller to remove
	 */
	public void removeFromGames(Controller controller) {
		try {
			LOCK.lock();
			games.remove(controller);
		} finally {
			LOCK.unlock();
		}	
	}
}
