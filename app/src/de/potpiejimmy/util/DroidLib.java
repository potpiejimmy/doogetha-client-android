package de.potpiejimmy.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.net.Uri;
import android.widget.Toast;


/**
 * Static utility methods for android
 */
public class DroidLib {
	public static void alert(Context context, String msg) {
		alert(context, msg, (String)null, null);
	}
	
	public static void alert(Context context, String title, String[] items, OnClickListener clicklistener) {
		alert(context, title, null, null, items, clicklistener);
	}
	
	public static void alert(Context context, String msg, String oktext, OnClickListener oklistener) {
		alert(context, null, msg, oktext, null, oklistener);
	}
	
	public static void alert(Context context, String title, String msg, String oktext, String[] items, OnClickListener clicklistener) {
		alert(context, title, msg, oktext, items, clicklistener, null);
	}
	
	public static void alert(Context context, String title, String msg, String oktext, String[] items, OnClickListener clicklistener, OnDismissListener dismissListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null)
			builder.setTitle(title);
		if (msg != null)
			builder.setMessage(msg);
		if (oktext != null)
			builder.setPositiveButton(oktext, clicklistener);
		if (items != null)
			builder.setItems(items, clicklistener);
		builder.setCancelable(true);
		AlertDialog dialog = builder.create();
		if (dismissListener != null)
			dialog.setOnDismissListener(dismissListener);
		dialog.show();
	}
	
	public static void toast(Context context, String text) {
		Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	public static void invokeBrowser(Context context, String url){
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}
}
