package gbgsh.moveit;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;


import java.sql.Time;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    private static Context mContext;

    public static Context getContext(){
        return mContext;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if(getActionBar() != null){
            getActionBar().setIcon(android.R.mipmap.sym_def_app_icon);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Call super :
        super.onCreate(savedInstanceState);

        // Set the activity's fragment :
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private TimePreference fromPref;
        private TimePreference toPref;
        public static final String TO_KEY = "timePrefB_Key";
        public static final String FROM_KEY = "timePrefA_Key";

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);

            // Get widgets :
            toPref = (TimePreference) this.findPreference(TO_KEY);
            fromPref = (TimePreference) this.findPreference(FROM_KEY);

            // Set listener :
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            // Set seekbar summary :


            String from = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(FROM_KEY, "DERP");
            fromPref.setSummary(this.getString(R.string.pref_from_summary).replace("$1", from));

            String to = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(TO_KEY, "DERP");
            toPref.setSummary(this.getString(R.string.pref_to_summary).replace("$1", to));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(FROM_KEY)) {
                String from_time = sharedPreferences.getString(FROM_KEY, "");
                fromPref.setSummary(this.getString(R.string.pref_from_summary).replace("$1",from_time));
            } else if(key.equals(TO_KEY)) {
                String to_time = sharedPreferences.getString(TO_KEY, "");
                toPref.setSummary(this.getString(R.string.pref_to_summary).replace("$1",to_time));

            }
        }
    }
}
