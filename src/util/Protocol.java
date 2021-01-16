package util;

import java.util.concurrent.TimeUnit;

import model.Tile;

/**
 * The class holding all the protocol values.
 * @author pmbos
 */
public class Protocol {
	public static final String EXITCOMMAND = "exit";
	public static final int INPUTCHECKINTERVAL = 100;
	public static final int PORT = 6666;
	public static final TimeUnit TIMEUNIT = TimeUnit.SECONDS; 
	public static final int RETRIES = 1;
	public static final int TIMEOUT = 90;
	public static final String DELIMITER = ",";
	public static final String MOVESHORT = "M";
	public static final String SKIPSHORT = "S";
	public static final String REPLACESHORT = "R";
	public static final String CONNECTREQUEST = "CONNECTREQUEST";
	public static final String CONNECTACCEPT = "CONNECTACCEPT";
	public static final String JOINGAME = "JOINGAME";
	public static final String GAMESTARTED = "GAMESTARTED";
	public static final String MOVEREQUEST = "MOVEREQUEST";
	public static final String MOVE = "MOVE";
	public static final String TILEREPLACE = "TILEREPLACE";
	public static final String SKIP = "SKIP";
	public static final String TURNMADE = "TURNMADE";
	public static final String PLAYERKICKED = "PLAYERKICKED";
	public static final String GAMEOVER = "GAMEOVER";
	public static final String INVALID = "INVALID";

	public static final char RED = 'R';
	public static final char BLUE = 'B';
	public static final char GREEN = 'G';
	public static final char YELLOW = 'Y';
	public static final char PURPLE = 'P';
	public static final char WHITE = 'W';
	
	public static final int ROTATION1 = 0;
	public static final int ROTATION2 = 1;
	public static final int ROTATION3 = 2;
	public static final Tile JOKER = new Tile('W', 'W', 'W', 1); 
	
	public static final String ACTIONONTIMEOUT = PLAYERKICKED;
	public static final Tile[] VALIDTILES = {
		new Tile('R', 'R', 'R', 6),
		new Tile('B', 'B', 'B', 6),
		new Tile('G', 'G', 'G', 6),
		new Tile('Y', 'Y', 'Y', 6),
		new Tile('P', 'P', 'P', 6),
		new Tile('R', 'R', 'Y', 5),
		new Tile('R', 'R', 'P', 5),
		new Tile('B', 'B', 'R', 5),
		new Tile('B', 'B', 'P', 5),
		new Tile('G', 'G', 'R', 5),
		new Tile('G', 'G', 'B', 5),
		new Tile('Y', 'Y', 'G', 5),
		new Tile('Y', 'Y', 'B', 5),
		new Tile('P', 'P', 'Y', 5),
		new Tile('P', 'P', 'G', 5),
		new Tile('R', 'R', 'B', 4),
		new Tile('R', 'R', 'G', 4),
		new Tile('B', 'B', 'G', 4),
		new Tile('B', 'B', 'Y', 4),
		new Tile('G', 'G', 'Y', 4),
		new Tile('G', 'G', 'P', 4),
		new Tile('Y', 'Y', 'R', 4),
		new Tile('Y', 'Y', 'P', 4),
		new Tile('P', 'P', 'R', 4),
		new Tile('P', 'P', 'B', 4),
		new Tile('Y', 'B', 'P', 3),
		new Tile('R', 'G', 'Y', 3),
		new Tile('B', 'G', 'P', 3),
		new Tile('G', 'R', 'B', 3),
		new Tile('B', 'R', 'P', 2),
		new Tile('Y', 'P', 'R', 2),
		new Tile('Y', 'P', 'G', 2),
		new Tile('G', 'R', 'P', 1),
		new Tile('B', 'Y', 'G', 1),
		new Tile('R', 'Y', 'B', 1),
		JOKER
	};
	
}
