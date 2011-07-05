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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);
        
    	Button registerbutton = (Button) findViewById(R.id.registerbutton);
    	loginbutton = (Button) findViewById(R.id.loginbutton);
    	
    	registerbutton.setOnClickListener(this);
    	loginbutton.setOnClickListener(this);
    	
    	loginbutton.setEnabled(false);
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
		}
	}

	protected void register()
	{
		EditText email = (EditText) findViewById(R.id.email);
		new RegisterTask(email.getText().toString()).go("Creating login credentials...");
	}
	
	protected void login()
	{
		new LoginTask(getLoginToken()).go("Logging in...");
	}
	
	protected String getLoginToken() {
		return Utils.getApp(this).getPreferences().getString("logintoken", null);
	}
	
	protected void setLoginToken(String logintoken) {
    	Utils.getApp(this).getPreferences().edit().putString("logintoken", logintoken).commit();
	}
	
	protected void registerSuccess(String logintoken)
	{
    	//String authtoken = Base64.encodeToString("thorsten@potpiejimmy.de:asdfasdf".getBytes(), Base64.NO_WRAP);
    	((TextView)findViewById(R.id.registerresulttext)).setText("Successful, got token: " + logintoken);
    	setLoginToken(logintoken);
    	loginbutton.setEnabled(true);
	}
	
	protected void loginSuccess(String credentials)
	{
		String userid = credentials.substring(0, credentials.indexOf(":"));
		String authkey = credentials.substring(credentials.indexOf(":")+1);
		BigInteger authkeyI = new BigInteger(authkey, 16);
		BigInteger password = authkeyI.xor(new BigInteger(getLoginToken(), 16));
    	String authtoken = Base64.encodeToString((userid + ":" + password.toString(16)).getBytes(), Base64.NO_WRAP);
    	Utils.getApp(this).login(authtoken);
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
				result = Utils.getApp(LoginActivity.this).getLoginAccessor().insertItemWithResult(email);
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
	
	protected class LoginTask extends AsyncUITask<String[]>
	{
		private String logintoken = null;
		
		public LoginTask(String logintoken) {
			super(LoginActivity.this);
			this.logintoken = logintoken;
		}

		@Override
		public String[] doTask() {
			try {
				return new String[] {Utils.getApp(LoginActivity.this).getLoginAccessor().getItem(logintoken),null};
			} catch (Exception ex) {
				return new String[] {null,ex.toString()};
			}
		}

		@Override
		public void done(String[] result) {
			String credentials = result[0];
			String exceptionText = result[1];
			if (credentials != null) loginSuccess(credentials);
			else DroidLib.alert(LoginActivity.this, "Login failed: " + exceptionText);
		}
	}
}
