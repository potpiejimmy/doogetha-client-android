package com.doogetha.client.android;

import java.net.ConnectException;
import java.net.SocketException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.VersionVo;
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
        
        checkVersion();
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
    
    protected void sessionCreateFail()
    {
    	DroidLib.alert(this, null, getString(R.string.sessioncreatefailed), null, null, null,
        		new android.content.DialogInterface.OnDismissListener() {
    				public void onDismiss(DialogInterface dialog) {
    					doneStartup(); // start up anyway to get to settings if needed
    				}
        		});
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
    	doneStartup();
    }
    
    protected void doneStartup()
    {
    	label.setText("Starte Doogetha...");
    	startMainView();
    }
	
    protected void checkVersion()
    {
    	performTask(new VersionCheckTask(), getString(R.string.process_checkversion));
    }
    
    protected void doneCheckVersion()
    {
    	startSession();
    }
    
    protected void startMainView()
    {
    	Intent i = new Intent(getApplicationContext(), EventsActivity.class);
    	i.putExtras(getIntent());
    	startActivity(i);
    }
    
    protected void newerVersionExists(boolean protocolVersionIncompatible)
    {
    	DroidLib.alert(this, null, 
    			protocolVersionIncompatible ?
    					getString(R.string.newversionavailable) + " " + getString(R.string.newversionavailablemustupdate) :
    					getString(R.string.newversionavailable),
    			"Jetzt herunterladen", null, new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				DroidLib.invokeBrowser(StartupActivity.this, Letsdoo.DOWNLOADURL);
			}
    	}, protocolVersionIncompatible ?
    		new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					finishCancel();
				}
    		} :
    		new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					doneCheckVersion();
				}
    		});
    }
    
    protected void incompatibleProtocolVersion()
    {
    	DroidLib.alert(this, null, getString(R.string.protocolversionincompatible), null, null, null,
    		new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					finishCancel();
				}
    		});
    }
    
    protected void performTask(AsyncUITask<?> task, String msg)
    {
    	label.setText(msg);
    	task.go(msg, false);
    }
    
    protected void finishCancel()
    {
    	setResult(RESULT_CANCELED);
    	finish();
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
	    	if (userid.length() == 0 || password.length() == 0) {
	    		// no session credentials received:
	    		sessionCreateFail();
	    		return;
	    	}
	    	String sessionkey = Base64.encodeToString((userid + ":" + password).getBytes(), Base64.NO_WRAP);
	    	sessionCreateSuccess(sessionkey);
		}
		
		public void doneFail(Throwable throwable) 
		{
			DroidLib.alert(StartupActivity.this, null, 
					(throwable instanceof ConnectException || throwable instanceof SocketException) ?
					getString(R.string.servernotavailable) :
					"Die Anmeldung ist fehlgeschlagen: "+throwable,
					null, null, null, new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					finishCancel();
				}
			});
		}
	}
	
	protected class VersionCheckTask extends AsyncUITask<VersionVo>
	{
		public VersionCheckTask() 
		{
			super(StartupActivity.this);
		}
		
		public VersionVo doTask() throws Throwable
		{
			return Utils.getApp(StartupActivity.this).getVersionAccessor().getItems();
		}
		
		public void doneOk(VersionVo result)
		{
			if (result == null) return; // could not fetch current version, ignore
			try {
				// protocol version handling:
				int protocolVersion = result.getProtocolVersion();
				boolean protocolVersionIncompatible = (protocolVersion != Letsdoo.PROTOCOL_VERSION);
				
				// client version handling:
				int currentVersion = result.getClientVersionCode();
				if (currentVersion > Utils.getApp(StartupActivity.this).getVersionCode())
					newerVersionExists(protocolVersionIncompatible);
				else if (protocolVersionIncompatible)
					incompatibleProtocolVersion();
				else
					doneCheckVersion();
			} catch (Exception nfe) {
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
