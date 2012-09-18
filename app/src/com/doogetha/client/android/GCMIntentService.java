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
		NotificationManager notificationManager =
			    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		UserVo user = new UserVo();
		user.setEmail(intent.getExtras().getString("user"));
		ContactsUtils.fillUserInfo(getContentResolver(), user);
		String userName = ContactsUtils.userDisplayName((Letsdoo)getApplication(), user);
		boolean accepted = "1".equals(intent.getExtras().getString("state"));
		String eventName = intent.getExtras().getString("eventName");
		
		String barText = getString(R.string.eventconfirm_bartext);
		String titleText = eventName;
		String text = getString(accepted ? R.string.eventconfirm_text_accepted : R.string.eventconfirm_text_declined);
		barText = MessageFormat.format(barText, userName);
		text = MessageFormat.format(text, userName);
		
		Notification notification = new Notification(R.drawable.notification_icon, barText, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		//notification.defaults |= Notification.DEFAULT_VIBRATE;  // requires VIBRATE permission
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags    |= Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, EventsActivity.class), 0);
		notification.setLatestEventInfo(this, titleText, text, contentIntent);
		notificationManager.notify(1, notification);
	}

	@Override
	protected void onError(Context context, String errorId) {
	}

}
