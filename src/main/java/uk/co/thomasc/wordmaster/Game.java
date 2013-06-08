package uk.co.thomasc.wordmaster;

import java.util.ArrayList;

public class Game {

	/* Properties */
	private User player, opponent;
	private ArrayList<Turn> turns;
	private int playerScore = 0, opponentScore = 0, turnNumber = 1;
	private String playerWord = "", opponentWord = "";
	
	/* Constructors */
	public Game(User player, User opponent) {
		this.player = player;
		this.opponent = opponent;
	}
	
	/* Getters */
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
	
	/* Other Methods */
	public void addTurn(Turn turn) {
		turns.add(turn);
	}
	
}
