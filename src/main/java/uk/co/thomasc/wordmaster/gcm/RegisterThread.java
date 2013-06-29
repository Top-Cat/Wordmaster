package uk.co.thomasc.wordmaster.gcm;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.api.ServerAPI;

public class RegisterThread extends Thread {
	
	private BaseGame activity;
	
	public RegisterThread(BaseGame activity) {
		this.activity = activity;
	}
	
	public void run() {
		SharedPreferences prefs = activity.getSharedPreferences("wordmaster.gcm", Context.MODE_PRIVATE);
		
		String regid = prefs.getString("regid", "");
		
		int appVersion = prefs.getInt("version", Integer.MIN_VALUE);
		int currentVersion = 0;
		try {
			PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			currentVersion = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		if (currentVersion > appVersion || regid.length() == 0) {
			// Old regid invalid or doesn't exist, get a new one
			try {
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
				regid = gcm.register("731445442831");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (regid.length() > 0) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("regid", regid);
			editor.putInt("version", currentVersion);
			editor.commit();
			
			ServerAPI.registerGCM(activity.getUserId(), regid);
		}
	}
	
}