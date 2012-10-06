package com.doogetha.client.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.doogetha.client.android.rest.SurveysAccessor;
import com.doogetha.client.util.CommentsPreviewer;
import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.Utils;
import com.doogetha.client.util.VerticalLabelView;

import de.letsdoo.server.vo.EventCommentsVo;
import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.SurveyItemUserVo;
import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;
import de.letsdoo.server.vo.UserVo;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class SurveyConfirmActivity extends AbstractSurveyEditActivity implements OnClickListener {

	private EventVo event = null;
	private SurveyVo survey = null;
	
	private List<ImageView> confirmViews = new ArrayList<ImageView>();
	private List<View> closeViews = new ArrayList<View>();
	
	private UserVo myself = null;
	
	private boolean myOwn = false;
	private boolean dirty = false;
	
	protected CommentsPreviewer commentsPreviewer = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.surveyconfirm);
    	
    	this.event = (EventVo)getIntent().getExtras().get("event");
		this.survey = (SurveyVo)getIntent().getExtras().get("survey");
    	
		Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);

        for (UserVo user : event.getUsers())
        	if (Utils.getApp(this).getEmail().equalsIgnoreCase(user.getEmail()))
        		myself = user;
        
        this.myOwn = event.getOwner().getId().equals(myself.getId());
        
		this.commentsPreviewer = new CommentsPreviewer(this, event);

		((TextView)findViewById(R.id.surveyname)).setText(survey.getName());
        recreateSurveyView();
        
        if (survey.getState() == 1) /* closed */
        	findViewById(R.id.surveyconfirmbuttonpanel).setVisibility(View.GONE);
    }
    
    protected void recreateSurveyView() {
    	this.confirmViews.clear();
    	this.closeViews.clear();
    	addSurveyView(survey);
    }
    
    protected void addSurveyView(SurveyVo survey) {
    	View surveyconfirmview = getLayoutInflater().inflate(R.layout.surveyconfirmtable, null);
    	
    	((TextView)surveyconfirmview.findViewById(R.id.surveydescription)).setText(survey.getDescription());
    	((TextView)surveyconfirmview.findViewById(R.id.surveyclosedlabel)).setVisibility(survey.getState() == 1 ? View.VISIBLE : View.GONE);
    	TableLayout table = (TableLayout)surveyconfirmview.findViewById(R.id.surveyconfirmtable);
    	
    	TableRow.LayoutParams tableParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
    	
    	// add header
    	//table.addView(horizontalSeparator(), tableParams);
    	
    	TableRow row = new TableRow(this);
    	//row.addView(verticalSeparator(), tableParams);
    	VerticalLabelView emptyCorner = new VerticalLabelView(this);
		//emptyCorner.setBackgroundColor(Color.LTGRAY);
		emptyCorner.setText(/*(survey.getState()==0 && myOwn) ? "<- schlie�en" : */" ");
    	row.addView(emptyCorner, tableParams);
    	//row.addView(verticalSeparator(), tableParams);
    	row.setLayoutParams(tableParams);
    	
    	for (int i=0; i<event.getUsers().length; i++) {
    		UserVo user = event.getUsers()[i];
    		ContactsUtils.fillUserInfo(this.getContentResolver(), user);
    		row.addView(tableTextView(ContactsUtils.userDisplayName(Utils.getApp(this), user), true, i==0 && survey.getState()==0, false), tableParams);
        	//row.addView(verticalSeparator(), tableParams);
    	}
    	
    	table.addView(row, tableParams);
    	addHorizontalSeparator(table);
    	
    	// add survey items:
    	if (survey.getSurveyItems() != null) {
	    	for (SurveyItemVo item : survey.getSurveyItems()) {
	    		
	    		row = new TableRow(this);
	        	//row.addView(verticalSeparator(), tableParams);
	        	row.setLayoutParams(tableParams);
	        	boolean clickable = survey.getState()==0 && myOwn && item.getId()!=null; // open && my own && not newly added
	        	View itemLabel = tableTextView(Utils.formatSurveyItem(survey, item), false, clickable || item.getState()==1, clickable);
	    		row.addView(itemLabel, tableParams);
	        	//row.addView(verticalSeparator(), tableParams);
	        	
	        	if (survey.getState()==0 && myOwn) {
	        		closeViews.add(itemLabel);
	        		if (item.getId()!=null)
	        			itemLabel.setOnClickListener(this);
	        	}
	    		
	    		for (int i=0; i<event.getUsers().length; i++) {
	    			
	    			UserVo user = event.getUsers()[i];
	    			ImageView iv = new ImageView(this);
	    			boolean highlight = (survey.getState()==0 && i==0) || item.getState()==1; /* open && my (first) column || closeItem */
	    			setImageForUserStatus(iv, item, user, highlight, highlight && i==0 || event.getUsers().length <= 5);
	    			row.addView(iv, tableParams);
	    	    	//row.addView(verticalSeparator(), tableParams);

	    	    	if (survey.getState()==0 && i==0) {
	    				confirmViews.add(iv);
	    				iv.setOnClickListener(this);
	    			}
	    		}
	        	table.addView(row, tableParams);
	        	addHorizontalSeparator(table);
	    	}
    	}
		
		// add button
		if (survey.getMode() == 1 /* editable */ &&
			survey.getState() == 0 /* open */) {
			row = new TableRow(this);
        	//row.addView(verticalSeparator(), tableParams);
			ImageButton addbutton = new ImageButton(this);
			addbutton.setBackgroundDrawable(null);
			addbutton.setId(1);
			addbutton.setTag("add");
			addbutton.setImageResource(android.R.drawable.ic_input_add);
			addbutton.setOnClickListener(this);
			row.addView(addbutton, tableParams);
			table.addView(row, tableParams);
		}
    	
		LinearLayout tableViewContainer = (LinearLayout)findViewById(R.id.confirmtableview);
		tableViewContainer.removeAllViews();
		tableViewContainer.addView(surveyconfirmview);
    }
    
    protected void setImageForUserStatus(ImageView view, SurveyItemVo item, UserVo user, boolean highlight, boolean wide) {
    	// find user status:
		view.setImageResource(R.drawable.survey_neutral);
		int padding = wide ? 20 : 5;
		view.setPadding(padding,padding,padding,padding);
		if (!highlight) view.setBackgroundColor(Color.LTGRAY);
    	if (item.getConfirmations() != null) {
    		for (SurveyItemUserVo confirmation : item.getConfirmations()) {
    			if (user.getId().equals(confirmation.getUserId())) {
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
    	final int MAXLEN = 22;
    	name = name.trim();
    	if (name.length() <= MAXLEN) return name;
    	return name.substring(0, MAXLEN-2) + "...";
    }
    
    protected View tableTextView(String text, boolean vertical, boolean highlight, boolean clickable) {
    	if (vertical) {
    		VerticalLabelView v = new VerticalLabelView(this);
    		if (!highlight) v.setBackgroundColor(Color.LTGRAY);
    		v.setText(trimParticipantName(text));
    		return v;
    	} else {
			TextView v = new TextView(this);
			if (!highlight) v.setBackgroundColor(Color.LTGRAY);
			v.setWidth(120);
			if (clickable) {
				SpannableString hiText = new SpannableString(text);
				hiText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, hiText.length(), 0);
				hiText.setSpan(new UnderlineSpan(), 0, hiText.length(), 0);
				v.setText(hiText);
			} else {
				v.setText(text);
			}
			v.setGravity(Gravity.CENTER_VERTICAL);
			v.setPadding(5,5,5,5);
			v.setTextColor(Color.BLACK);
			v.setTextSize(11f);
			return v;
    	}
    }

    protected void addHorizontalSeparator(ViewGroup view) {
		View ruler = new View(getApplicationContext());
		ruler.setBackgroundResource(R.color.doogetha_titlebar);
		view.addView(ruler, new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 3));
    }
    
    protected void toggleConfirmation(SurveyItemVo item, ImageView v) {
		SurveyItemUserVo itemUser = null;
		if (item.getConfirmations() != null) {
			for (SurveyItemUserVo user : item.getConfirmations())
				if (user.getUserId().equals(myself.getId()))
					itemUser = user;
		}
		if (itemUser == null) {
			itemUser = new SurveyItemUserVo();
			itemUser.setUserId(myself.getId());

			if (item.getConfirmations() == null) {
				item.setConfirmations(new SurveyItemUserVo[] {itemUser});
			} else {
				SurveyItemUserVo[] newArray = new SurveyItemUserVo[item.getConfirmations().length+1];
				System.arraycopy(item.getConfirmations(), 0, newArray, 0, item.getConfirmations().length);
				newArray[newArray.length-1] = itemUser;
				item.setConfirmations(newArray);
			}
		}
		itemUser.setState((itemUser.getState()+1) % 3);
		setImageForUserStatus(v, item, myself, true, true);
    }
    
    protected void closeSurvey(final SurveyItemVo closeItem) {
    	DroidLib.alert(this, "Abstimmung jetzt schliessen mit dem Ergebnis\n\""+Utils.formatSurveyItem(survey, closeItem)+"\"?", "Abstimmung schliessen", getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				closeSurveyImpl(closeItem);
				DroidLib.alert(SurveyConfirmActivity.this, "Die Abstimmung wurde zum Schliessen vorgemerkt.\n\nBitte speichere die aktuelle Ansicht, um die Abstimmung endgueltig zu schliessen.", "OK", null);
			}
    	});
    }
    
    protected void closeSurveyImpl(final SurveyItemVo closeItem) {
    	survey.setState(1); /* survey closed */
    	for (SurveyItemVo item : survey.getSurveyItems()) {
    		if (item.getId().equals(closeItem.getId()))
    			item.setState(1); // close reason
    		else
    			item.setState(0); // reset the others if previously closed
    	}
    	TextView surveyclosedlabel = (TextView)findViewById(R.id.surveyclosedlabel);
    	surveyclosedlabel.setText("Diese Abstimmung wird geschlossen mit dem Ergebnis: " + Utils.formatSurveyItem(survey, closeItem));
    	surveyclosedlabel.setVisibility(View.VISIBLE);
    }
    
    public void onBackPressed()
    {
    	// do we need to update the main view (because of updated comments?)
    	if (dirty)
    		finishDirty();
    	else
    		super.onBackPressed();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_FIRST_USER)
    	{
    		// set in CommentsActivity to return the current commments:
    		EventCommentsVo comments = (EventCommentsVo)data.getExtras().get("comments");
    		if (comments != null) {
    			dirty = true;
    			event.setComments(comments);
    			commentsPreviewer.update();
    		}
    	}
    }
    
	public void onClick(View v) {
		if (v.getId() < 0) {
			for (int i=0; i<confirmViews.size(); i++) {
				if (confirmViews.get(i) == v) {
					toggleConfirmation(survey.getSurveyItems()[i], (ImageView)v);
				}
			}
			
			if (this.myOwn) {
				for (int i=0; i<closeViews.size(); i++) {
					if (closeViews.get(i) == v) {
						closeSurvey(survey.getSurveyItems()[i]);
					}
				}
			}
		}
		switch (v.getId())
		{
			case R.id.editok:
				confirm();
				break;
			case R.id.editcancel:
				finishCancel();
				break;
		}
		if ("add".equals(v.getTag())) {
			// add button was clicked:
			startEditSurveyItem(null);
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
    	if (dirty)
    		finishDirty();
    	else {
    		setResult(RESULT_CANCELED);
    		finish();
    	}
    }

    protected void finishDirty()
    {
    	Intent returnValue = new Intent();
    	returnValue.putExtra("comments", event.getComments());
    	setResult(RESULT_FIRST_USER, returnValue);
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
			sa.updateItem(survey.getId(), survey);
			
			if (SurveyConfirmActivity.this.myOwn) {
				// if this is my own event, also close the surveys if they're marked as closed
					if (survey.getState() == 1)  /* closed */ {
						for (SurveyItemVo item : survey.getSurveyItems()) {
							if (item.getState() == 1) { /* survey result */
								sa.getWebRequest().setParam("close", ""+item.getId());
								sa.getItem(survey.getId());
							}
						}
					}
			}
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

	@Override
	protected SurveyVo getSurvey() {
		return survey;
	}

	@Override
	protected void finishEditSurveyItem(String item) {
		List<SurveyItemVo> itemList = new ArrayList<SurveyItemVo>(Arrays.asList(survey.getSurveyItems()));
    	if (!checkUnique(itemList, item, -1)) return;
		
		SurveyItemVo newItem = createNewSurveyItem(item);
		for (int i=0; i<itemList.size(); i++)
			if (itemList.get(i).getName().compareTo(newItem.getName()) > 0) {
				itemList.add(i, newItem);
				break;
			} else if (i == itemList.size()-1) {
				itemList.add(newItem);
				break;
			}
		
		survey.setSurveyItems(itemList.toArray(new SurveyItemVo[itemList.size()]));
		
		recreateSurveyView();
	}
}
