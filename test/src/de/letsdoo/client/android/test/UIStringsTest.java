package de.letsdoo.client.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.letsdoo.client.android.EventsActivity;
import de.letsdoo.client.android.R;
import de.potpiejimmy.util.PullRefreshableListView;

public class UIStringsTest extends
		ActivityInstrumentationTestCase2<EventsActivity> {

	private Solo solo; 
	private EventsActivity mActivity;
	 String createButton;
	 String abbrechenButton;        
	 String speichernButton;
	 String meineAktivitaeten;
	 String aktuelleAktivitaeten;
	 String community;
	 PullRefreshableListView eventlist;

	public UIStringsTest() {
		super("de.letsdoo.client.android", EventsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        solo = new Solo(getInstrumentation(), getActivity()); 
        mActivity = getActivity();
		createButton = mActivity.getString(R.string.createnewactivity);
	    abbrechenButton = mActivity.getString(R.string.cancel);        
		speichernButton = mActivity.getString(R.string.save);
		meineAktivitaeten = mActivity.getString(R.string.myactivities);
		aktuelleAktivitaeten = mActivity.getString(R.string.currentactivities);
		community = mActivity.getString(R.string.publicactivities);
		eventlist = (PullRefreshableListView)mActivity.findViewById(R.id.currenteventslist);
	}

	 public void testMainViewStrings()
	 {
		 assertTrue(solo.searchText("Doogetha"));
		 assertTrue(solo.searchText("Version 0.6.2"));
		 assertTrue(solo.searchText("Neue\nAktivität..."));
		 assertTrue(solo.searchText("Aktuelle Aktivitäten"));
		 assertTrue(solo.searchText("Meine Aktivitäten"));
		 assertTrue(solo.searchText("Community"));
		 solo.clickOnButton(meineAktivitaeten);
		 assertTrue(solo.searchText("Doogetha"));
		 assertTrue(solo.searchText("Version 0.6.2"));
		 assertTrue(solo.searchText("Neue\nAktivität..."));
		 assertTrue(solo.searchText("Aktuelle Aktivitäten"));
		 assertTrue(solo.searchText("Meine Aktivitäten"));
		 assertTrue(solo.searchText("Community"));
		 solo.clickOnButton(community);
		 assertTrue(solo.searchText("Doogetha"));
		 assertTrue(solo.searchText("Version 0.6.2"));
		 assertTrue(solo.searchText("Neue\nAktivität..."));
		 assertTrue(solo.searchText("Aktuelle Aktivitäten"));
		 assertTrue(solo.searchText("Meine Aktivitäten"));
		 assertTrue(solo.searchText("Community"));		 
	 }
	 	 	
	   @Override    
	public void tearDown() throws Exception 
	{         
	    solo.finishOpenedActivities();   
	}
	   
}
