package pb.foodtruckfinder.Fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import pb.foodtruckfinder.R;

/**
 * Created by hugo on 24/03/15.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public String toString() {
        return "Inställningar";
    }
}