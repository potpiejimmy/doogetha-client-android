package com.doogetha.client.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.doogetha.client.util.SlidePreferenceActivity;
import com.doogetha.client.util.Utils;

import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;
import de.potpiejimmy.util.KeyUtil;

public class SettingsActivity extends SlidePreferenceActivity implements OnPreferenceClickListener, DialogInterface.OnClickListener {

	protected Preference unregisterPref = null;
	protected Preference keyGenPref = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPreferenceScreen(createPreferenceHierarchy());
    }

    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // Inline preferences
        PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
        inlinePrefCat.setTitle("Doogetha - Version " + Utils.getApp(this).getVersionName());
        root.addPreference(inlinePrefCat);

        // Intent preference
        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
//        intentPref.setIntent(new Intent().setAction(Intent.ACTION_VIEW)
//                .setData(Uri.parse("http://www.android.com")));
        intentPref.setTitle("Abmelden");
        intentPref.setSummary(Utils.getApp(this).getEmail());
        intentPref.setOnPreferenceClickListener(this);
        this.unregisterPref = intentPref;
        inlinePrefCat.addPreference(intentPref);

        intentPref = getPreferenceManager().createPreferenceScreen(this);
		intentPref.setTitle("Server");
		intentPref.setSummary(Letsdoo.URI + "\n(Version <unversioned>)"); // XXX
		intentPref.setEnabled(false);
		inlinePrefCat.addPreference(intentPref);

        intentPref = getPreferenceManager().createPreferenceScreen(this);
		intentPref.setTitle("Create key pair");
		intentPref.setSummary("Test creation of 2048 bit RSA key pair"); // XXX
        intentPref.setOnPreferenceClickListener(this);
		this.keyGenPref = intentPref;
		inlinePrefCat.addPreference(intentPref);

        return root;
    }

	public boolean onPreferenceClick(Preference preference) {
		if (preference == unregisterPref) {
			DroidLib.alert(this, "Wirklich abmelden und Registrierung erneut durchführen?", "Ja", "Nein", this);
		} else if (preference == keyGenPref) {
			new KeyGenTask(this).go("Creating key pair...");
		}
		return true;
	}

	public void onClick(DialogInterface dialog, int which) {
		Utils.getApp(this).unregister();
		startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
		setResult(RESULT_FIRST_USER); /* signal unregistration */
		finish();
	}
	
	public static class KeyGenTask extends AsyncUITask<String> {

		private long genTime = 0;
		
		public KeyGenTask(Context context) {
			super(context);
		}

		@Override
		public String doTask() throws Throwable {
			genTime = System.currentTimeMillis();
			KeyUtil.generateKeyPair();
			genTime = System.currentTimeMillis() - genTime;
			return null;
		}

		@Override
		public void doneOk(String result) {
			DroidLib.alert(getContext(), "Key pair generated in " + genTime + " ms");
		}

		@Override
		public void doneFail(Throwable throwable) {
			DroidLib.alert(getContext(), "Key pair generation failed: " + throwable);
		}
		
	}
}