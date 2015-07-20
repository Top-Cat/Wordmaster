package uk.co.thomasc.wordmaster.gcm;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.InboxStyle;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;

public class TurnReceiver extends GcmListenerService {

	@Override
	public void onMessageReceived(String from, Bundle intent) {
		String gameid = intent.getString("gameid");
		String word = intent.getString("word");
		long timestamp = Long.valueOf(intent.getString("time"));

		System.out.println("notify: " + gameid + ", " + timestamp);

		Game game = Game.getGame(gameid);

		SharedPreferences gPrefs = getSharedPreferences("wordmaster.game." + gameid, Context.MODE_PRIVATE);
		long lastupdate = gPrefs.getLong("time", 0);
		String opponentName = gPrefs.getString("oppname", "Unknown");

		if (game.isLoaded()) {
			if (game.getOpponent().getName() != null) {
				opponentName = game.getOpponent().getName();
			}
			lastupdate = game.getLastUpdateTimestamp();
		}

		if (lastupdate > 0 && lastupdate < timestamp) {
			SharedPreferences prefs = TurnReceiver.getBuildupPrefs(this);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(gameid, word + " - " + opponentName);
			editor.commit();

			int count = 0;
			InboxStyle style = new InboxStyle();
			for (String key : prefs.getAll().keySet()) {
				count++;
				style.addLine(prefs.getString(key, ""));
			}

			// Tell Everybody
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Intent i = new Intent(this, BaseGame.class);
			if (count == 1) {
				i.putExtra("gameid", gameid);
			}

			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 9000, i, 0);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.noteicon)
				.setLargeIcon(bm)
				.setContentTitle("It's your turn")
				.setContentInfo(count + "")
				.setColor(getResources().getColor(R.color.notif_color))
				.setStyle(style)
				.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
				.setVibrate(new long[] {1000, 1000})
				.setLights(Color.YELLOW, 3000, 3000)
				.setNumber(count)
				.setContentText(opponentName + (count == 2 ? " and 1 other" : count > 2 ? " and " + count + " others" : ""))
				.setAutoCancel(true)
				.setContentIntent(contentIntent);

			mNotificationManager.notify(1, mBuilder.build());
		}
	}

	private static SharedPreferences getBuildupPrefs(Context context) {
		return context.getSharedPreferences("wordmaster.turnreceiver", Context.MODE_PRIVATE);
	}

	public static void resetNotifications(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);

		SharedPreferences prefs = TurnReceiver.getBuildupPrefs(context);
		SharedPreferences.Editor editor = prefs.edit();
		for (String key : prefs.getAll().keySet()) {
			editor.remove(key);
		}
		editor.commit();
	}
}
