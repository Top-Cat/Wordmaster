package uk.co.thomasc.wordmaster;

public class GameAPI {

	public static Object[] getMatches(String playerID) {
		// do some server stuff
		return null;
	}
	
	public static Object[] getTurns(String gameID, int turnID, int number) {
		// do some server stuff
		return null;
	}
	
	public static boolean takeTurn(String playerID, String gameID, String word) {
		// do some server stuff
		return false;
	}
	
	public static Game createGame(String playerID, String opponentID) {
		// do some server stuff
		return null;
	}
	
	public static boolean setWord(String playerID, String gameID, String word) {
		// do some server stuff
		return false;
	}
	
}
