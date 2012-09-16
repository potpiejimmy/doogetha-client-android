package com.doogetha.client.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.doogetha.client.android.rest.DevicesAccessor;
import com.doogetha.client.android.rest.EventsAccessor;
import com.doogetha.client.android.rest.LoginAccessor;
import com.doogetha.client.android.rest.RegisterAccessor;
import com.doogetha.client.android.rest.SurveysAccessor;
import com.doogetha.client.android.rest.UsersAccessor;
import com.doogetha.client.android.rest.VersionAccessor;
import com.google.android.gcm.GCMRegistrar;

public class Letsdoo extends Application {
	
	public final static String PROTO    = "http://";
	public final static String PROTOSEC = "http://";
	
	//public final static String URI = "www.potpiejimmy.de/doogetha/res/";
	public final static String URI = "192.168.178.21:8080/doogetha/res/";
	//public final static String URI = "192.168.100.22:8089/doogetha/res/";
	//public final static String URI = "172.18.119.203:8089/doogetha/res/";
	
	public final static String DOWNLOADURL = "http://www.potpiejimmy.de/download/Doogetha.apk";
	
	public final static String GCM_SENDER_ID = "273459076205";
	
	private EventsAccessor eventsAccessor = null;
	private SurveysAccessor surveysAccessor = null;
	private UsersAccessor usersAccessor = null;
	private DevicesAccessor devicesAccessor = null;
	private RegisterAccessor registerAccessor = null;
	private LoginAccessor loginAccessor = null;
	private VersionAccessor versionAccessor = null;
	
	private SharedPreferences preferences = null;
	
	private int versionCode = 0;
	private String versionName = null;
	
	private String[] knownAddresses = null;
	
	@Override
	public void onCreate() {
		eventsAccessor = new EventsAccessor(PROTOSEC + URI + "events");
		surveysAccessor = new SurveysAccessor(PROTOSEC + URI + "surveys");
		usersAccessor = new UsersAccessor(PROTOSEC + URI + "users");
		devicesAccessor = new DevicesAccessor(PROTOSEC + URI + "devices/1"); // 1 = Google Device
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
		
		registerGcm();
	}
	
	public EventsAccessor getEventsAccessor() {
		return eventsAccessor;
	}
	
	public SurveysAccessor getSurveysAccessor() {
		return surveysAccessor;
	}
	
	public UsersAccessor getUsersAccessor() {
		return usersAccessor;
	}
	
	public DevicesAccessor getDevicesAccessor() {
		return devicesAccessor;
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
	
	public String[] getKnownAddresses() {
		return knownAddresses;
	}

	public void setKnownAddresses(String[] knownAddresses) {
		this.knownAddresses = knownAddresses;
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
	
	protected void registerGcm() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
		  GCMRegistrar.register(this, GCM_SENDER_ID);
		} else {
		  Log.v("Letsdoo", "Already registered");
		}
	}

	public boolean isGcmServerSynced() {
		return GCMRegistrar.isRegisteredOnServer(getApplicationContext());
	}
	
	/**
	 * Not to be called from event UI thread.
	 */
	public void gcmServerSync() {
		try {
			if (GCMRegistrar.isRegistered(getApplicationContext())) {
				devicesAccessor.insertItem(getPreferences().getString("gcmRegistrationId", null));
			} else {
				devicesAccessor.deleteItem(getPreferences().getString("gcmRegistrationId", null));
			}
			GCMRegistrar.setRegisteredOnServer(getApplicationContext(), true);
		} catch (Exception ex) {
			// failed? try again next time.
		}
	}
	
	public void removeSession() {
    	eventsAccessor.getWebRequest().removeHeader("Authorization");
    	surveysAccessor.getWebRequest().removeHeader("Authorization");
    	usersAccessor.getWebRequest().removeHeader("Authorization");
    	devicesAccessor.getWebRequest().removeHeader("Authorization");
	}
	
	public void newSession(String sessionkey) {
    	eventsAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
    	surveysAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
    	usersAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
    	devicesAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
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
