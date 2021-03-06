package com.doogetha.client.android;

import java.security.KeyPair;
import java.security.PrivateKey;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.doogetha.client.android.rest.CommentsAccessor;
import com.doogetha.client.android.rest.DevicesAccessor;
import com.doogetha.client.android.rest.EventsAccessor;
import com.doogetha.client.android.rest.LoginAccessor;
import com.doogetha.client.android.rest.RegisterAccessor;
import com.doogetha.client.android.rest.SurveysAccessor;
import com.doogetha.client.android.rest.UsersAccessor;
import com.doogetha.client.android.rest.VersionAccessor;
import com.doogetha.client.util.DoogethaFriends;
import com.google.android.gcm.GCMRegistrar;

import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.UserVo;
import de.letsdoo.server.vo.VersionVo;
import de.potpiejimmy.util.KeyUtil;

public class Letsdoo extends Application {
	
	public final static int PROTOCOL_VERSION = 2;
	
	public final static String PROTO    = "http://";
	public final static String PROTOSEC = "https://";
	
	public final static String URI = "www.doogetha.com/beta/res/";
	//public final static String URI = "192.168.178.21:8080/beta/res/";
	//public final static String URI = "192.168.100.22:8089/beta/res/";
	//public final static String URI = "172.18.119.203:8089/beta/res/";
	
	public final static String DOWNLOADURL = "http://www.doogetha.com/download/Doogetha.apk";
	
	public final static String GCM_SENDER_ID = "273459076205";
	
	private EventsAccessor eventsAccessor = null;
	private SurveysAccessor surveysAccessor = null;
	private UsersAccessor usersAccessor = null;
	private DevicesAccessor devicesAccessor = null;
	private CommentsAccessor commentsAccessor = null;
	private RegisterAccessor registerAccessor = null;
	private LoginAccessor loginAccessor = null;
	private VersionAccessor versionAccessor = null;
	
	private SharedPreferences preferences = null;
	
	private int versionCode = 0;
	private String versionName = null;
	
	private VersionVo serverVersionVo = null;
	
	private DoogethaFriends friends = null;

	@Override
	public void onCreate() {
		eventsAccessor = new EventsAccessor(PROTOSEC + URI + "events");
		surveysAccessor = new SurveysAccessor(PROTOSEC + URI + "surveys");
		usersAccessor = new UsersAccessor(PROTOSEC + URI + "users");
		devicesAccessor = new DevicesAccessor(PROTOSEC + URI + "devices/1"); // 1 = Google Device
		commentsAccessor = new CommentsAccessor(PROTOSEC + URI + "comments");
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
	
	public CommentsAccessor getCommentsAccessor() {
		return commentsAccessor;
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
	
	public String getLoginToken() {
		return getPreferences().getString("logintoken", null);
	}
	
	public void setLoginToken(String logintoken) {
		getPreferences().edit().putString("logintoken", logintoken).commit();
	}
	
	public void removeLoginToken() {
		getPreferences().edit().remove("logintoken").commit();
	}
	
	public boolean isRegistered() {
		return Boolean.parseBoolean(getPreferences().getString("registered", "false"));
	}
	
	public void setRegistered(boolean registered) {
		getPreferences().edit().putString("registered", Boolean.valueOf(registered).toString()).commit();
	}
	
	public synchronized DoogethaFriends getDoogethaFriends() {
		if (friends == null) friends = new DoogethaFriends(this);
		return friends;
	}
	
	public void unregister() {
		setRegistered(false);
		removeLoginToken();
		removeSession();
		unregisterGcm();
	}
	
	public void generateNewKeyPair() throws Exception {
		KeyPair kp = KeyUtil.generateKeyPair();
		getPreferences().
			edit().
			putString("pubkey",  KeyUtil.encodeKey(kp.getPublic())).
			putString("privkey", KeyUtil.encodeKey(kp.getPrivate())).
			commit();
	}
	
	public String getEncodedPublicKey() {
		return getPreferences().getString("pubkey", null);
	}
	
	public PrivateKey getPrivateKey() throws Exception {
		String privkey = getPreferences().getString("privkey", null);
		return KeyUtil.decodePrivateKey(privkey);
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

	protected void unregisterGcm() {
		// do not really unregister, just set server sync false:
		GCMRegistrar.setRegisteredOnServer(this, false);
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
	
	public EventVo newEvent() {
    	EventVo event = new EventVo();
    	event.setName("");
    	UserVo myself = new UserVo();
    	myself.setEmail(getEmail());
    	myself.setState(1); /* confirmed */
    	event.setUsers(new UserVo[] {myself});
    	event.setOwner(myself);
    	return event;
	}
	
	public void removeSession() {
    	eventsAccessor.getWebRequest().removeHeader("Authorization");
    	surveysAccessor.getWebRequest().removeHeader("Authorization");
    	usersAccessor.getWebRequest().removeHeader("Authorization");
    	devicesAccessor.getWebRequest().removeHeader("Authorization");
    	commentsAccessor.getWebRequest().removeHeader("Authorization");
	}
	
	public void newSession(String sessionkey) {
    	eventsAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
    	surveysAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
    	usersAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
    	devicesAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
    	commentsAccessor.getWebRequest().setHeader("Authorization", "Basic "+sessionkey);
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

	public VersionVo getServerVersionVo() {
		return serverVersionVo;
	}

	public void setServerVersionVo(VersionVo serverVersionVo) {
		this.serverVersionVo = serverVersionVo;
	}
}
