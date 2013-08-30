package com.doogetha.client.android.uitasks;

import android.app.Activity;
import android.content.DialogInterface;

import com.doogetha.client.android.Letsdoo;
import com.doogetha.client.android.R;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.VersionVo;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class VersionCheckTask extends AsyncUITask<VersionVo>
{
	private Activity activity = null;
	private VersionCheckTaskCallback callback = null;
	
	public VersionCheckTask(Activity activity, VersionCheckTaskCallback callback) 
	{
		super(activity);
		this.activity = activity;
		this.callback = callback;
	}
	
	public VersionVo doTask() throws Throwable
	{
		return Utils.getApp(activity).getVersionAccessor().getItems();
	}
	
	public void doneOk(VersionVo result)
	{
		if (result == null) {
			// could not fetch current version, ignore
			callback.doneCheckVersionOk();
			return;
		}
		try {
			Utils.getApp(activity).setServerVersionVo(result);
			
			// protocol version handling:
			int protocolVersion = result.getProtocolVersion();
			boolean protocolVersionIncompatible = (protocolVersion != Letsdoo.PROTOCOL_VERSION);
			
			// client version handling:
			int currentVersion = result.getClientVersionCode();
			if (currentVersion > Utils.getApp(activity).getVersionCode())
				newerVersionExists(protocolVersionIncompatible);
			else if (protocolVersionIncompatible)
				incompatibleProtocolVersion();
			else
				callback.doneCheckVersionOk();
		} catch (Exception nfe) {
			// no valid version value received, just ignore and do nothing
			callback.doneCheckVersionOk();
		}
	}
	
	public void doneFail(Throwable throwable) 
	{
		// just ignore
		callback.doneCheckVersionOk();
	}
	
    protected void newerVersionExists(final boolean protocolVersionIncompatible)
    {
    	DroidLib.alert(activity, null, 
			protocolVersionIncompatible ?
					activity.getString(R.string.newversionavailable) + " " + activity.getString(R.string.newversionavailablemustupdate) :
					activity.getString(R.string.newversionavailable),
			"Jetzt herunterladen", null,
			new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int i) {
					DroidLib.invokeBrowser(activity, Letsdoo.DOWNLOADURL);
				}
	    	}, 
    		new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					if (protocolVersionIncompatible)
						callback.doneCheckVersionFail();
					else
						callback.doneCheckVersionOk();
				}
	    	});
    }
    
    protected void incompatibleProtocolVersion()
    {
    	DroidLib.alert(activity, null, activity.getString(R.string.protocolversionincompatible), null, null, null,
    		new android.content.DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					callback.doneCheckVersionFail();
				}
    		});
    }
    
}
