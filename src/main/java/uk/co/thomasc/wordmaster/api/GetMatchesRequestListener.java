package uk.co.thomasc.wordmaster.api;

import uk.co.thomasc.wordmaster.objects.Game;

public interface GetMatchesRequestListener {

	public void onRequestComplete(Game[] games);

	public void onRequestFailed(int errorCode);

}
