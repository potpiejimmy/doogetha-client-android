package de.letsdoo.client.android;

import android.app.Activity;
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

public class LoginActivity extends Activity implements OnClickListener {

	private Button loginbutton = null;
	private Button registerbutton = null;
	private Button continuebutton = null;
	private EditText email = null;
	private TextView registerresulttext = null;
	private ViewFlipper viewflipper = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.welcome);
        
    	registerbutton = (Button) findViewById(R.id.registerbutton);
    	continuebutton = (Button) findViewById(R.id.continuebutton);
    	loginbutton = (Button) findViewById(R.id.loginbutton);
		email = (EditText) findViewById(R.id.email);
		registerresulttext = (TextView) findViewById(R.id.registerPinLabel);
		viewflipper = (ViewFlipper) findViewById(R.id.viewFlipper);
    	
    	registerbutton.setOnClickListener(this);
    	loginbutton.setOnClickListener(this);
    	continuebutton.setOnClickListener(this);
    	
    	viewflipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
    	viewflipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
    	
    	if (getLoginToken() != null) {
    		viewflipper.showNext();
    		registerSuccess(getLoginToken());  // display login token and PIN again when reopening
    	}
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
		public String doTask() throws Throwable {
			return Utils.getApp(LoginActivity.this).getRegisterAccessor().insertItemWithResult(email);
		}

		@Override
		public void doneOk(String registertoken) {
			registerSuccess(registertoken);
		}

		@Override
		public void doneFail(Throwable throwable) {
			DroidLib.alert(LoginActivity.this, "Sorry, could not send registration request.");
		}
	}
	
	protected class FetchCredentialsTask extends AsyncUITask<String>
	{
		private String logintoken = null;
		
		public FetchCredentialsTask(String logintoken) {
			super(LoginActivity.this);
			this.logintoken = logintoken;
		}

		@Override
		public String doTask() throws Throwable {
			return Utils.getApp(LoginActivity.this).getRegisterAccessor().getItem(logintoken.substring(0, logintoken.indexOf(":")));
		}

		@Override
		public void doneOk(String credentials) {

			fetchCredentialsSuccess(credentials);
		}

		@Override
		public void doneFail(Throwable throwable) {
			DroidLib.alert(LoginActivity.this, "Sorry, registration failed. Please make sure you have confirmed your registration request by clicking the confirmation link in your registration email.");
		}
	}
}
