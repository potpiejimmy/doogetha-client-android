package de.potpiejimmy.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;


/**
 * Static utility methods for android
 */
public class DroidLib {
	public static void alert(Activity activity, String msg) {
		alert(activity, msg, null, null);
	}
	
	public static void alert(Activity activity, String msg, String oktext, OnClickListener oklistener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(msg);
		if (oktext != null)
			builder.setPositiveButton(oktext, oklistener);
		builder.setCancelable(true);
		builder.create().show();
	}
	public static void invokeBrowser(Activity activity, String url){
		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}
}
