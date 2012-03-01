package de.letsdoo.client.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.letsdoo.client.android.EventsActivity;
import de.letsdoo.client.android.R;
import de.potpiejimmy.util.PullRefreshableListView;

public class MainViewTest extends
		ActivityInstrumentationTestCase2<EventsActivity> {

	private Solo solo; 
	private EventsActivity mActivity;
	 String createButton;
	 String abbrechenButton;        
	 String speichernButton;
	 String meineAktivitaeten;
	 String aktuelleAktivitaeten;
	 PullRefreshableListView eventlist;

	public MainViewTest() {
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
		eventlist = (PullRefreshableListView)mActivity.findViewById(R.id.currenteventslist);
	}

	 public void testAnlegenAbbrechen()
	 {
		 assertFalse(solo.searchText("TestAktivität"));
		 solo.clickOnButton(createButton);
		 solo.enterText(0, "TestAktivität");
		 solo.enterText(1, "Blablablabla und mit recht viel Text vielleicht auch mit\nZeilenumbruch und Umlauten äüö");
		 solo.clickOnButton(abbrechenButton);
		 assertFalse(solo.searchText("TestAktivität"));
	 }
	 
	 public void testAnlegenSpeichernLoeschen()
	 {
		 assertFalse(solo.searchText("TestAktivität"));
		 solo.clickOnButton(createButton);
		 solo.enterText(0, "TestAktivität");
		 solo.enterText(1, "Blablablabla und mit recht viel Text vielleicht auch mit\nZeilenumbruch und Umlauten äüö");
		 solo.clickOnButton(speichernButton);
		 solo.clickOnText("TestAktivität");
		 solo.sleep(500);
		 //solo.sendKey(KeyEvent.KEYCODE_BACK);
//		 solo.goBackToActivity("EventsActivity");
		 solo.goBack();
		 solo.sleep(500);
		 solo.clickOnButton(meineAktivitaeten);
		 solo.clickLongOnText("TestAktivität");
		 solo.clickOnText("Eintrag löschen");
		 solo.clickOnButton(aktuelleAktivitaeten);
		 assertFalse(solo.searchText("TestAktivität"));
	 }
	 	
	   @Override    
	public void tearDown() throws Exception 
	{         
	    solo.finishOpenedActivities();   
	}
	   
}
