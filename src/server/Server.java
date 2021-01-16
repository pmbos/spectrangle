package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import exceptions.CannotConnect;
import exceptions.EndOfProgram;
import exceptions.IllegalBoot;

/**
 * @author pmbos
 * The class modelling a spectrangle game server.
 */
public class Server {
	public static final String USAGE = "Usage: java [path].Server <name> <port>"; 
	public static final char[] EXTENSIONS = {};
	private int port;
	private static ServerSocket serverSocket;
	private static int connections;
	private List<GameMakerThread> gameMakers = new ArrayList<>();
	private final ServerUI tui;
	
	/**
	 * The method which will boot the server.
	 * @param args the arguments containing the name of the server
	 * and the port on which it listens.
	 */
	public static void main(String[] args) throws IllegalBoot {
		Server server = new Server();
		server.start();
	}
	
	/**
	 * Creates a new <code>Server</code>.
	 */
	public Server() {
		connections = 0;
		tui = new ServerUI();
		this.gameMakers.add(new GameMakerThread(tui));
	}
	
	//Queries
	
	/**
	 * @return the port on which the server listens.
	 */
	/*@pure */public int getPort() {
		return port;
	}
	
	/**
	 * @return the server socket of the server.
	 */
	/*@pure */public static ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	/**
	 * @return the amount of active connections.
	 */
	/*@pure */public static int getConnections() {
		return connections;
	}
	
	//Commands
	
	/**
	 * Increments the amount of active connections by the given amount.
	 * @param amount the amount by which to increment.
	 */
	//@ensures getConnections() == \old(getConnections()) + amount;
	public static void incConnections(int amount) {
		connections += amount;
	}
	
	/**
	 * Starts the server. The server will start waiting and accepting connections.
	 */
	public void start() {
		try {
			//Starts the game maker.
			this.gameMakers.get(0).start();
			while (true) {
				try {
					//Asks for a port to listen on.
					port = tui.askPort();
					serverSocket = new ServerSocket(port);
					break;
				} catch (IllegalArgumentException e) {
					tui.writeError(e, "Server");
				} catch (IOException e) {
					tui.write("Socket already in use", "Server");
				}
				
			}
			//Indicates the server is live and listening.
			tui.write("Server Live on: " + port, "Server");
			tui.showServerStarted("Server");
			while (true) {
				Socket socket = getServerSocket().accept();
				Thread handleConnection = 
						new HandleConnectionThread(connections, socket, gameMakers.get(0), tui);
				handleConnection.start();

			}
		} catch (IOException e) {
			tui.writeError(new CannotConnect("Server socket closed. Exiting.."), 
					"Server");
			gameMakers.get(0).interrupt();
		} catch (EndOfProgram e) {
			tui.writeError(e, "Server");
			gameMakers.get(0).interrupt();
		}
		System.exit(0);
	}
}
