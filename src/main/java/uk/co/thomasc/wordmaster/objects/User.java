package uk.co.thomasc.wordmaster.objects;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import android.graphics.drawable.Drawable;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.objects.callbacks.ImageLoadedListener;
import uk.co.thomasc.wordmaster.objects.callbacks.NameLoadedListener;

public class User {

	public static String keySegment = "pGjhCD9hbcfsBKsjGTVSntvrGo2+BFn7+fq4JyUtVtTzuXGUiLAxiz1Bg5fW2KeIVHzrbBAoju2hRKK0cR8TxjAxlzMEoBYhI9";

	private static Set<String> notLoaded = new HashSet<String>();
	private static Map<String, User> users = new HashMap<String, User>();
	private static boolean connected = false;
	public static User none = new User("");
	private static User me = none;
	
	static {
		User.users.put("", none);
	}

	public static User getUser(Person player, BaseGame activity) {
		return getUser(player.getId(), activity).update(player);
	}

	public static User getUser(String plusID, BaseGame activity) {
		if (!User.users.containsKey(plusID)) {
			User.users.put(plusID, new User(plusID).loadName(activity));
		}
		return User.users.get(plusID);
	}
	
	public static User getCurrentUser() {
		return me;
	}

	public static void onPlusConnected(final BaseGame activity, Person person) {
		User.connected = true;
		User.me = User.getUser(person, activity);
		
		if (notLoaded.size() > 0) {
			Plus.PeopleApi.load(activity.getApiClient(), notLoaded).setResultCallback(new ResultCallback<LoadPeopleResult>() {
				@Override
				public void onResult(LoadPeopleResult arg0) {
					try {
						for (Person p : arg0.getPersonBuffer()) {
							getUser(p, activity);
						}
					} finally {
						arg0.getPersonBuffer().release();
					}
				}
			});
		}
	}

	/* Properties */
	@Getter private String plusID;
	@Getter private String name;
	
	private String avatarUri;
	private Drawable drawable;
	private List<NameLoadedListener> userListeners = new ArrayList<NameLoadedListener>();
	private List<ImageLoadedListener> imageListeners = new ArrayList<ImageLoadedListener>();

	private User(String plusID) {
		this.plusID = plusID;
	}

	private User loadName(BaseGame activity) {
		if (User.connected) {
			Plus.PeopleApi.load(activity.getApiClient(), plusID).setResultCallback(new ResultCallback<LoadPeopleResult>() {
				@Override
				public void onResult(LoadPeopleResult arg0) {
					try {
						Person person = arg0.getPersonBuffer().get(0);
						name = person.getDisplayName();
						loadImage(person);
	
						for (NameLoadedListener listener : userListeners) {
							listener.onNameLoaded(name);
						}
						userListeners.clear();
					} finally {
						arg0.getPersonBuffer().release();
					}
				}
			});
		} else {
			notLoaded.add(getPlusID());
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
		if (avatarUri != null) return this;
		
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

						for (ImageLoadedListener listener : imageListeners) {
							listener.onImageLoaded(drawable);
						}
						imageListeners.clear();
						return;
					} catch (IOException e) {
						//No internet :<
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
	public Drawable getAvatar() {
		return drawable;
	}
	
	public User setName(String name) {
		this.name = name;
		
		for (NameLoadedListener listener : userListeners) {
			listener.onNameLoaded(name);
		}
		userListeners.clear();
		return this;
	}

}
