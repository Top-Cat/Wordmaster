package uk.co.thomasc.wordmaster.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import uk.co.thomasc.wordmaster.objects.Game;

public abstract class GetMatchesResponse extends APIResponse {

	@Override
	public void processResponse(Object obj) {
		JSONArray response = (JSONArray) obj;
		Game[] games = new Game[response.size()];
		for (int i = 0; i < response.size(); i++) {
			JSONObject gameObject = (JSONObject) response.get(i);
			games[i] = Game.getGame((String) gameObject.get("gameid")).update(gameObject);
		}
		this.onRequestComplete(games);
	}

	@Override
	public final void onRequestComplete(Object obj) {
		onRequestComplete((Game[]) obj);
	}

	public abstract void onRequestComplete(Game[] games);

}
