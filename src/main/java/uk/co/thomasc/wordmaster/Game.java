package uk.co.thomasc.wordmaster;

import java.util.ArrayList;

public class Game {

	/* Properties */
	private User player, opponent;
	private ArrayList turns;
	private int playerScore = 0, opponentScore = 0, turnNumber = 0;
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
	
	/* Setters */
	public void setPlayerWord(String word) {
		this.playerWord = word;
	}
	
	public void setOpponentWord(String word) {
		this.opponentWord = word;
	}
	
}
