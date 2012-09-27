package com.doogetha.client.android;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.doogetha.client.util.SlideActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.EventVo;
import de.potpiejimmy.util.DatePickerDialog;
import de.potpiejimmy.util.TimePickerDialog;

public abstract class AbstractEventEditDateTimeActivity extends SlideActivity implements android.app.DatePickerDialog.OnDateSetListener,  android.app.TimePickerDialog.OnTimeSetListener {
	
	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;
	
	protected EventVo event = null;

	protected ImageButton editdatetime = null;
	protected TextView datetimelabel = null;
	
	protected DatePickerDialog datePickerDialog = null;
	protected TimePickerDialog timePickerDialog = null;
	
    protected void editDatetime()
    {
    	showDialog(DATE_DIALOG_ID);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar cal = Calendar.getInstance();
    	if (event.getEventtime() != null)
    		cal.setTimeInMillis(event.getEventtime());
    	
        switch (id) {
        case DATE_DIALOG_ID:
        	datePickerDialog = DatePickerDialog.newInstance(this, cal);
        	datePickerDialog.getDialog().setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					DatePicker dp = datePickerDialog.getDatePicker();
					onDateSet(dp, dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
				}
            });
        	datePickerDialog.getDialog().setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel), (DialogInterface.OnClickListener)null);
        	datePickerDialog.getDialog().setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.clear_datetime), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					event.setEventtime(null);
					updateUI();
				}
            });
            return datePickerDialog.getDialog();
            
        case TIME_DIALOG_ID:
        	timePickerDialog = TimePickerDialog.newInstance(this, cal);
        	timePickerDialog.getDialog().setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					TimePicker tp = timePickerDialog.getTimePicker();
					onTimeSet(tp, tp.getCurrentHour(), tp.getCurrentMinute());
				}
            });
        	timePickerDialog.getDialog().setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel), (DialogInterface.OnClickListener)null);
        	timePickerDialog.getDialog().setButton(AlertDialog.BUTTON_NEGATIVE, "Keine Uhrzeit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(event.getEventtime());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					event.setEventtime(cal.getTimeInMillis());
					updateUI();
				}
            });
            return timePickerDialog.getDialog();
        }
        return null;
    }
    
    protected void updateUI() {
    	datetimelabel.setText(event.getEventtime()==null ? "Datum und Uhrzeit nicht festgelegt" : Utils.formatDateTime(event.getEventtime()));
    }
    
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
    	if (event.getEventtime() != null) {
    		// keep time setting if we already have one
    		cal.setTimeInMillis(event.getEventtime());
    	}
    	else {
    		cal.set(Calendar.HOUR_OF_DAY, 0);
    		cal.set(Calendar.MINUTE, 0);
    	}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		this.event.setEventtime(cal.getTimeInMillis());
		updateUI();
		showDialog(TIME_DIALOG_ID);
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(event.getEventtime());
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		this.event.setEventtime(cal.getTimeInMillis());
		updateUI();
	}
	
}
