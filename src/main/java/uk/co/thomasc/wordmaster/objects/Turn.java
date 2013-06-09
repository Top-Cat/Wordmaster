package uk.co.thomasc.wordmaster.objects;

import java.util.Date;

public class Turn {

	/* Properties */
	private int turnID;
	private Date timestamp;
	private User user;
	private String guess;
	private int correctLetters, displacedLetters;
	
	/* Constructors */
	public Turn(int id, Date timestamp, User user, String guess, int correctLetters, int displacedLetters) {
		this.turnID = id;
		this.timestamp = timestamp;
		this.user = user;
		this.guess = guess;
		this.correctLetters = correctLetters;
		this.displacedLetters = displacedLetters;
	}
	
	/* Getters */
	public int getID() {
		return this.turnID;
	}
	
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
