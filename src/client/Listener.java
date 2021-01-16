package client;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import exceptions.CannotCreatePlayer;
import exceptions.EndOfProgram;
import exceptions.NotOfProtocol;
import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Player;
import model.Tile;
import util.Protocol;

/**
 * The class handling server communication.
 * @author pmbos
 */
public class Listener extends Thread {
	private BufferedReader in;
	private SpectrangleTUI ui;
	private PrintWriter out;
	private static Player player;
	private List<Player> opponents;
	private Socket socket;
	private boolean gameOver = false;
	
	/**
	 * Creates a new <code>Listener</code> with the given socket, ui and player.
	 * @param socket the socket to use for communication.
	 * @param ui the ui to write messages to.
	 * @param player the player to use.
	 */
	public Listener(Socket socket, SpectrangleTUI ui, Player player) {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
			out = new PrintWriter(socket.getOutputStream(), true);
			Listener.player = player;
			this.ui = ui;
			this.socket = socket;
			opponents = new ArrayList<>();
		} catch (IOException e) {
			ui.writeError(e, "Listener");
		}
	}
	
	/**
	 * @return the player the listener uses.
	 */
	/*@pure */public static Player getPlayer() {
		return player;
	}
	
	/**
	 * Runs the listener thread, follows the protocolled flow of the game.
	 */
	public void run() {
		//Sending connect request;
		out.println(constructConnectRequest());
		String input;
		while (!gameOver) {
			try {
				int onRead = ' ';
				while (!in.ready()) {
					Thread.sleep(300);
					if (!ui.isInputAlive()) {
						throw new EndOfProgram("Exit confirmed...");
					}
					//Blocks for input from server. A check to confirm the server is still
					//connected.
					onRead = in.read();
					if (onRead == -1) {
						throw new EndOfProgram("Server disconected!");
					}
				}
				//Reconstructs the original server message.
				input = in.readLine();
				if (onRead != ' ') {
					input = (char) onRead + input;
				}
				
				interpret(input);
				
			} catch (IOException e) {
				ui.writeError("Server disconnected!", "Listener");
				break;
			} catch (EndOfProgram e) {
				ui.writeError(e, "Listener");				
				break;
			} catch (InterruptedException e) {
				ui.writeError(e, "Listener");
			}
		}
		out.close();
		try {
			in.close();
			socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Interprets the given arg as a server response.
	 * @param arg the argument to interpret.
	 * @throws EndOfProgram when the program ended.
	 */
	//@requires arg != null;
	private void interpret(String arg) throws EndOfProgram {
		String[] split = arg.split(Protocol.DELIMITER);
		switch (split[0]) {
			case Protocol.CONNECTACCEPT:
				//Notifies the user that the client has connected and enables extensions.
				ui.showConnected();
				if (split.length == 2) {
					SpectrangleClient.enableExtensions(split[1].toCharArray());
				}
				out.println(constructJoin());
				break;
			case Protocol.GAMESTARTED:
				//Notifies the user that the game has started.
				String[] gsParts = arg.split("\\|");
				for (int i = 1; i < gsParts.length; i++) {
					Player playerFromString;
					List<Tile> tiles = new ArrayList<>();
					String[] playerParts = gsParts[i].split(Protocol.DELIMITER);
					
					for (String part : playerParts) {
						//Fetches all the users tiles
						if (Arrays.asList(playerParts).indexOf(part) < 4) {
							Tile tile = new Tile(part.charAt(0), part.charAt(1), part.charAt(2),
									Integer.parseInt("" + part.charAt(3)));
							tiles.add(tile);
						} else if (Arrays.asList(playerParts).indexOf(part) == 4) {
							try {
								//Attempts to create players with their respective tiles
								//According to the server's information
								playerFromString = new HumanPlayer(part);
								playerFromString.getHand().addAll(tiles);
								opponents.add(playerFromString);
							} catch (IOException e) {
								ui.writeError(new CannotCreatePlayer(
										"Unable to create human player"), 
										"ServerCommunicationThread");
							}
							
						} else {
							ui.writeError(new NotOfProtocol(
									"Protocol was not observed"), 
									"ServerCommunicationThread");
						}
					}
				}
				
				Iterator<Player> it = opponents.iterator();
				while (it.hasNext()) {
					//Checks for this client's player in the list of opponents and
					//removes it. Assigns that player's hand to the local player.
					Player inIterator = it.next();
					if (inIterator.name().equals(player.name())) {
						player.getHand().addAll(inIterator.getHand());
						it.remove();
					}
				}
				ui.showBoard();
				break;
				
			case Protocol.MOVEREQUEST:
				//Notifies the user that it is their move.
				try {
					//Checks if the player is human or not.
					if (player instanceof HumanPlayer) {
						if (afterMoveRequest().equals(Protocol.MOVESHORT)) {
							ui.write("Your move! For a hint type: 'hint'");
							for (Player opponent : opponents) {
								ui.showTiles(opponent, false);
							}
							
							ui.showTiles(player, true);
							
							try {
								//Asks for the necessary information to build a move string.
								int index = ui.askForIndex();
								Tile tile = ui.askForTile(index);
								int permutation = ui.askForPermutation(tile, index);
								
								//Writes the move to the server.
								out.println(constructMove(tile, permutation, index));
							} catch (IOException e) {
								ui.writeError(e, "Listener");
							}
						} else if (afterMoveRequest().equals(Protocol.SKIPSHORT)) {
							out.println(constructSkip());
						} else if (afterMoveRequest().equals(Protocol.REPLACESHORT)) {
							//Asks for a tile to replace
							Tile toReplace = null;
							try {
								toReplace = ui.askForReplace();
							} catch (IOException e) {
								ui.writeError(e, "Listener");
							}
							
							out.println(constructTilereplace(toReplace));
						}
					} else if (player instanceof ComputerPlayer) {
						//Fetches a move according to the player's strategy
						String move = ((ComputerPlayer) player).determineMove(ui.board());
						out.println(move);
					} else {
						ui.write("Illegal return on after move request");
					}
				} catch (NotOfProtocol e) {
					ui.writeError(e, "Listener");
				}
				
				
				break;
				
			case Protocol.TURNMADE:
				//Notifies the user of the last turn made.
				if (split[1].equals(Protocol.MOVESHORT)) {
					List<Tile> newHand = new ArrayList<>();
					
					//Checks if this user has been the last to make a move.
					if (split[2].equals(player.name())) {
						//Resets this user's hand and refills it with the tiles from the server.
						player.getHand().clear();
						int i = 2;
						while (i < split.length - 4) {
							i++;
							Tile tile = new Tile(split[i].charAt(0), split[i].charAt(1), 
									split[i].charAt(2), Integer.parseInt("" + split[i].charAt(3)));
							newHand.add(tile);
						}
						player.getHand().addAll(newHand);
						//Constructs the information needed to inform the user about the move made.
						String tile = split[split.length - 3];
						Tile toSet = new Tile(tile.charAt(0), tile.charAt(1), 
								tile.charAt(2), Integer.parseInt("" + tile.charAt(3)));
						int index = Integer.parseInt(split[split.length - 1]);
						int rotation = Integer.parseInt(split[split.length - 2]);
						
						ui.tellUserMoveMade("You", toSet, 
								index, rotation);
					} else {
						//Does the same for all opponents as for the local user.
						for (Player opponent : opponents) {
							if (opponent.name().equals(split[2])) {
								opponent.getHand().clear();
								int i = 2;
								
								while (i < split.length - 4) {
									i++;
									Tile tile = new Tile(split[i].charAt(0), split[i].charAt(1), 
											split[i].charAt(2), 
											Integer.parseInt("" + split[i].charAt(3)));
									newHand.add(tile);
								}
								
								opponent.getHand().addAll(newHand);
								String tile = split[split.length - 3];
								int index = Integer.parseInt(split[split.length - 1]);
								int rotation = Integer.parseInt(split[split.length - 2]);
								Tile moved = new Tile(tile.charAt(0), 
										tile.charAt(1), tile.charAt(2), 
										Integer.parseInt("" + tile.charAt(3)));
								ui.tellUserMoveMade(opponent.name(), 
										moved, index, 
										rotation);
								break;
							}
						}
						
					}
					ui.showBoard();
				} else if (split[1].equals(Protocol.REPLACESHORT)) {
					//Informs the user that a tile has been replaced.
					//Again rebuilding the hand with information from the server.
					if (split[2].equals(player.name())) {
						String replace = split[split.length - 1];
						Tile replaced = new Tile(replace.charAt(0), replace.charAt(1), 
								replace.charAt(2), Integer.parseInt("" + replace.charAt(3)));
						player.getHand().clear();
						int i = 2;
						while (i < split.length - 1) {
							i++;
							Tile tile = new Tile(split[i].charAt(0), split[i].charAt(1), 
									split[i].charAt(2), Integer.parseInt("" + split[i].charAt(3)));
							player.add(tile);
						}

						ui.tellUserExchanged("You", replaced);
					} else {
						//Does the same as the above case for the player. But then for the opponents
						for (Player opponent : opponents) {
							if (opponent.name().equals(split[2])) {
								String replace = split[split.length - 1];
								Tile replaced = new Tile(replace.charAt(0), replace.charAt(1), 
										replace.charAt(2), 
										Integer.parseInt("" + replace.charAt(3)));
								opponent.getHand().clear();
								int i = 2;
								while (i < split.length - 1) {
									i++;
									Tile tile = new Tile(split[i].charAt(0), split[i].charAt(1), 
											split[i].charAt(2),
											Integer.parseInt("" + split[i].charAt(3)));
									opponent.add(tile);
								}
								ui.tellUserExchanged(opponent.name(), replaced);
								break;
							}
						}
					}
				} else if (split[1].equals(Protocol.SKIPSHORT)) {
					ui.tellUserSkipped(split[2]);
				}
				break;
			case Protocol.PLAYERKICKED:
				//Informs the user that a player was kicked from the server.
				boolean okay = true;
				if (opponents.size() < Game.MIN_PLAYERS) {
					okay = false;
				}
				if (split[1].equals(player.name())) {
					ui.tellUserPlayerKicked(player.name(), okay);
				} else {
					opponents.removeIf(player -> player.name().equals(split[1]));
					ui.tellUserPlayerKicked(split[1], okay);
				}
				
				if (!okay) {
					//If the amount of players in the game falls below the minimum
					//the client will disconnect.
					try {
						in.close();
						out.close();
						socket.close();
					} catch (IOException e) {
						ui.writeError(e, "Listener");
					}
					
				}
				break;
			case Protocol.GAMEOVER:
				//Informs the user that the game is over and prints the final results.
				//Finally resets the board.
				String name = "";
				String score = "";
				ui.write("Game Over!");
				int counter = 0;
				for (int i = 1; i < split.length; i++) {
					if (i % 2 == 0) {
						score = split[i];
						counter++;
					} else {
						name = split[i];
						counter++;
					} 
					
					if (counter == 2) {
						ui.tellUserFinalResults(name, score);
						counter = 0;
					}
				}
				gameOver = true;
				ui.board().reset();
				break;
		}
	}
	
	/**
	 * Constructs a string representation of a move request.
	 * @param tile the tile to place.
	 * @param rotation the rotation to use.
	 * @param index the index of the field to place the tile on.
	 * @return a move request.
	 */
	//@requires tile != null;
	private String constructMove(Tile tile, int rotation, int index) {
		return Protocol.MOVE + Protocol.DELIMITER + tile.getColourL() + tile.getColourV()
			+ tile.getColourR() + tile.getValue() + Protocol.DELIMITER
			+ rotation + Protocol.DELIMITER + index;
	}
	
	/**
	 * Constructs a string representation of a skip request.
	 * @return a skip request.
	 */
	private String constructSkip() {
		return Protocol.SKIP;
	}
	
	/**
	 * Constructs a string representation of a replace request.
	 * @param tile the tile to replace.
	 * @return a replace request.
	 */
	//@requires tile != null;
	private String constructTilereplace(Tile tile) {
		return Protocol.TILEREPLACE + Protocol.DELIMITER + tile.getColourL()
			+ tile.getColourV() + tile.getColourR() + tile.getValue();
	}
	
	/**
	 * Checks what kind of move the player should make.
	 * @return a single character representation as to which move should be made.
	 * @throws EndOfProgram when the program ends.
	 * @throws NotOfProtocol when the input does not comply with the protocol.
	 */
	/*@ensures \result == Protocol.MOVESHORT || \result == Protocol.SKIPSHORT ||
				\result == Protocol.REPLACESHORT;			
	 */
	public String afterMoveRequest() throws EndOfProgram, NotOfProtocol {
		if (player.hasPlay(ui.board())) {
			return Protocol.MOVESHORT;
		} else {
			String type = ui.askForMoveType();
			if (type.equals(Protocol.SKIPSHORT)) {
				return Protocol.SKIPSHORT;
			} else if (type.equals(Protocol.REPLACESHORT)) {
				return Protocol.REPLACESHORT;
			} else {
				throw new NotOfProtocol("Invalid input");
			}
		}
	}
	
	/**
	 * Constructs a string representation of a join request.
	 * @return a join request.
	 */
	private String constructJoin() {
		return Protocol.JOINGAME + Protocol.DELIMITER + player.getPreference();
	}
	
	/**
	 * Constructs a connect request.
	 * @return a string representation of a connect request.
	 */
	private String constructConnectRequest() {
		StringBuilder extensions = new StringBuilder();
		for (char extension : SpectrangleClient.EXTENSIONS) {
			extensions.append(extension);
		}
		return Protocol.CONNECTREQUEST + Protocol.DELIMITER + 
				player.name() + Protocol.DELIMITER + extensions.toString();
	}
}
