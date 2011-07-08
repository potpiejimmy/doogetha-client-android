package de.letsdoo.client.android;

import java.math.BigInteger;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);
        
    	registerbutton = (Button) findViewById(R.id.registerbutton);
    	unregisterbutton = (Button) findViewById(R.id.unregisterbutton);
    	loginbutton = (Button) findViewById(R.id.loginbutton);
		email = (EditText) findViewById(R.id.email);
    	
    	registerbutton.setOnClickListener(this);
    	loginbutton.setOnClickListener(this);
    	unregisterbutton.setOnClickListener(this);
    	
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
		new RegisterTask(email.getText().toString()).go("Creating login credentials...");
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
    	//String authtoken = Base64.encodeToString("thorsten@potpiejimmy.de:asdfasdf".getBytes(), Base64.NO_WRAP);
    	((TextView)findViewById(R.id.registerresulttext)).setText(
    			"A request for creating your login credentials has been sent out successfully. "+
    			"Please check your email and click the confirm link in the mail to verify your account. "+
    			"Click the Login button below after you have done so to log in.");
    	setLoginToken(logintoken);
    	switchUIRegistered(true);
	}
	
	protected void switchUIRegistered(boolean registered) 
	{
		loginbutton.setEnabled(registered);
		unregisterbutton.setEnabled(registered);
		registerbutton.setEnabled(!registered);
		email.setEnabled(!registered);
	}
	
	protected void fetchCredentialsSuccess(String authkey)
	{
		String id = authkey.substring(0, authkey.indexOf(":"));
		String password = authkey.substring(authkey.indexOf(":")+1);
		password = Utils.xorHex(password, getLoginToken());
		removeLoginToken();
    	Utils.getApp(this).register(id + ":" + password);
    	setResult(RESULT_OK);
    	finish();
	}
	
	protected class RegisterTask extends AsyncUITask<String>
	{
		private String email = null;
		
		public RegisterTask(String email) {
			super(LoginActivity.this);
			this.email = email;
		}

		@Override
		public String doTask() {
			String result = null;
			try {
				result = Utils.getApp(LoginActivity.this).getRegisterAccessor().insertItemWithResult(email);
			} catch (Exception ex) {
				result = ex.toString();
			}
			return result;
		}

		@Override
		public void done(String result) {
			registerSuccess(result);
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
				return new String[] {Utils.getApp(LoginActivity.this).getRegisterAccessor().getItem(logintoken),null};
			} catch (Exception ex) {
				return new String[] {null,ex.toString()};
			}
		}

		@Override
		public void done(String[] result) {
			String credentials = result[0];
			String exceptionText = result[1];
			if (credentials != null) fetchCredentialsSuccess(credentials);
			else DroidLib.alert(LoginActivity.this, "Login failed: " + exceptionText);
		}
	}
}
