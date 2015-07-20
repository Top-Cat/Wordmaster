package uk.co.thomasc.wordmaster.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.co.thomasc.wordmaster.objects.Game;

public abstract class GetTurnsResponse extends APIResponse {

	private final Game game;

	public GetTurnsResponse(Game game) {
		this.game = game;
	}

	@Override
	public void processResponse(Object obj) {
		JSONArray turns = (JSONArray) obj;
		for (int i = 0; i < turns.size(); i++) {
			game.addTurn((JSONObject) turns.get(i));
		}

		onRequestComplete(game);
	}

}
