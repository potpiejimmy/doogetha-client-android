package de.potpiejimmy.util;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.DatePicker;

public class DatePickerDialog implements OnClickListener
{
	protected AlertDialog dialog = null;
	protected DatePicker datePicker = null;
	protected OnDateSetListener listener = null;
	
	public DatePickerDialog(Context context, OnDateSetListener listener, Calendar cal) {
		this.listener = listener;
		datePicker = new DatePicker(context);
		datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
		dialog = new AlertDialog.Builder(context)
			.setPositiveButton(android.R.string.ok, this)
			.setNeutralButton(android.R.string.cancel, null)
			.setView(datePicker)
			.create();
	}
	
	public AlertDialog getDialog() {
		return dialog;
	}
	
	public DatePicker getDatePicker() {
		return datePicker;
	}

	public void onClick(DialogInterface dialog, int which) {
		listener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
	}
}
