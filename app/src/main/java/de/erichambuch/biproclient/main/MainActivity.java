package de.erichambuch.biproclient.main;

import static androidx.navigation.Navigation.findNavController;
import static de.erichambuch.biproclient.R.id;
import static de.erichambuch.biproclient.R.layout;
import static de.erichambuch.biproclient.R.string;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.provider.PfefferminziaConfiguration;
import de.erichambuch.biproclient.testprovider.TestServer;

/**
 * Main Activity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(layout.main_activity);

        try {
            new TestServer(PfefferminziaConfiguration.PORT).loadMessages(getResources());
        } catch (IOException e) {
            Log.e(AppInfo.APP_NAME, "Fehler beim Start des Testservers", e);
            Toast.makeText(this, "Fehler beim Start des Testservers", Toast.LENGTH_LONG).show();
        }

        // erster Start
        initDefaultConfig();

        // read data
        ((MainApplication) getApplication()).loadStaticData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return findNavController(this, id.navHostFragment).navigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_about) {
            showAbout();
            return true;
        } else if (id == R.id.menu_feedback) {
            sendMail();
            return true;
        } else if (id == R.id.menu_log) {
            showLog();
            return true;
        } else if (id == R.id.menu_licenses) {
            showLicenses();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void  showLicenses()  {
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
    }

    private void showLog() {
        final MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        if (viewModel.getRequestLogger() != null) {
            adapter.addAll();
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle("Logfile")
                    .setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            View dialogView = this.getLayoutInflater().inflate(layout.dialog_logfile, null);
            builder.setView(dialogView);
            ((TextView) dialogView.findViewById(R.id.logfileTextView)).setText((((StandardRequestLogger) viewModel.getRequestLogger()).iterateLog().toString()));
            builder.show();
        }
    }

    private void sendMail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(string.email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback zu BiPRO Client");
        try {
            startActivity(Intent.createChooser(intent, "Versende Email..."));
        } catch (ActivityNotFoundException ex) {
            Log.e(AppInfo.APP_NAME, "Fehler beim Versenden der Email", ex);
        }
    }

    private void showWelcome() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(string.app_name))
                .setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setMessage(Html.fromHtml(getString(string.message_welcome), Html.FROM_HTML_MODE_COMPACT))
                .show();
        TextView view = (TextView) dialog.findViewById(android.R.id.message);
        if (view != null)
            view.setMovementMethod(LinkMovementMethod.getInstance()); // make links clickable
    }

    private void showAbout() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(string.app_name))
                .setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setMessage((Html.fromHtml(getString(string.message_about), Html.FROM_HTML_MODE_COMPACT))).show();
        TextView view = (TextView) dialog.findViewById(android.R.id.message);
        if (view != null)
            view.setMovementMethod(LinkMovementMethod.getInstance()); // make links clickable
    }

    private void initDefaultConfig() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("prefs_firstrun", true)) {
            SettingsDataStore dataStore = new SettingsDataStore(getApplicationContext(), 0);
            PfefferminziaConfiguration configuration = new PfefferminziaConfiguration(getResources());
            dataStore.putString("prefs_provider_name", "Pfefferminzia (Demo)");
            dataStore.putString("prefs_provider_gdvnr", "0000");
            dataStore.putString("prefs_auth_verfahren", "sts");
            dataStore.putString("prefs_sts_url", configuration.getSTServiceURL());
            dataStore.putString("prefs_extranetservice_url", configuration.getBipro440ServiceURL());
            dataStore.putString("prefs_transferservice_url", configuration.getTransferServiceURL());
            dataStore.putString("prefs_vertragservice_url", configuration.getVertragServiceURL());
            dataStore.putString("prefs_partnerservice_url", configuration.getPartnerServiceURL());
            dataStore.putString("prefs_schadenservice_url", configuration.getSchadenServiceURL());
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("prefs_firstrun", false).apply();
            showWelcome();
        }
    }
}