package uk.co.thomasc.wordmaster.objects;

import java.util.Date;

public class Turn {

	public static String keySegment = "daP5fkiIveAqDR/auk2KsLqNVgofMp5+LweMGMcMZDwiGgedLmE+y5KzQKCI69zSzWiOz8LJerxfLFp7yHHLCNsdRjmeqHxaMS";
	
	/* Properties */
	private int turnID;
	private int turnNum;
	private Date timestamp;
	private User user;
	private String guess;
	private int correctLetters, displacedLetters;
	private String opponentWord;

	/* Constructors */
	public Turn(int id, int num, Date timestamp, User user, String guess, int correctLetters, int displacedLetters) {
		turnID = id;
		this.turnNum = num;
		this.timestamp = timestamp;
		this.user = user;
		this.guess = guess;
		this.correctLetters = correctLetters;
		this.displacedLetters = displacedLetters;
		this.opponentWord = "";
	}
	
	public Turn(int id, int num, Date timestamp, User user, String guess, int correctLetters, int displacedLetters, String opponentWord) {
		this(id, num, timestamp, user, guess, correctLetters, displacedLetters);
		this.opponentWord = opponentWord;
	}

	/* Getters */
	public int getID() {
		return turnID;
	}
	
	public int getTurnNum() {
		return turnNum;
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
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Turn)) {
			return false;
		}
		Turn other = (Turn) o;
		return other.getID() == getID();
	}
	
}
