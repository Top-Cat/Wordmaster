package uk.co.thomasc.wordmaster.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.SparseArray;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.api.UpdateAlphaRequestListener;
import uk.co.thomasc.wordmaster.objects.callbacks.GameListener;
import uk.co.thomasc.wordmaster.view.game.GameAdapter;

@Accessors(chain = true)
public class Game implements UpdateAlphaRequestListener {

	public static String keySegment = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApAOqKoj3zH7ADRMM9zHZkUegL8xRAoD8Qb7tl7Xz94T99y7qFiphoZ";

	public static HashMap<String, Game> games = new HashMap<String, Game>();
	public static long updatePoint;

	public static Game getGame(String id) {
		if (!Game.games.containsKey(id)) {
			Game.games.put(id, new Game(id));
		}
		return Game.games.get(id);
	}

	/* Properties */
	private final String gameID;
	@Getter private User opponent;
	@Getter private final SparseArray<Turn> turns = new SparseArray<Turn>();
	private int latestTurnId = -1;
	private int oldestTurnId = Integer.MAX_VALUE;
	@Getter private int playerScore = 0, opponentScore = 0;
	@Getter @Setter private int turnNumber = 1;
	@Getter @Setter private String playerWord = "", opponentWord = "";
	@Getter @Setter private boolean needingWord = true, playersTurn = false;
	private final ArrayList<GameListener> gameListeners = new ArrayList<GameListener>();
	@Getter private long lastUpdateTimestamp = 0;
	private int alpha = 0;
	private byte alphaStatus = 0;
	private GameAdapter adapter;
	@Getter @Setter private boolean loaded = false;

	/* Constructors */
	private Game(String id) {
		gameID = id;
	}

	/* Modifiers */
	public String getID() {
		return gameID;
	}

	public Game setScore(int playerScore, int opponentScore) {
		this.playerScore = playerScore;
		this.opponentScore = opponentScore;

		return this;
	}

	public GameAdapter getAdapter(BaseGame activity) {
		if (adapter == null) {
			adapter = new GameAdapter(activity);
		}
		return adapter;
	}

	public Game setLastUpdateTimestamp(Context c, long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;

		saveState(c);

		return this;
	}

	public Game setOpponent(Context c, User opponent) {
		this.opponent = opponent;

		saveState(c);

		return this;
	}

	/* Other Methods */
	public void addTurn(JSONObject turnObj, final BaseGame activity) {
		int id = ((Long) turnObj.get("turnid")).intValue();
		boolean newTurn = false;
		boolean latestTurn = false;
		if (turns.get(id) == null) {
			Turn turn = new Turn(id);
			turns.put(id, turn);

			newTurn = true;
		}
		final Turn turn = turns.get(id).update(turnObj, activity);

		if (turn.getID() < oldestTurnId) {
			oldestTurnId = turn.getID();
		}
		if (turn.getID() > latestTurnId) {
			latestTurn = true;

			latestTurnId = turn.getID();

			turnNumber = turn.getTurnNum() / 2 + 1;

			if (turn.getGuess().length() > 0) {
				setNeedingWord(turn.getCorrectLetters() == 4);
			}

			if (turn.getTurnNum() > 0) {
				playersTurn = turn.getUser().equals(opponent);
			}
		}

		if (newTurn) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					getAdapter(activity).add(turn);
				}
			});
		}
		if (latestTurn) {
			for (GameListener l : gameListeners) {
				l.onTurnAdded(this, turn);
			}
		}
		for (GameListener l : gameListeners) {
			l.onGameUpdated(this);
		}
	}

	public void addTurnListener(GameListener listener) {
		gameListeners.add(listener);
	}

	public void removeTurnListener(GameListener listener) {
		gameListeners.remove(listener);
	}

	public int getPivotLatest() {
		return latestTurnId;
	}

	public int getPivotOldest() {
		return oldestTurnId;
	}

	public void saveState(Context context) {
		if (isLoaded()) {
			SharedPreferences prefs = context.getSharedPreferences("wordmaster.game." + getID(), Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			updatePreferences(editor);
			editor.commit();
		}
	}

	public static void saveState(Bundle outState) {
		for (String gameid : Game.games.keySet()) {
			outState.putBundle(gameid, Game.games.get(gameid).toBundle());
		}
		outState.putStringArray("games", Game.games.keySet().toArray(new String[Game.games.size()]));
	}

	public static void restoreState(Bundle inState, BaseGame activity) {
		String[] gameids = inState.getStringArray("games");
		for (String gameid : gameids) {
			Bundle gameData = inState.getBundle(gameid);
			Game.getGame(gameid)
				.setOpponent(activity, User.getUser(gameData.getString("opponentid"), activity))
				.setPlayersTurn(gameData.getBoolean("playersturn"))
				.setNeedingWord(gameData.getBoolean("needsword"))
				.setScore(gameData.getInt("playerscore"), gameData.getInt("opponentscore"))
				.setLoaded(true);
		}
	}

	public Bundle toBundle() {
		Bundle bundle = null;
		if (isLoaded()) {
			bundle = new Bundle();
			bundle.putString("opponentid", opponent.getPlusID());
			bundle.putBoolean("playersturn", playersTurn);
			bundle.putBoolean("needsword", needingWord);
			bundle.putInt("playerscore", playerScore);
			bundle.putInt("opponentscore", opponentScore);
		}
		return bundle;
	}

	private void updatePreferences(Editor editor) {
		editor.putLong("time", getLastUpdateTimestamp());
		editor.putString("oppname", opponent.getName());
	}

	public Game setAlpha(int alpha) {
		if (alphaStatus == 0) {
			this.alpha = alpha;
		}

		return this;
	}

	public boolean getAlpha(int j) {
		return (alpha >> j & 1) == 1;
	}

	public void updateAlpha(int id, boolean strike, BaseGame activityReference) {
		alpha ^= 1 << id;
		if ((alphaStatus & 1) == 0) {
			alphaStatus |= 1;
			ServerAPI.updateAlpha(gameID, alpha, activityReference, this);
		} else {
			alphaStatus |= 2;
		}
	}

	@Override
	public void onRequestComplete(int errorCode, BaseGame activityReference) {
		if (errorCode != 0) {
			alphaStatus |= 2;
		}

		alphaStatus &= ~1;

		if ((alphaStatus & 2) == 2) {
			alphaStatus |= 1;
			ServerAPI.updateAlpha(gameID, alpha, activityReference, this);
		}
		alphaStatus &= ~2;
	}

	public void clearTurns() {
		turns.clear();
		latestTurnId = -1;
		oldestTurnId = Integer.MAX_VALUE;
	}

	public Game update(JSONObject gameObject, BaseGame activity) {
		User opp = null;
		int opponentScore = 0;
		long updated = gameObject.containsKey("updated") ? (Long) gameObject.get("updated") : 0;
		boolean hasUpdated = getLastUpdateTimestamp() < updated;

		if (gameObject.containsKey("oppid")) {
			opp = User.getUser((String) gameObject.get("oppid"), activity);
			opponentScore = ((Long) gameObject.get("oscore")).intValue();
		}

		setOpponent(activity, opp)
			.setPlayersTurn((Boolean) gameObject.get("turn"))
			.setNeedingWord((Boolean) gameObject.get("needword"))
			.setScore(((Long) gameObject.get("pscore")).intValue(), opponentScore)
			.setAlpha(gameObject.containsKey("alpha") ? ((Long) gameObject.get("alpha")).intValue() : 0)
			.setLastUpdateTimestamp(activity, gameObject.containsKey("updated_user") ? (Long) gameObject.get("updated_user") : 0)
			.setLoaded(true);

		Game.updatePoint = Math.max(Game.updatePoint, updated);

		if (hasUpdated) {
			getMoreTurns(activity);
		}

		for (GameListener l : gameListeners) {
			l.onGameUpdated(this);
		}

		return this;
	}

	private void getMoreTurns(BaseGame activity) {
		if (getPivotLatest() < 0) {
			ServerAPI.getTurns(getID(), activity, null);
		} else {
			ServerAPI.getTurns(getID(), getPivotLatest(), 10, activity, null);
		}
	}

	public boolean isTurn() {
		return isNeedingWord() || isPlayersTurn();
	}

}
