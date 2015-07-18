package uk.co.thomasc.wordmaster.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;

public class IDListenerService extends InstanceIDListenerService {

	@Override
	public void onTokenRefresh() {
		Intent intent = new Intent(this, RegistrationIntentService.class);
		startService(intent);
	}

}
