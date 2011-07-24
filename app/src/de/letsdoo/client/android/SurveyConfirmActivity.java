package de.letsdoo.client.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import de.letsdoo.client.util.ContactsUtils;
import de.letsdoo.client.util.Utils;
import de.letsdoo.client.util.VerticalLabelView;
import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.SurveyItemUserVo;
import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;
import de.letsdoo.server.vo.UserVo;
import de.potpiejimmy.util.AsyncUITask;

public class SurveyConfirmActivity extends Activity implements OnClickListener {

	private EventVo event = null;

	private int currentIndex = 0;
	
	private ImageButton nextbutton = null;
	private ImageButton previousbutton = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.surveyconfirm);
    	
    	this.event = (EventVo)getIntent().getExtras().get("event");
    	
		TextView eventdatetime = (TextView) findViewById(R.id.eventconfirmdatetime);
		eventdatetime.setText(event.getEventtime() != null ? Utils.formatDateTime(event.getEventtime()) : "");
		TextView eventconfirmtitle = (TextView) findViewById(R.id.eventconfirmtitle);
		eventconfirmtitle.setText(event.getName());
		TextView eventconfirmdescription = (TextView) findViewById(R.id.eventconfirmdescription);
		eventconfirmdescription.setText(event.getDescription());
		
		previousbutton = (ImageButton) findViewById(R.id.previousbutton);
		nextbutton = (ImageButton) findViewById(R.id.nextbutton);
		previousbutton.setOnClickListener(this);
		nextbutton.setOnClickListener(this);

		TextView activityconfirmtitle = (TextView) findViewById(R.id.activityconfirmtitle);
        activityconfirmtitle.setText(Utils.getActivityTitle(this, event));
    	
        for (SurveyVo survey : event.getSurveys())
        	addSurveyView(survey);
        
        showSurvey(0);
    }
    
    protected void showSurvey(int index) {
    	((TextView)findViewById(R.id.surveyname)).setText(event.getSurveys()[index].getName());
    	ViewFlipper flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
    	
    	if (index > currentIndex)
    		for (int i=0; i<(index-currentIndex); i++)
    			flipper.showNext();
    	else if (index < currentIndex)
    		for (int i=0; i<(currentIndex-index); i++)
    			flipper.showPrevious();
    	
    	currentIndex = index;
    	updateUI();
    }
    
    protected void updateUI() {
    	nextbutton.setEnabled(event.getSurveys().length > currentIndex+1);
    	previousbutton.setEnabled(currentIndex > 0);
    }
    
    protected void addSurveyView(SurveyVo survey) {
    	ViewFlipper flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
    	
    	View surveyconfirmview = getLayoutInflater().inflate(R.layout.surveyconfirmtable, null);
    	
    	((TextView)surveyconfirmview.findViewById(R.id.surveydescription)).setText(survey.getDescription());
    	TableLayout table = (TableLayout)surveyconfirmview.findViewById(R.id.surveyconfirmtable);
    	
    	TableRow.LayoutParams tableParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
    	
    	// add header
    	TableRow row = new TableRow(this);
    	row.addView(new TextView(this), tableParams);
    	row.addView(verticalSeparator(), tableParams);
    	row.setLayoutParams(tableParams);
    	
    	for (UserVo user : event.getUsers()) {
    		ContactsUtils.fillUserInfo(this, user);
    		row.addView(tableTextView(ContactsUtils.userDisplayName(this, user), true), tableParams);
        	row.addView(verticalSeparator(), tableParams);
    	}
    	
    	table.addView(row, tableParams);
    	table.addView(horizontalSeparator(), tableParams);
    	
    	// add survey items:
    	if (survey.getSurveyItems() != null) {
	    	for (SurveyItemVo item : survey.getSurveyItems()) {
	    		row = new TableRow(this);
	        	row.setLayoutParams(tableParams);
	    		row.addView(tableTextView(item.getName(), false), tableParams);
	        	row.addView(verticalSeparator(), tableParams);
	    		
	    		for (UserVo user : event.getUsers()) {
	    			ImageView iv = new ImageView(this);
	    			setImageForUserStatus(iv, item, user);
	    			row.addView(iv, tableParams);
	    	    	row.addView(verticalSeparator(), tableParams);
	    		}
	        	table.addView(row, tableParams);
	        	table.addView(horizontalSeparator(), tableParams);
	    	}
    	}
    	
    	flipper.addView(surveyconfirmview);
    }
    
    protected void setImageForUserStatus(ImageView view, SurveyItemVo item, UserVo user) {
    	// find user status:
		view.setImageResource(android.R.drawable.presence_offline);
		view.setPadding(20,20,20,20);
    	if (item.getConfirmations() != null) {
    		for (SurveyItemUserVo confirmation : item.getConfirmations()) {
    			if (confirmation.getUserId() == user.getId()) {
    				switch (confirmation.getState()) {
    				case 0:
    		    		view.setImageResource(android.R.drawable.presence_offline);
    		    		break;
    				case 1:
    		    		view.setImageResource(android.R.drawable.presence_online);
    		    		break;
    				case 2:
    		    		view.setImageResource(android.R.drawable.presence_busy);
    		    		break;
    				}
    			}
    		}
    	}
    }
    
    protected View tableTextView(String text, boolean vertical) {
    	if (vertical) {
    		VerticalLabelView v = new VerticalLabelView(this);
    		v.setText(text);
    		return v;
    	} else {
			TextView v = new TextView(this);
			v.setText(text);
			v.setGravity(Gravity.CENTER_VERTICAL);
			v.setPadding(5,5,5,5);
			v.setTextColor(Color.BLACK);
			v.setTextSize(10f);
			return v;
    	}
    }

    protected View verticalSeparator() {
    	return getLayoutInflater().inflate(R.layout.vertical_separator, null).findViewById(R.id.vertical_separator);
    }
    
    protected View horizontalSeparator() {
    	return getLayoutInflater().inflate(R.layout.horizontal_separator, null).findViewById(R.id.horizontal_separator);
    }
    
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.nextbutton:
				showSurvey(currentIndex+1);
				break;
			case R.id.previousbutton:
				showSurvey(currentIndex-1);
				break;
		}
	}
	
	protected void confirm(int state) {
		new Confirmer(state).go("Speichern...");
	}
	
    protected void finishOk()
    {
    	setResult(RESULT_OK);
    	finish();
    }
    
	protected class Confirmer extends AsyncUITask<String>
	{
		private int state = 0;
		
		public Confirmer(int state) {
			super(SurveyConfirmActivity.this);
			this.state = state;
		}
		
		public String doTask() throws Throwable
		{
			//EventsAccessor ea = Utils.getApp(SurveyConfirmActivity.this).getEventsAccessor();
			//ea.getWebRequest().setParam("confirm", ""+state);
			//ea.getItem(event.getId());
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
}
