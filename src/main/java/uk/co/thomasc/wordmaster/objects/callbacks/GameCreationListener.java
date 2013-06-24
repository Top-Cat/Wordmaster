package uk.co.thomasc.wordmaster.objects.callbacks;

import uk.co.thomasc.wordmaster.objects.Game;

public interface GameCreationListener {

	public void onCreateGame(String playerID, String opponentID);
	
}
