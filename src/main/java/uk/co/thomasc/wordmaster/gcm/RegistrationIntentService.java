package uk.co.thomasc.wordmaster.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import uk.co.thomasc.wordmaster.api.ServerAPI;

public class RegistrationIntentService extends IntentService {

	private static final String TAG = "RegIntentService";
	private static final String[] TOPICS = {"global"};

	public RegistrationIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			synchronized (TAG) {
				InstanceID instanceID = InstanceID.getInstance(this);
				String token = instanceID.getToken("731445442831", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

				ServerAPI.registerGCM(token, null);

				subscribeTopics(token);
			}
		} catch (Exception e) {
			Log.d(TAG, "Failed to complete token refresh", e);
		}
	}

	private void subscribeTopics(String token) throws IOException {
		for (String topic : TOPICS) {
			GcmPubSub pubSub = GcmPubSub.getInstance(this);
			pubSub.subscribe(token, "/topics/" + topic, null);
		}
	}

}