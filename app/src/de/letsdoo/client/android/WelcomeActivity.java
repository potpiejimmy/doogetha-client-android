package de.letsdoo.client.android;

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
import de.letsdoo.client.util.Utils;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class WelcomeActivity extends Activity implements OnClickListener {

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
        this.setContentView(R.layout.welcome);
        
        if (Utils.getApp(this).isRegistered()) {
        	// already registered? proceed to main view
        	startMainView();
        	return;
        }
        
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
    	
    	viewflipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
    	viewflipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
    	
    	if (getLoginToken() != null) {
    		viewflipper.showNext();
    		registerSuccess(getLoginToken());  // display login token and PIN again when reopening
    	}
    }
    
    protected void startMainView() {
    	startActivity(new Intent(getApplicationContext(), EventsActivity.class));
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
		String mailstring = email.getText().toString();
		Utils.getApp(this).setEmail(mailstring);
		new RegisterTask(mailstring).go("Registrierungsanfrage wird gesendet...");
	}
	
	protected void login()
	{
		new FetchCredentialsTask(getLoginToken()).go("Anmelden...");
	}
	
	protected void cancelRegistration()
	{
		removeLoginToken();
		viewflipper.showPrevious();
		viewflipper.showPrevious();
	}
	
	protected String getLoginToken() {
		return Utils.getApp(this).getPreferences().getString("logintoken", null);
	}
	
	protected void setLoginToken(String logintoken) {
    	Utils.getApp(this).getPreferences().edit().putString("logintoken", logintoken).commit();
	}
	
	protected void removeLoginToken() {
    	Utils.getApp(this).getPreferences().edit().remove("logintoken").commit();
	}
	
	protected void registerSuccess(String logintoken)
	{
		String pin = logintoken.substring(logintoken.indexOf(":")+1);
    	//String authtoken = Base64.encodeToString("thorsten@potpiejimmy.de:asdfasdf".getBytes(), Base64.NO_WRAP);
    	registerresulttext.setText("PIN: " + pin);
    	setLoginToken(logintoken);
    	viewflipper.showNext();
	}
	
	protected void fetchCredentialsSuccess(String authkey)
	{
		String id = authkey.substring(0, authkey.indexOf(":"));
		String password = authkey.substring(authkey.indexOf(":")+1);
		String logintoken = getLoginToken();
		password = Utils.xorHex(password, logintoken.substring(0, logintoken.indexOf(":")));
		removeLoginToken();
    	Utils.getApp(this).register(id + ":" + password);
    	startMainView();
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
			return Utils.getApp(WelcomeActivity.this).getRegisterAccessor().insertItemWithResult(email);
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
	
	protected class FetchCredentialsTask extends AsyncUITask<String>
	{
		private String logintoken = null;
		
		public FetchCredentialsTask(String logintoken) {
			super(WelcomeActivity.this);
			this.logintoken = logintoken;
		}

		@Override
		public String doTask() throws Throwable {
			return Utils.getApp(WelcomeActivity.this).getRegisterAccessor().getItem(logintoken.substring(0, logintoken.indexOf(":")));
		}

		@Override
		public void doneOk(String credentials) {

			fetchCredentialsSuccess(credentials);
		}

		@Override
		public void doneFail(Throwable throwable) {
			DroidLib.alert(WelcomeActivity.this, "Die Registrierung war nicht erfolgreich. Bitte prüfe, ob du die E-Mail-Bestätigung korrekt durchgeführt hast.");
		}
	}
}
