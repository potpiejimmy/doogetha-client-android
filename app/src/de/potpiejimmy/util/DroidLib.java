package de.potpiejimmy.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.widget.Toast;


/**
 * Static utility methods for android
 */
public class DroidLib {
	public static void alert(Activity activity, String msg) {
		alert(activity, msg, (String)null, null);
	}
	
	public static void alert(Activity activity, String title, String[] items, OnClickListener clicklistener) {
		alert(activity, title, null, null, items, clicklistener);
	}
	
	public static void alert(Activity activity, String msg, String oktext, OnClickListener oklistener) {
		alert(activity, null, msg, oktext, null, oklistener);
	}
	
	public static void alert(Activity activity, String title, String msg, String oktext, String[] items, OnClickListener clicklistener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (title != null)
			builder.setTitle(title);
		if (msg != null)
			builder.setMessage(msg);
		if (oktext != null)
			builder.setPositiveButton(oktext, clicklistener);
		if (items != null)
			builder.setItems(items, clicklistener);
		builder.setCancelable(true);
		builder.create().show();
	}
	
	public static void toast(Activity activity, String text) {
		Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	public static void invokeBrowser(Activity activity, String url){
		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}
}
