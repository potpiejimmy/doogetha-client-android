package de.letsdoo.client.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import de.letsdoo.client.util.SlidePreferenceActivity;
import de.letsdoo.client.util.Utils;
import de.potpiejimmy.util.DroidLib;

public class SettingsActivity extends SlidePreferenceActivity implements OnPreferenceClickListener, DialogInterface.OnClickListener {

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
        
        inlinePrefCat.addPreference(intentPref);

        return root;
    }

	public boolean onPreferenceClick(Preference preference) {
		DroidLib.alert(this, "Wirklich abmelden und Registrierung erneut durchführen?", "Ja", "Nein", this);
		return true;
	}

	public void onClick(DialogInterface dialog, int which) {
		Utils.getApp(this).unregister();
		startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
		setResult(RESULT_FIRST_USER); /* signal unregistration */
		finish();
	}
}