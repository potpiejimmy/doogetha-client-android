package de.letsdoo.client.android;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;

public abstract class AbstractSurveyEditActivity extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;
	
	protected Calendar currentEditingTime = null;
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar cal = currentEditingTime != null ? currentEditingTime : Calendar.getInstance();
    	
        switch (id) {
        case DATE_DIALOG_ID:
        	return new DatePickerDialog(this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            
        case TIME_DIALOG_ID:
        	return new TimePickerDialog(this, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        }
        return null;
    }
    
    protected void editSurveyItemDialog(String text) {
		final EditText input = new EditText(this);
		input.setText(text);
		new AlertDialog.Builder(this)
	    .setMessage("Auswahlmšglichkeit eingeben:")
	    .setView(input)
	    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	insertOrUpdateSurveyItem(input.getText().toString().trim());
	        }
	    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        }
	    }).show();
    }
    
    protected void invokeSurveyItemDialog(String value) {
		switch (getSurvey().getType()) {
			case 0: /* generic survey */
				editSurveyItemDialog(value==null ? "" : value);
				break;
			case 1: /* survey date determination */
			case 2: /* survey date and time determination */
				if (value != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(Long.parseLong(value));
					currentEditingTime = cal;
					/* must remove the dialogs so they are created again using the correct initial values */
					removeDialog(DATE_DIALOG_ID); 
					removeDialog(TIME_DIALOG_ID);
				} else {
					currentEditingTime = null;
				}
				showDialog(DATE_DIALOG_ID);
				break;
		}
    }
    
    protected SurveyItemVo createNewSurveyItem(String item) {
		SurveyItemVo si = new SurveyItemVo();
		si.setName(item);
		return si;
    }
    
    protected abstract SurveyVo getSurvey();
    
    protected abstract void insertOrUpdateSurveyItem(String item);

	private Calendar currentDatePickerTime = null;
	
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		if (getSurvey().getType() == 1) /* date selection only */
			insertOrUpdateSurveyItem(""+cal.getTimeInMillis());
		else if (getSurvey().getType() == 2) {/* date and time selection */
			currentDatePickerTime = cal; /* memorize date selection until time is selected */
			showDialog(TIME_DIALOG_ID);
		}
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		currentDatePickerTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		currentDatePickerTime.set(Calendar.MINUTE, minute);
		insertOrUpdateSurveyItem("" + currentDatePickerTime.getTimeInMillis());
	}
}
