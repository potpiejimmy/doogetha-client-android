package de.letsdoo.client.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
//import android.preference.SwitchPreference;

public class SettingsActivity extends PreferenceActivity {

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
        inlinePrefCat.setTitle("Inline Preferences");
        root.addPreference(inlinePrefCat);

        // Checkbox preference
        CheckBoxPreference checkboxPref = new CheckBoxPreference(this);
        checkboxPref.setKey("checkbox_preference");
        checkboxPref.setTitle("Checkbox Preference");
        checkboxPref.setSummary("Summary Checkbox Preference");
        inlinePrefCat.addPreference(checkboxPref);

        // Switch preference
//        SwitchPreference switchPref = new SwitchPreference(this);
//        switchPref.setKey("switch_preference");
//        switchPref.setTitle(R.string.title_switch_preference);
//        switchPref.setSummary(R.string.summary_switch_preference);
//        inlinePrefCat.addPreference(switchPref);

        // Dialog based preferences
        PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(this);
        dialogBasedPrefCat.setTitle("Dialog Based Preferences");
        root.addPreference(dialogBasedPrefCat);

        // Edit text preference
        EditTextPreference editTextPref = new EditTextPreference(this);
        editTextPref.setDialogTitle("Edit Text Preference");
        editTextPref.setKey("edittext_preference");
        editTextPref.setTitle("Edit Text Title");
        editTextPref.setSummary("Edit Text Summary");
        dialogBasedPrefCat.addPreference(editTextPref);

        // List preference
//        ListPreference listPref = new ListPreference(this);
//        listPref.setEntries(R.array.entries_list_preference);
//        listPref.setEntryValues(R.array.entryvalues_list_preference);
//        listPref.setDialogTitle(R.string.dialog_title_list_preference);
//        listPref.setKey("list_preference");
//        listPref.setTitle(R.string.title_list_preference);
//        listPref.setSummary(R.string.summary_list_preference);
//        dialogBasedPrefCat.addPreference(listPref);

        // Launch preferences
        PreferenceCategory launchPrefCat = new PreferenceCategory(this);
        launchPrefCat.setTitle("Launch Preferences");
        root.addPreference(launchPrefCat);

        /*
         * The Preferences screenPref serves as a screen break (similar to page
         * break in word processing). Like for other preference types, we assign
         * a key here so that it is able to save and restore its instance state.
         */
        // Screen preference
        PreferenceScreen screenPref = getPreferenceManager().createPreferenceScreen(this);
        screenPref.setKey("screen_preference");
        screenPref.setTitle("Screen Preference");
        screenPref.setSummary("Screen Preference Summary");
        launchPrefCat.addPreference(screenPref);

        /*
         * You can add more preferences to screenPref that will be shown on the
         * next screen.
         */

        // Example of next screen toggle preference
        CheckBoxPreference nextScreenCheckBoxPref = new CheckBoxPreference(this);
        nextScreenCheckBoxPref.setKey("next_screen_toggle_preference");
        nextScreenCheckBoxPref.setTitle("Next Screen Toggle Pref");
        nextScreenCheckBoxPref.setSummary("Next Screen Toggle Pref Summary");
        screenPref.addPreference(nextScreenCheckBoxPref);

        // Intent preference
        PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
        intentPref.setIntent(new Intent().setAction(Intent.ACTION_VIEW)
                .setData(Uri.parse("http://www.android.com")));
        intentPref.setTitle("Intent Preference");
        intentPref.setSummary("Intent Preference Title");
        launchPrefCat.addPreference(intentPref);

        // Preference attributes
        PreferenceCategory prefAttrsCat = new PreferenceCategory(this);
        prefAttrsCat.setTitle("Attribute Preferences");
        root.addPreference(prefAttrsCat);

        // Visual parent toggle preference
        CheckBoxPreference parentCheckBoxPref = new CheckBoxPreference(this);
        parentCheckBoxPref.setTitle("Parent Preference");
        parentCheckBoxPref.setSummary("Parent Preference Summary");
        prefAttrsCat.addPreference(parentCheckBoxPref);

        // Visual child toggle preference
        // See res/values/attrs.xml for the <declare-styleable> that defines
        // TogglePrefAttrs.
//        TypedArray a = obtainStyledAttributes(R.styleable.TogglePrefAttrs);
//        CheckBoxPreference childCheckBoxPref = new CheckBoxPreference(this);
//        childCheckBoxPref.setTitle(R.string.title_child_preference);
//        childCheckBoxPref.setSummary(R.string.summary_child_preference);
//        childCheckBoxPref.setLayoutResource(
//                a.getResourceId(R.styleable.TogglePrefAttrs_android_preferenceLayoutChild,
//                        0));
//        prefAttrsCat.addPreference(childCheckBoxPref);
//        a.recycle();

        return root;
    }
}