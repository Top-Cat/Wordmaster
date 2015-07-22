package uk.co.thomasc.wordmaster.api;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;

import com.google.android.gms.common.ConnectionResult;

import uk.co.thomasc.wordmaster.BaseGame;

@SuppressWarnings("unchecked")
public class ServerAPI {

	private static final String BASE_URL = "https://thomasc.co.uk/wm/";

	static final String TAG = "ServerAPI";
	static JSONObject notIdentifiedResponse = new JSONObject();
	static JSONObject failedResponse = new JSONObject();

	private final Set<Thread> requests = new HashSet<Thread>();
	private String playerid = "";

	static {
		ServerAPI.notIdentifiedResponse.put("error", -2L);
		ServerAPI.failedResponse.put("error", -3L);
	}

	public ServerAPI() {

	}

	/**
	 * Calls the getMatches function of the server API. Retrieves
	 * a list of all the games a player is involved in.
	 *
	 * @param playerID – the Google+ ID of the player to retrieve games for
	 * @param activity – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetMatchesRequestListener to be notified when the request finishes
	 */
	public void getMatches(final GetMatchesResponse listener) {
		makeRequest("getMatches", new String[] {}, listener);
	}

	public void longPoll(final long updated, final GetMatchesResponse listener) {
		makeRequest("longPoll", new String[] {String.valueOf(updated)}, listener);
	}

	/**
	 * Calls the getTurns function on the server API. Returns an array
	 * containing all the turns taken in the game.
	 *
	 * @param gameID – the game ID of the game to retrieve turns from
	 * @param activity – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetTurnsRequestListener to be notified when the request finishes
	 */
	public void getTurns(final String gameID, final GetTurnsResponse listener) {
		makeRequest("getTurns", new String[] {gameID}, listener);
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
	public void getTurns(final String gameID, final int turnID, final GetTurnsResponse listener) {
		makeRequest("getTurns", new String[] {gameID, String.valueOf(turnID)}, listener);
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
	public void getTurns(final String gameID, final int turnID, final int number, final GetTurnsResponse listener) {
		makeRequest("getTurns", new String[] {gameID, String.valueOf(turnID), Integer.toString(number)}, listener);
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
	public void takeTurn(final String gameID, final String word, final SimpleResponse listener) {
		makeRequest("takeTurn", new String[] {gameID, word}, listener);
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
	public void createGame(CreateResponse listener) {
		makeRequest("createGame", new String[] {}, listener);
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
	public void createGame(String opponentID, CreateResponse listener) {
		makeRequest("createGame", new String[] {opponentID}, listener);
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
	public void setWord(final String gameID, final String word, final SimpleResponse listener) {
		makeRequest("setWord", new String[] {gameID, word}, listener);
	}

	public void setGameVisible(String gameID, boolean visible, SimpleResponse listener) {
		makeRequest("setGameVisible", new String[] {gameID, visible ? "1" : "0"}, listener);
	}

	public void registerGCM(final String regid, SimpleResponse listener) {
		makeRequest("registerGCM", new String[] {regid}, listener);
	}

	public void upgradePurchased(final String token) {
		makeRequest("upgradePurchased", new String[] {token}, null);
	}

	public void updateAlpha(final String gameID, final int alpha, final SimpleResponse response) {
		makeRequest("updateAlpha", new String[] {gameID, String.valueOf(alpha)}, response);
	}

	public void identify(final String authToken, final BaseGame activity) {
		if (!BaseGame.isRunning()) {
			activity.getMenuFragment().onSignInFailed();
			return;
		}

		doRequest(ServerAPI.BASE_URL + "identify" + "/" + authToken, new APIResponse() {
			@Override
			public void processResponse(Object obj) {
				JSONObject response = (JSONObject) obj;
				playerid = (String) response.get("key");
				activity.onIdentified();
			}

			@Override
			public void onRequestFailed(int errorCode) {
				activity.onConnectionFailed(new ConnectionResult(ConnectionResult.NETWORK_ERROR, null));
			}

			@Override
			public void onRequestComplete(Object obj) {

			}
		});
	}

	public void revoke() {
		for (Thread t : requests) {
			t.interrupt();
		}
		playerid = "";
	}

	public boolean isIdentified() {
		return playerid != null && playerid.length() > 0 && BaseGame.isRunning();
	}

	private void makeRequest(String iface, String[] params, APIResponse response) {
		if (!isIdentified()) {
			if (response != null) {
				response._processResponse(ServerAPI.notIdentifiedResponse);
			}
			return;
		}

		doRequest(ServerAPI.BASE_URL + iface + "/" + playerid + ServerAPI.implode("/", params), response);
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

	private void doRequest(String url, APIResponse response) {
		Thread t = new APIRequest(this, url, response);
		requests.add(t);
		t.start();
	}

	void requestDone(Thread t) {
		requests.remove(t);
	}

}
