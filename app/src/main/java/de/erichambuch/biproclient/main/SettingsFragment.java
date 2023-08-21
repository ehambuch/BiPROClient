package de.erichambuch.biproclient.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.io.OutputStream;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.hub.BiPROHubAuthenticator;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_CODE_LOAD = 432;
    private static final int REQUEST_CODE_SAVE = 433;

    /**
     * Versionsnummer a.b.c.d.e
     */
    private static final String VERSION_REGEX = "\\d\\.\\d.\\d.\\d.\\d";
    private static final String VERSION_TEXT = "a.b.c.d.e";

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

        findPreference("prefs_biprohub_getauth").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                requestBiPROHubAuth();
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

        setValidator("prefs_provider_gdvnr", "(\\d\\d\\d\\d)?(,\\d\\d\\d\\d)*", "nnnn,nnnn,...");
        setValidator("prefs_extranetservice_version", "\\d\\.\\d.\\d.\\d", "a.b.c.d");
        setValidator("prefs_transferservice_version", VERSION_REGEX, VERSION_TEXT);
        setValidator("prefs_listservice_version", VERSION_REGEX, VERSION_TEXT);
        setValidator("prefs_partnerservice_version", VERSION_REGEX, VERSION_TEXT);
        setValidator("prefs_vertragservice_version", VERSION_REGEX, VERSION_TEXT);
        setValidator("prefs_schadenservice_version", VERSION_REGEX, VERSION_TEXT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOAD && resultCode == Activity.RESULT_OK) {
            try(InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData())) {
                myDataStore.readFromJSON(inputStream);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Einstellungen geladen - Bitte App neu starten", Snackbar.LENGTH_LONG).show();
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

    private void setValidator(String key, String regExPattern, String errorMsg) {
        EditTextPreference prefs = (EditTextPreference) findPreference(key);
        prefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null && !"".equals(newValue) && !String.valueOf(newValue).matches(regExPattern)) {
                    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                    builder.setTitle("Ungültige Eingabe");
                    builder.setMessage(newValue + " entspricht nicht dem erforderlichen Format "+errorMsg);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    return false;
                } else
                    return true;
            }
        });
    }

    private void loadSettings() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // fester MIME-Type funktioniert nicht beim Download von Dateien
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(Environment.getDownloadCacheDirectory()));
        startActivityForResult(intent, REQUEST_CODE_LOAD);
    }

    private void exportSettings() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "biproconfig.json");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(Environment.getDownloadCacheDirectory()));
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_SAVE);
    }

    private void requestBiPROHubAuth() {
        final String key = myDataStore.getString("prefs_biprohub_apikey", "").trim();
        int idx = key.indexOf(':');
        if (idx > 1) {
            String clientId = key.substring(0, idx);
            String clientSecret = key.substring(idx+1);
            new BiPROHubAuthenticator(requireContext(), myDataStore).
                    authenticateOAuth2(clientId, clientSecret);
        } else
            new BiPROHubAuthenticator(requireContext(), myDataStore).
                    authenticateAPIKey(key);
    }
}
