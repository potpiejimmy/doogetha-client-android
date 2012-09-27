package de.potpiejimmy.util;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.DatePicker;

public class DatePickerDialog
{
	protected AlertDialog dialog = null;
	protected DatePicker datePicker = null;
	
	protected DatePickerDialog(AlertDialog dialog, DatePicker datePicker) {
		this.dialog = dialog;
		this.datePicker = datePicker;
	}
	
	public static DatePickerDialog newInstance(Context context, Calendar cal) {
		DatePicker datePicker = new DatePicker(context);
		datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
		AlertDialog dialog = new AlertDialog.Builder(context).setView(datePicker).create();
		return new DatePickerDialog(dialog, datePicker);
	}
	
	public AlertDialog getDialog() {
		return dialog;
	}
	
	public DatePicker getDatePicker() {
		return datePicker;
	}
}
