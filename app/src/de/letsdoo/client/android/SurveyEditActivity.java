package de.letsdoo.client.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;
import de.potpiejimmy.util.DroidLib;

public class SurveyEditActivity extends Activity implements OnClickListener {
	
	private SurveyVo survey = null;

	private EditText surveyname = null;
	private EditText surveydescription = null;
	private ImageButton addsurveyitem = null;
	
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
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	addsurveyitem.setOnClickListener(this);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().get("survey") != null)
        	survey = (SurveyVo)getIntent().getExtras().get("survey");
        else {
        	survey = new SurveyVo();
        }
        
        if (survey.getSurveyItems() != null) {
        	for (SurveyItemVo item : survey.getSurveyItems())
        		addSurveyItem(item);
        }
        
        updateUI();
    }
    
    protected void addSurveyItem(SurveyItemVo item) {
		View vi = getLayoutInflater().inflate(R.layout.survey_item_item, null);
		((TextView)vi.findViewById(R.id.surveyitemname)).setText(item.getName());
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
    	
    	survey.setSurveyItems(items.size()==0 ? null : items.toArray(new SurveyItemVo[items.size()]));
    }
    
    protected void finishOk()
    {
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
    }
    
    protected String getSurveyItemName(int index) {
    	return items.get(index).getName();
    }
    
    protected void setSurveyItemName(int index, String item) {
    	((TextView)surveyItemsList.getChildAt(index).findViewById(R.id.surveyitemname)).setText(item);
    	items.get(index).setName(item);
    }
    
    protected void editSurveyItem(String item) {
    	if (item == null || item.length() == 0) return;
    	
    	if (currentSelection >= 0) {
    		setSurveyItemName(currentSelection, item);
    	} else {
    		SurveyItemVo si = new SurveyItemVo();
    		si.setName(item);
    		addSurveyItem(si);
    	}

    }
    
    protected void editSurveyItemDialog(String text) {
		final EditText input = new EditText(this);
		input.setText(text);
		new AlertDialog.Builder(this)
	    .setMessage("Auswahlmöglichkeit eingeben:")
	    .setView(input)
	    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	editSurveyItem(input.getText().toString().trim());
	        }
	    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        }
	    }).show();
    }
    
    protected void validateAndFinish() {
    	if (surveyname.getText().toString().trim().length() == 0) {
    		DroidLib.toast(this, "Bitte geben Sie ein Thema für die Abstimmung ein");
    		return;
    	}
    	finishOk();
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
			
			DroidLib.alert(this, getSurveyItemName(index), null, null, new String[] {"bearbeiten", "löschen"}, new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							editSurveyItemDialog(getSurveyItemName(currentSelection));
							break;
						case 1:
							removeSurveyItem(currentSelection);
							break;
					}
				}
			}, new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
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
			currentSelection = -1;
			editSurveyItemDialog("");
			break;
		}
	}
}
