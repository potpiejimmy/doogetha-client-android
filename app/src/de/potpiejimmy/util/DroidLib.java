package de.potpiejimmy.util;

import android.app.Activity;
import android.app.AlertDialog;


/**
 * Static utility methods for android
 */
public class DroidLib {
	public static void alert(Activity activity, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(msg);
		builder.create().show();
	}
}
