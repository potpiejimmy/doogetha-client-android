package de.letsdoo.client.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import de.letsdoo.client.entity.Event;
import de.letsdoo.client.entity.Events;
import de.potpiejimmy.util.AsyncUITask;

public class EventsActivity extends ListActivity implements OnItemClickListener {

	private ArrayAdapter<Event> data = null;
	
	private DataLoader dataLoader = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        
    	this.data = new ArrayAdapter<Event>(this, R.layout.event_item);
    	this.setListAdapter(data);
    	getListView().setTextFilterEnabled(true);
    	getListView().setOnItemClickListener(this);
    	registerForContextMenu(getListView());
    	
    	this.dataLoader = new DataLoader();
    	
    	if (((Letsdoo)getApplication()).getPreferences().getString("authtoken", null) == null) {
    		login();
    	} else {
			refresh();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
//      MenuInflater inflater = getMenuInflater();
//      inflater.inflate(R.menu.context, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
//      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
//      case R.id.delete:
//  		new Deleter(data.getItem(info.position)).go(getString(R.string.deletingitem));
//        return true;
      default:
        return super.onContextItemSelected(item);
      }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
//        case R.id.quit:
//            finish();
//            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    protected void refresh()
    {
    	dataLoader.go(getString(R.string.loading));
    }
    
    protected void login()
    {
    	Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    	startActivityForResult(intent, 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK)
    		refresh();
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
//    	Intent intent = new Intent(getApplicationContext(), EditAct.class);
//    	intent.putExtra("msg", data.getItem(position));
//    	startActivityForResult(intent, 0);
	}
	
	protected class DataLoader extends AsyncUITask<Events>
	{
		public DataLoader() { super(EventsActivity.this); }
		
		public Events doTask()
		{
	    	Events msgs = null;
	    	try{
	    		msgs = ((Letsdoo)getApplication()).getRestAccessor().getItems();
	    	} catch (Exception ex) {
	    		ex.printStackTrace();
	    		msgs = new Events();
	    		msgs.setEvents(new Event[] {new Event(ex.toString())});
	    	}
	    	return msgs;
		}
		
		public void done(Events result)
		{
			data.clear();
			if (result != null && result.getEvents() != null)
				for (Event e : result.getEvents()) data.add(e);
		}
	}
}