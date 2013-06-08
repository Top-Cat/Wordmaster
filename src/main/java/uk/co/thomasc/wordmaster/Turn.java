package uk.co.thomasc.wordmaster;

import java.util.Date;

public class Turn {

	/* Properties */
	private Date timestamp;
	private User user;
	private String guess;
	private int correctLetters, displacedLetters;
	
	/* Constructors */
	public Turn(Date timestamp, User user, String guess, int correctLetters, int displacedLetters) {
		this.timestamp = timestamp;
		this.user = user;
		this.guess = guess;
		this.correctLetters = correctLetters;
		this.displacedLetters = displacedLetters;
	}
	
	/* Getters */
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	public long getUnixTimestamp() {
		return this.timestamp.getTime();
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
}
