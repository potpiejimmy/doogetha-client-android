package de.letsdoo.client.android.test;

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

	private LoginActivity mActivity;
	//private Button unregisterbutton;
	private Button registerbutton;
	private Button loginbutton;
	private EditText email;
	//private TextView registerresulttext;

	public LoginActivityTest() {
		super("de.letsdoo.client.android", LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
    	registerbutton = (Button) mActivity.findViewById(R.id.registerbutton);
    	//unregisterbutton = (Button) mActivity.findViewById(R.id.unregisterbutton);
    	loginbutton = (Button) mActivity.findViewById(R.id.loginbutton);
		email = (EditText) mActivity.findViewById(R.id.email);
		//registerresulttext = (TextView) mActivity.findViewById(R.id.registerresulttext);
	}

	 @UiThreadTest
	public void testPreConditions()
	{
		assertNotNull(registerbutton);
		//assertNotNull(unregisterbutton);
		assertNotNull(loginbutton);
		assertNotNull(email);
		//assertNotNull(registerresulttext);
		//registerresulttext.setText("EinsZweiDrei");
	}

	public void _testUIStatesWithToken()
	{
		Utils.getApp(mActivity).register("dummy:123");
	}
	
}
