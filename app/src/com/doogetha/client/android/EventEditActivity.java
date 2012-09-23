package com.doogetha.client.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doogetha.client.android.rest.EventsAccessor;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.EventVo;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class EventEditActivity extends AbstractEventEditDateTimeActivity implements OnClickListener {
	
	private EditText activityname = null;
	private EditText activitydescription = null;
	private ImageButton editparticipants = null;
	private ImageButton editsurveys = null;
	private TextView participantssummary = null;
	private TextView surveyssummary = null;
	private TextView activitytitle = null;
	
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
    	this.editsurveys = (ImageButton) findViewById(R.id.editsurveys);
    	this.participantssummary = (TextView) findViewById(R.id.participantssummary);
    	this.surveyssummary = (TextView) findViewById(R.id.surveyssummary);
    	this.activitytitle = (TextView) findViewById(R.id.activitytitle);
    	this.datetimelabel = (TextView) findViewById(R.id.datetimelabel);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	editparticipants.setOnClickListener(this);
    	editsurveys.setOnClickListener(this);
    	editdatetime.setOnClickListener(this);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().get("event") != null)
        	event = (EventVo)getIntent().getExtras().get("event");
        else {
        	event = Utils.getApp(this).newEvent();
        }
        
        activityname.setText(event.getName());
        activitydescription.setText(event.getDescription());
        activitytitle.setText(Utils.getActivityTitle(this, event));
        
        updateUI();
    }

    protected void addOrUpdate()
    {
    	event.setName(activityname.getText().toString());
    	event.setDescription(activitydescription.getText().toString());
		new Updater().go("Speichern...");
    }
    
    protected void validateAndSave() {
    	if (activityname.getText().toString().trim().length() == 0) {
    		DroidLib.toast(this, getString(R.string.please_enter_activity_name));
    		return;
    	}
    	addOrUpdate();
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
    
    protected void editSurveys()
    {
    	Intent intent = new Intent(getApplicationContext(), EventSurveysActivity.class);
    	intent.putExtra("event", event);
    	startActivityForResult(intent, 0);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK)
    	{
    		event.setUsers(((EventVo)data.getExtras().get("event")).getUsers());
    		event.setSurveys(((EventVo)data.getExtras().get("event")).getSurveys());
    		updateUI();
    	}
    }
    
    protected void updateUI() {
    	super.updateUI();
    	participantssummary.setText(event.getUsers().length + " Teilnehmer");
    	int snum = event.getSurveys() != null ? event.getSurveys().length : 0;
    	surveyssummary.setText((snum == 0 ? "Keine" : ""+snum) + (snum == 1 ? " Abstimmung" : " Abstimmungen"));
    }
    
	protected class Updater extends AsyncUITask<String>
	{
		public Updater() { super(EventEditActivity.this); }
		
		public String doTask() throws Throwable
		{
    		EventsAccessor ea = Utils.getApp(EventEditActivity.this).getEventsAccessor();
    		if (event.getId() != null)
    			ea.updateItem(event.getId(), event);
    		else
    			ea.insertItem(event);
    		return "Gespeichert";
		}
		
		public void doneOk(String result)
		{
    		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    		finishOk();
		}

		public void doneFail(Throwable throwable)
		{
    		Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	public void onClick(View view) {
		switch (view.getId())
		{
		case R.id.editok:
			validateAndSave();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		case R.id.editparticipants:
			editParticipants();
			break;
		case R.id.editsurveys:
			editSurveys();
			break;
		case R.id.editdatetime:
			editDatetime();
			break;
		}
	}
}
