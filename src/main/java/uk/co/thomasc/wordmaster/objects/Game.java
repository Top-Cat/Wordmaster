package uk.co.thomasc.wordmaster.objects;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.thomasc.wordmaster.objects.callbacks.TurnAddedListener;

public class Game {

	public static HashMap<String, Game> games = new HashMap<String, Game>();
	
	public static Game getGame(String id) {
		if (games.containsKey(id)) {
			return games.get(id);
		}
		return null;
	}
	
	public static Game getGame(String id, User player, User opponent) {
		if (games.containsKey(id)) {
			return games.get(id);
		} else {
			Game newGame = new Game(id, player, opponent);
			games.put(id, newGame);
			return newGame;
		}
	}
	
	public static Game getGame(String playerID, String opponentID) {
		for (Game g : games.values()) {
			if (g.getPlayer().getPlusID().equals(playerID) &&
					g.getOpponent().getPlusID().equals(opponentID)) {
				return g;
			}
		}
		return null;
	}
	
	/* Properties */
	private String gameID;
	private User player, opponent;
	private ArrayList<Turn> turns = new ArrayList<Turn>();
	private int latestTurnId = 0;
	private int oldestTurnId = 0;
	private int playerScore = 0, opponentScore = 0, turnNumber = 1;
	private String playerWord = "", opponentWord = "";
	private boolean needsWord = false, playersTurn = false;
	private ArrayList<TurnAddedListener> turnListeners = new ArrayList<TurnAddedListener>();
	private long lastUpdated = 0;

	/* Constructors */
	private Game(String id, User player, User opponent) {
		gameID = id;
		this.player = player;
		this.opponent = opponent;
	}

	/* Getters */
	public String getID() {
		return gameID;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public int getOpponentScore() {
		return opponentScore;
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public String getPlayerWord() {
		return playerWord;
	}

	public String getOpponentWord() {
		return opponentWord;
	}

	public ArrayList<Turn> getTurns() {
		return turns;
	}

	public boolean needsWord() {
		return needsWord;
	}

	public boolean isPlayersTurn() {
		return playersTurn;
	}

	public User getPlayer() {
		return player;
	}

	public User getOpponent() {
		return opponent;
	}
	
	public long getLastUpdateTimestamp() {
		return lastUpdated;
	}

	/* Setters */
	public void setPlayerWord(String word) {
		playerWord = word;
	}

	public void setOpponentWord(String word) {
		opponentWord = word;
	}

	public void setScore(int playerScore, int opponentScore) {
		this.playerScore = playerScore;
		this.opponentScore = opponentScore;
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	public void setNeedsWord(boolean needsWord) {
		this.needsWord = needsWord;
	}

	public void setPlayersTurn(boolean isPlayersTurn) {
		playersTurn = isPlayersTurn;
	}
	
	public void setLastUpdateTimestamp(long timestamp) {
		lastUpdated = timestamp;
	}

	/* Other Methods */
	public void addTurn(Turn turn) {
		turns.add(turn);
		if (turn.getID() > latestTurnId) {
			latestTurnId = turn.getID();
			turnNumber = (turn.getTurnNum() / 2) + 1;
			setLastUpdateTimestamp(turn.getUnixTimestamp());
		}
		if (turn.getID() < oldestTurnId) {
			oldestTurnId = turn.getID();
		}
		for (TurnAddedListener l : turnListeners) {
			l.onTurnAdded(turn);
		}
	}
	
	public void addTurnListener(TurnAddedListener listener) {
		turnListeners.add(listener);
	}

	public void removeTurnListener(TurnAddedListener listener) {
		turnListeners.remove(listener);
	}

	public int getPivotLatest() {
		return latestTurnId;
	}
	
	public int getPivotOldest() {
		return oldestTurnId;
	}

}
