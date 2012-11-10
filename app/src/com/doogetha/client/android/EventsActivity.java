package com.doogetha.client.android;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.doogetha.client.android.rest.EventsAccessor;
import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.SlideActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.EventsVo;
import de.letsdoo.server.vo.UserVo;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;
import de.potpiejimmy.util.PullRefreshableListView;
import de.potpiejimmy.util.PullRefreshableListView.OnRefreshListener;

public class EventsActivity extends SlideActivity implements OnItemClickListener, OnClickListener, OnRefreshListener {

	private final static int NUMBER_OF_SCREENS = 3;
	private final static int SCREEN_CURRENT_ACTIVITIES = 0;
	private final static int SCREEN_MY_ACTIVITIES = 1;
	//private final static int SCREEN_PUBLIC_ACTIVITIES = 2;
	private final static int SCREEN_SETTINGS = 2;
	
	private ArrayAdapter<EventVo> data = null;
	
	private Button newactivitybutton = null;
	private Button[] screenButtons = new Button[NUMBER_OF_SCREENS];
	
	private DataLoader dataLoader = null;
	
	private PullRefreshableListView currentEventsList = null;
	private PullRefreshableListView myEventsList = null;
	
	private int currentScreen = 0;
	
	protected int pendingEventToOpen = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        ((TextView)findViewById(R.id.versionlabel)).setText("Version " + Utils.getApp(this).getVersionName());

    	checkPendingEvent(getIntent());
    	
        this.currentEventsList = (PullRefreshableListView) findViewById(R.id.currenteventslist);
        this.myEventsList = (PullRefreshableListView) findViewById(R.id.myeventslist);
        
        newactivitybutton = (Button) findViewById(R.id.newactivitybutton);
        screenButtons[SCREEN_CURRENT_ACTIVITIES] = (Button) findViewById(R.id.currentactivitiesbutton);
        screenButtons[SCREEN_MY_ACTIVITIES] = (Button) findViewById(R.id.myactivitiesbutton);
//        screenButtons[SCREEN_PUBLIC_ACTIVITIES] = (Button) findViewById(R.id.publicactivitiesbutton);
        screenButtons[SCREEN_SETTINGS] = (Button) findViewById(R.id.settingsbutton);
        
        newactivitybutton.setOnClickListener(this);
        for (Button screenButton : screenButtons) screenButton.setOnClickListener(this);
        
    	this.data = new ArrayAdapter<EventVo>(this, R.layout.event_item) {
			@Override
			public View getView(int position, View convertView, ViewGroup viewGroup) {
				if (convertView == null) {
		            LayoutInflater inflater = (LayoutInflater) getContext()
		                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            convertView = inflater.inflate(R.layout.event_item, null);
		        }
				EventVo event = getItem(position);
				TextView displayName = (TextView) convertView.findViewById(R.id.eventitemname);
				displayName.setText(event.getName());
				TextView datetime = (TextView) convertView.findViewById(R.id.eventitemdatetime);
				if (event.getEventtime() != null) {
					datetime.setVisibility(View.VISIBLE);
					datetime.setText(Utils.formatDateTime(event.getEventtime()));
				} else {
					datetime.setVisibility(View.GONE);
				}
				TextView subInfo = (TextView) convertView.findViewById(R.id.eventsubinfo);
				String participants = ContactsUtils.participantNames(Utils.getApp(EventsActivity.this), event);
				if (participants != null) {
					subInfo.setText("mit " + participants);
				} else {
					subInfo.setText("keine Teilnehmer");
				}
				ImageView icon = (ImageView) convertView.findViewById(R.id.eventstateiconview);
				if (Utils.hasOpenSurveys(event)) {
					icon.setImageResource(R.drawable.question_mark);
				} else {
					UserVo myself = null;
					for (UserVo user : event.getUsers())
						if (user.getEmail().equalsIgnoreCase(Utils.getApp(EventsActivity.this).getEmail()))
							myself = user;
					if (myself != null) Utils.setIconForConfirmState(icon, myself);
					else icon.setImageDrawable(null);
				}
		        return convertView;
			}
    	};
    	
    	setupListView(currentEventsList);
    	setupListView(myEventsList);
    	
    	this.dataLoader = new DataLoader();
    	
    	showScreen(0, false);
    	updateUI();
    	
		refresh();
   }
    
    protected void showScreen(int index, boolean refresh) {
    	if (refresh) {
	    	if (currentScreen == index) {
	    		refresh = false;
	    		return;
	    	} else {
	    		data.clear();
	    	}
    	}
    	
    	screenButtons[currentScreen].setBackgroundResource(android.R.color.transparent);
    	
    	screenButtons[index].setBackgroundResource(R.color.doogetha_bgsel);
    	
    	ViewFlipper flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
    	
    	if (index > currentScreen)
    		for (int i=0; i<(index-currentScreen); i++)
    			flipper.showNext();
    	else if (index < currentScreen)
    		for (int i=0; i<(currentScreen-index); i++)
    			flipper.showPrevious();
    	
    	currentScreen = index;

    	if (refresh) refresh();
    }
    
    protected void setupListView(PullRefreshableListView listView) {
    	listView.setAdapter(data);
    	listView.setTextFilterEnabled(true);
    	listView.setOnItemClickListener(this);
    	listView.setOnRefreshListener(this);
    	listView.setUseScreenLockWhileRefreshing(true);
    	registerForContextMenu(listView);
    }
    
    protected void updateUI() {
    	boolean loggedIn = Utils.getApp(this).hasSession();
    	newactivitybutton.setEnabled(loggedIn);
    	if (!loggedIn) data.clear();
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.options, menu);
//        return true;
//    }
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//        case R.id.unregister:
//            unregister();
//            return true;
//        default:
//            return super.onOptionsItemSelected(item);
//        }
//    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      
      boolean myEvent = false;
      if (v == myEventsList) {
    	  // events in "my events list" are all mine:
    	  myEvent = true;
      } else {
    	  // check whether the selected event in "current events list" is my own:
    	  EventVo selectedEvent = data.getItem(((AdapterContextMenuInfo)menuInfo).position-1);
    	  myEvent = selectedEvent != null && selectedEvent.getOwner().getEmail().equalsIgnoreCase(Utils.getApp(this).getEmail());
      }
      
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(!myEvent ? R.menu.context_currentactivities : R.menu.context_myactivities, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
	      case R.id.showitem:
		  		confirmEvent(data.getItem(info.position-1));
		        return true;
	      case R.id.edititem:
		  		editEvent(data.getItem(info.position-1));
		        return true;
	      case R.id.deleteitem:
		  		deleteEvent(data.getItem(info.position-1));
		        return true;
	      default:
	    	    return super.onContextItemSelected(item);
      }
    }
    
    protected void deleteEvent(final EventVo event)
    {
		DroidLib.alert(this, getString(R.string.delete_event_confirm), getString(R.string.ok), getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		  		new Deleter(event).go(getString(R.string.process_delete));
			}
		});
    }
    
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.newactivitybutton:
				addEvent();
				break;
			case R.id.currentactivitiesbutton:
				showScreen(SCREEN_CURRENT_ACTIVITIES, true);
				break;
			case R.id.myactivitiesbutton:
				showScreen(SCREEN_MY_ACTIVITIES, true);
				break;
//			case R.id.publicactivitiesbutton:
//				showScreen(SCREEN_PUBLIC_ACTIVITIES, false);
//				break;
			case R.id.settingsbutton:
//				showScreen(SCREEN_SETTINGS, false);
				startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), 0);
				break;
		}
	}
	
    protected void refresh()
    {
    	refresh(false);
    }
    
    protected void refresh(boolean showDialog)
    {
    	if (!showDialog) {
    		if (!getCurrentListView().isRefreshing()) getCurrentListView().setRefreshing();
    	}
    	dataLoader.go(getString(R.string.loading), showDialog);
    	updateUI();
    }
    
    protected void unregister()
    {
    	Utils.getApp(this).unregister();
    	updateUI();
    	register();
    }
    
    protected void register()
    {
    	Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
    	startActivityForResult(intent, 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK)
    	{
    		//if (!Utils.getApp(this).hasSession()) startSession();
    		/*else*/ refresh();
    	} else if (resultCode == RESULT_FIRST_USER) {
    		// set when unregistered in settings view - quit:
    		finish();
    	}
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (position == 0) return; // click on header - do nothing
//		if (currentScreen == SCREEN_CURRENT_ACTIVITIES)
			confirmEvent(data.getItem(position-1));
//		else if (currentScreen == SCREEN_MY_ACTIVITIES)
//		    editEvent(data.getItem(position-1));
	}
	
    /**
     * Called by PullRefreshableListView if pulled
     */
	public void onRefresh() {
		refresh(false);
	}
	
	protected void checkPendingEvent(Intent intent) {
		if (intent == null || intent.getExtras() == null) {
			this.pendingEventToOpen = 0;
			return;
		}
		this.pendingEventToOpen = intent.getExtras().getInt("eventId", 0);
	}
	
	public void onNewIntent(Intent intent) {
    	checkPendingEvent(intent);
    	if (pendingEventToOpen > 0) {
    		showScreen(0, false); // flip to screen 0
    		refresh(); // force reload
    	}
	}
	
	protected void openPendingEvent() {
		EventVo event = findEventInCurrentList(this.pendingEventToOpen);
		this.pendingEventToOpen = 0;
		if (event != null) confirmEvent(event);
	}
	
	protected EventVo findEventInCurrentList(int eventId) {
		EventVo found = null;
		for (int i=0; i<data.getCount(); i++)
			if (data.getItem(i).getId() == eventId)
				found = data.getItem(i);
		return found;
	}

	protected void editEvent(EventVo event) {
    	Intent intent = new Intent(getApplicationContext(), EventEditActivity.class);
    	intent.putExtra("event", event);
    	startActivityForResult(intent, 0);
	}
	
	protected void confirmEvent(EventVo event) {
//		if (hasOpenSurveys(event)) {
//			startEventActivityClass(SurveyConfirmActivity.class, event);
//		} else {
			startEventActivityClass(EventConfirmActivity.class, event);
//		}
	}
	
	protected void startEventActivityClass(Class<? extends Activity> clazz, EventVo event) {
    	Intent intent = new Intent(getApplicationContext(), clazz);
    	intent.putExtra("event", event);
    	startActivityForResult(intent, 0);
	}
	
    protected void addEvent()
    {
    	Intent intent = new Intent(getApplicationContext(), WizardNewEventActivity.class);
    	startActivityForResult(intent, 0);
    }
    
    protected EventVo meFirst(EventVo e) {
    	int found = -1;
    	UserVo[] users = e.getUsers();
    	for (int i=0; i<users.length; i++) {
    		if (users[i].getEmail().equalsIgnoreCase(Utils.getApp(this).getEmail()))
    			{ found = i; break; }
    	}
    	if (found>0) {
    		UserVo me = users[found];
    		users[found] = users[0];
    		users[0] = me;
    	}
    	return e;
    }
    
    protected PullRefreshableListView getCurrentListView()
    {
    	if (currentScreen == SCREEN_CURRENT_ACTIVITIES)
			return this.currentEventsList;
		else if (currentScreen == SCREEN_MY_ACTIVITIES)
			return this.myEventsList;
    	return null;
    }
    
    protected void loadingDone()
    {
    	if (this.pendingEventToOpen > 0) {
    		// is there a pending event that should be opened?
    		openPendingEvent();
    	}
    	getCurrentListView().onRefreshComplete();
    }
    
	protected class DataLoader extends AsyncUITask<EventsVo>
	{
		public DataLoader() { super(EventsActivity.this); }
		
		public EventsVo doTask() throws Throwable
		{
			EventsAccessor ea = Utils.getApp(EventsActivity.this).getEventsAccessor();
			if (currentScreen == 1)
				ea.getWebRequest().setParam("mine", "true");
			else
				ea.getWebRequest().removeParam("mine");
			return ea.getItems();
		}
		
		public void doneOk(EventsVo result)
		{
			data.clear();
			Map<String,String> allMails = new HashMap<String,String>();
			if (result != null && result.getEvents() != null)
				for (EventVo e : result.getEvents()) {
					data.add(meFirst(e));
					for (UserVo user : e.getUsers())
						allMails.put(user.getEmail(), user.getEmail());
				}
			String[] knownAddresses = allMails.keySet().toArray(new String[allMails.size()]);
			Arrays.sort(knownAddresses);
			Utils.getApp(EventsActivity.this).setKnownAddresses(knownAddresses);
			
			loadingDone();
		}

		public void doneFail(Throwable throwable)
		{
    		Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
			
			loadingDone();
		}
	}
	
	protected class Deleter extends AsyncUITask<String>
	{
		private EventVo event = null;
		
		public Deleter(EventVo event)
		{
			super(EventsActivity.this);
			this.event = event;
		}
		
		public String doTask() throws Throwable
		{
    		Utils.getApp(EventsActivity.this).getEventsAccessor().deleteItem(event.getId());
    		return getString(R.string.deleted);
		}
		
		public void doneOk(String result)
		{
			data.remove(event);
    		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
		}

		public void doneFail(Throwable throwable) 
		{
    		Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
		}
	}
}