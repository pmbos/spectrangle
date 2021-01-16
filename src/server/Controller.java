package server;

import java.io.IOException;
//import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
//import java.util.concurrent.TimeUnit;

import exceptions.IllegalMove;
import model.Game;
import model.Player;
import model.Tile;
import util.Protocol;

/**
 * A class modelling the game controller on the server.
 * @author pmbos
 */
public class Controller extends Thread implements Observer {
	private final Game game;
	private final int id;
	private boolean gameStarted = false;
	private final ServerUI tui;
	private boolean forceGameOver = false;

	/**
	 * Creates a controller with the given id, preference and ui.
	 * @param id the id to assign this controller.
	 * @param preference the preference to assign the game associated with this controller.
	 * @param tui the ui to use.
	 */
	public Controller(int id, int preference, ServerUI tui) {
		this.game = new Game(preference, this);
		this.id = id;
		this.tui = tui;
		this.setName("Controller #" + id);
		
	}

	/**
	 * @return true if the game has already started, false otherwise.
	 */
	/*@pure */public boolean hasStarted() {
		return gameStarted;
	}

	/**
	 * @return the id of this <code>Controller</code>
	 */
	/*@pure */public int getID() {
		return id;
	}

	/**
	 * @return the <code>Game</code> linked to this controller.
	 */
	/*@pure */public Game getGame() {
		return this.game;
	}

	/**
	 * Waits for the game to reach the preferred amount of players, or the minimal
	 * amount.
	 */
	public synchronized void run() {
		//Starts a game controller.
		try {
			//Will wait until the game starts or the game is forced to terminate.
			while (!gameStarted && !forceGameOver) {
				tui.write("Waiting for start conditions to be met", "Game Controller#" + id);
				this.wait();
				tui.write("Start conditions  met, starting...", "Game Controller#" + id);
				try {
					//Will wait for 30 seconds if the players do not care how many they play with.
					if (game.getPreference() == -1) {
						Thread.sleep(30000);
					}
				} catch (InterruptedException e) {
					tui.writeError(e, "Game Controller#" + id);
				}
				//Indicates the game has started.
				gameStarted = true;
			}
		} catch (InterruptedException | IllegalMonitorStateException e) {
			tui.writeError(e, "Game Controller#" + id);
		}


		try {
			//If the game is not forced to terminate, 
			//will select a starting player and run the game.
			//Will clean up afterwards.
			if (!forceGameOver) {
				setStartingPlayer();
				play();
				tui.write("\n Game #" + id + " is over. Cleaning up...", "Game Controller#" + id);
				cleanUp();
			}
		} catch (InterruptedException e) {
			cleanUp();
		}
	}

	/**
	 * Checks if the start requirements are met when the number of players of the
	 * game increases. Decreases the number of players if a client is unreachable.
	 */
	@Override
	public synchronized void update(Observable o, Object arg) {
		//Checks if the game is ready to start each time the player count increases.
		tui.write("Number of players: " + game.getNumberOfPlayers(), "Game Controller#" + id);
		if ((game.getNumberOfPlayers() == game.getPreference() || game.getPreference() == -1)
				&& game.getNumberOfPlayers() >= Game.MIN_PLAYERS 
				&& game.getNumberOfPlayers() <= Game.MAX_PLAYERS) {
			notify();
		}
	}

	/**
	 * Shuts down every connection of every player of the game connected to this
	 * controller.
	 */
	public void cleanUp() {
		List<Player> players = game.getPlayers();
		for (Player player : players) {
			player.shutdown();
			Server.incConnections(-1);
		}
		GameMakerThread.getGames().remove(this);
	}

	/**
	 * Plays a game of spectrangle.
	 * @throws InterruptedException when the game gets interrupted.
	 */
	public void play() throws InterruptedException {
		//Plays the game until it terminates.
		
		//Deals 4 tiles to the players.
		for (Player player : game.getPlayers()) {
			dealPlayerTiles(player, 4);
		}
		//Tells the clients the game has started.
		tellAllPlayers(constructGameStarted());

		while (!game.gameOver() && !forceGameOver) {
			//Runs as long as the game runs and is not force to terminate.
			try {
				//Sends a move request and waits on input from the client.
				tellPlayer(game.getCurrentPlayer(), Protocol.MOVEREQUEST);
				Tile tile = null;
				Tile copy;
				int index = -1;
				int rotation = -1;
				try {
					//Uses a future type to put a time-out on the retrieval of input.
					//If a client exceeds this time-out a move will be made for them
					//as specified in the protocol
					GetInputOnTimeout getMove = new 
							GetInputOnTimeout(Protocol.RETRIES, Protocol.TIMEOUT,
							game.getCurrentPlayer().getInput(),
							Protocol.TIMEUNIT);
					String move;

					try {
						//Fetches the move. Replaces any white-spaces that may be introduced
						//due to reading attempts on the client whilst a move is being sent.
						//These attempts are to ensure the client is still connected while the
						//server waits for input.
						move = getMove.readLine();
						move = move.replaceAll(" ", "");
					} catch (InterruptedException e) {
						tui.write("Timed-out", "Game Controller#" + id);
						move = Protocol.ACTIONONTIMEOUT;
					}

					//If the server receives a non-null response, interprets it.
					String[] partsOfMove = move.split(Protocol.DELIMITER);

					switch (partsOfMove[0]) {
						case Protocol.MOVE:
							tile = getTile(partsOfMove);
							index = getIndex(partsOfMove[3]);
							rotation = getRotation(move);
							copy = new Tile(tile.getColourL(), tile.getColourV(),
									tile.getColourR(), tile.getValue());

							game.attemptMove(copy, index, rotation);
							game.getCurrentPlayer().
								updateScore(game.getBoard().getScore(index));
							break;
						case Protocol.TILEREPLACE:
							tile = getTile(partsOfMove);
							break;
						case Protocol.SKIP:
							break;
						default:
							tui.write("Warning: command possibly invalid",
									"Game Controller#" + id);
							break;
					}

					//Removes the played or to be replaced tile from the player's hand.
					//Then deals them a new one.
					if (partsOfMove[0].equals(Protocol.MOVE)
							|| partsOfMove[0].equals(Protocol.TILEREPLACE)) {

						game.getCurrentPlayer().removeTile(tile);
						game.getBag().dealHand(game.getCurrentPlayer(), 1);
					}
					//Informs the clients of the turn made.
					tellAllPlayers(constructTurnMade(tile, index, rotation, partsOfMove));

				} catch (IllegalMove e) {
					//In case of an illegal move this exception is thrown and the player will
					//be kicked.
					tui.writeError(e, "Game Controller#" + id);
					game.getPlayers().remove(game.getCurrentPlayer());
					game.getCurrentPlayer().shutdown();
					tellAllPlayers(constructKick());
				}
				

			} catch (IOException e) {
				//In case of a client disconnect, the server will terminate that client's socket
				//Check if the game can still continue (and if not, forcefully terminate it).
				//and tell the other players that the player has been kicked.
				tui.write("client disconnected", "Game Controller#" + id);
				game.getPlayers().remove(game.getCurrentPlayer());
				game.getCurrentPlayer().shutdown();
				if (game.getNumberOfPlayers() < Game.MIN_PLAYERS) {
					forceGameOver = true;
				} else {
					tellAllPlayers(constructKick());
				}
			}
			game.setCurrentPlayer(game.getNextPlayer());
		}
		//Informs all players that the game is over.
		tellAllPlayers(constructGameOver());
	}

	/**
	 * Selects the starting player of a game of spectrangle and sets that player as
	 * current player.
	 */ 
	//@ensures getGame().getCurrentPlayer() != null;
	private void setStartingPlayer() {
		Random random = new Random();
		int index = random.nextInt(game.getNumberOfPlayers());
		game.setCurrentPlayer(game.getPlayers().get(index));
	}

	/*
	 * @requires player != null && num >= 0;
	 * @ensures player.getNumInHand() >= num;
	 */
	private void dealPlayerTiles(Player player, int number) {
		game.getBag().dealHand(player, number);
	}

	/**
	 * Sends the given message to all players in the game.
	 * 
	 * @param msg the message to send all players.
	 */
	public void tellAllPlayers(String msg) {
		for (Player player : game.getPlayers()) {
			player.getOutput().println(msg);
		}
	}

	/**
	 * Tells the given player the given message.
	 * 
	 * @param player the player whom to send the message to.
	 * @param msg    the message to send the player.
	 * @throws IOException when there is an error on the output stream.
	 */
	public void tellPlayer(Player player, String msg) throws IOException {
		if (player.getOutput().checkError()) {
			tui.write("Error in output", "Game Controller#" + id);
		}
		player.getOutput().println(msg);
		if (player.getOutput().checkError()) {
			throw new IOException("Client disconnect");
		}

	}

	/**
	 * @return a <code>String</code> indicating the game is over.
	 */
	public String constructGameOver() {
		List<Player> players = game.getPlayers();
		StringBuilder result = new StringBuilder();
		result.append(Protocol.GAMEOVER + Protocol.DELIMITER);
		for (Player player : players) {
			result.append(player.name()).append(Protocol.DELIMITER).append(player.getScore()).append(Protocol.DELIMITER);
		}

		return result.toString();
	}

	/**
	 * @return the string representation of the kick message.
	 */
	//@requires getGame().getCurrentPlayer() != null;
	private String constructKick() {
		return Protocol.PLAYERKICKED + Protocol.DELIMITER + game.getCurrentPlayer().name();
	}

	/**
	 * @return the string representation of the game started message.
	 */
	private String constructGameStarted() { 
		StringBuilder result = new StringBuilder();
		result.append(Protocol.GAMESTARTED + Protocol.DELIMITER);
		result.append("|");
		for (Player player : game.getPlayers()) {
			for (Tile tile : player.getHand()) {
				result.append(tile.getColourL()).append(tile.getColourV())
						.append(tile.getColourR()).append(tile.getValue());
				result.append(Protocol.DELIMITER);
			}
			result.append(player.name());
			result.append("|");
		}
		return result.toString();
	}

	/**
	 * Constructs a turn made message depending on what move was made.
	 * @param old the tile used in the play.
	 * @param index the index the tile was placed on.
	 * @param rotation the rotation that was used.
	 * @param move the type of move that was made.
	 * @return a <code>String</code> indicating that a turn has been made.
	 */
	public String constructTurnMade(Tile old, int index, int rotation, String[] move) {
		StringBuilder result = new StringBuilder();
		result.append(Protocol.TURNMADE + Protocol.DELIMITER);

		switch (move[0]) {
			case Protocol.MOVE:
				result.append(Protocol.MOVESHORT + Protocol.DELIMITER);
				result.append(game.getCurrentPlayer().name()).append(Protocol.DELIMITER);
	
				for (Tile tile : game.getCurrentPlayer().getHand()) {
					result.append(tile.getColourL()).append(tile.getColourV()).append(tile.getColourR())
							.append(tile.getValue()).append(Protocol.DELIMITER);
				}
				result.append(old.getColourL()).append(old.getColourV()).append(old.getColourR())
						.append(old.getValue()).append(Protocol.DELIMITER);
				result.append(rotation).append(Protocol.DELIMITER);
				result.append(index);
				break;
			case Protocol.TILEREPLACE:
				result.append(Protocol.REPLACESHORT + Protocol.DELIMITER);
				result.append(game.getCurrentPlayer().name()).append(Protocol.DELIMITER);
	
				for (Tile tile : game.getCurrentPlayer().getHand()) {
					result.append(tile.getColourL()).append(tile.getColourV()).append(tile.getColourR())
							.append(tile.getValue()).append(Protocol.DELIMITER);
				}
	
				result.append(old.getColourL()).append(old.getColourV()).append(old.getColourR())
						.append(old.getValue()).append(Protocol.DELIMITER);
				break;
			case Protocol.SKIP:
				result.append(Protocol.SKIPSHORT + Protocol.DELIMITER);
				result.append(game.getCurrentPlayer().name()).append(Protocol.DELIMITER);
	
				for (Tile tile : game.getCurrentPlayer().getHand()) {
					result.append(tile.getColourL()).append(tile.getColourV()).append(tile.getColourR())
							.append(tile.getValue()).append(Protocol.DELIMITER);
				}
				break;
		}
		tui.write("Turn made: " + result, "Game Controller#" + id);
		return result.toString();
	}

	/**
	 * Finds the first tile in the given argument if it complies with the protocol.
	 * 
	 * @param arg the argument to extract a tile from.
	 * @return a <code>Tile</code> extracted from the string, null if it doesn't
	 *         comply.
	 */
	public Tile getTile(String[] arg) {
		Tile tile;
		char[] colours = arg[1].toCharArray();
		tile = new Tile(colours[0], colours[1], colours[2], 
				Integer.parseInt("" + colours[3]));
		return tile;
	}

	/**
	 * Finds the index in the given argument if it complies with the protocol.
	 * 
	 * @param arg the argument to extract an index from.
	 * @return a <code>int</code> extracted from the string.
	 */
	public int getIndex(String arg) {
		return Integer.parseInt(arg);
	}

	/**
	 * Finds the rotation in the given argument if it complies with the protocol.
	 * 
	 * @param arg the argument to extract a rotation from.
	 * @return an <code>int</code> extracted from the string, -1 if it doesn't
	 *         comply.
	 */
	public int getRotation(String arg) {
		String[] move = arg.split(Protocol.DELIMITER);
		if (move[0].equals(Protocol.MOVE)) {
			return Integer.parseInt(move[2]);
		}
		return -1;
	}

}
