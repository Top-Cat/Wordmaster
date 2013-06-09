package uk.co.thomasc.wordmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;

public class ServerAPI {
	
	private static final String BASE_URL = "http://thomasc.co.uk/wm/";

	public static Game[] getMatches(String playerID, BaseGame activityReference) {
		JSONObject json = makeRequest("getMatches", playerID, null, null); 
		boolean success = ((Boolean) json.get("success")).booleanValue();
		if (success) {
			JSONArray response = (JSONArray) json.get("response");
			Game[] games = new Game[response.size()];
			for (int i = 0; i < response.size(); i ++) {
				JSONObject gameObject = (JSONObject) response.get(i);
				String opponentID = gameObject.get("oppid").toString();
				String gameID = gameObject.get("gameid").toString();
				boolean needsWord = ((Boolean) gameObject.get("needword")).booleanValue();
				int playerScore = ((Integer) gameObject.get("pscore")).intValue();
				int opponentScore = ((Integer) gameObject.get("oscore")).intValue();
				boolean playersTurn = ((Boolean) gameObject.get("turn")).booleanValue();
				Game game = new Game(gameID, new User(playerID, activityReference), new User(opponentID, activityReference));
				game.setPlayersTurn(playersTurn);
				game.setNeedsWord(needsWord);
				game.setScore(playerScore, opponentScore);
				games[i] = game;
			}
			return games;
		} else {
			return null;
		}
	}
	
	public static Object[] getTurns(String gameID, int turnID, int number) {
		// do some server stuff
		return null;
	}
	
	public static boolean takeTurn(String playerID, String gameID, String word) {
		// do some server stuff
		return false;
	}
	
	public static Game createGame(String playerID, String opponentID) {
		// do some server stuff
		return null;
	}
	
	public static boolean setWord(String playerID, String gameID, String word) {
		// do some server stuff
		return false;
	}
	
	private static JSONObject makeRequest(String iface, String param1, String param2, String param3) {
		String url = BASE_URL + iface + "/" + param1;
		if (param2 != null && param2.length() > 0)
			url += "/" + param2;
		if (param3 != null && param3.length() > 0)
			url += "/" + param3;
		
		String jsonText = "";
		
		try {
			InputStream is = new URL(url).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String inputLine;
	        while ((inputLine = reader.readLine()) != null)
	            jsonText += inputLine;
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
