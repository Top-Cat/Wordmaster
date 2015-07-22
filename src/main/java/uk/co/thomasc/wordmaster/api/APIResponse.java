package uk.co.thomasc.wordmaster.api;

import org.json.simple.JSONObject;

import uk.co.thomasc.wordmaster.BaseGame;

public abstract class APIResponse {

	public final void _processResponse(JSONObject json) {
		int errorCode = ((Long) json.get("error")).intValue();
		switch (errorCode) {
			case 0:
				processResponse(json.get("response"));
				break;
			case -2:
				if (BaseGame.isRunning()) {
					BaseGame.getApiClient().reconnect();
				}
			default:
				onRequestFailed(errorCode);
		}
	}

	public abstract void processResponse(Object obj);

	public abstract void onRequestComplete(Object obj);

	public abstract void onRequestFailed(int errorCode);

}
