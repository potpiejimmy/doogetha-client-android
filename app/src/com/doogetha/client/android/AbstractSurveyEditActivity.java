package com.doogetha.client.android;

import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.doogetha.client.util.SlideActivity;

import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;
import de.potpiejimmy.util.DatePickerDialog;
import de.potpiejimmy.util.DroidLib;
import de.potpiejimmy.util.TimePickerDialog;

public abstract class AbstractSurveyEditActivity extends SlideActivity implements OnDateSetListener, OnTimeSetListener {
	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;
	
	protected Calendar currentEditingTime = null;
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar cal = currentEditingTime != null ? currentEditingTime : createDefaultCalendar();
    	
        switch (id) {
        case DATE_DIALOG_ID:
        	return new DatePickerDialog(this, this, cal).getDialog();
            
        case TIME_DIALOG_ID:
        	return new TimePickerDialog(this, this, cal, true).getDialog();
        }
        return null;
    }
    
    protected Calendar createDefaultCalendar() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal;
    }
    
    protected void editSurveyItemDialog(String text) {
		final EditText input = new EditText(this);
		input.setText(text);
		new AlertDialog.Builder(this)
	    .setMessage(R.string.enter_survey_item)
	    .setView(input)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	finishEditSurveyItem(input.getText().toString().trim());
	        }
	    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        }
	    }).show();
    }
    
    protected void startEditSurveyItem(String value) {
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
    
    protected abstract void finishEditSurveyItem(String item);

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
			finishEditSurveyItem(""+cal.getTimeInMillis());
		else if (getSurvey().getType() == 2) {/* date and time selection */
			currentDatePickerTime = cal; /* memorize date selection until time is selected */
			showDialog(TIME_DIALOG_ID);
		}
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		currentDatePickerTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		currentDatePickerTime.set(Calendar.MINUTE, minute);
		finishEditSurveyItem("" + currentDatePickerTime.getTimeInMillis());
	}
	
	protected boolean checkUnique(List<SurveyItemVo> items, String item, int currentIndex) {
		for (int i=0; i<items.size(); i++) {
			SurveyItemVo si = items.get(i);
			if (si.getName().equals(item) && i != currentIndex) {
				DroidLib.toast(this, "Diese Auswahl existiert bereits.");
				return false;
			}
		}
		return true;
	}
}
