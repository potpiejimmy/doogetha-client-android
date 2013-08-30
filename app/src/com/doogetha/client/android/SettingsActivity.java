package com.doogetha.client.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.doogetha.client.util.SlidePreferenceActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.VersionVo;
import de.potpiejimmy.util.DroidLib;

public class SettingsActivity extends SlidePreferenceActivity implements OnPreferenceClickListener, DialogInterface.OnClickListener {

	protected Preference unregisterPref = null;
	
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

        VersionVo versionVo = Utils.getApp(this).getServerVersionVo(); 
        if (versionVo != null) {
            intentPref = getPreferenceManager().createPreferenceScreen(this);
    		intentPref.setTitle("Client - " + versionVo.getClientVersionName());
    		intentPref.setSummary("Last change: " + versionVo.getDescription());
    		intentPref.setEnabled(false);
    		inlinePrefCat.addPreference(intentPref);

            intentPref = getPreferenceManager().createPreferenceScreen(this);
    		intentPref.setTitle("Server - " + versionVo.getServerVersionName());
    		intentPref.setSummary(Letsdoo.URI + " - Protocol " + versionVo.getProtocolVersion());
    		intentPref.setEnabled(false);
    		inlinePrefCat.addPreference(intentPref);
        }
        
        return root;
    }

	public boolean onPreferenceClick(Preference preference) {
		if (preference == unregisterPref) {
			DroidLib.alert(this, "Wirklich abmelden und Registrierung erneut durchführen?", "Ja", "Nein", this);
		}
		return true;
	}

	public void onClick(DialogInterface dialog, int which) {
		Utils.getApp(this).unregister();
		startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
		setResult(RESULT_FIRST_USER); /* signal unregistration */
		finish();
	}
}