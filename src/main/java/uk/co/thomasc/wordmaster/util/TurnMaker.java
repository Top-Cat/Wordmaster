package uk.co.thomasc.wordmaster.util;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.api.SetWordRequestListener;
import uk.co.thomasc.wordmaster.api.TakeTurnRequestListener;
import uk.co.thomasc.wordmaster.api.TakeTurnSpinnerListener;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;

public class TurnMaker implements OnClickListener, TakeTurnRequestListener, SetWordRequestListener {

	private final Game game;
	private final BaseGame activity;
	private final EditText input;
	private final DialogPanel errorMessage;
	private final TakeTurnSpinnerListener listener;

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
			if (game.isNeedingWord()) {
				listener.startSpinner();
				ServerAPI.setWord(game.getID(), guess, activity, this);
			} else {
				listener.startSpinner();
				ServerAPI.takeTurn(game.getID(), guess, activity, this);
			}
		}
	}

	@Override
	public void onSetWordComplete(int errorCode) {
		game.setNeedingWord(false);
		onComplete(errorCode);
	}

	@Override
	public void onRequestComplete(final int errorCode) {
		game.setPlayersTurn(false);
		onComplete(errorCode);
	}

	private void onComplete(final int errorCode) {
		input.post(new Runnable() {
			@Override
			public void run() {
				input.setText("");
			}
		});
		listener.stopSpinner();
		if (errorCode != 0) {
			errorMessage.post(new Runnable() {
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

}
