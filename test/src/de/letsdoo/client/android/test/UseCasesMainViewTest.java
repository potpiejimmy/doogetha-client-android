package de.letsdoo.client.android.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.letsdoo.client.android.EventsActivity;
import de.letsdoo.client.android.R.*;
import de.letsdoo.client.android.test.R;
import de.potpiejimmy.util.PullRefreshableListView;

public class UseCasesMainViewTest extends
		ActivityInstrumentationTestCase2<EventsActivity> {

	private Solo solo; 
	private EventsActivity mActivity;
	 String createButton;
	 String abbrechenButton;        
	 String speichernButton;
	 String meineAktivitaeten;
	 String aktuelleAktivitaeten;
	 String eintragLoeschen;
	 PullRefreshableListView eventlist;

	public UseCasesMainViewTest() {
		super("de.letsdoo.client.android", EventsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        solo = new Solo(getInstrumentation(), getActivity()); 
        mActivity = getActivity();
		createButton = mActivity.getString(string.createnewactivity);
	    abbrechenButton = mActivity.getString(string.cancel);        
		speichernButton = mActivity.getString(string.save);
		meineAktivitaeten = mActivity.getString(string.myactivities);
		aktuelleAktivitaeten = mActivity.getString(string.currentactivities);
		eintragLoeschen = mActivity.getString(string.deleteitem);
		eventlist = (PullRefreshableListView)mActivity.findViewById(id.currenteventslist);
	}

	 public void testAnlegenAbbrechen()
	 {
		 createDummyActivity();
		 solo.clickOnButton(abbrechenButton);
		 assertFalse(solo.searchText(TS(R.string.Testaktivitaet)));
	 }
	 
	 public void testAnlegenSpeichernLoeschen()
	 {
		 createDummyActivity();
		 solo.clickOnButton(speichernButton);
		 solo.clickOnText(TS(R.string.Testaktivitaet));
		 solo.sleep(500);
		 //solo.sendKey(KeyEvent.KEYCODE_BACK);
//		 solo.goBackToActivity("EventsActivity");
		 solo.goBack();
		 solo.sleep(500);
		 solo.clickOnButton(meineAktivitaeten);
		 solo.clickLongOnText(TS(R.string.Testaktivitaet));
		 solo.clickOnText(eintragLoeschen);
		 solo.clickOnButton(aktuelleAktivitaeten);
		 assertFalse(solo.searchText(TS(R.string.Testaktivitaet)));
	 }
	 
	 public void testRestart()
	 {
		 createDummyActivity();
		 solo.clickOnButton(speichernButton);
		 solo.clickOnText(TS(R.string.Testaktivitaet));
		 solo.finishOpenedActivities();
		 //this.
		 assertFalse(solo.searchText(TS(R.string.Testaktivitaet)));
	 }
	 
	 private void createDummyActivity()
	 {
		 assertFalse(solo.searchText(TS(R.string.Testaktivitaet)));
		 solo.clickOnButton(createButton);
		 solo.enterText(0, TS(R.string.Testaktivitaet));
		 solo.enterText(1, TS(R.string.TestaktBeschr));
	 }
	 
	 private String TS(int id)
	 {
		 return getInstrumentation().getContext().getString(id);
	 }
	 
	   @Override    
	public void tearDown() throws Exception 
	{         
		solo.waitForDialogToClose(5000);
	    solo.finishOpenedActivities();   
	}
	   
}
