package com.doogetha.client.android.uitasks;

public interface SessionLoginTaskCallback
{
	public void doneLoginOk(String sessionkey);
	
	public void doneLoginFail();

	public void doneLoginError();
}
