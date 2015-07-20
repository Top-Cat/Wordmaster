package uk.co.thomasc.wordmaster.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.util.Log;

import uk.co.thomasc.wordmaster.game.Achievements;

public class APIRequest extends Thread {

	private final ServerAPI api;
	private final String url;
	private final APIResponse response;

	public APIRequest(ServerAPI api, String url, APIResponse response) {
		this.api = api;
		this.url = url;
		this.response = response;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
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
			JSONObject jsonResponse = (JSONObject) parser.parse(jsonText);

			if (jsonResponse != null) {
				Achievements.process(jsonResponse);
				if (response != null) {
					response._processResponse(jsonResponse);
				}
			}

			return;
		} catch (Exception ex) {
			Log.d(ServerAPI.TAG, "Error in request " + url, ex);
		} finally {
			Log.d(ServerAPI.TAG, url + " [" + (System.currentTimeMillis() - startTime) + "ms]");
			api.requestDone(this);
		}
		if (response != null) {
			response._processResponse(ServerAPI.failedResponse);
		}
	}

}
