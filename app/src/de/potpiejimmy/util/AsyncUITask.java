package de.potpiejimmy.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

public abstract class AsyncUITask<T> implements Runnable
{
	private final Handler HANDLER = new Handler();
	private Activity parent = null;
	private ProgressDialog dialog = null;

	public AsyncUITask(Activity parent)
	{
		this.parent = parent;
	}
	
	public void go(String msg)
	{
    	dialog = ProgressDialog.show(parent, "", msg, true, true);
    	new Thread(this).start();
	}
	
	public void run()
	{
		T result = null;
		try {
			result = doTask();
		} finally {
			HANDLER.post(new CallBackNotifier(result));
		}
	}
	
	public abstract T doTask();
	
	public abstract void done(T result);
	
	protected class CallBackNotifier implements Runnable
	{
		private T result = null;
		public CallBackNotifier(T result) {
			this.result = result;
		}
		public void run() {
			done(result);
    		dialog.cancel();
		}
	}
}
