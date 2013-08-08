package uk.co.thomasc.wordmaster.api;

import uk.co.thomasc.wordmaster.BaseGame;

public interface UpdateAlphaRequestListener {

	public void onRequestComplete(int errorCode, BaseGame activityReference);

}
