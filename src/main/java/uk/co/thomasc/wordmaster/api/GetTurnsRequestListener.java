package uk.co.thomasc.wordmaster.api;

import uk.co.thomasc.wordmaster.objects.Turn;

public interface GetTurnsRequestListener {

	public void onRequestComplete(Turn[] turns);

	public void onRequestFailed();

}
