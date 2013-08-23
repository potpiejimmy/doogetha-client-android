package com.doogetha.client.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.doogetha.client.android.uitasks.SessionLoginTask;
import com.doogetha.client.android.uitasks.SessionLoginTaskCallback;
import com.doogetha.client.android.uitasks.VersionCheckTask;
import com.doogetha.client.android.uitasks.VersionCheckTaskCallback;
import com.doogetha.client.util.Utils;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class WelcomeActivity extends Activity implements OnClickListener,VersionCheckTaskCallback,SessionLoginTaskCallback {

	private Button loginbutton = null;
	private Button registerbutton = null;
	private Button continuebutton = null;
	private Button cancelbutton = null;
	private EditText email = null;
	private TextView registerresulttext = null;
	private ViewFlipper viewflipper = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (Utils.getApp(this).isRegistered()) {
        	// already registered? proceed to main view
        	startMainView();
        	return;
        }

        this.setContentView(R.layout.welcome);
        
    	registerbutton = (Button) findViewById(R.id.registerbutton);
    	continuebutton = (Button) findViewById(R.id.continuebutton);
    	loginbutton = (Button) findViewById(R.id.loginbutton);
    	cancelbutton = (Button) findViewById(R.id.cancelregistrationbutton);
		email = (EditText) findViewById(R.id.email);
		registerresulttext = (TextView) findViewById(R.id.registerPinLabel);
		viewflipper = (ViewFlipper) findViewById(R.id.viewFlipper);
    	
    	registerbutton.setOnClickListener(this);
    	loginbutton.setOnClickListener(this);
    	continuebutton.setOnClickListener(this);
    	cancelbutton.setOnClickListener(this);
    	
    	viewflipper.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
    	viewflipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_left);
    	
    	if (Utils.getApp(this).getLoginToken() != null) {
    		viewflipper.showNext();
    		registerSuccess(Utils.getApp(this).getLoginToken());  // display login token and PIN again when reopening
    	}
    }
    
    protected void startMainView() {
    	Intent i = new Intent(getApplicationContext(), StartupActivity.class);
    	i.putExtras(getIntent());
    	startActivity(i);
    }
    
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.registerbutton:
				register();
				break;
			case R.id.loginbutton:
				login();
				break;
			case R.id.continuebutton:
				viewflipper.showNext();
				break;
			case R.id.cancelregistrationbutton:
				DroidLib.alert(this, "Willst du die Registrierung wirklich abbrechen und von vorne beginnen?", getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						cancelRegistration();
					}
				});
		}
	}

	protected void register()
	{
		// prior to registering, do a version check:
		new VersionCheckTask(this, this).go(getString(R.string.process_checkversion));
	}
	
	public void doneCheckVersionOk()
	{
		// version ok, do register:
		doRegister();
	}
	
	public void doneCheckVersionFail()
	{
		finishCancel();
	}
	
	protected void doRegister()
	{
		String mailstring = email.getText().toString();
		Utils.getApp(this).setEmail(mailstring);
		new RegisterTask(mailstring).go(getString(R.string.doregisterwait));
	}
	
	protected void login()
	{
		// perform a test login to see whether registration was successful.
		// (note: real session login is done in StartupActivity).
		new SessionLoginTask(this, this, Utils.getApp(this).getLoginToken()).go("Anmelden...");
	}
	
	protected void cancelRegistration()
	{
		Utils.getApp(this).removeLoginToken();
		viewflipper.showPrevious();
		viewflipper.showPrevious();
	}
	
	protected void registerSuccess(String logintoken)
	{
		String pin = logintoken.substring(logintoken.indexOf(":")+1);
    	registerresulttext.setText("PIN: " + pin);
    	Utils.getApp(this).setLoginToken(logintoken);
    	viewflipper.showNext();
	}
	

	public void doneLoginOk(String sessionkey) {
		Utils.getApp(this).setRegistered(true);
    	DroidLib.pause(500); // wait a few milliseconds before trying to log on with new credentials
    	startMainView();
	}

	public void doneLoginFail() {
		DroidLib.alert(this, "Die Registrierung war nicht erfolgreich. Bitte prüfe, ob du die E-Mail-Bestätigung korrekt durchgeführt hast.");
	}

	public void doneLoginError() {
		doneLoginFail();
	}
	
    protected void finishCancel()
    {
    	setResult(RESULT_CANCELED);
    	finish();
    }
    
	protected class RegisterTask extends AsyncUITask<String>
	{
		private String email = null;
		
		public RegisterTask(String email) {
			super(WelcomeActivity.this);
			this.email = email;
		}

		@Override
		public String doTask() throws Throwable {
			Utils.getApp(WelcomeActivity.this).createNewKeyPair();
			String registerData = email + ":" + Utils.getApp(WelcomeActivity.this).getPublicKey();
			return Utils.getApp(WelcomeActivity.this).getRegisterAccessor().insertItemWithResult(registerData);
		}

		@Override
		public void doneOk(String registertoken) {
			registerSuccess(registertoken);
		}

		@Override
		public void doneFail(Throwable throwable) {
			DroidLib.alert(WelcomeActivity.this, "Die Registrierungsanfrage konnte nicht gesendet werden.");
		}
	}
}
