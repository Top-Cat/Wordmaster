package uk.co.thomasc.wordmaster.objects.callbacks;

import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;

public interface GameListener {

	public void onTurnAdded(Game game, Turn turn);

	public void onGameUpdated(Game game);

}
