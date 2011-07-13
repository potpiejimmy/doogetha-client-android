package de.letsdoo.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.letsdoo.client.util.Utils;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class LoginActivity extends Activity implements OnClickListener {

	private Button loginbutton = null;
	private Button registerbutton = null;
	private Button unregisterbutton = null;
	private EditText email = null;
	private TextView registerresulttext = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);
        
    	registerbutton = (Button) findViewById(R.id.registerbutton);
    	unregisterbutton = (Button) findViewById(R.id.unregisterbutton);
    	loginbutton = (Button) findViewById(R.id.loginbutton);
		email = (EditText) findViewById(R.id.email);
		registerresulttext = (TextView) findViewById(R.id.registerresulttext);
    	
    	registerbutton.setOnClickListener(this);
    	loginbutton.setOnClickListener(this);
    	unregisterbutton.setOnClickListener(this);
    	
    	if (getLoginToken() != null)
    		registerSuccess(getLoginToken());  // display login token and PIN again when reopening
    	else
    		switchUIRegistered(false);
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
			case R.id.unregisterbutton:
				switchUIRegistered(false);
				break;
		}
	}

	protected void register()
	{
		String mailstring = email.getText().toString();
		Utils.getApp(this).setEmail(mailstring);
		new RegisterTask(mailstring).go("Sending registration request...");
	}
	
	protected void login()
	{
		new FetchCredentialsTask(getLoginToken()).go("Logging in...");
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
    	registerresulttext.setText(
    			"A request for creating your login credentials has been sent out successfully.\n"+
    			"\nYour Request PIN is " + pin + "\n\n"+
    			"Please check your email and click the confirm link in the mail to verify your account. "+
    			"After you have confirmed your registration, you may log in using the button below.");
    	setLoginToken(logintoken);
    	switchUIRegistered(true);
	}
	
	protected void switchUIRegistered(boolean registered) 
	{
		loginbutton.setEnabled(registered);
		unregisterbutton.setEnabled(registered);
		registerbutton.setEnabled(!registered);
		email.setEnabled(!registered);
		if (!registered) registerresulttext.setText(" ");
	}
	
	protected void fetchCredentialsSuccess(String authkey)
	{
		String id = authkey.substring(0, authkey.indexOf(":"));
		String password = authkey.substring(authkey.indexOf(":")+1);
		String logintoken = getLoginToken();
		password = Utils.xorHex(password, logintoken.substring(0, logintoken.indexOf(":")));
		removeLoginToken();
    	Utils.getApp(this).register(id + ":" + password);
    	setResult(RESULT_OK);
    	finish();
	}
	
	protected class RegisterTask extends AsyncUITask<String[]>
	{
		private String email = null;
		
		public RegisterTask(String email) {
			super(LoginActivity.this);
			this.email = email;
		}

		@Override
		public String[] doTask() {
			String[] result = null;
			try {
				result = new String[] {Utils.getApp(LoginActivity.this).getRegisterAccessor().insertItemWithResult(email), null};
			} catch (Exception ex) {
				result = new String[] {null, ex.toString()};
			}
			return result;
		}

		@Override
		public void done(String[] result) {
			String registertoken = result[0];
			String exceptiontext = result[1];
			if (registertoken != null) registerSuccess(registertoken);
			else DroidLib.alert(LoginActivity.this, "Sorry, could not send registration request.");
		}
	}
	
	protected class FetchCredentialsTask extends AsyncUITask<String[]>
	{
		private String logintoken = null;
		
		public FetchCredentialsTask(String logintoken) {
			super(LoginActivity.this);
			this.logintoken = logintoken;
		}

		@Override
		public String[] doTask() {
			try {
				return new String[] {Utils.getApp(LoginActivity.this).getRegisterAccessor().getItem(logintoken.substring(0, logintoken.indexOf(":"))),null};
			} catch (Exception ex) {
				return new String[] {null,ex.toString()};
			}
		}

		@Override
		public void done(String[] result) {
			String credentials = result[0];
			String exceptionText = result[1];
			if (credentials != null) fetchCredentialsSuccess(credentials);
			else DroidLib.alert(LoginActivity.this, "Sorry, registration failed. Please make sure you have confirmed your registration request by clicking the confirmation link in your registration email.");
		}
	}
}
