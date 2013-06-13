package uk.co.thomasc.wordmaster.objects;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.Player;
import com.google.android.gms.plus.PlusClient.OnPersonLoadedListener;
import com.google.android.gms.plus.model.people.Person;

public class User {

	/* Properties */
	private String plusID; 
	private String name;
	private String avatarUri;
	private List<UserLoadedListener> listeners = new ArrayList<UserLoadedListener>();
	
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
	
	public User(String plusID, BaseGameActivity activityReference) {
		this.plusID = plusID;
		activityReference.getPlusClient().loadPerson(new OnPersonLoadedListener() {
			@Override
			public void onPersonLoaded(ConnectionResult result, Person person) {
				name = person.getDisplayName();
				avatarUri = Uri.parse(person.getImage().getUrl()).toString(); //TODO: I don't think parsing then toString actually does anything :P
				System.out.println(avatarUri);
				for (UserLoadedListener listener : listeners) {
					listener.onUserLoaded(User.this);
				}
				listeners.clear();
			}
		}, plusID);
	}
	
	/* Getters */
	public String getPlusID() {
		return plusID;
	}
	public void getNameBlocking(UserLoadedListener listener) {
		if (name != null) {
			listener.onUserLoaded(this);
		} else {
			listeners.add(listener);
		}
	}
	public Uri getAvatarURL() {
		return Uri.parse(avatarUri);
	}
	public Drawable getAvatar(BaseGame activityReference) {
		try {
			InputStream stream = activityReference.getContentResolver().openInputStream(getAvatarURL());
			Drawable d = Drawable.createFromStream(stream, "player avatar");
			return d;
		} catch (IOException ex) {
			return null;
		}
	}

	public String getName() {
		return name;
	}
	
}
