package de.letsdoo.client.android;

import android.app.Application;
import android.content.SharedPreferences;
import de.letsdoo.client.android.rest.EventsAccessor;
import de.letsdoo.client.android.rest.LoginAccessor;
import de.letsdoo.client.android.rest.RegisterAccessor;

public class Letsdoo extends Application {
	
	public final static String URL = "https://www.potpiejimmy.de/doogetha/res/";
	//public final static String URL = "https://192.168.100.30:8181/doogetha/res/";
	//public final static String URL = "http://172.18.119.203:8089/doogetha/res/";
	
	private EventsAccessor eventsAccessor = null;
	private RegisterAccessor registerAccessor = null;
	private LoginAccessor loginAccessor = null;
	private SharedPreferences preferences = null;
	
	@Override
	public void onCreate() {
		eventsAccessor = new EventsAccessor(URL + "events");
		registerAccessor = new RegisterAccessor(URL + "register");
		loginAccessor = new LoginAccessor(URL + "login");
	}
	
	public EventsAccessor getEventsAccessor() {
		return eventsAccessor;
	}
	
	public LoginAccessor getLoginAccessor() {
		return loginAccessor;
	}
	
	public RegisterAccessor getRegisterAccessor() {
		return registerAccessor;
	}
	
	public SharedPreferences getPreferences() {
		if (preferences == null) {
			preferences = getSharedPreferences("letsdooprefs", MODE_PRIVATE);
		}
		return preferences;
	}
	
	public String getAuthtoken() {
		return getPreferences().getString("authtoken", null);
	}
	
	public void setAuthtoken(String authtoken) {
		getPreferences().edit().putString("authtoken", authtoken).commit();
	}
	
	public void removeAuthtoken() {
		getPreferences().edit().remove("authtoken").commit();
	}
	
	public boolean isRegistered() {
		return getAuthtoken() != null;
	}
	
	public void register(String authtoken) {
		setAuthtoken(authtoken);
	}
	
	public void unregister() {
		removeAuthtoken();
		removeSession();
	}
	
	public void removeSession() {
    	eventsAccessor.getWebRequest().removeHeader("Authorization");
	}
	
	public void newSession(String sessionkey) {
    	eventsAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
	}
	
	public boolean hasSession() {
    	return eventsAccessor.getWebRequest().getHeader("Authorization") != null;
	}
}
