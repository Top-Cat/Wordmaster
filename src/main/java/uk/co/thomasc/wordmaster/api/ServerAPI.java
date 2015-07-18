package uk.co.thomasc.wordmaster.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.android.gms.common.ConnectionResult;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.game.Achievements;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;

@SuppressWarnings("unchecked")
public class ServerAPI {

	private static final String BASE_URL = "https://thomasc.co.uk/wm/";
	private static String playerid = "";
	private static JSONObject notIdentifiedResponse = new JSONObject();

	static {
		ServerAPI.notIdentifiedResponse.put("error", -2);
	}

	/**
	 * Calls the getMatches function of the server API. Retrieves
	 * a list of all the games a player is involved in.
	 *
	 * @param playerID – the Google+ ID of the player to retrieve games for
	 * @param activity – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetMatchesRequestListener to be notified when the request finishes
	 */
	public static void getMatches(final BaseGame activity, final GetMatchesRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("getMatches", new String[] {}, activity);
				int errorCode = -3;
				if (json != null) {
					errorCode = ((Long) json.get("error")).intValue();
					if (errorCode == 0) {
						JSONArray response = (JSONArray) json.get("response");
						Game[] games = new Game[response.size()];
						for (int i = 0; i < response.size(); i++) {
							JSONObject gameObject = (JSONObject) response.get(i);
							games[i] = Game.getGame((String) gameObject.get("gameid")).update(gameObject, activity);
						}
						listener.onRequestComplete(games);
						return;
					}
				}
				listener.onRequestFailed(errorCode);
			}
		};
		t.start();
	}

	public static void longPoll(final BaseGame activity, final long updated, final GetMatchesRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("longPoll", new String[] {String.valueOf(updated)}, activity);
				int errorCode = -3;
				if (json != null) {
					errorCode = ((Long) json.get("error")).intValue();
					if (errorCode == 0) {
						JSONArray response = (JSONArray) json.get("response");
						Game[] games = new Game[response.size()];
						for (int i = 0; i < response.size(); i++) {
							JSONObject gameObject = (JSONObject) response.get(i);
							games[i] = Game.getGame((String) gameObject.get("gameid")).update(gameObject, activity);
						}
						listener.onRequestComplete(games);
						return;
					}
				}
				listener.onRequestFailed(errorCode);
			}
		};
		t.start();
	}

	/**
	 * Calls the getTurns function on the server API. Returns an array
	 * containing all the turns taken in the game.
	 *
	 * @param gameID – the game ID of the game to retrieve turns from
	 * @param activity – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetTurnsRequestListener to be notified when the request finishes
	 */
	public static void getTurns(final String gameID, final BaseGame activity, final GetTurnsRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("getTurns", new String[] {gameID}, activity);
				ServerAPI.getTurns(json, gameID, listener, activity);
			}
		};
		t.start();
	}

	/**
	 * Calls the getTurns function on the server API. Starting from a 'pivot'
	 * turn, retrieves a given number of turns from before or after this point.
	 * Returns an array containing the turns.
	 *
	 * @param gameID – the game ID of the game to retrieve turns from
	 * @param turnID – the turn ID of the 'pivot' turn
	 * @param activity – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetTurnsRequestListener to be notified when the request finishes
	 */
	public static void getTurns(final String gameID, final int turnID, final BaseGame activity, final GetTurnsRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("getTurns", new String[] {gameID, String.valueOf(turnID)}, activity);
				ServerAPI.getTurns(json, gameID, listener, activity);
			}
		};
		t.start();
	}

	/**
	 * Calls the getTurns function on the server API. Starting from a 'pivot'
	 * turn, retrieves a given number of turns from before or after this point.
	 * Returns an array containing the turns.
	 *
	 * @param gameID – the game ID of the game to retrieve turns from
	 * @param turnID – the turn ID of the 'pivot' turn
	 * @param number – the number of turns to retrieve (negative for less recent, positive for more recent)
	 * @param activity – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetTurnsRequestListener to be notified when the request finishes
	 */
	public static void getTurns(final String gameID, final int turnID, final int number, final BaseGame activity, final GetTurnsRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("getTurns", new String[] {gameID, String.valueOf(turnID), Integer.toString(number)}, activity);
				ServerAPI.getTurns(json, gameID, listener, activity);
			}
		};
		t.start();
	}

	private static void getTurns(JSONObject json, String gameID, GetTurnsRequestListener listener, BaseGame activity) {
		if (json != null) {
			int errorCode = ((Long) json.get("error")).intValue();
			if (errorCode == 0) {
				JSONArray turns = (JSONArray) json.get("response");
				for (int i = 0; i < turns.size(); i++) {
					Game.getGame(gameID).addTurn((JSONObject) turns.get(i), activity);
				}
				if (listener != null) {
					listener.onRequestComplete();
				}
				return;
			}
		}
		if (listener != null) {
			listener.onRequestFailed();
		}
	}

	/**
	 * Calls the takeTurn function on the server API. Makes a guess for the player
	 * and updates the state of the game on the server. Returns two booleans to
	 * represent the outcome of the call.
	 *
	 * @param playerID – the Google+ ID of the player taking the turn
	 * @param gameID – the game ID of the game the turn is from
	 * @param word – the guess the player has made
	 * @param listener – a TakeTurnRequestListener to be notified when the request finishes
	 */
	public static void takeTurn(final String gameID, final String word, final BaseGame activity, final TakeTurnRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("takeTurn", new String[] {gameID, word}, activity);
				int errorCode = -3;
				if (json != null) {
					errorCode = ((Long) json.get("error")).intValue();
				}

				listener.onRequestComplete(errorCode);
			}
		};
		t.start();
	}

	/**
	 * Calls the createGame function of the server API. Creates a new game
	 * involving the given players. If no opponent is specified, the player
	 * is put into the automatch pool.
	 *
	 * @param playerID – the Google+ ID of the user
	 * @param opponentID – the Google+ ID of the opponent (null for automatch)
	 * @param activity – reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a CreateGameRequestListener to be notified when the request finishes
	 */
	public static void createGame(final String opponentID, final BaseGame activity, final CreateGameRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json;
				if (opponentID == null) {
					json = ServerAPI.makeRequest("createGame", new String[] {}, activity);
				} else {
					json = ServerAPI.makeRequest("createGame", new String[] {opponentID}, activity);
				}
				int errorCode = ((Long) json.get("error")).intValue();
				if (errorCode == 0) {
					JSONObject response = (JSONObject) json.get("response");
					String gameID = (String) response.get("gameid");

					User opp = null;
					if (opponentID != null) {
						opp = User.getUser(opponentID, activity);
					}

					Game game = Game.getGame(gameID).setOpponent(activity, opp);
					listener.onRequestComplete(game);
				} else {
					listener.onRequestFailed(errorCode);
				}
			}
		};
		t.start();
	}

	/**
	 * Calls the setWord function of the server API. If no word has been set
	 * for the given player in the given game, their word is updated. Returns
	 * two booleans representing the response from the server.
	 *
	 * @param playerID – the Google+ ID of the player whose word is being set
	 * @param gameID – the game ID of the game the player is involved in
	 * @param word – the word the player wishes to use
	 * @param listener – a SetWordRequestListener to be notified when the request finishes
	 */
	public static void setWord(final String gameID, final String word, final BaseGame activity, final SetWordRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("setWord", new String[] {gameID, word}, activity);
				int errorCode = -3;
				if (json != null) {
					errorCode = ((Long) json.get("error")).intValue();
				}

				listener.onSetWordComplete(errorCode);
			}
		};
		t.start();
	}

	public static void registerGCM(final String regid, final BaseGame activity) {
		Thread t = new Thread() {
			@Override
			public void run() {
				ServerAPI.makeRequest("registerGCM", new String[] {regid}, activity);
			}
		};
		t.start();
	}

	public static void upgradePurchased(final String token, final BaseGame activity) {
		Thread t = new Thread() {
			@Override
			public void run() {
				ServerAPI.makeRequest("upgradePurchased", new String[] {token}, activity);
			}
		};
		t.start();
	}

	public static void updateAlpha(final String gameID, final int alpha, final BaseGame activityReference, final UpdateAlphaRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("updateAlpha", new String[] {gameID, String.valueOf(alpha)}, activityReference);

				int errorCode = -3;
				if (json != null) {
					errorCode = ((Long) json.get("error")).intValue();
				}

				listener.onRequestComplete(errorCode, activityReference);
			}
		};
		t.start();
	}

	public static void identify(final String authToken, final BaseGame activity) {
		JSONObject json = ServerAPI.doRequest(ServerAPI.BASE_URL + "identify" + "/" + authToken, activity);
		if (json != null) {
			JSONObject response = (JSONObject) json.get("response");
			ServerAPI.playerid = (String) response.get("key");

			activity.onIdentified();
		} else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					activity.onConnectionFailed(new ConnectionResult(ConnectionResult.NETWORK_ERROR, null));
				}
			});
		}
	}

	public static void revoke() {
		ServerAPI.playerid = "";
	}

	private static JSONObject makeRequest(String iface, String[] params, BaseGame activity) {
		return ServerAPI.playerid == null ? ServerAPI.notIdentifiedResponse : ServerAPI.doRequest(ServerAPI.BASE_URL + iface + "/" + ServerAPI.playerid + ServerAPI.implode("/", params), activity);
	}

	public static String implode(String separator, String... data) {
		StringBuilder sb = new StringBuilder();
		if (data.length > 0) {
			sb.append(separator);
			for (int i = 0; i < data.length - 1; i++) {
				// data.length - 1 => to not add separator at the end
				if (!data[i].matches(" *")) {// empty string are ""; " "; "  "; and so on
					sb.append(data[i]);
					sb.append(separator);
				}
			}
			sb.append(data[data.length - 1].trim());
		}
		return sb.toString();
	}

	private static JSONObject doRequest(String url, final BaseGame activityReference) {
		String jsonText = "";
		System.out.println(url);

		try {
			InputStream is = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				jsonText += inputLine;
			}
			is.close();

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonText);
			ServerAPI.processAchievements(jsonObject, activityReference);
			return jsonObject;
		} catch (IOException ex) {
			return null;
		} catch (ParseException ex) {
			return null;
		}
	}

	private static void processAchievements(JSONObject json, BaseGame activityReference) {
		JSONObject achievements = (JSONObject) json.get("achievements");
		for (Object key : achievements.keySet()) {
			int sid = Integer.parseInt((String) key);
			Achievements achievement = Achievements.forServerId(sid);
			if (achievement != null) {
				int increment = ((Long) achievements.get(key)).intValue();
				activityReference.unlockAchievement(achievement, increment);
			}
		}
	}

}
