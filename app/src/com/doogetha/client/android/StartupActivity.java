package com.doogetha.client.android;

import java.net.ConnectException;
import java.net.SocketException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.doogetha.client.util.Utils;

import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class StartupActivity extends Activity
{
	
	protected TextView label = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utils.getApp(this).hasSession()) {
        	startMainView();
        	return;
        }
        
        this.setContentView(R.layout.startup);
        
        this.label = (TextView)findViewById(R.id.startupLabel);
        
        startSession();
    }

    protected void startSession()
    {
    	performTask(new SessionLoginTask(Utils.getApp(this).getAuthtoken()), "Sitzung wird gestartet...");
    }
    
    protected void sessionCreateSuccess(String sessionkey)
    {
    	Utils.getApp(this).newSession(sessionkey);
    	checkGcmRegistration();
    }
    
    protected void checkGcmRegistration()
    {
    	if (!Utils.getApp(this).isGcmServerSynced())
    		performTask(new DeviceRegisterTask(), "Registriere Messaging...");
    	else
    		doneGcmCheck();
    }
    
    protected void doneGcmCheck()
    {
		checkVersion();
    }
	
    protected void checkVersion()
    {
    	performTask(new VersionCheckTask(), getString(R.string.process_checkversion));
    }
    
    protected void doneCheckVersion()
    {
    	label.setText("Starte Doogetha...");
    	startMainView();
    }
    
    protected void startMainView()
    {
    	Intent i = new Intent(getApplicationContext(), EventsActivity.class);
    	i.putExtras(getIntent());
    	startActivity(i);
    }
    
    protected void newerVersionExists()
    {
    	DroidLib.alert(this, null, getString(R.string.newversionavailable), "Jetzt herunterladen", null, new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				DroidLib.invokeBrowser(StartupActivity.this, Letsdoo.DOWNLOADURL);
			}
    	}, new android.content.DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				doneCheckVersion();
			}
		});
    }
    
    protected void performTask(AsyncUITask<?> task, String msg)
    {
    	label.setText(msg);
    	task.go(msg, false);
    }
    
    protected class SessionLoginTask extends AsyncUITask<String>
	{
		private String authkey = null;
		
		public SessionLoginTask(String authkey) 
		{
			super(StartupActivity.this);
			this.authkey = authkey;
		}
		
		public String doTask() throws Throwable
		{
			return Utils.getApp(StartupActivity.this).getLoginAccessor().insertItemWithResult(authkey);
		}
		
		public void doneOk(String sessioncredentials)
		{
	    	String userid = sessioncredentials.substring(0, sessioncredentials.indexOf(":"));
	    	String password = sessioncredentials.substring(sessioncredentials.indexOf(":")+1);
	    	String sessionkey = Base64.encodeToString((userid + ":" + password).getBytes(), Base64.NO_WRAP);
	    	sessionCreateSuccess(sessionkey);
		}
		
		public void doneFail(Throwable throwable) 
		{
			if (throwable instanceof ConnectException ||
				throwable instanceof SocketException) {
				DroidLib.alert(StartupActivity.this, getString(R.string.servernotavailable));
			} else {
				DroidLib.alert(StartupActivity.this, "Die Anmeldung ist fehlgeschlagen: "+throwable);
				checkVersion();
			}
		}
	}
	
	protected class VersionCheckTask extends AsyncUITask<String>
	{
		public VersionCheckTask() 
		{
			super(StartupActivity.this);
		}
		
		public String doTask() throws Throwable
		{
			return Utils.getApp(StartupActivity.this).getVersionAccessor().getItems();
		}
		
		public void doneOk(String result)
		{
			if (result == null) return; // could not fetch current version, ignore
			try {
				int currentVersion = Integer.parseInt(result);
				if (currentVersion > Utils.getApp(StartupActivity.this).getVersionCode())
					newerVersionExists();
				else
					doneCheckVersion();
			} catch (NumberFormatException nfe) {
				// no valid version value received, just ignore and do nothing
				doneCheckVersion();
			}
		}
		
		public void doneFail(Throwable throwable) 
		{
    		// just ignore
			doneCheckVersion();
		}
	}
	
	protected class DeviceRegisterTask extends AsyncUITask<String>
	{
		public DeviceRegisterTask() 
		{
			super(StartupActivity.this);
		}
		
		public String doTask() throws Throwable
		{
			Utils.getApp(StartupActivity.this).gcmServerSync();
			return null;
		}
		
		public void doneOk(String result)
		{
			doneGcmCheck();
		}
		
		public void doneFail(Throwable throwable) 
		{
    		Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
			doneGcmCheck();
		}
	}
}
