package gbgsh.moveit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

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




        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();


    }




        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class  SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private TimePreference fromPref;
        private TimePreference toPref;
        public static final String TO_KEY = "timePrefB_Key";
        public static final String FROM_KEY = "timePrefA_Key";
        private String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

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




            String from = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(FROM_KEY, "08:00");
            fromPref.setSummary(this.getString(R.string.pref_from_summary).replace("$1", from));

            String to = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getString(TO_KEY, "17:00");
            toPref.setSummary(this.getString(R.string.pref_to_summary).replace("$1", to));


        }



        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(FROM_KEY)) {
                String from_time = sharedPreferences.getString(FROM_KEY, "08:00");
                fromPref.setSummary(this.getString(R.string.pref_from_summary).replace("$1",from_time));
            } else if(key.equals(TO_KEY)) {
                String to_time = sharedPreferences.getString(TO_KEY, "17:00");
                toPref.setSummary(this.getString(R.string.pref_to_summary).replace("$1",to_time));

            }

        }
    }
}
