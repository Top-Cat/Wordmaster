package uk.co.thomasc.wordmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerAPI {
	
	private static final String BASE_URL = "http://thomasc.co.uk/wm/";

	public static Object[] getMatches(String playerID) {
		// do some server stuff
		return null;
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
		if (param2.length() > 0)
			url += "/" + param2;
		if (param3.length() > 0)
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
			// some kind of server error alert for user?
			return null;
		} catch (ParseException ex) {
			// some kind of server error alert for user?
			return null;
		}
	}
	
}
