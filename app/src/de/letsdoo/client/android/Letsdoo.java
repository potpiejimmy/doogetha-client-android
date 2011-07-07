package de.letsdoo.client.android;

import android.app.Application;
import android.content.SharedPreferences;
import de.letsdoo.client.android.rest.EventsAccessor;
import de.letsdoo.client.android.rest.LoginAccessor;

public class Letsdoo extends Application {
	
	public final static String URL = "https://www.potpiejimmy.de:8181/letsdoo/res/";
	//public final static String URL = "https://192.168.100.30:8181/letsdoo/res/";
	
	private EventsAccessor eventsAccessor = null;
	private LoginAccessor loginAccessor = null;
	private SharedPreferences preferences = null;
	
	@Override
	public void onCreate() {
		eventsAccessor = new EventsAccessor(URL + "events");
		loginAccessor = new LoginAccessor(URL + "login");
	}
	
	public EventsAccessor getEventsAccessor() {
		return eventsAccessor;
	}
	
	public LoginAccessor getLoginAccessor() {
		return loginAccessor;
	}
	
	public SharedPreferences getPreferences() {
		if (preferences == null) {
			preferences = getSharedPreferences("letsdooprefs", MODE_PRIVATE);
		}
		return preferences;
	}
	public boolean isLoggedIn() {
		return getPreferences().getString("authtoken", null) != null;
	}
	public void login(String authtoken) {
    	eventsAccessor.getWebRequest().setHeader("Authorization", "Basic "+authtoken);
	}
}
