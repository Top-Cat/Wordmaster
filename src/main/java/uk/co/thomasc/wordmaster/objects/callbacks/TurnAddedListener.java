package uk.co.thomasc.wordmaster.objects.callbacks;

import uk.co.thomasc.wordmaster.objects.Turn;

public interface TurnAddedListener {

	public void onTurnAdded(Turn turn, boolean newerTurn);
	
}
