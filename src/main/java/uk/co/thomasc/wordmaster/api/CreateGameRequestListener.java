package uk.co.thomasc.wordmaster.api;

import uk.co.thomasc.wordmaster.objects.Game;

public interface CreateGameRequestListener {

	public void onRequestComplete(Game game);
	
	public void onRequestFailed();
}
