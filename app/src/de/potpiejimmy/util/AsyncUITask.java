package de.potpiejimmy.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

public abstract class AsyncUITask<T> implements Runnable
{
	private final Handler HANDLER = new Handler();
	private Context context = null;
	private ProgressDialog dialog = null;

	public AsyncUITask(Context context)
	{
		this.context = context;
	}
	
	public void go(String msg)
	{
    	dialog = ProgressDialog.show(context, "", msg, true, true);
    	new Thread(this).start();
	}
	
	public void run()
	{
		try {
			T result = doTask();
			HANDLER.post(new CallBackNotifier(result, null));
		} catch (Throwable throwable) {
			HANDLER.post(new CallBackNotifier(null, throwable));
		}
	}
	
	public abstract T doTask() throws Throwable;
	
	public abstract void doneOk(T result);
	
	public abstract void doneFail(Throwable throwable);
	
	protected class CallBackNotifier implements Runnable
	{
		private T result = null;
		private Throwable throwable = null;
		public CallBackNotifier(T result, Throwable throwable) {
			this.result = result;
			this.throwable = throwable;
		}
		public void run() {
			if (throwable != null)
				doneFail(throwable);
			else
				doneOk(result);
    		dialog.cancel();
		}
	}
}
