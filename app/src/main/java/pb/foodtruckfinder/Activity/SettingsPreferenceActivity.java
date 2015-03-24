package pb.foodtruckfinder.Activity;

/**
 * Created by hugo on 24/03/15.
 *//**
 * Hugo Lindström (C) 2013
 * huli1000
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import pb.foodtruckfinder.Fragment.SettingsFragment;
import pb.foodtruckfinder.R;


public class SettingsPreferenceActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.action_settings));

        // Display the fragment as the main content.
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

    }

    // Updates the fragment/view
    protected void updateFragment() {
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateFragment();
    }

    public String getPreferenceString(Integer id, Integer defaultId) {
        return preferences.getString(getResources().getString(id), getResources().getString(defaultId));
    }
}
