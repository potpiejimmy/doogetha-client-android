package com.doogetha.client.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.doogetha.client.android.uitasks.SessionLoginTask;
import com.doogetha.client.android.uitasks.SessionLoginTaskCallback;
import com.doogetha.client.android.uitasks.VersionCheckTask;
import com.doogetha.client.android.uitasks.VersionCheckTaskCallback;
import com.doogetha.client.util.Utils;

import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class StartupActivity extends Activity implements VersionCheckTaskCallback, SessionLoginTaskCallback
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
    	performTask(new SessionLoginTask(this, this, Utils.getApp(this).getLoginToken()), "Sitzung wird gestartet...");
    }
    
	public void doneLoginOk(String sessionkey) {
		sessionCreateSuccess(sessionkey);
	}

	public void doneLoginFail() {
		sessionCreateFail();
	}

	public void doneLoginError() {
		finishCancel();
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
    	performTask(new VersionCheckTask(this, this), getString(R.string.process_checkversion));
    }
    
    public void doneCheckVersionOk()
    {
    	startSession();
    }
    
    public void doneCheckVersionFail()
    {
    	finishCancel();
    }
    
    protected void startMainView()
    {
    	Intent i = new Intent(getApplicationContext(), EventsActivity.class);
    	i.putExtras(getIntent());
    	startActivity(i);
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
