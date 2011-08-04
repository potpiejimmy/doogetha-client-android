package de.letsdoo.client.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;
import de.potpiejimmy.util.DroidLib;

public class SurveyEditActivity extends Activity implements OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	
	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;
	
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
        	if (getIntent().getExtras() != null && getIntent().getExtras().get("type") != null)
        		survey.setType(getIntent().getExtras().getByte("type"));
        }
        
        if (survey.getSurveyItems() != null) {
        	for (SurveyItemVo item : survey.getSurveyItems())
        		addSurveyItem(item);
        }
        
        updateUI();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar cal = Calendar.getInstance();
    	
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        }
        return null;
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
    
    protected void newSurveyItem() {
		currentSelection = -1;
		switch (survey.getType()) {
			case 0: /* generic survey */
				editSurveyItemDialog("");
				break;
			case 1: /* survey date determination */
			case 2: /* survey date and time determination */
				showDialog(DATE_DIALOG_ID);
				break;
		}
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

	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		editSurveyItem(""+cal.getTimeInMillis());
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	}
}
