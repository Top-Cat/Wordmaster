package uk.co.thomasc.wordmaster;

public class Player {

	/* Properties */
	private String plusID; 
	private String name;
	private String avatarURL;
	
	public Player(String plusID, String name, String avatarURL) {
		this.plusID = plusID;
		this.name = name;
		this.avatarURL = avatarURL;
	}
	
	/* Getters */
	public String getPlusID() {
		return plusID;
	}
	public String getName() {
		return name;
	}
	public String getAvatarURL() {
		return avatarURL;
	}
	
}
