package de.letsdoo.client.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.jayway.android.robotium.solo.Solo;

import de.letsdoo.client.android.EventsActivity;
import de.letsdoo.client.android.WelcomeActivity;
import de.letsdoo.client.android.R;

public class UseCasesRegisterTest extends
		ActivityInstrumentationTestCase2<WelcomeActivity> {

	private Solo solo; 
	private WelcomeActivity mActivity;
	private String fortfahren;
	private String registrieren;
	private String login;
	private String unregister;
//	private Button registerbutton;
//	private Button loginbutton;
//	private EditText email;

	public UseCasesRegisterTest() {
		super("de.letsdoo.client.android", WelcomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        solo = new Solo(getInstrumentation(), getActivity()); 
        mActivity = getActivity();
        fortfahren = mActivity.getString(R.string.welcome_continue);
        registrieren = mActivity.getString(R.string.register);
        login = mActivity.getString(R.string.login);
        unregister = mActivity.getString(R.string.unregister);
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
	 public void testRegister()
	 {
//		 if (solo.getCurrentActivity().getClass() ==  EventsActivity.class)
//		 {
//			 solo.sendKey(KeyEvent.KEYCODE_MENU);
//			 solo.clickOnText(unregister);
//		 }
//		 solo.assertCurrentActivity("Wrong Activity", WelcomeActivity.class);
//		 solo.clickOnButton(fortfahren);
//	     solo.enterText(0, "wolfram.liese@t-online.de");
//		 solo.clickOnButton(registrieren);
//	     solo.sleep(60000);  // 60 seconds time to confirm via mail link manually ;-)
//		 solo.clickOnButton(login);
//		 solo.assertCurrentActivity("LOGIN FAILED", EventsActivity.class);
	 }
	 	
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
