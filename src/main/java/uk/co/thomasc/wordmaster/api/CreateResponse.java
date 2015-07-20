package uk.co.thomasc.wordmaster.api;

import org.json.simple.JSONObject;

import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;

public abstract class CreateResponse extends APIResponse {

	private final User opp;

	public CreateResponse(User opp) {
		this.opp = opp;
	}

	@Override
	public final void processResponse(Object obj) {
		JSONObject response = (JSONObject) obj;
		String gameID = (String) response.get("gameid");

		this.onRequestComplete(Game.getGame(gameID).setOpponent(opp));
	}

	@Override
	public final void onRequestComplete(Object obj) {
		onRequestComplete((Game) obj);
	}

	public abstract void onRequestComplete(Game game);

}
