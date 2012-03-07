package de.letsdoo.client.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.letsdoo.client.android.EventsActivity;
import de.letsdoo.client.android.R;

public class EventChangeScenariosTest extends
		ActivityInstrumentationTestCase2<EventsActivity> {

	private Solo solo; 
	private EventsActivity mActivity;

	public EventChangeScenariosTest() {
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
	 }
	 
	 public void testAnlegenSpeichernLoeschen()
	 {
	 }
	 	
	   @Override    
	public void tearDown() throws Exception 
	{         
	    solo.finishOpenedActivities();   
	}
	   
}
