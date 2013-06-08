package uk.co.thomasc.wordmaster;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.gms.games.Player;

public class User {

	/* Properties */
	private String plusID; 
	private String name;
	private String avatarUri;
	
	public User(String plusID, String name, Uri avatarUri) {
		this.plusID = plusID;
		this.name = name;
		this.avatarUri = avatarUri.toString();
	}
	
	public User(Player player) {
		this.plusID = player.getPlayerId();
		this.name = player.getDisplayName();
		this.avatarUri = player.getIconImageUri().toString();
	}
	
	/* Getters */
	public String getPlusID() {
		return plusID;
	}
	public String getName() {
		return name;
	}
	public Uri getAvatarURL() {
		return Uri.parse(avatarUri);
	}
	public Drawable getAvatar() {
		try {
			InputStream is = (InputStream) new URL(avatarUri).getContent();
			Drawable d = Drawable.createFromStream(is, "player avatar");
			return d;
		} catch (MalformedURLException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}
	
}
