package de.erichambuch.biproclient.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Index 0 - 9 für die jeweilige Konfiguration des VU.
     */
    public static final String EXTRA_CONFIGURATION_INDEX = "extraConfigurationIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int index = getIntent().getIntExtra(EXTRA_CONFIGURATION_INDEX, 0);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment(new SettingsDataStore(getApplicationContext(), index)))
                .commit();
    }
}
