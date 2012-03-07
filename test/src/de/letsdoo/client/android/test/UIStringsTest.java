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
	 String einstellungen;
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
		einstellungen = mActivity.getString(R.string.settings);
		eventlist = (PullRefreshableListView)mActivity.findViewById(R.id.currenteventslist);
	}

	 public void testMainViewStrings()
	 {
		 assertTrue(solo.searchText("Doogetha", true));
		 assertTrue(solo.searchText("Version 0.6.5", true));
		 assertTrue(solo.searchText("Neue\nAktivität...", true));
		 assertTrue(solo.searchText("Aktuelle Aktivitäten", true));
		 assertTrue(solo.searchText("Aktuelle\nAktivitäten", true));
		 assertTrue(solo.searchText("Meine\nAktivitäten", true));
		 assertTrue(solo.searchText("Einstellungen", true));
		 solo.clickOnButton(meineAktivitaeten);
		 assertTrue(solo.searchText("Doogetha", true));
		 assertTrue(solo.searchText("Version 0.6.5", true));
		 assertTrue(solo.searchText("Neue\nAktivität...", true));
		 assertTrue(solo.searchText("Meine Aktivitäten", true));
		 assertTrue(solo.searchText("Aktuelle\nAktivitäten", true));
		 assertTrue(solo.searchText("Meine\nAktivitäten", true));
		 assertTrue(solo.searchText("Einstellungen", true));
		 solo.clickOnButton(einstellungen);
//		 assertTrue(solo.searchText("Doogetha"));
//		 assertTrue(solo.searchText("Version 0.6.5"));
//		 assertTrue(solo.searchText("Neue\nAktivität..."));
//		 assertTrue(solo.searchText("Aktuelle Aktivitäten"));
//		 assertTrue(solo.searchText("Meine Aktivitäten"));
//		 assertTrue(solo.searchText("Community"));		 
	 }
	 	 	
	 public void testFindHardCodedStrings()
	 {
	 }
	 
	 
	   @Override    
	public void tearDown() throws Exception 
	{         
	    solo.finishOpenedActivities();   
	}
	   
}
