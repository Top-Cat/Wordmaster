package uk.co.thomasc.wordmaster.game;

import java.io.IOException;

import android.app.PendingIntent;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.util.GameHelper;

public class TokenFetchThread extends Thread {
	
	private BaseGame activity;
	private GameHelper gameHelper;
	
	public TokenFetchThread(BaseGame activity, GameHelper gameHelper) {
		this.activity = activity;
		this.gameHelper = gameHelper;
	}
	
	@Override
	public void run() {
		try {
			String authToken = GoogleAuthUtil.getToken(activity, gameHelper.getGamesClient().getCurrentAccountName(), "oauth2:" + Scopes.GAMES);
			ServerAPI.identify(authToken, activity, gameHelper);
			return;
		} catch (UserRecoverableAuthException e) {
			final PendingIntent intent = PendingIntent.getActivity(activity, 1002, e.getIntent(), 0);
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gameHelper.onConnectionFailed(new ConnectionResult(ConnectionResult.SIGN_IN_REQUIRED, intent));
				}
			});
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				gameHelper.onConnectionFailed(new ConnectionResult(ConnectionResult.SIGN_IN_REQUIRED, null));
			}
		});
	}
	
}