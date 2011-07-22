package de.letsdoo.client.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import de.letsdoo.client.android.rest.EventsAccessor;
import de.letsdoo.client.android.rest.LoginAccessor;
import de.letsdoo.client.android.rest.RegisterAccessor;
import de.letsdoo.client.android.rest.VersionAccessor;

public class Letsdoo extends Application {
	
	public final static String PROTO    = "http://";
	public final static String PROTOSEC = "http://";
	
	//public final static String URI = "www.potpiejimmy.de/doogetha/res/";
	public final static String URI = "192.168.100.30:8080/doogetha/res/";
	//public final static String URI = "192.168.100.22:8089/doogetha/res/";
	//public final static String URI = "172.18.119.203:8089/doogetha/res/";
	
	public final static String DOWNLOADURL = "http://www.potpiejimmy.de/download/Doogetha.apk";
	
	private EventsAccessor eventsAccessor = null;
	private RegisterAccessor registerAccessor = null;
	private LoginAccessor loginAccessor = null;
	private VersionAccessor versionAccessor = null;
	
	private SharedPreferences preferences = null;
	
	private int versionCode = 0;
	private String versionName = null;
	
	@Override
	public void onCreate() {
		eventsAccessor = new EventsAccessor(PROTOSEC + URI + "events");
		registerAccessor = new RegisterAccessor(PROTOSEC + URI + "register");
		loginAccessor = new LoginAccessor(PROTOSEC + URI + "login");
		versionAccessor = new VersionAccessor(PROTO + URI + "version");
		
		try
		{
			PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			versionCode = pInfo.versionCode;
			versionName = pInfo.versionName;
		}
		catch (NameNotFoundException e)
		{
		    Log.v("Letsdoo", e.getMessage());
		}
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
	
	public VersionAccessor getVersionAccessor() {
		return versionAccessor;
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
	
	public String getEmail() {
		return getPreferences().getString("email", null);
	}
	
	public void setEmail(String email) {
		getPreferences().edit().putString("email", email).commit();
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
	
	public int getVersionCode() {
		return versionCode;
	}
	
	public String getVersionName() {
		return versionName;
	}
}
