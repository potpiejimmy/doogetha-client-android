package de.letsdoo.client.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import de.letsdoo.client.entity.EventVo;
import de.letsdoo.client.entity.EventsVo;
import de.letsdoo.client.entity.UserVo;
import de.letsdoo.client.util.Utils;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class EventsActivity extends ListActivity implements OnItemClickListener, OnClickListener {

	private ArrayAdapter<EventVo> data = null;
	
	private Button newactivitybutton = null;
	
	private DataLoader dataLoader = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        ((TextView)findViewById(R.id.versionlabel)).setText("Version " + Utils.getApp(this).getVersionName());
        
        newactivitybutton = (Button) findViewById(R.id.newactivitybutton);
        
        newactivitybutton.setOnClickListener(this);
        
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
					datetime.setText(Utils.formatDateTime(event.getEventtime()));
				} else {
					datetime.setText("");
				}
				ImageView icon = (ImageView) convertView.findViewById(R.id.eventstateiconview);
				UserVo myself = null;
				for (UserVo user : event.getUsers())
					if (user.getEmail().equalsIgnoreCase(Utils.getApp(EventsActivity.this).getEmail()))
						myself = user;
				if (myself != null) Utils.setIconForConfirmState(icon, myself);
				else icon.setImageDrawable(null);
		        return convertView;
			}
    	};
    	
    	this.setListAdapter(data);
    	getListView().setTextFilterEnabled(true);
    	getListView().setOnItemClickListener(this);
    	registerForContextMenu(getListView());
    	
    	this.dataLoader = new DataLoader();
    	
    	updateUI();
    	
    	if (!Utils.getApp(this).isRegistered()) {
    		register();
    	} else {
			startSession();
    	}
    	
    	checkVersion();
    }
    
    protected void updateUI() {
    	boolean loggedIn = Utils.getApp(this).hasSession();
    	newactivitybutton.setEnabled(loggedIn);
    	if (!loggedIn) data.clear();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.unregister:
            unregister();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.context, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
	      case R.id.edititem:
		  		editEvent(data.getItem(info.position));
		        return true;
	      case R.id.deleteitem:
	  		new Deleter(data.getItem(info.position)).go("Lšschen...");
	        return true;
	      default:
	        return super.onContextItemSelected(item);
      }
    }
    
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.newactivitybutton:
				addEvent();
				break;
		}
	}
	
    protected void refresh()
    {
    	dataLoader.go(getString(R.string.loading));
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
    	Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    	startActivityForResult(intent, 0);
    }
    
    protected void startSession()
    {
    	new SessionLoginTask(Utils.getApp(this).getAuthtoken()).go("Starting session...");
    }
    
    protected void sessionCreateSuccess(String sessionkey)
    {
    	Utils.getApp(this).newSession(sessionkey);
    	refresh();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK)
    	{
    		if (!Utils.getApp(this).hasSession()) startSession();
    		else refresh();
    	}
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		//editEvent(data.getItem(position));
		confirmEvent(data.getItem(position));
	}
	
	protected void editEvent(EventVo event) {
    	Intent intent = new Intent(getApplicationContext(), EventEditActivity.class);
    	intent.putExtra("event", event);
    	startActivityForResult(intent, 0);
	}
	
	protected void confirmEvent(EventVo event) {
    	Intent intent = new Intent(getApplicationContext(), EventConfirmActivity.class);
    	intent.putExtra("event", event);
    	startActivityForResult(intent, 0);
	}
	
    protected void addEvent()
    {
    	Intent intent = new Intent(getApplicationContext(), EventEditActivity.class);
    	startActivityForResult(intent, 0);
    }
    
    protected void checkVersion()
    {
    	new VersionCheckTask().go("Checking version...");
    }
    
    protected void newerVersionExists()
    {
    	DroidLib.alert(this, "A newer version of the application exists.", "Download now", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				DroidLib.invokeBrowser(EventsActivity.this, Letsdoo.DOWNLOADURL);
			}
    	});
    }
	
	protected class DataLoader extends AsyncUITask<EventsVo>
	{
		public DataLoader() { super(EventsActivity.this); }
		
		public EventsVo doTask() throws Throwable
		{
			return Utils.getApp(EventsActivity.this).getEventsAccessor().getItems();
		}
		
		public void doneOk(EventsVo result)
		{
			data.clear();
			if (result != null && result.getEvents() != null)
				for (EventVo e : result.getEvents()) data.add(e);
		}

		public void doneFail(Throwable throwable)
		{
    		EventsVo msgs = new EventsVo();
    		msgs.setEvents(new EventVo[] {new EventVo(throwable.toString())});
    		doneOk(msgs); // XXX
		}
	}
	
	protected class SessionLoginTask extends AsyncUITask<String>
	{
		private String authkey = null;
		
		public SessionLoginTask(String authkey) 
		{
			super(EventsActivity.this);
			this.authkey = authkey;
		}
		
		public String doTask() throws Throwable
		{
			return Utils.getApp(EventsActivity.this).getLoginAccessor().insertItemWithResult(authkey);
		}
		
		public void doneOk(String sessioncredentials)
		{
	    	String userid = sessioncredentials.substring(0, sessioncredentials.indexOf(":"));
	    	String password = sessioncredentials.substring(sessioncredentials.indexOf(":")+1);
	    	String sessionkey = Base64.encodeToString((userid + ":" + password).getBytes(), Base64.NO_WRAP);
	    	sessionCreateSuccess(sessionkey);
		}
		
		public void doneFail(Throwable throwable) 
		{
			DroidLib.alert(EventsActivity.this, "Sorry, session login failed using your current credentials.");
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
    		return "Gelšscht";
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
	
	protected class VersionCheckTask extends AsyncUITask<String>
	{
		public VersionCheckTask() 
		{
			super(EventsActivity.this);
		}
		
		public String doTask() throws Throwable
		{
			return Utils.getApp(EventsActivity.this).getVersionAccessor().getItems();
		}
		
		public void doneOk(String result)
		{
			if (result == null) return; // could not fetch current version, ignore
			try {
				int currentVersion = Integer.parseInt(result);
				if (currentVersion > Utils.getApp(EventsActivity.this).getVersionCode())
					newerVersionExists();
			} catch (NumberFormatException nfe) {
				// no valid version value received, just ignore and do nothing
			}
		}
		
		public void doneFail(Throwable throwable) 
		{
    		// just ignore
		}
	}	
}