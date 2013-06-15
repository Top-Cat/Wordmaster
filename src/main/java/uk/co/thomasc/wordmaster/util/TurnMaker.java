package uk.co.thomasc.wordmaster.util;

import java.util.ArrayList;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.GetTurnsRequestListener;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.api.TakeTurnRequestListener;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class TurnMaker implements OnClickListener, TakeTurnRequestListener, GetTurnsRequestListener {

	private Game game;
	private Turn turn;
	private BaseGame activity;
	private View rootView;
	private EditText input;
	
	public TurnMaker(Game game, BaseGame activity, View rootView) {
		this.game = game;
		this.activity = activity;
		this.rootView = rootView;
		input = (EditText) rootView.findViewById(R.id.editText1);
	}
	
	@Override
	public void onClick(View v) {
		if (game.isPlayersTurn()) {
			String guess = input.getText().toString();
			if (guess.length() == 4) {
				ServerAPI.takeTurn(game.getPlayer().getPlusID(), game.getID(), guess, this);
			}
		} else {
			// TODO: Should probably tell user off for trying to cheat
			System.out.println("It's not your turn!");
		}
	}

	@Override
	public void onRequestComplete(boolean[] result) {
		boolean success = result[0];
		boolean validWord = result[1];
		
		if (success) {
			ArrayList<Turn> turns = game.getTurns();
			Turn pivot = turns.get(turns.size() - 1);
			if (pivot != null) {
				ServerAPI.getTurns(game.getID(), pivot.getID(), 1, activity, this);
			} else {
				ServerAPI.getTurns(game.getID(), activity, this);
			}
		} else {
			if (validWord) {
				// TODO: Tell the user seal bars aren't tasty
			} else {
				// TODO: Tell the user they need a new dictionary
			}
		}
	}

	@Override
	public void onRequestComplete(Turn[] turns) {
		ArrayList<Turn> gameTurns = game.getTurns();
		for (Turn turn : turns) {
			game.addTurn(turn);
		}
		game.setPlayersTurn(false);
		activity.updateGame(game.getID(), game);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				input.setText("");
			}
		});
	}

	@Override
	public void onRequestFailed() {
		// TODO: Tell the user their soul was sold to satan
	}

}
