package de.erichambuch.biproclient.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.OutputStream;

import de.erichambuch.biproclient.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_CODE_LOAD = 432;
    private static final int REQUEST_CODE_SAVE = 433;

    private final SettingsDataStore myDataStore;

    public SettingsFragment(SettingsDataStore dataStore) {
        super();
        this.myDataStore = dataStore;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(myDataStore);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("loadsettings").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                loadSettings();
                return true;
            }
        });
        findPreference("savesettings").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                exportSettings();
                return true;
            }
        });
        // SAML Tgic ServiceId freischalten wenn TGIC ausgewählt ist
        findPreference("prefs_auth_verfahren").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                findPreference("prefs_saml_service_id").setEnabled("saml".equals(newValue));
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOAD && resultCode == Activity.RESULT_OK) {
            try(InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData())) {
                myDataStore.readFromJSON(inputStream);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Einstellungen geladen", Snackbar.LENGTH_LONG).show();
            }
            catch(Exception e) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Abbruch", v -> {
                            }).show();
            }
        } else if (requestCode == REQUEST_CODE_SAVE && resultCode == Activity.RESULT_OK) {
            try(OutputStream out = getActivity().getContentResolver().openOutputStream(data.getData(), "w")) {
                myDataStore.writeToJSON(out);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Einstellungen gespeichert", Snackbar.LENGTH_LONG).show();
            } catch(Exception e) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Abbruch", v -> {
                        }).show();
            }
        }
    }

    private void loadSettings() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // fester MIME-Type funktioniert nicht beim Download von Dateien
        startActivityForResult(intent, REQUEST_CODE_LOAD);
    }

    private void exportSettings() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "biproconfig.json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_SAVE);
    }
}
