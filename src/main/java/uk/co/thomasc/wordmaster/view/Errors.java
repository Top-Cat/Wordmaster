package uk.co.thomasc.wordmaster.view;

import uk.co.thomasc.wordmaster.R;

public enum Errors {
	NETWORK(R.string.network_error, R.string.network_error_help),
	TURN(R.string.turn_error, R.string.turn_error_help),
	WORD(R.string.word_error, R.string.word_error_help),
	SERVER(R.string.server_error, R.string.server_error_help),
	OPPONENT(R.string.opponent_error, R.string.opponent_error_help),
	MATCH(R.string.match_error, R.string.match_error_help),
	WORDSET(R.string.word_set_error, R.string.word_set_error_help), ;

	private int title;
	private int subtitle;

	private Errors(int title, int subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}

	public int getTitle() {
		return title;
	}

	public int getSubtitle() {
		return subtitle;
	}
}
