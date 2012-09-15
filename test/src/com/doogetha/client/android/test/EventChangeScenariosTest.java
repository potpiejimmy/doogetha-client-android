package com.doogetha.client.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.doogetha.client.android.EventsActivity;
import com.jayway.android.robotium.solo.Solo;

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
	    solo.waitForDialogToClose(5000);
	    solo.finishOpenedActivities();   
	}
	   
}
