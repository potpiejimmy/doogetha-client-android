package de.letsdoo.client.android.test;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.letsdoo.client.android.LoginActivity;
import de.letsdoo.client.android.R;
import de.letsdoo.client.util.Utils;

public class LoginActivityTest extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private Solo solo; 
	private LoginActivity mActivity;
//	private Button registerbutton;
//	private Button loginbutton;
//	private EditText email;

	public LoginActivityTest() {
		super("de.letsdoo.client.android", LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        solo = new Solo(getInstrumentation(), getActivity()); 
        mActivity = getActivity();
//    	registerbutton = (Button) mActivity.findViewById(R.id.registerbutton);
//    	loginbutton = (Button) mActivity.findViewById(R.id.loginbutton);
//		email = (EditText) mActivity.findViewById(R.id.email);
	}

	 public void testRegister()
	 {
		 solo.clickOnButton("Fortfahren");
	     solo.enterText(0, "wolfram.liese@t-online.de");
		 solo.clickOnButton("Registrieren");
		 solo.clickOnButton("Login");
	 }
	 	
	public void _testUIStatesWithToken()
	{
		Utils.getApp(mActivity).register("dummy:123");
	}
	   @Override    
	public void tearDown() throws Exception 
	{         
	    solo.finishOpenedActivities();   
	}
	   
}
