package de.letsdoo.client.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import de.letsdoo.client.android.rest.SurveysAccessor;
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
	
	private List<List<ImageView>> selectionViews = new ArrayList<List<ImageView>>();
	
	private UserVo myself = null;
	
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

		Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
		
		previousbutton = (ImageButton) findViewById(R.id.previousbutton);
		nextbutton = (ImageButton) findViewById(R.id.nextbutton);
		previousbutton.setOnClickListener(this);
		nextbutton.setOnClickListener(this);

		TextView activityconfirmtitle = (TextView) findViewById(R.id.activityconfirmtitle);
        activityconfirmtitle.setText(Utils.getActivityTitle(this, event));
    	
        for (UserVo user : event.getUsers())
        	if (Utils.getApp(this).getEmail().equalsIgnoreCase(user.getEmail()))
        		myself = user;
        
        for (SurveyVo survey : event.getSurveys())
        	addSurveyView(survey);
        
        showSurvey(0);
    }
    
    protected void showSurvey(int index) {
    	((TextView)findViewById(R.id.surveyname)).setText(event.getSurveys()[index].getName() + " (" + (index+1) + "/" + event.getSurveys().length + ")");
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
    	table.addView(horizontalSeparator(), tableParams);
    	
    	TableRow row = new TableRow(this);
    	row.addView(verticalSeparator(), tableParams);
    	TextView emptyCorner = new TextView(this);
		emptyCorner.setBackgroundColor(Color.LTGRAY);
    	row.addView(emptyCorner, tableParams);
    	row.addView(verticalSeparator(), tableParams);
    	row.setLayoutParams(tableParams);
    	
    	for (int i=0; i<event.getUsers().length; i++) {
    		UserVo user = event.getUsers()[i];
    		ContactsUtils.fillUserInfo(this, user);
    		row.addView(tableTextView(ContactsUtils.userDisplayName(this, user), true, i==0), tableParams);
        	row.addView(verticalSeparator(), tableParams);
    	}
    	
    	table.addView(row, tableParams);
    	table.addView(horizontalSeparator(), tableParams);
    	
    	// add survey items:
    	List<ImageView> currentSelectionViews = new ArrayList<ImageView>();
    	if (survey.getSurveyItems() != null) {
	    	for (SurveyItemVo item : survey.getSurveyItems()) {
	    		row = new TableRow(this);
	        	row.addView(verticalSeparator(), tableParams);
	        	row.setLayoutParams(tableParams);
	    		row.addView(tableTextView(item.getName(), false, false), tableParams);
	        	row.addView(verticalSeparator(), tableParams);
	    		
	    		for (int i=0; i<event.getUsers().length; i++) {
	    			
	    			UserVo user = event.getUsers()[i];
	    			ImageView iv = new ImageView(this);
	    			setImageForUserStatus(iv, item, user, i==0);
	    			row.addView(iv, tableParams);
	    	    	row.addView(verticalSeparator(), tableParams);

	    	    	if (i==0) {
	    				currentSelectionViews.add(iv);
	    				iv.setOnClickListener(this);
	    			}
	    		}
	        	table.addView(row, tableParams);
	        	table.addView(horizontalSeparator(), tableParams);
	    	}
    	}
		this.selectionViews.add(currentSelectionViews);
    	
    	flipper.addView(surveyconfirmview);
    }
    
    protected void setImageForUserStatus(ImageView view, SurveyItemVo item, UserVo user, boolean myColumn) {
    	// find user status:
		view.setImageResource(R.drawable.survey_neutral);
		int padding = myColumn ? 20 : 5;
		view.setPadding(padding,padding,padding,padding);
		if (!myColumn) view.setBackgroundColor(Color.LTGRAY);
    	if (item.getConfirmations() != null) {
    		for (SurveyItemUserVo confirmation : item.getConfirmations()) {
    			if (confirmation.getUserId() == user.getId()) {
    				switch (confirmation.getState()) {
    				case 0:
    		    		view.setImageResource(R.drawable.survey_neutral);
    		    		break;
    				case 1:
    		    		view.setImageResource(R.drawable.survey_confirm);
    		    		break;
    				case 2:
    		    		view.setImageResource(R.drawable.survey_deny);
    		    		break;
    				}
    			}
    		}
    	}
    }
    
    protected String trimParticipantName(String name) {
    	final int MAXLEN = 20;
    	name = name.trim();
    	if (name.length() <= MAXLEN) return name;
    	return name.substring(0, MAXLEN) + "...";
    }
    
    protected View tableTextView(String text, boolean vertical, boolean myColumn) {
    	if (vertical) {
    		VerticalLabelView v = new VerticalLabelView(this);
    		if (!myColumn) v.setBackgroundColor(Color.LTGRAY);
    		v.setText(trimParticipantName(text));
    		return v;
    	} else {
			TextView v = new TextView(this);
			if (!myColumn) v.setBackgroundColor(Color.LTGRAY);
			v.setWidth(120);
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
		if (v.getId() < 0) {
			List<ImageView> currentSelectionViews = this.selectionViews.get(currentIndex);
			for (int i=0; i<currentSelectionViews.size(); i++) {
				if (currentSelectionViews.get(i) == v) {
					SurveyItemVo item = event.getSurveys()[currentIndex].getSurveyItems()[i];
					SurveyItemUserVo itemUser = null;
					if (item.getConfirmations() != null) {
						for (SurveyItemUserVo user : item.getConfirmations())
							if (user.getUserId() == myself.getId())
								itemUser = user;
					}
					if (itemUser == null) {
						itemUser = new SurveyItemUserVo();
						itemUser.setUserId(myself.getId());
					}
					itemUser.setState((itemUser.getState()+1) % 3);
					if (item.getConfirmations() == null) {
						item.setConfirmations(new SurveyItemUserVo[] {itemUser});
					} else {
						SurveyItemUserVo[] newArray = new SurveyItemUserVo[item.getConfirmations().length+1];
						System.arraycopy(item.getConfirmations(), 0, newArray, 0, item.getConfirmations().length);
						newArray[newArray.length-1] = itemUser;
						item.setConfirmations(newArray);
					}
					setImageForUserStatus((ImageView)v, item, myself, true);
				}
			}
		}
		switch (v.getId())
		{
			case R.id.nextbutton:
				showSurvey(currentIndex+1);
				break;
			case R.id.previousbutton:
				showSurvey(currentIndex-1);
				break;
			case R.id.editok:
				confirm();
				break;
			case R.id.editcancel:
				finishCancel();
				break;
		}
	}
	
	protected void confirm() {
		new Confirmer().go("Speichern...");
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
    
	protected class Confirmer extends AsyncUITask<String>
	{
		public Confirmer() {
			super(SurveyConfirmActivity.this);
		}
		
		public String doTask() throws Throwable
		{
			SurveysAccessor sa = Utils.getApp(SurveyConfirmActivity.this).getSurveysAccessor();
			for (SurveyVo survey : event.getSurveys())
				sa.updateItem(survey.getId(), survey);
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
