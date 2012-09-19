package com.doogetha.client.android;

import java.text.MessageFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.doogetha.client.util.ContactsUtils;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import de.letsdoo.server.vo.UserVo;

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
		UserVo user = new UserVo();
		user.setEmail(intent.getExtras().getString("user"));
		ContactsUtils.fillUserInfo(getContentResolver(), user);
		String userName = ContactsUtils.userDisplayName((Letsdoo)getApplication(), user);
		String eventName = intent.getExtras().getString("eventName");
		int eventId = 0;
		try {eventId = Integer.parseInt(intent.getExtras().getString("eventId"));}
		catch (NumberFormatException nfe) {}
		String type = intent.getExtras().getString("type");
		
		if ("eventconfirm".equals(type))
			onMessageEventConfirm(intent, userName, eventName, eventId);
	}		
	
	@Override
	protected void onError(Context context, String errorId) {
	}

	protected void onMessageEventConfirm(Intent intent, String userName, String eventName, int eventId) {
		String ticker = getString(R.string.eventconfirm_bartext);
		String title = eventName;
		boolean accepted = "1".equals(intent.getExtras().getString("state"));
		String text = getString(accepted ? R.string.eventconfirm_text_accepted : R.string.eventconfirm_text_declined);
		ticker = MessageFormat.format(ticker, userName);
		text = MessageFormat.format(text, userName);
		sendNotification(eventId, ticker, title, text);
	}

	protected void sendNotification(int eventId, String ticker, String title, String text) {
		NotificationManager notificationManager =
			    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.notification_icon, ticker, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		//notification.defaults |= Notification.DEFAULT_VIBRATE;  // requires VIBRATE permission
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags    |= Notification.FLAG_AUTO_CANCEL;
		Intent notificationIntent =  new Intent(this, EventsActivity.class);
		notificationIntent.putExtra("eventId", eventId);
		int notificationId = (int)System.currentTimeMillis();
		PendingIntent contentIntent = PendingIntent.getActivity(this, notificationId, notificationIntent, 0);
		notification.setLatestEventInfo(this, title, text, contentIntent);
		notificationManager.notify(notificationId, notification);
	}
}
