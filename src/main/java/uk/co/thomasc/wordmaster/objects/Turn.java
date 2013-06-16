package uk.co.thomasc.wordmaster.objects;

import java.util.Date;

public class Turn {

	/* Properties */
	private int turnID;
	private Date timestamp;
	private User user;
	private String guess;
	private int correctLetters, displacedLetters;
	private String opponentWord;

	/* Constructors */
	public Turn(int id, Date timestamp, User user, String guess, int correctLetters, int displacedLetters) {
		turnID = id;
		this.timestamp = timestamp;
		this.user = user;
		this.guess = guess;
		this.correctLetters = correctLetters;
		this.displacedLetters = displacedLetters;
		this.opponentWord = "";
	}
	
	public Turn(int id, Date timestamp, User user, String guess, int correctLetters, int displacedLetters, String opponentWord) {
		this(id, timestamp, user, guess, correctLetters, displacedLetters);
		this.opponentWord = opponentWord;
	}

	/* Getters */
	public int getID() {
		return turnID;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public long getUnixTimestamp() {
		return timestamp.getTime();
	}

	public User getUser() {
		return user;
	}

	public String getGuess() {
		return guess;
	}

	public int getCorrectLetters() {
		return correctLetters;
	}

	public int getDisplacedLetters() {
		return displacedLetters;
	}
	
	public String getOpponentWord() {
		return opponentWord;
	}
}
