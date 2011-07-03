package de.letsdoo.client.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Base64;
import de.letsdoo.client.entity.Event;
import de.letsdoo.client.entity.Events;
import de.potpiejimmy.util.RestResourceAccessor;

public class Letsdoo extends Application {
	
	//public final static String URL = "http://www.potpiejimmy.de/letsdoo/res/events/";
	public final static String URL = "https://192.168.100.30:8181/letsdoo/res/events/";
	
	private RestResourceAccessor<Events, Event> req = null;
	private SharedPreferences preferences = null;
	
	@Override
	public void onCreate() {
        req = new RestResourceAccessor<Events, Event>(URL, Events.class, Event.class);
    	req.getWebRequest().addParam("max", "255");
    	String auth = Base64.encodeToString("thorsten@potpiejimmy.de:asdfasdf".getBytes(), Base64.NO_WRAP);
    	req.getWebRequest().addHeader("Authorization", "Basic "+auth);
	}
	
	public RestResourceAccessor<Events, Event> getRestAccessor() {
		return req;
	}
	
	public SharedPreferences getPreferences() {
		if (preferences == null) {
			preferences = getSharedPreferences("letsdooprefs", MODE_PRIVATE);
		}
		return preferences;
	}
}
