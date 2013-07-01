package uk.co.thomasc.wordmaster.gcm;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class TurnReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

		String messageType = gcm.getMessageType(intent);
		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			String gameid = intent.getStringExtra("gameid");
			long timestamp = Long.valueOf(intent.getStringExtra("time"));
			
			Game game = Game.getGame(gameid);
			String opponentName;
			long lastupdate;
			
			if (game == null) {
				SharedPreferences prefs = context.getSharedPreferences("wordmaster.game." + gameid, Context.MODE_PRIVATE);
				lastupdate = prefs.getLong("time", 0);
				opponentName = prefs.getString("oppname", "");
			} else {
				opponentName = game.getOpponent().getName();
				lastupdate = game.getLastUpdateTimestamp();
			}
			
			if (lastupdate > 0 && lastupdate < timestamp) {
				//Remember
				SharedPreferences prefs = getBuildupPrefs(context);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(gameid, opponentName);
				editor.commit();
				
				int count = 0;
				String longMessage = "";
				for (String key : prefs.getAll().keySet()) {
					count++;
					if (longMessage.length() > 0) {
						longMessage += "\n";
					}
					longMessage += "vs " + prefs.getString(key, "");
				}
				
				// Tell Everybody
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				Intent i = new Intent(context, BaseGame.class);
				if (count == 1) {
					i.putExtra("gameid", gameid);
				}
				PendingIntent contentIntent = PendingIntent.getActivity(context, gameid.hashCode(), i, 0);
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("It's your turn!")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(longMessage))
				.setContentInfo(count + "")
				.setContentText("vs " + opponentName + (count > 1 ? " and others" : ""))
				.setAutoCancel(true);
				
				mBuilder.setContentIntent(contentIntent);
				mNotificationManager.notify(1, mBuilder.build());
			}
		}
		setResultCode(Activity.RESULT_OK);
	}
	
	private static SharedPreferences getBuildupPrefs(Context context) {
		return context.getSharedPreferences("wordmaster.turnreceiver", Context.MODE_PRIVATE);
	}
	
	public static void resetNotifications(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
		
		SharedPreferences prefs = getBuildupPrefs(context);
		SharedPreferences.Editor editor = prefs.edit();
		for (String key : prefs.getAll().keySet()) {
			editor.remove(key);
		}
		editor.commit();
	}
}
