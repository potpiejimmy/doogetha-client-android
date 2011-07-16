package de.letsdoo.client.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import de.letsdoo.client.entity.Event;
import de.letsdoo.client.entity.User;
import de.letsdoo.client.util.ContactsUtils;
import de.letsdoo.client.util.Utils;
import de.potpiejimmy.util.AsyncUITask;

public class EventEditActivity extends Activity implements OnClickListener {
	
	private EditText activityname = null;
	private EditText activitydescription = null;
	private Event event = null;
	private ImageButton editparticipants = null;
	private TextView participantssummary = null;
	private TextView activitytitle = null;
	
	private boolean myOwn = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.eventedit);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	this.activityname = (EditText) findViewById(R.id.activityname);
    	this.activitydescription = (EditText) findViewById(R.id.activitydescription);
    	this.editparticipants = (ImageButton) findViewById(R.id.editparticipants);
    	this.participantssummary = (TextView) findViewById(R.id.participantssummary);
    	this.activitytitle = (TextView) findViewById(R.id.activitytitle);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	editparticipants.setOnClickListener(this);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().get("event") != null)
        	event = (Event)getIntent().getExtras().get("event");
        else {
        	event = new Event("");
        	User myself = new User();
        	myself.setEmail(Utils.getApp(this).getEmail());
        	event.setUsers(new User[] {myself});
        	event.setOwner(myself);
        }
        
        this.myOwn = Utils.getApp(this).getEmail().equalsIgnoreCase(event.getOwner().getEmail());

        if (!myOwn) {
            // resolve owner name
        	ContactsUtils.fillUserInfo(this, event.getOwner());
        	
        	buttonok.setEnabled(false);  // XXX
        }
        
        activityname.setText(event.getName());
        activitydescription.setText(event.getDescription());
        activitytitle.setText(myOwn ? "Meine Aktivität" : "Aktivität von " + ContactsUtils.userDisplayName(this, event.getOwner()));
        
        updateUI();
    }

    protected void addOrUpdate()
    {
    	event.setName(activityname.getText().toString());
    	event.setDescription(activitydescription.getText().toString());
		new Updater().go("Speichern...");
    }
    
    protected void finishOk()
    {
    	setResult(RESULT_OK);
    	finish();
    }
    
    protected void finishCancel()
    {
    	setResult(RESULT_CANCELED);
    	finish();
    }
    
    protected void editParticipants()
    {
    	Intent intent = new Intent(getApplicationContext(), ParticipantsActivity.class);
    	intent.putExtra("event", event);
    	startActivityForResult(intent, 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK)
    	{
    		event.setUsers(((Event)data.getExtras().get("event")).getUsers());
    		updateUI();
    	}
    }
    
    protected void updateUI() {
    	participantssummary.setText(event.getUsers().length + " Teilnehmer");
    }
    
	protected class Updater extends AsyncUITask<String>
	{
		public Updater() { super(EventEditActivity.this); }
		
		public String doTask()
		{
	    	try{
	    		if (event.getId() != null)
	    			Utils.getApp(EventEditActivity.this).getEventsAccessor().updateItem(event.getId(), event);
	    		else
	    			Utils.getApp(EventEditActivity.this).getEventsAccessor().insertItem(event);
	    		return "Gespeichert";
	    	} catch (Exception ex) {
	    		ex.printStackTrace();
	    		return ex.toString();
	    	}
		}
		
		public void done(String result)
		{
    		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    		finishOk();
		}
	}

	public void onClick(View view) {
		switch (view.getId())
		{
		case R.id.editok:
			addOrUpdate();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		case R.id.editparticipants:
			editParticipants();
			break;
		}
	}
	
}
