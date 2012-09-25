package com.doogetha.client.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.doogetha.client.android.rest.EventsAccessor;
import com.doogetha.client.util.SlideActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.EventVo;
import de.potpiejimmy.util.AsyncUITask;

public class WizardNewEventActivity extends SlideActivity implements OnClickListener {

	private Button editbasebutton = null;
	private Button editdatetimebutton = null;
	private Button notnowdatetimebutton = null;
	private Button editparticipantsbutton = null;
	private Button editsurveysbutton = null;
	private Button notnowsurveysbutton = null;
	private Button donebutton = null;
	private ViewFlipper viewflipper = null;
	
	private EventVo event = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.wizard_newevent);
        
		viewflipper = (ViewFlipper) findViewById(R.id.viewFlipper);
    	
		editbasebutton = (Button) findViewById(R.id.editbasebutton);
		editdatetimebutton = (Button) findViewById(R.id.editdatetimebutton);
		notnowdatetimebutton = (Button) findViewById(R.id.notnowdatetimebutton);
		editparticipantsbutton = (Button) findViewById(R.id.editparticipantsbutton);
		editsurveysbutton = (Button) findViewById(R.id.editsurveysbutton);
		notnowsurveysbutton = (Button) findViewById(R.id.notnowsurveysbutton);
		donebutton = (Button) findViewById(R.id.donebutton);

		editbasebutton.setOnClickListener(this);
		editdatetimebutton.setOnClickListener(this);
		notnowdatetimebutton.setOnClickListener(this);
		editparticipantsbutton.setOnClickListener(this);
		editsurveysbutton.setOnClickListener(this);
		notnowsurveysbutton.setOnClickListener(this);
		donebutton.setOnClickListener(this);
    	
    	this.event = Utils.getApp(this).newEvent();
    }
    
    public void onBackPressed()
    {
    	if (viewflipper.getDisplayedChild() == 0)
    		finishCancel();
    	else
    		showPreviousPage();
    }
    
    protected void showPreviousPage()
    {
    	viewflipper.setInAnimation(getApplicationContext(), R.anim.slide_in_left);
    	viewflipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_right);
    	viewflipper.showPrevious();
    }
    
    protected void showNextPage()
    {
    	viewflipper.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
    	viewflipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_left);
    	viewflipper.showNext();
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
    
    protected void startEditActivity(Class<? extends Activity> clazz)
    {
    	Intent intent = new Intent(getApplicationContext(), clazz);
    	intent.putExtra("event", event);
		startActivityForResult(intent, 0);
    }
    
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        super.onActivityResult(reqCode, resultCode, data);
    	if (resultCode == RESULT_OK) {
    		this.event = (EventVo)data.getExtras().get("event");
    		showNextPage();
    	}
    }

    protected void saveAndExit()
    {
		new Inserter().go("Speichern...");
    }
    
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.editbasebutton:
				startEditActivity(EventEditBaseActivity.class);
				break;
			case R.id.editdatetimebutton:
				startEditActivity(EventEditDateTimeActivity.class);
				break;
			case R.id.notnowdatetimebutton:
				event.setEventtime(null);
				showNextPage();
				break;
			case R.id.editparticipantsbutton:
				startEditActivity(ParticipantsActivity.class);
				break;
			case R.id.editsurveysbutton:
				startEditActivity(EventSurveysActivity.class);
				break;
			case R.id.notnowsurveysbutton:
				event.setSurveys(null);
				showNextPage();
				break;
			case R.id.donebutton:
				saveAndExit();
				break;
		}
	}

	protected class Inserter extends AsyncUITask<String>
	{
		public Inserter() { super(WizardNewEventActivity.this); }
		
		public String doTask() throws Throwable
		{
    		EventsAccessor ea = Utils.getApp(WizardNewEventActivity.this).getEventsAccessor();
			ea.insertItem(event);
    		return getString(R.string.event_successfully_created);
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
}
