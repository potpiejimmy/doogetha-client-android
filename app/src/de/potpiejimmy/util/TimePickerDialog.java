package de.potpiejimmy.util;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.TimePicker;

public class TimePickerDialog
{
	protected AlertDialog dialog = null;
	protected TimePicker timePicker = null;
	
	protected TimePickerDialog(AlertDialog dialog, TimePicker timePicker) {
		this.dialog = dialog;
		this.timePicker = timePicker;
	}
	
	public static TimePickerDialog newInstance(Context context, Calendar cal) {
		TimePicker timePicker = new TimePicker(context);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
		AlertDialog dialog = new AlertDialog.Builder(context).setView(timePicker).create();
		return new TimePickerDialog(dialog, timePicker);
	}
	
	public AlertDialog getDialog() {
		return dialog;
	}
	
	public TimePicker getTimePicker() {
		return timePicker;
	}
}
