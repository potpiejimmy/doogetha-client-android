package de.letsdoo.client.android;

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
import de.letsdoo.server.vo.SurveyVo;
import de.potpiejimmy.util.DroidLib;

public class SurveyEditActivity extends Activity implements OnClickListener {
	
	private SurveyVo survey = null;

	private EditText surveyname = null;
	private EditText surveydescription = null;
	private ImageButton addsurveyitem = null;
	
	private View currentSelection = null;
	private Drawable lastBackground = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.surveyedit);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	this.surveyname = (EditText) findViewById(R.id.surveyname);
    	this.surveydescription = (EditText) findViewById(R.id.surveydescription);
    	this.addsurveyitem = (ImageButton) findViewById(R.id.addsurveyitem);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	addsurveyitem.setOnClickListener(this);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().get("survey") != null)
        	survey = (SurveyVo)getIntent().getExtras().get("survey");
        else {
        	survey = new SurveyVo();
        }
        
        updateUI();
    }

    protected void saveValues()
    {
    	survey.setName(surveyname.getText().toString());
    	survey.setDescription(surveydescription.getText().toString());
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
    
    protected void editSurveyItem(String item) {
    	if (item == null || item.length() == 0) return;
    	
    	LinearLayout list = (LinearLayout)findViewById(R.id.surveyitemslist);
    	
    	if (currentSelection != null) {
    		TextView name = (TextView) currentSelection.findViewById(R.id.surveyitemname);
    		name.setText(item);
    	} else {
			View vi = getLayoutInflater().inflate(R.layout.survey_item_item, null);
			TextView name = (TextView) vi.findViewById(R.id.surveyitemname);
			name.setText(item);
			list.addView(vi);
			vi.setOnClickListener(this);
    	}

    }
    
    protected void editSurveyItemDialog(String text) {
		final EditText input = new EditText(this);
		input.setText(text);
		new AlertDialog.Builder(this)
	    .setMessage("Auswahlmšglichkeit eingeben:")
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
    
	public void onClick(View view) {
		
		if (view.getId()<0) {
			currentSelection = view;
			lastBackground = view.getBackground();
			view.setBackgroundColor(Color.WHITE);
			DroidLib.alert(this, "Auswahlmšglichkeit", null, null, new String[] {"bearbeiten", "lšschen"}, new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							editSurveyItemDialog(((TextView)currentSelection.findViewById(R.id.surveyitemname)).getText().toString());
							break;
						case 1:
							
					}
				}
			}, new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
		    		currentSelection.setBackgroundDrawable(lastBackground);
				}
			});
		}
		
		switch (view.getId())
		{
		case R.id.editok:
			finishOk();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		case R.id.addsurveyitem:
			currentSelection = null;
			editSurveyItemDialog("");
			break;
		}
	}
}
