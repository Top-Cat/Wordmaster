package uk.co.thomasc.wordmaster.objects;

import lombok.Getter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.graphics.drawable.Drawable;
import android.util.Log;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.objects.callbacks.UserListener;

public class User {

	public static String keySegment = "pGjhCD9hbcfsBKsjGTVSntvrGo2+BFn7+fq4JyUtVtTzuXGUiLAxiz1Bg5fW2KeIVHzrbBAoju2hRKK0cR8TxjAxlzMEoBYhI9";

	private static Set<String> remoteUsers = new HashSet<String>();
	private static Map<String, User> users = new HashMap<String, User>();
	private static boolean connected = false;
	public static User none = new User("");
	private static User me = User.none;

	static {
		User.users.put("", User.none);
	}

	public static User getUser(Person player) {
		return User.getUser(player.getId()).update(player);
	}

	public static User getUser(String plusID) {
		if (!User.users.containsKey(plusID)) {
			User.users.put(plusID, new User(plusID).loadName());
		}
		return User.users.get(plusID);
	}

	public static User getCurrentUser() {
		return User.me;
	}

	public static void onPlusConnected(Person person) {
		User.connected = true;
		User.me = User.getUser(person);

		if (User.remoteUsers.size() > 0) {
			Plus.PeopleApi.load(BaseGame.getApiClient(), User.remoteUsers).setResultCallback(new ResultCallback<LoadPeopleResult>() {
				@Override
				public void onResult(LoadPeopleResult arg0) {
					try {
						for (Person p : arg0.getPersonBuffer()) {
							User.getUser(p);
						}
					} finally {
						arg0.getPersonBuffer().release();
					}
				}
			});
		}
	}

	/* Properties */
	@Getter private final String plusID;
	@Getter private String name;

	private String avatarUri;
	private Drawable drawable;
	private final List<UserListener> listeners = new ArrayList<UserListener>();

	private User(String plusID) {
		this.plusID = plusID;
	}

	private User loadName() {
		User.remoteUsers.add(getPlusID());
		if (User.connected) {
			Plus.PeopleApi.load(BaseGame.getApiClient(), plusID).setResultCallback(new ResultCallback<LoadPeopleResult>() {
				@Override
				public void onResult(LoadPeopleResult arg0) {
					try {
						Person person = arg0.getPersonBuffer().get(0);
						name = person.getDisplayName();
						loadImage(person);

						for (UserListener listener : listeners) {
							listener.onNameLoaded(User.this);
						}
					} catch (Exception e) {
						Log.d("User", "Failure to load user info", e);
						loadName();
					} finally {
						arg0.getPersonBuffer().release();
					}
				}
			});
		} else {
			Log.d("User", "Not loading " + getPlusID());
			User.remoteUsers.add(getPlusID());
		}

		return this;
	}

	public User update(Person person) {
		if (person.getId().equals(plusID)) {
			setName(person.getDisplayName());
			loadImage(person);
		}

		return this;
	}

	private User loadImage(final Person person) {
		if (avatarUri != null) {
			return this;
		}

		avatarUri = person.getImage().getUrl();

		new Thread() {
			@Override
			public void run() {
				int delay = 70;
				while (drawable == null) {
					try {
						HttpURLConnection conn = (HttpURLConnection) new URL(avatarUri).openConnection();
						conn.connect();
						drawable = Drawable.createFromStream(conn.getInputStream(), "player avatar");

						for (UserListener listener : listeners) {
							listener.onImageLoaded(User.this);
						}
						return;
					} catch (IOException e) {
						// No internet :<
						System.out.println("Failed to download avatar for " + person.getId());
						System.out.println(avatarUri);
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

		return this;
	}

	/* Getters */
	public Drawable getAvatar() {
		return drawable;
	}

	public User setName(String name) {
		this.name = name;

		for (UserListener listener : listeners) {
			listener.onNameLoaded(this);
		}
		return this;
	}

	public void addListener(UserListener listener) {
		listeners.add(listener);

		if (getAvatar() != null) {
			listener.onImageLoaded(this);
		}
		if (getName() != null) {
			listener.onNameLoaded(this);
		}
	}

	public void removeListener(UserListener listener) {
		listeners.remove(listener);
	}

	public String getFirstName() {
		return getName().substring(0, getName().indexOf(' '));
	}

}
