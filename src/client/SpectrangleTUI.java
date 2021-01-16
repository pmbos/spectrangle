package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import exceptions.EndOfProgram;
import model.Board;
import model.Player;
import model.Strategy;
import model.Tile;
import strategies.Strategies;
import util.ColourCompare;
import util.Protocol;

/**
 * A class representing the textual user interface of a game of 
 * spectrangle.
 * @author pmbos
 */
public class SpectrangleTUI implements Observer {
	private final PrintWriter console;
	private final Board board;
	private ConsoleInputThread input;
	private String received;
	private Thread in;

	/**
	 * Creates a new TUI.
	 */
	public SpectrangleTUI() {
		console = new PrintWriter(System.out, true);
		input = new ConsoleInputThread(this);
		board = new Board();
		received = null;
		in = new Thread(input);
		in.setDaemon(true);
		in.start();
	}

	/**
	 * Displays the welcome message.
	 */
	public void showWelcomeMessage() {
		write("Welcome to the spectrangle game client!");
		write("You can exit anytime by typing: '" + Protocol.EXITCOMMAND + "'.");
	}

	/**
	 * Displays the board.
	 */
	public void showBoard() {
		write(board.toString());
	}

	/**
	 * Writes a string representation of the given player's tiles to standard
	 * output. Possibly with selection numbers below.
	 * 
	 * @param player      the player whose tiles to print.
	 * @param showNumbers the indicator used to determine if selection numbers
	 *                    should be shown.
	 */
	public void showTiles(Player player, boolean showNumbers) {
		write(player.name() + "'s tiles" + ":");
		write(player.toString());

		if (showNumbers) {
			StringBuilder numbers = new StringBuilder();
			for (int i = 1; i <= player.numInHand(); i++) {
				numbers.append("  ").append(i).append("         ");
			}
			write(numbers.toString());
		}
	}

	/**
	 * Asks the user which move to make (skip or replace) if said user cannot make a
	 * move.
	 * 
	 * @return a single character representation of the type of move to make.
	 * @throws EndOfProgram when the program ended.
	 */
	//@ensures \result == Protocol.SKIPSHORT || \result == Protocol.REPLACESHORT;
	public String askForMoveType() throws EndOfProgram {
		while (true) {
			try {
				write("You do not hava a tile with which you can make a move");
				write("Please select what to do: skip (" + Protocol.SKIPSHORT + ")" 
						+ "/exchange tile ("
						+ Protocol.REPLACESHORT + "): ");
				String read = readString();

				if (read.equalsIgnoreCase(Protocol.SKIPSHORT)) {
					return Protocol.SKIPSHORT;
				} else if (read.equalsIgnoreCase(Protocol.REPLACESHORT)) {
					return Protocol.REPLACESHORT;
				}
			} catch (IOException ignored) {

			}
		}
	}

	/**
	 * Asks the player to select an index to place a tile on.
	 * 
	 * @return the index the user specified.
	 * @throws EndOfProgram when the program ended.
	 * @throws IOException when the client cannot read from the stream.
	 */
	//@ensures board().isValidField(\result);
	public int askForIndex() throws IOException, EndOfProgram {
		while (true) {
			try {
				String read = readString("Please enter the index of the field " 
						+ "you want to place a tile on: ");
				int index = -1;
				
				if (read.equalsIgnoreCase("hint")) {
					//In case a hint is requested, selects one random tile the player
					//can play and a random index the player can put the tile on.
					write("Mind: you only get one hint per move");
					Map<Tile, List<Integer>> plays = Listener.getPlayer().getPlays(board);
					Random random = new Random();
					List<Tile> validTiles = new ArrayList<>();
					for (Tile tile : plays.keySet()) {
						if (plays.get(tile).size() > 0) {
							validTiles.add(tile);
						}
					}
					
					int tileIndex = random.nextInt(validTiles.size());
					Tile tile = validTiles.get(tileIndex);
					Tile graphic = new Tile(tile.getColourL(), tile.getColourV(),
									tile.getColourR(), tile.getValue());
					List<Integer> indices = new ArrayList<>(plays.get(tile));
					
					//Displays a tile the user can place and at which index the tile can be placed.
					write("HINT: you can place the following tile: ");
					int field = random.nextInt(indices.size());
					if (!board.isUp(indices.get(field))) {
						graphic.flip();
					}
					write(graphic.graphicRepresentation(0));
					String hint = " at index: " + indices.get(field);
					
					write(hint);
					
					//Again asks the user for a choice of index.
					read = readString("Please enter the index of the field " 
							+ "you want to place a tile on: ");
				} else if (read.equals("exit")) {
					throw new EndOfProgram("Exit confirmed");
				}
				
				index = Integer.parseInt(read);

				//Verifies whether the index is one that the user can place a tile on.
				if (board.isValidField(index) && board.isEmpty(index)) {
					for (Entry<Tile, List<Integer>> entry : 
						Listener.getPlayer().getPlays(board).entrySet()) {
						if (entry.getValue().contains(index)) {
							received = null;
							return index;
						}
					}
				}
				write("Please enter a valid index upon which you can place a tile");
			} catch (NumberFormatException e) {
				write("Please enter a valid index upon which you can place a tile");
			}
		}
	}

	/**
	 * Asks the user which permutation to use.
	 * 
	 * @param tile  the tile whose permutations to consider.
	 * @param index the index of the field upon which the tile is to be placed.
	 * @return the number (0, 1, 2) representing the rotation of the tile.
	 * @throws EndOfProgram when the program ended.
	 * @throws NumberFormatException when the user enters an invalid integer.
	 * @throws IOException when the client cannot read from the stream.
	 */
	/*@requires tile.isValid() &&
			 	board().isValidField(index);
	 */
	public int askForPermutation(Tile tile, int index) throws IOException, NumberFormatException, 
		EndOfProgram {
		StringBuilder permutations = new StringBuilder();
		Tile copy = new Tile(tile.getColourL(), tile.getColourV(), tile.getColourR(),
				tile.getValue());

		List<Integer> validPermutations = new ArrayList<>();
		int j = 0;

		for (int i = 0; i < 3; i++) {
			if (ColourCompare.checkPermutation(copy, index, board.edges(index), board(), i)) {
				validPermutations.add(i);
				j++;
				if (tile.getColourL() == tile.getColourR() 
						&& tile.getColourL() == tile.getColourV()) {
					break;
				}
			}
		}

		if (!board.isUp(index)) {
			copy.flip();
		}
		int f = 0;
		while (f < 5) {
			for (Integer perm : validPermutations) {

				String rep = copy.graphicRepresentation(perm);

				switch (f) {
					case 0 -> permutations.append(rep, 0, 8).append("    ");
					case 1 -> permutations.append(rep, 10, 18).append("    ");
					case 2 -> permutations.append(rep, 20, 28).append("    ");
					case 3 -> permutations.append(rep, 30, 38).append("    ");
					case 4 -> permutations.append(rep, 40, 48).append("    ");
				}
			}
			permutations.append("\n");
			f++;
		}

		StringBuilder numbers = new StringBuilder();
		for (int p = 1; p <= j; p++) {
			numbers.append("     ").append(p).append("    ");
		}
		write(permutations.toString());
		write(numbers.toString());

		int permutation = -1;
		do {
			String read = readString("Please select the permutation you want to play: ");
			if (read.equals("exit")) {
				throw new EndOfProgram("Exit confirmed");
			}
			permutation = Integer.parseInt(read);
		} while (permutation < 0 || permutation > j);
		received = null;

		return validPermutations.get(permutation - 1);

	}

	/**
	 * Tels the user what move has been made based on the given name, tile, index and rotations.
	 * @param name the name of the player that made the move.
	 * @param tile the tile the player moved.
	 * @param index the index the player moved the tile to.
	 * @param rotation the rotation the tile has on the field.
	 */
	public void tellUserMoveMade(String name, Tile tile, int index, int rotation) {
		board.setTile(index, tile, rotation);
		write(name + " has placed: \n " + tile.graphicRepresentation(rotation) + "\n");
		write("On field " + index + ".");
	}

	/**
	 * Tells the user that a move has been skipped.
	 * @param name the name of the player who skipped their move.
	 */
	public void tellUserSkipped(String name) {
		write(name + " has skipped his/her move.");
	}

	/**
	 * Tells the user the final results of the game.
	 * @param name the name of the player.
	 * @param score the score belonging to the player.
	 */
	public void tellUserFinalResults(String name, String score) {
		write(name + " scored: " + score + " points\n");
	}

	/**
	 * Tells the user a player has been kicked and checks if the game can continue.
	 * @param name the name of the player who has been kicked.
	 * @param canContinue the flag indicating if the game can continue.
	 */
	public void tellUserPlayerKicked(String name, boolean canContinue) {
		write(name + " got kicked from the game.\n");

		if (!canContinue) {
			write("This game has fallen below the minumum required players, " 
					+ "you will be disconnected.");
		}
	}

	/**
	 * Tells the user that a player has exchanged one of their tiles.
	 * @param name the name of the player who exchanged a tile.
	 * @param old the tile they exchanged.
	 */
	public void tellUserExchanged(String name, Tile old) {
		write(name + " has exchanged:\n" + old.graphicRepresentation(0) + "\n");
	}

	/**
	 * Sets a tile to the client's board.
	 * @param tile the tile to set.
	 * @param index the index to set the tile on.
	 * @param rotation the rotation the tile should have.
	 */
	/*
	 * @requires tile.isValid() && board().isValidIndex();
	 * @ensures getBoard().setTile(index, tile, rotation);
	 */
	public void setTile(Tile tile, int index, int rotation) {
		board.setTile(index, tile, rotation);
	}

	/**
	 * Asks the user to select a tile to play.
	 * 
	 * @param index  the index on which the tiles should be placed.
	 * @return the tile the user specified.
	 * @throws EndOfProgram when the program ended.
	 * @throws NumberFormatException when the user entered an invalid integer.
	 * @throws IOException when the client cannot read form the stream.
	 */
	//@requires board().isValidField(index);
	//@ensures \result.isValid();
	public Tile askForTile(int index) throws IOException, NumberFormatException, EndOfProgram {
		List<Tile> validTiles = new ArrayList<>();
		for (Entry<Tile, List<Integer>> entry : Listener.getPlayer().getPlays(board()).entrySet()) {
			if (entry.getValue().contains(index)) {
				validTiles.add(entry.getKey());
			}
		}

		StringBuilder res = new StringBuilder();

		int f = 0;
		while (f < 5) {
			for (Tile tile : validTiles) {
				String rep = tile.graphicRepresentation(0);

				switch (f) {
					case 0 -> res.append(rep, 0, 8).append("    ");
					case 1 -> res.append(rep, 10, 18).append("    ");
					case 2 -> res.append(rep, 20, 28).append("    ");
					case 3 -> res.append(rep, 30, 38).append("    ");
					case 4 -> res.append(rep, 40, 48).append("    ");
				}
			}
			res.append("\n");
			f++;
		}

		write(res.toString());

		StringBuilder numbers = new StringBuilder();
		for (int i = 1; i <= validTiles.size(); i++) {
			numbers.append("  ").append(i).append("         ");
		}
		write(numbers.toString());

		int tile;
		do {
			String read = readString("Please enter the number of the tile you want to play: ");
			if (read.equals("exit")) {
				throw new EndOfProgram("Exit confirmed");
			}
			tile = Integer.parseInt(read);
		} while (tile < 1 || tile > validTiles.size());
		received = null;

		if (!validTiles.get(tile - 1).isValid()) {
			write(" get tile: Invalid tile");
		}
		return validTiles.get(tile - 1);

	}

	/**
	 * Asks the user what tile to replace.
	 * 
	 * @return the tile to replace.
	 * @throws EndOfProgram when the program ended.
	 * @throws NumberFormatException when an invalid integer was entered.
	 * @throws IOException when the stream cannot be read.
	 */
	//@ensures \result.isValid();
	public Tile askForReplace() throws IOException, NumberFormatException, EndOfProgram {
		int tile;
		showTiles(Listener.getPlayer(), true);
		do {
			String read = readString("Please enter the number of the tile you want to replace");
			if (read.equals("exit")) {
				throw new EndOfProgram("Exit confirmed");
			}
			tile = Integer.parseInt(read);
		} while (tile < 1 || tile > Listener.getPlayer().numInHand());
		received = null;
		return Listener.getPlayer().getHand().get(tile - 1);
	}

	/**
	 * @return the client's board.
	 */
	/*@pure */public Board board() {
		return board;
	}

	/**
	 * Asks the user if they want to play another game.
	 * @return true if answered in the affirmative, false otherwise.
	 * @throws IOException when the client is unable to read from the input stream.
	 * @throws EndOfProgram when the program has been ended.
	 */
	//@ensures \result == true || \result == false;
	public boolean askContinue() throws IOException, EndOfProgram {
		if (!in.isAlive()) {
			input = new ConsoleInputThread(this);
			in = new Thread(input);
			in.start();
		}
		
		while (true) {
			String read = readString("Do you want to play another game? (y/n): ");
			received = null;
			if (read.equalsIgnoreCase("y")) {
				return true;
			} else if (read.equalsIgnoreCase("n")) {
				in.interrupt();
				return false;
			}
		}
	}
	
	/**
	 * Asks the player which strategy to use and adds the player to the strategy.
	 * @param player the player to add.
	 * @return the selected strategy.
	 * @throws IOException when the client was unable to read from the input stream.
	 * @throws NumberFormatException when the client did not enter a valid integer.
	 * @throws EndOfProgram when the program was ended.
	 */
	//@requires player != null;
	public Strategy askStrategy(Player player) throws IOException, NumberFormatException, 
		EndOfProgram {
		write(Strategies.display());
		int index = -1;
		do {
			index = readInt("Please select the strategy you want to use:");
		} while (index > Strategies.STRATEGIES.length || index < 1);
		received = null;
		return Strategies.get(index - 1, player);
	}

	/**
	 * Asks for the user's preference.
	 * @return an integer representing the amount of player's the user wants to play with.
	 * @throws IOException when the client was unable to read from the input stream.
	 * @throws NumberFormatException when the client did not enter a valid integer.
	 * @throws EndOfProgram when the program was ended.
	 */
	public int askPreference() throws IOException, NumberFormatException, EndOfProgram {
		String preference = readString("With how many players do you want to play? 2-4");
		int pref = 0;
		if (preference.isEmpty()) {
			pref = -1;
		} else {
			pref = Integer.parseInt(preference);
		} 
		received = null;
		return pref;
	}

	/**
	 * Asks the user if they want to play on the same server.
	 * @return true if answered in the affirmative, false otherwise.
	 * @throws IOException when the client was unable to read from the input stream.
	 * @throws EndOfProgram when the program was ended.
	 */
	//@ensures \result == true || \result == false;
	public boolean askSameServer() throws IOException, EndOfProgram {
		while (true) {
			String read = readString("Do you want to play on the same server? (y/n)");
			received = null;
			if (read.equalsIgnoreCase("y")) {
				return true;
			} else if (read.equalsIgnoreCase("n")) {
				return false;
			}
		}
	}

	/**
	 * Asks the user for their name.
	 * @return the name the user entered.
	 * @throws IOException when the client was unable to read from the input stream.
	 * @throws EndOfProgram when the program was ended.
	 */
	public String askName() throws IOException, EndOfProgram {
		String name = readString("Please enter your name: ");
		received = null;
		return name;
	}

	/**
	 * Asks the user which port to connect over.
	 * @return the port number the user entered.
	 * @throws IOException when the client was unable to read from the input stream.
	 * @throws NumberFormatException when the user did not enter a valid integer.
	 * @throws EndOfProgram when the program was ended.
	 */
	public int askPort() throws IOException, NumberFormatException, EndOfProgram {
		int port = readInt("Please enter the port you want to connect over: ");
		received = null;
		return port;
	}

	/**
	 * Asks the user for the type of player to use.
	 * @return the user's choice.
	 * @throws IOException when the client was unable to read from the input stream.
	 * @throws NumberFormatException when the user did not enter a valid integer.
	 * @throws EndOfProgram when the program was ended.
	 */
	public int askType() throws IOException, NumberFormatException, EndOfProgram {
		String toWrite1 = "[1] Play yourself";
		String toWrite2 = "[2] Have the computer play";
		String result = String.format("%1$-26s%2$s", toWrite1, toWrite2);
		write(result);
		int type = readInt("Please select the option you want:");
		received = null;
		return type;

	}

	/**
	 * Asks the user which ip to connect to.
	 * @return the ip address of the server to connect to.
	 * @throws UnknownHostException when the given ip does not exist.
	 * @throws IOException when the client was unable to read from the input stream.
	 * @throws EndOfProgram when the program was ended.
	 */
	public InetAddress askIp() throws UnknownHostException, IOException, EndOfProgram {
		String read = readString("Please enter the ip you want to connect to: ");
		received = null;
		return InetAddress.getByName(read);
	}

	/**
	 * Writes the given message to standard output.
	 * @param msg the message to write to standard output.
	 */
	public synchronized void write(String msg) {
		console.println(msg);
	}

	/**
	 * Writes the given error to standard output.
	 * @param e the error to document.
	 * @param location the location where the error occurred.
	 */
	public void writeError(Exception e, String location) {
		write("(" + location + "): " + e.getMessage());
	}

	/**
	 * Writes a given message as an error to standard output.
	 * @param e the string to write.
	 * @param location the location where the error occurred.
	 */
	public void writeError(String e, String location) {
		write("(" + location + "): " + e);
	}

	/**
	 * Reads a string from standard input after writing the given msg.
	 * @param msg the msg to write.
	 * @return the user's input.
	 * @throws IOException if the client was unable to read from the input stream.
	 * @throws EndOfProgram when the program was ended.
	 */
	public String readString(String msg) throws IOException, EndOfProgram {
		write(msg);
		return readString();

	}

	/**
	 * Reads a string from standard input.
	 * @return the user's input.
	 * @throws IOException if the client was unable to read from the input stream.
	 * @throws EndOfProgram when the program was ended.
	 */
	public String readString() throws IOException, EndOfProgram {
		getInput();
		return received;
	}

	/**
	 * Reads an integer from standard input after writing the given msg.
	 * @param msg the message to write.
	 * @return the integer the user entered.
	 * @throws NumberFormatException when the user did not enter a valid integer.
	 * @throws IOException if the client was unable to read from the input stream.
	 * @throws EndOfProgram when the program was ended.
	 */
	public int readInt(String msg) throws NumberFormatException, IOException, EndOfProgram {
		int result = Integer.parseInt(readString(msg));
		received = null;
		return result;
	}
	
	public void showConnected() {
		write("Connection established!");
		write("Waiting for game start...");
	}
	
	/**
	 * @return true if the input thread is still alive, false otherwise.
	 */
	public boolean isInputAlive() {
		return in.isAlive();
	}
	
	/**
	 * Instructs the input thread to start reading input.
	 * @throws EndOfProgram when the program was ended.
	 */
	private synchronized void getInput() throws EndOfProgram {
		input.notifyMe();
		try {
			wait();
			if (received.equalsIgnoreCase(Protocol.EXITCOMMAND)) {
				throw new EndOfProgram("Exit confirmed...");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if (arg instanceof String) {
			received = input.getResult();
			this.notify();
		} else {
			write("Error: argument not of type string");
		}
	}
}
