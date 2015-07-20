package uk.co.thomasc.wordmaster.api;

import org.json.simple.JSONObject;

import uk.co.thomasc.wordmaster.BaseGame;

public abstract class APIResponse {

	public final void _processResponse(JSONObject json) {
		int errorCode = ((Long) json.get("error")).intValue();
		if (errorCode == 0) {
			processResponse(json.get("response"));
		} else if (errorCode == -2) {
			BaseGame.getApiClient().reconnect();
			onRequestFailed(errorCode);
		} else {
			onRequestFailed(errorCode);
		}
	}

	public abstract void processResponse(Object obj);

	public abstract void onRequestComplete(Object obj);

	public abstract void onRequestFailed(int errorCode);

}
