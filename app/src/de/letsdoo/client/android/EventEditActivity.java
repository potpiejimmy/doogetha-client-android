package de.letsdoo.client.android;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import de.letsdoo.client.android.rest.EventsAccessor;
import de.letsdoo.client.entity.Event;
import de.letsdoo.client.entity.User;
import de.letsdoo.client.util.ContactsUtils;
import de.letsdoo.client.util.Utils;
import de.potpiejimmy.util.AsyncUITask;

public class EventEditActivity extends Activity implements OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	
	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;
	
	private Event event = null;

	private EditText activityname = null;
	private EditText activitydescription = null;
	private ImageButton editdatetime = null;
	private ImageButton editparticipants = null;
	private TextView participantssummary = null;
	private TextView activitytitle = null;
	private TextView datetimelabel = null;
	
	private boolean myOwn = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.eventedit);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	this.activityname = (EditText) findViewById(R.id.activityname);
    	this.activitydescription = (EditText) findViewById(R.id.activitydescription);
    	this.editdatetime = (ImageButton) findViewById(R.id.editdatetime);
    	this.editparticipants = (ImageButton) findViewById(R.id.editparticipants);
    	this.participantssummary = (TextView) findViewById(R.id.participantssummary);
    	this.activitytitle = (TextView) findViewById(R.id.activitytitle);
    	this.datetimelabel = (TextView) findViewById(R.id.datetimelabel);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	editparticipants.setOnClickListener(this);
    	editdatetime.setOnClickListener(this);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().get("event") != null)
        	event = (Event)getIntent().getExtras().get("event");
        else {
        	event = new Event("");
        	User myself = new User();
        	myself.setEmail(Utils.getApp(this).getEmail());
        	myself.setState(1); /* confirmed */
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
    
    protected void editDatetime()
    {
    	showDialog(DATE_DIALOG_ID);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar cal = Calendar.getInstance();
    	if (event.getEventtime() != null)
    		cal.setTimeInMillis(event.getEventtime());
    	
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        }
        return null;
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
    	datetimelabel.setText(event.getEventtime()==null ? "Nicht festgelegt" : Utils.formatDateTime(event.getEventtime()));
    }
    
	protected class Updater extends AsyncUITask<String>
	{
		public Updater() { super(EventEditActivity.this); }
		
		public String doTask()
		{
	    	try{
	    		EventsAccessor ea = Utils.getApp(EventEditActivity.this).getEventsAccessor();
	    		if (event.getId() != null)
	    			ea.updateItem(event.getId(), event);
	    		else
	    			ea.insertItem(event);
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
		case R.id.editdatetime:
			editDatetime();
			break;
		}
	}

	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
    	if (event.getEventtime() != null) {
    		// keep time setting if we already have one
    		cal.setTimeInMillis(event.getEventtime());
    	}
    	else {
    		cal.set(Calendar.HOUR_OF_DAY, 0);
    		cal.set(Calendar.MINUTE, 0);
    	}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		this.event.setEventtime(cal.getTimeInMillis());
		updateUI();
		showDialog(TIME_DIALOG_ID);
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(event.getEventtime());
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		this.event.setEventtime(cal.getTimeInMillis());
		updateUI();
	}
	
}
