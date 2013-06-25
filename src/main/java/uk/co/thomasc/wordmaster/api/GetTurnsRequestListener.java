package uk.co.thomasc.wordmaster.api;

import java.util.List;

import uk.co.thomasc.wordmaster.objects.Turn;

public interface GetTurnsRequestListener {

	public void onRequestComplete(List<Turn> turns);

	public void onRequestFailed();

}
