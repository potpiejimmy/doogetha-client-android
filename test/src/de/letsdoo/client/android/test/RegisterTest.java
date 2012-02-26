package de.letsdoo.client.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.letsdoo.client.android.WelcomeActivity;

public class RegisterTest extends
		ActivityInstrumentationTestCase2<WelcomeActivity> {

	private Solo solo; 
	private WelcomeActivity mActivity;
//	private Button registerbutton;
//	private Button loginbutton;
//	private EditText email;

	public RegisterTest() {
		super("de.letsdoo.client.android", WelcomeActivity.class);
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

//	 public void testRegister()
//	 {
//		 solo.clickOnButton("Fortfahren");
//	     solo.enterText(0, "wolfram.liese@t-online.de");
//		 solo.clickOnButton("Registrieren");
//		 solo.clickOnButton("Login");
//	 }
	 	
//	public void _testUIStatesWithToken()
//	{
//		Utils.getApp(mActivity).register("dummy:123");
//	}
	   @Override    
	public void tearDown() throws Exception 
	{         
	    solo.finishOpenedActivities();   
	}
	   
}
