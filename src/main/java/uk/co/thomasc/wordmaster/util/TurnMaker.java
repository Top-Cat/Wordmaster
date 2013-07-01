package uk.co.thomasc.wordmaster.util;

import java.util.List;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.GetTurnsRequestListener;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.api.SetWordRequestListener;
import uk.co.thomasc.wordmaster.api.TakeTurnRequestListener;
import uk.co.thomasc.wordmaster.api.TakeTurnSpinnerListener;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class TurnMaker implements OnClickListener, TakeTurnRequestListener, GetTurnsRequestListener, SetWordRequestListener {

	private Game game;
	private BaseGame activity;
	private EditText input;
	private DialogPanel errorMessage;
	private TakeTurnSpinnerListener listener;
	
	public TurnMaker(Game game, BaseGame activity, View rootView, TakeTurnSpinnerListener listener) {
		this.game = game;
		this.activity = activity;
		this.listener = listener;
		input = (EditText) rootView.findViewById(R.id.guess_input);
		errorMessage = (DialogPanel) rootView.findViewById(R.id.errorMessage);
	}
	
	@Override
	public void onClick(View v) {
		String guess = input.getText().toString();
		if (guess.length() == 4) {
			if (game.needsWord()) {
				ServerAPI.setWord(game.getPlayer().getPlusID(), game.getID(), guess, activity, this);
			} else if (game.isPlayersTurn()) {
				listener.startSpinner();
				ServerAPI.takeTurn(game.getPlayer().getPlusID(), game.getID(), guess, activity, this);
			} else {
				errorMessage.show(Errors.TURN);
			}
		}
	}

	@Override
	public void onRequestComplete(final int errorCode) {
		if (errorCode == 0) {
			int pivot = game.getPivotLatest();
			if (pivot > 0) {
				ServerAPI.getTurns(game.getID(), pivot, 1, activity, this);
			} else {
				ServerAPI.getTurns(game.getID(), activity, this);
			}
		} else {
			listener.stopSpinner();
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (errorCode == 1) {
						errorMessage.show(Errors.WORD);
						final String guess = input.getText().toString();
						errorMessage.setSubtitle(guess + " is not in the Wordmaster dictionary.");
					} else if (errorCode == 2) {
						errorMessage.show(Errors.OPPONENT);
					} else if (errorCode == 3) {
						game.setPlayersTurn(false);
						errorMessage.show(Errors.TURN);
					} else if (errorCode == 6) {
						errorMessage.show(Errors.WORDSET);
					} else {
						errorMessage.show(Errors.SERVER);
					}
				}
			});
		}
	}

	@Override
	public void onRequestComplete(List<Turn> turns) {
		for (Turn turn : turns) {
			game.addTurn(turn);
		}
		game.setPlayersTurn(false);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.menuFragment.adapter.notifyDataSetChanged();
				input.setText("");
			}
		});
		listener.stopSpinner();
	}

	@Override
	public void onRequestFailed() {
		listener.stopSpinner();
		errorMessage.show(Errors.NETWORK);
	}

}
