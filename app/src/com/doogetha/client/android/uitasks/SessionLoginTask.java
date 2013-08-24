package com.doogetha.client.android.uitasks;

import java.net.ConnectException;
import java.net.SocketException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Base64;

import com.doogetha.client.android.R;
import com.doogetha.client.util.Utils;

import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;
import de.potpiejimmy.util.KeyUtil;

public class SessionLoginTask extends AsyncUITask<String>
{
	private Activity activity = null;
	private SessionLoginTaskCallback callback = null;
	private String logintoken = null;
	
	public SessionLoginTask(Activity activity, SessionLoginTaskCallback callback, String logintoken) 
	{
		super(activity);
		this.activity = activity;
		this.callback = callback;
		this.logintoken = logintoken;
	}
	
	public String doTask() throws Throwable
	{
		String challenge = null;
		try {
			challenge = Utils.getApp(activity).getLoginAccessor().insertItemWithResult(logintoken.substring(0, logintoken.indexOf(":")));
		} catch (Exception ex) {
			challenge = null;
		}
		if (challenge == null || challenge.length() == 0) {
			// no challenge received
			return ":"; // no credentials received
		}
		String userid = challenge.substring(0, challenge.indexOf(":"));
		byte[] challengeData = Utils.hexToBytes(challenge.substring(challenge.indexOf(":")+1));
		// sign challenge data:
		challengeData = KeyUtil.sign(challengeData, Utils.getApp(activity).getPrivateKey());
		return Utils.getApp(activity).getLoginAccessor().updateItemWithResult(userid, Utils.bytesToHex(challengeData));
	}
	
	public void doneOk(String sessioncredentials)
	{
    	String userid = sessioncredentials.substring(0, sessioncredentials.indexOf(":"));
    	String password = sessioncredentials.substring(sessioncredentials.indexOf(":")+1);
    	if (userid.length() == 0 || password.length() == 0) {
    		// no session credentials received:
    		callback.doneLoginFail();
    		return;
    	}
    	String sessionkey = Base64.encodeToString((userid + ":" + password).getBytes(), Base64.NO_WRAP);
    	callback.doneLoginOk(sessionkey);
	}
	
	public void doneFail(Throwable throwable) 
	{
		DroidLib.alert(activity, null, 
				(throwable instanceof ConnectException || throwable instanceof SocketException) ?
				activity.getString(R.string.servernotavailable) :
				"Die Anmeldung ist fehlgeschlagen: "+throwable,
				null, null, null, new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				callback.doneLoginError();
			}
		});
	}
}
