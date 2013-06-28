package uk.co.thomasc.wordmaster.objects;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.Drawable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnPersonLoadedListener;
import com.google.android.gms.plus.model.people.Person;

import uk.co.thomasc.wordmaster.objects.callbacks.ImageLoadedListener;
import uk.co.thomasc.wordmaster.objects.callbacks.NameLoadedListener;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;

public class User {

	private static Map<String, User> users = new HashMap<String, User>();
	private static boolean connected = false;

	public static User getUser(Person player, BaseGameActivity activityReference) {
		if (User.users.containsKey(player.getId())) {
			return User.users.get(player.getId());
		} else {
			User user = new User(player, activityReference);
			User.users.put(player.getId(), user);
			return user;
		}
	}

	public static User getUser(String plusID, BaseGameActivity activityReference) {
		if (User.users.containsKey(plusID)) {
			return User.users.get(plusID);
		} else {
			User user = new User(plusID, activityReference);
			User.users.put(plusID, user);
			return user;
		}
	}
	
	public static void onPlusConnected(BaseGameActivity activityReference) {
		connected = true;
		for (User user : users.values()) {
			if (user.name == null) {
				user.loadName(activityReference);
			}
		}
	}

	/* Properties */
	private String plusID;
	private String name;
	private Drawable drawable;
	private List<NameLoadedListener> userListeners = new ArrayList<NameLoadedListener>();
	private List<ImageLoadedListener> imageListeners = new ArrayList<ImageLoadedListener>();

	private User(Person person, BaseGameActivity activityReference) {
		plusID = person.getId();
		name = person.getDisplayName();
		loadImage(person, activityReference);
	}

	private User(String plusID, BaseGameActivity activityReference) {
		this.plusID = plusID;
		loadName(activityReference);
	}
	
	private void loadName(final BaseGameActivity activityReference) {
		if (connected) {
			activityReference.getPlusClient().loadPerson(new OnPersonLoadedListener() {
				@Override
				public void onPersonLoaded(ConnectionResult result, Person person) {
					name = person.getDisplayName();
					loadImage(person, activityReference);
	
					for (NameLoadedListener listener : userListeners) {
						listener.onNameLoaded(name);
					}
					userListeners.clear();
				}
			}, plusID);
		}
	}

	private void loadImage(final Person person, final BaseGameActivity activityReference) {
		final String avatarUri = person.getImage().getUrl();

		new Thread() {
			@Override
			public void run() {
				int delay = 500;
				while (drawable == null) {
					try {
						URL url = new URL(avatarUri);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.connect();
						drawable = Drawable.createFromStream(conn.getInputStream(), "player avatar");
	
						for (ImageLoadedListener listener : imageListeners) {
							listener.onImageLoaded(drawable);
						}
						imageListeners.clear();
						return;
					} catch (IOException e) {
						//No internet :<
						System.out.println("Failed to download avatar for " + person.getId());
					}
					try {
						Thread.sleep(delay);
						if (delay < 30000) {
							delay *= 2;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void listenForLoad(NameLoadedListener listener) {
		if (name != null) {
			listener.onNameLoaded(name);
		} else {
			userListeners.add(listener);
		}
	}

	public void listenForImage(ImageLoadedListener listener) {
		if (drawable != null) {
			listener.onImageLoaded(drawable);
		} else {
			imageListeners.add(listener);
		}
	}

	/* Getters */
	public String getPlusID() {
		return plusID;
	}

	public Drawable getAvatar() {
		return drawable;
	}

	public String getName() {
		return name;
	}

}
