package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import exceptions.EndOfProgram;
import model.ComputerPlayer;
import model.HumanPlayer;
import model.Player;
import model.Strategy;
/**
 * @author pmbos A class modelling a spectrangle client.
 */
public class SpectrangleClient {
	public static final char[] EXTENSIONS = {};
	private static final List<Character> enabledExtensions = new ArrayList<>();

	private Socket socket;
	private String name;
	private final SpectrangleTUI ui;

	/**
	 * The bootloader of the client.
	 * 
	 * @param args bootloader arguments, not used.
	 */
	public static void main(String[] args) {
		SpectrangleClient client = new SpectrangleClient();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.start();
	}

	/**
	 * Creates a new spectrangle client.
	 */
	public SpectrangleClient() {
		ui = new SpectrangleTUI();
	}

	/**
	 * @return a list of enabled extensions.
	 */
	public static List<Character> getExtensions() {
		return enabledExtensions;
	}

	/**
	 * Adds the matching extensions from extensions and the supported extensions to
	 * the enabledExtensions list.
	 * 
	 * @param extensions the extensions to check and activate.
	 */
	public static void enableExtensions(char[] extensions) {
		for (char extension : extensions) {
			for (char ext : EXTENSIONS) {
				if (ext == extension) {
					enabledExtensions.add(extension);
				}
			}
		}
	}

	/**
	 * Starts the client.
	 */
	public void start() {
		ui.showWelcomeMessage();
		boolean next = true;
		boolean same;
		InetAddress address;
		int preference = 0;
		int port;
		int type;
		Player player = null;

		while (next) {
			//Runs for as long as the user wishes to play another game.
			same = true;
			try {
				try {
					do {
						//Asks for the user's name.
						name = ui.askName();
					} while (name.isEmpty());

				} catch (IOException e) {
					ui.writeError(e, "SpectrangleClient");
				}

				while (true) {
					try {
						//Asks for the type of player the user wants to use.
						type = ui.askType();
						if (type == 1 || type == 2) {
							break;
						}
					} catch (NumberFormatException e) {
						ui.writeError("Enter either 1 or 2", "SpectrangleClient");
					} catch (IOException e) {
						ui.writeError(e, "SpectrangleClient");
					}
				}

				Strategy strategy = null;

				while (true) {
					do {
						try {
							//Asks for the ip address the user wants to connect to.
							address = ui.askIp();
							break;
						} catch (UnknownHostException e) {
							ui.write("Enter a valid ip address");
						} catch (IOException e) {
							ui.writeError(e, "SpectrangleClient");
						}
					} while (true);

					do {
						try {
							//Asks for the port the connection should use.
							port = ui.askPort();
							break;
						} catch (NumberFormatException e) {
							ui.write("Enter a valid port number");
						} catch (IOException e) {
							ui.writeError(e, "SpectrangleClient");
						}
					} while (true);

					do {
						try {
							//Asks for the user's preference in player count.
							preference = ui.askPreference();
						} catch (NumberFormatException e) {
							ui.write("Enter a valid integer");
						} catch (IOException e) {
							ui.writeError(e, "SpectrangleClient");
						}
					} while ((preference < 2 && preference != -1) || preference > 4);

					while (same) {
						//Runs for as long as the user wants to keep playing on the same server.
						try {
							//Attempts to create the proper player depending 
							//on the user's choice in type.
							if (type == 1) {
								player = new HumanPlayer(name, preference);
							} else if (type == 2) {
								player = new ComputerPlayer(name, preference);
								strategy = ui.askStrategy(player);
								((ComputerPlayer) player).setStrategy(strategy);
							}
						} catch (IOException e) {
							ui.writeError(e, "SpectrangleClient");
						}

						ui.write("Attempting connection: " + address + ":" + port);
						try {
							//Attempts to open a connection.
							socket = new Socket(address, port);
						} catch (IOException e) {
							ui.writeError(e, "SpectrangleClient");
							break;
						}
						
						//Creates a stream input handler for server communication.
						Listener handleStreamInput = new Listener(socket, ui, player);
						handleStreamInput.start();
						try {
							//Waits for the handler to terminate.
							handleStreamInput.join();
						} catch (InterruptedException e) {
							ui.writeError(e, "SpectrangleClient");
						}

						try {
							//Asks whether the user wants to play another game.
							next = ui.askContinue();
							if (!next) {
								throw new EndOfProgram("Exit confirmed...");
							}
							//Asks whether the user wants to continue on the same server.
							same = ui.askSameServer();
							if (!same) {
								break;
							}
						} catch (IOException e) {
							ui.writeError(e, "Spectrangleclient");
						}
					}
					break;
				}
				
			} catch (EndOfProgram e) {
				ui.writeError(e, "SpectrangleClient");
				break;
			}
		}
		try {
			//Closes the connection.
			socket.close();
		} catch (IOException e) {
			ui.writeError(e, "SpectrangleClient");
		} catch (NullPointerException e) {
			ui.writeError("Socket not open", "SpectrangleClient");
		}
	}
}
