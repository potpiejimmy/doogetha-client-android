package com.doogetha.client.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onRegistered(Context context, String regId) {
		handleRegUnreg(context, regId, true);
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		handleRegUnreg(context, regId, false);
	}
	
	protected void handleRegUnreg(Context context, String regId, boolean registered) {
		Letsdoo app = (Letsdoo)getApplication();
		
		Editor prefs = app.getPreferences().edit(); 
		prefs.putString("gcmRegistrationId", regId);
		prefs.commit();
		
//		// now try to sync with server:
//		app.gcmServerSync();
		GCMRegistrar.setRegisteredOnServer(context, false);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		NotificationManager notificationManager =
			    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.notification_icon, "Mitteilung empfangen.", System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags    |= Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, EventsActivity.class), 0);
		notification.setLatestEventInfo(this, "1 Nachricht", "Dies ist der Inhalt der Nachricht", contentIntent);
		notificationManager.notify(1, notification);
	}

	@Override
	protected void onError(Context context, String errorId) {
	}

}
