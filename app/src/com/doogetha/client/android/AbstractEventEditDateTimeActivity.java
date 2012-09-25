package com.doogetha.client.android;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.doogetha.client.util.SlideActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.EventVo;

public abstract class AbstractEventEditDateTimeActivity extends SlideActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	
	protected static final int DATE_DIALOG_ID = 0;
	protected static final int TIME_DIALOG_ID = 1;
	
	protected int currentDialogSelection = AlertDialog.BUTTON_NEUTRAL;
	
	protected EventVo event = null;

	protected ImageButton editdatetime = null;
	protected TextView datetimelabel = null;
	
    protected void editDatetime()
    {
    	currentDialogSelection = AlertDialog.BUTTON_NEUTRAL;
    	showDialog(DATE_DIALOG_ID);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar cal = Calendar.getInstance();
    	if (event.getEventtime() != null)
    		cal.setTimeInMillis(event.getEventtime());
    	
        switch (id) {
        case DATE_DIALOG_ID:
            DatePickerDialog dpd = new DatePickerDialog(this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					currentDialogSelection = AlertDialog.BUTTON_POSITIVE;
				}
            });
            dpd.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel), (DialogInterface.OnClickListener)null);
            dpd.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.clear_datetime), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					currentDialogSelection = AlertDialog.BUTTON_NEGATIVE;
					event.setEventtime(null);
					updateUI();
				}
            });
            return dpd;
            
        case TIME_DIALOG_ID:
            TimePickerDialog tpd = new TimePickerDialog(this, this, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            tpd.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					currentDialogSelection = AlertDialog.BUTTON_POSITIVE;
				}
            });
            tpd.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel), (DialogInterface.OnClickListener)null);
            tpd.setButton(AlertDialog.BUTTON_NEGATIVE, "Keine Uhrzeit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(event.getEventtime());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					event.setEventtime(cal.getTimeInMillis());
					updateUI();
				}
            });
            return tpd;
        }
        return null;
    }
    
    protected void updateUI() {
    	datetimelabel.setText(event.getEventtime()==null ? "Datum und Uhrzeit nicht festgelegt" : Utils.formatDateTime(event.getEventtime()));
    }
    
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		if (currentDialogSelection != AlertDialog.BUTTON_POSITIVE) return;
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
    	currentDialogSelection = AlertDialog.BUTTON_NEUTRAL;
		showDialog(TIME_DIALOG_ID);
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (currentDialogSelection != AlertDialog.BUTTON_POSITIVE) return;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(event.getEventtime());
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		this.event.setEventtime(cal.getTimeInMillis());
		updateUI();
	}
	
}
