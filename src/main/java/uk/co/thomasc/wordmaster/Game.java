package uk.co.thomasc.wordmaster;

import java.util.ArrayList;

public class Game {

	/* Properties */
	private String gameID;
	private User player, opponent;
	private ArrayList<Turn> turns;
	private int playerScore = 0, opponentScore = 0, turnNumber = 1;
	private String playerWord = "", opponentWord = "";
	private boolean needsWord = false, playersTurn = false;
	
	/* Constructors */
	public Game(String id, User player, User opponent) {
		this.gameID = id;
		this.player = player;
		this.opponent = opponent;
	}
	
	/* Getters */
	public String getID() {
		return this.gameID;
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
		return this.needsWord;
	}
	
	public boolean isPlayersTurn() {
		return this.playersTurn;
	}
	
	/* Setters */
	public void setPlayerWord(String word) {
		this.playerWord = word;
	}
	
	public void setOpponentWord(String word) {
		this.opponentWord = word;
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
		this.playersTurn = isPlayersTurn;
	}
	
	/* Other Methods */
	public void addTurn(Turn turn) {
		turns.add(turn);
	}
	
}
