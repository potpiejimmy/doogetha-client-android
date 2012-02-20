package de.letsdoo.client.android.test;

import java.util.ArrayList;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import de.letsdoo.client.android.EventsActivity;
import de.letsdoo.client.util.Utils;
import de.letsdoo.client.android.R;

public class MainViewTest extends
		ActivityInstrumentationTestCase2<EventsActivity> {

	private Solo solo; 
	private EventsActivity mActivity;

	public MainViewTest() {
		super("de.letsdoo.client.android", EventsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        solo = new Solo(getInstrumentation(), getActivity()); 
        mActivity = getActivity();
	}

	 public void testAnlegenAbbrechen()
	 {
		 String createButton = mActivity.getString(R.string.createnewactivity);
		 String abbrechenButton = mActivity.getString(R.string.cancel);
		 solo.clickOnButton(createButton);
		 solo.enterText(0, "TestAktivität");
		 solo.enterText(1, "Blablablabla und mit recht viel Text vielleicht auch mit\nZeilenumbruch und Umlauten äüö");
		 solo.clickOnButton(abbrechenButton);
		 solo.sleep(3000);
	 }
	 
	 public void testAnlegenSpeichernLoeschen()
	 {
		 String createButton = mActivity.getString(R.string.createnewactivity);
		 String speichernButton = mActivity.getString(R.string.save);
		 solo.clickOnButton(createButton);
		 solo.enterText(0, "TestAktivität");
		 solo.enterText(1, "Blablablabla und mit recht viel Text vielleicht auch mit\nZeilenumbruch und Umlauten äüö");
		 solo.clickOnButton(speichernButton);
		 solo.sleep(3000);
		 //solo.clickOnButton(index)
	 }
	 	
	   @Override    
	public void tearDown() throws Exception 
	{         
	    solo.finishOpenedActivities();   
	}
	   
}
