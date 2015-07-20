package uk.co.thomasc.wordmaster.util;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.SimpleResponse;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;

public class TurnMaker extends SimpleResponse implements OnClickListener {

	private final Game game;
	private final EditText input;
	private final DialogPanel errorMessage;
	private final MenuDetailFragment listener;

	public TurnMaker(Game game, View rootView, MenuDetailFragment listener) {
		this.game = game;
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
				BaseGame.getServerApi().setWord(game.getID(), guess, this);
			} else {
				listener.startSpinner();
				BaseGame.getServerApi().takeTurn(game.getID(), guess, this);
			}
		}
	}

	@Override
	public void onRequestFailed(final int errorCode) {
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
					game.setNeedingWord(false);
					errorMessage.show(Errors.WORDSET);
				} else {
					errorMessage.show(Errors.SERVER);
				}
			}
		});
	}

	@Override
	public void onRequestComplete(Object obj) {
		input.post(new Runnable() {
			@Override
			public void run() {
				input.setText("");
			}
		});
		listener.stopSpinner();
	}

}
