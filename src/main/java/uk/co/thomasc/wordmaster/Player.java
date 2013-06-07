package uk.co.thomasc.wordmaster;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

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
	public Drawable getAvatar() {
		try {
			InputStream is = (InputStream) new URL(avatarURL).getContent();
			Drawable d = Drawable.createFromStream(is, "player avatar");
			return d;
		} catch (MalformedURLException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}
	
}
