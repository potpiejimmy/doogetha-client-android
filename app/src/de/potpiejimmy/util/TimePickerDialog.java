package de.potpiejimmy.util;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TimePicker;

public class TimePickerDialog implements OnClickListener
{
	protected AlertDialog dialog = null;
	protected TimePicker timePicker = null;
	protected OnTimeSetListener listener = null;
	
	public TimePickerDialog(Context context, OnTimeSetListener listener, Calendar cal, boolean is24HourView) {
		this.listener = listener;
		timePicker = new TimePicker(context);
		timePicker.setIs24HourView(is24HourView);
		timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
		dialog = new AlertDialog.Builder(context)
			.setPositiveButton(android.R.string.ok, this)
			.setNeutralButton(android.R.string.cancel, null)
			.setView(timePicker)
			.create();
	}
	
	public AlertDialog getDialog() {
		return dialog;
	}
	
	public TimePicker getTimePicker() {
		return timePicker;
	}

	public void onClick(DialogInterface dialog, int which) {
		listener.onTimeSet(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
	}
}
