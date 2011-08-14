package de.letsdoo.client.android;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.letsdoo.client.util.Utils;
import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;
import de.potpiejimmy.util.DroidLib;

public class SurveyEditActivity extends AbstractSurveyEditActivity implements OnClickListener {
	
	private SurveyVo survey = null;

	private EditText surveyname = null;
	private EditText surveydescription = null;
	private ImageButton addsurveyitem = null;
	private CheckBox surveyEditable = null;
	
	private int currentSelection = -1;
	private Drawable lastBackground = null;
	
	private LinearLayout surveyItemsList = null;
	
	private List<SurveyItemVo> items = new ArrayList<SurveyItemVo>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.surveyedit);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	this.surveyname = (EditText) findViewById(R.id.surveyname);
    	this.surveydescription = (EditText) findViewById(R.id.surveydescription);
    	this.addsurveyitem = (ImageButton) findViewById(R.id.addsurveyitem);
    	this.surveyItemsList =  (LinearLayout)findViewById(R.id.surveyitemslist);
    	this.surveyEditable =  (CheckBox)findViewById(R.id.surveyeditablecheckbox);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	addsurveyitem.setOnClickListener(this);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().get("survey") != null)
        	survey = (SurveyVo)getIntent().getExtras().get("survey");
        else {
        	survey = new SurveyVo();
        	if (getIntent().getExtras() != null && getIntent().getExtras().get("type") != null)
        		survey.setType(getIntent().getExtras().getByte("type"));
        }
        
        if (survey.getSurveyItems() != null) {
        	for (SurveyItemVo item : survey.getSurveyItems())
        		addSurveyItem(item);
        }
        
        updateUI();
    }
    
    protected void addSurveyItem(SurveyItemVo item) {
		View vi = getLayoutInflater().inflate(R.layout.survey_item_item, null);
		((TextView)vi.findViewById(R.id.surveyitemname)).setText(Utils.formatSurveyItem(survey, item));
		surveyItemsList.addView(vi);
		vi.setOnClickListener(this);
		items.add(item);
    }
    
    protected void removeSurveyItem(int index) {
		surveyItemsList.removeViewAt(index);
		items.remove(index);
    }

    protected void saveValues()
    {
    	survey.setName(surveyname.getText().toString());
    	survey.setDescription(surveydescription.getText().toString());
    	survey.setMode(surveyEditable.isChecked() ? (byte)1 : (byte)0);
    	
    	survey.setSurveyItems(items.size()==0 ? null : items.toArray(new SurveyItemVo[items.size()]));
    }
    
    protected void finishOk()
    {
    	if (items.size() < 2) {
    		DroidLib.toast(this, "Bitte fügen Sie mindestens zwei Auswahlmöglichkeiten hinzu.");
    		return;
    	}
    	
    	saveValues();
    	
    	Intent returnValue = new Intent();
    	returnValue.putExtra("survey", survey);
    	setResult(RESULT_OK, returnValue);
    	finish();
    }
    
    protected void finishCancel()
    {
    	setResult(RESULT_CANCELED);
    	finish();
    }
    
    protected void updateUI()
    {
        surveyname.setText(survey.getName());
        surveydescription.setText(survey.getDescription());
        surveyEditable.setChecked(survey.getMode()==1);
    }
    
    protected String getSurveyItemName(int index) {
    	return items.get(index).getName();
    }
    
    protected void setSurveyItemName(int index, String item) {
    	items.get(index).setName(item);
    	((TextView)surveyItemsList.getChildAt(index).findViewById(R.id.surveyitemname)).setText(Utils.formatSurveyItem(survey, items.get(index)));
    }
    
    protected void editSurveyItem(String item) {
    	if (item == null || item.length() == 0) return;
    	
    	if (!checkUnique(items, item, currentSelection)) return;
    	
    	if (currentSelection >= 0) {
    		setSurveyItemName(currentSelection, item);
    	} else {
    		addSurveyItem(createNewSurveyItem(item));
    	}

    }
    
    protected void validateAndFinish() {
    	if (surveyname.getText().toString().trim().length() == 0) {
    		DroidLib.toast(this, "Bitte geben Sie ein Thema für die Abstimmung ein");
    		return;
    	}
    	finishOk();
    }
    
    protected void newSurveyItem() {
		currentSelection = -1;
		startEditSurveyItem(null);
    }
    
	public void onClick(View view) {
		
		if (view.getId()<0) {
			
			int index = -1;
			for (int i=0; i<surveyItemsList.getChildCount(); i++)
				if (surveyItemsList.getChildAt(i) == view)
					{index = i; break;}
			if (index < 0) return;
			
			this.currentSelection = index;
			lastBackground = view.getBackground();
			view.setBackgroundColor(Color.WHITE);
			
			DroidLib.alert(this, Utils.formatSurveyItem(survey, items.get(currentSelection)), null, null, new String[] {"bearbeiten", "löschen"}, new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							startEditSurveyItem(getSurveyItemName(currentSelection));
							break;
						case 1:
							removeSurveyItem(currentSelection);
							currentSelection = -1;
							break;
					}
				}
			}, new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					if (currentSelection >= 0)
						surveyItemsList.getChildAt(currentSelection).setBackgroundDrawable(lastBackground);
				}
			});
		}
		
		switch (view.getId())
		{
		case R.id.editok:
			validateAndFinish();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		case R.id.addsurveyitem:
			newSurveyItem();
			break;
		}
	}
	
    protected SurveyVo getSurvey() {
    	return survey;
    }
    
    protected void finishEditSurveyItem(String item) {
    	editSurveyItem(item);
    }

}
