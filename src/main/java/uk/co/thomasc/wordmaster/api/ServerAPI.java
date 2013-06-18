package uk.co.thomasc.wordmaster.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.User;

public class ServerAPI {

	private static final String BASE_URL = "http://thomasc.co.uk/wm/";

	/**
	 * Calls the getMatches function of the server API. Retrieves
	 * a list of all the games a player is involved in.
	 * 
	 * @param playerID – the Google+ ID of the player to retrieve games for
	 * @param activityReference – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetMatchesRequestListener to be notified when the request finishes 
	 */
	public static void getMatches(final String playerID, final BaseGame activityReference, final GetMatchesRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("getMatches", playerID);
				if (json != null) {
					boolean success = ((Boolean) json.get("success")).booleanValue();
					if (success) {
						JSONArray response = (JSONArray) json.get("response");
						Game[] games = new Game[response.size()];
						for (int i = 0; i < response.size(); i++) {
							JSONObject gameObject = (JSONObject) response.get(i);
							String opponentID = (String) gameObject.get("oppid");
							String gameID = (String) gameObject.get("gameid");
							boolean needsWord = (Boolean) gameObject.get("needword");
							int playerScore = ((Long) gameObject.get("pscore")).intValue();
							int opponentScore = ((Long) gameObject.get("oscore")).intValue();
							boolean playersTurn = (Boolean) gameObject.get("turn");
							Game game = new Game(gameID, User.getUser(playerID, activityReference), User.getUser(opponentID, activityReference));
							game.setPlayersTurn(playersTurn);
							game.setNeedsWord(needsWord);
							game.setScore(playerScore, opponentScore);
							if (gameObject.get("updated") != null) {
								long updated = (Long) gameObject.get("updated");
								game.setLastUpdateTimestamp(updated);
							}
							games[i] = game;
						}
						listener.onRequestComplete(games);
					} else {
						listener.onRequestFailed();
					}
				} else {
					listener.onRequestFailed();
				}
			}
		};
		t.start();
	}

	/**
	 * Calls the getTurns function on the server API. Returns an array
	 * containing all the turns taken in the game.
	 * 
	 * @param gameID – the game ID of the game to retrieve turns from
	 * @param activityReference – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetTurnsRequestListener to be notified when the request finishes 
	 */
	public static void getTurns(final String gameID, final BaseGame activityReference, final GetTurnsRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("getTurns", gameID);
				Turn[] turns = ServerAPI.getTurns(json, activityReference);
				if (turns != null) {
					listener.onRequestComplete(turns);
				} else {
					listener.onRequestFailed();
				}
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
	 * @param activityReference – a reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a GetTurnsRequestListener to be notified when the request finishes 
	 */
	public static void getTurns(final String gameID, final int turnID, final int number, final BaseGame activityReference, final GetTurnsRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("getTurns", gameID, String.valueOf(turnID), Integer.toString(number));
				Turn[] turns = ServerAPI.getTurns(json, activityReference);
				if (turns != null) {
					listener.onRequestComplete(turns);
				} else {
					listener.onRequestFailed();
				}
			}
		};
		t.start();
	}

	private static Turn[] getTurns(JSONObject json, BaseGame activityReference) {
		boolean success = ((Boolean) json.get("success")).booleanValue();
		if (success) {
			JSONArray response = (JSONArray) json.get("response");
			Turn[] turns = new Turn[response.size()];
			for (int i = 0; i < response.size(); i++) {
				JSONObject turnObject = (JSONObject) response.get(i);
				int id = ((Long) turnObject.get("turnid")).intValue();
				String playerID = (String) turnObject.get("playerid");
				String guess = (String) turnObject.get("guess");
				long when = (Long) turnObject.get("when");
				int correct = ((Long) turnObject.get("correct")).intValue();
				int displaced = ((Long) turnObject.get("displaced")).intValue();
				Turn turn;
			//	if (correct == 4) {
			//		String opponentWord = (String) turnObject.get("oppword");
			//		turn = new Turn(id, new Date(when), User.getUser(playerID, activityReference), guess, correct, displaced, opponentWord);
			//	} else {
					turn = new Turn(id, new Date(when), User.getUser(playerID, activityReference), guess, correct, displaced);
			//	}
				turns[i] = turn;
			}
			return turns;
		} else {
			return null;
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
	public static void takeTurn(final String playerID, final String gameID, final String word, final TakeTurnRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("takeTurn", playerID, gameID, word);
				boolean success = ((Boolean) json.get("success")).booleanValue();
				JSONObject response = (JSONObject) json.get("response");
				boolean validWord = (Boolean) response.get("validword");
				boolean[] result = { success, validWord };
				listener.onRequestComplete(result);
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
	 * @param activityReference – reference to the BaseGame activity, used to get avatars from Google+
	 * @param listener – a CreateGameRequestListener to be notified when the request finishes 
	 */
	public static void createGame(final String playerID, final String opponentID, final BaseGame activityReference, final CreateGameRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("createGame", playerID, opponentID);
				boolean success = ((Boolean) json.get("success")).booleanValue();
				if (success) {
					JSONArray response = (JSONArray) json.get("response");
					JSONObject gameObject = (JSONObject) response.get(0);
					String gameID = (String) gameObject.get("gameid");
					Game game = new Game(gameID, User.getUser(playerID, activityReference), User.getUser(opponentID, activityReference));
					listener.onRequestComplete(game);
				} else {
					listener.onRequestFailed();
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
	public static void setWord(final String playerID, final String gameID, final String word, final SetWordRequestListener listener) {
		Thread t = new Thread() {
			@Override
			public void run() {
				JSONObject json = ServerAPI.makeRequest("setWord", playerID, gameID, word);
				boolean success = ((Boolean) json.get("success")).booleanValue();
				JSONObject response = (JSONObject) json.get("response");
				boolean validWord = (Boolean) response.get("validword");
				boolean[] result = { success, validWord };
				listener.onRequestComplete(result);
			}
		};
		t.start();
	}

	private static JSONObject makeRequest(String iface, String param1, String param2, String param3) {
		return ServerAPI.makeRequest(ServerAPI.BASE_URL + iface + "/" + param1 + "/" + param2 + "/" + param3);
	}

	private static JSONObject makeRequest(String iface, String param1, String param2) {
		return ServerAPI.makeRequest(ServerAPI.BASE_URL + iface + "/" + param1 + "/" + param2);
	}

	private static JSONObject makeRequest(String iface, String param1) {
		return ServerAPI.makeRequest(ServerAPI.BASE_URL + iface + "/" + param1);
	}

	private static JSONObject makeRequest(String url) {
		String jsonText = "";

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
			return jsonObject;
		} catch (IOException ex) {
			return null;
		} catch (ParseException ex) {
			return null;
		}
	}

}
