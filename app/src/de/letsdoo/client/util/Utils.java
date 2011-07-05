package de.letsdoo.client.util;

import android.app.Activity;
import de.letsdoo.client.android.Letsdoo;

/**
 * Static helper methods for Letsdoo app
 */
public class Utils {
	public static Letsdoo getApp(Activity activity) {
		return (Letsdoo)activity.getApplication();
	}
}
