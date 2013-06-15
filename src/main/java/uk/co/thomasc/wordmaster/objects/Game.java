package uk.co.thomasc.wordmaster.objects;

import java.util.ArrayList;

import uk.co.thomasc.wordmaster.objects.callbacks.TurnAddedListener;

public class Game {

	/* Properties */
	private String gameID;
	private User player, opponent;
	private ArrayList<Turn> turns = new ArrayList<Turn>();
	private int playerScore = 0, opponentScore = 0, turnNumber = 1;
	private String playerWord = "", opponentWord = "";
	private boolean needsWord = false, playersTurn = false;
	private ArrayList<TurnAddedListener> turnListeners = new ArrayList<TurnAddedListener>();

	/* Constructors */
	public Game(String id, User player, User opponent) {
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

	/* Other Methods */
	public void addTurn(Turn turn) {
		turns.add(turn);
		turnNumber = (turns.size() / 2) + 1;
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

}
