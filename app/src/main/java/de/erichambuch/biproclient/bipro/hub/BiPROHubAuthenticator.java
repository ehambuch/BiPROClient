package de.erichambuch.biproclient.bipro.hub;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.SettingsDataStore;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Diese Klasse beschafft sich die notwendigen Authentifizierung zum Serviceabruf über den Hub.
 * <p>Dazu bekommt jeder Subscriber und Client einen API-Key bei der Registrierung zugewiesen.
 * Der API-Key wird gegen einen HTTP Authorization Header "eingetauscht", der beim Abruf
 * der BiPRO-Services mitgegeben wird.</p>
 * <p>Die URL für das <code>login</code> ergibt sich aus der Subscriber-URL des jeweiligen Services, aktuell nur der 430.</p>
 */
public class BiPROHubAuthenticator {

    private final String authUrl;

    private final SettingsDataStore configuration;

    private final Context context; // TODO: leak

    public BiPROHubAuthenticator(Context context, SettingsDataStore configuration) {
        this.authUrl = cutLastPath(configuration.getString(context.getString(R.string.prefs_transferservice_url), "https://localhost/TransferService")) + "/login";
        this.configuration = configuration;
        this.context = context;
    }

    public synchronized void authenticateAPIKey(String apiKey) {
        final String jsonRequest = "{\"api-key\": \"" +apiKey+"\"}\n";
        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
        Log.d(AppInfo.APP_NAME, "Requesting Key at: "+this.authUrl);
        Log.d(AppInfo.APP_NAME, "Requesting Key with: "+jsonRequest);
        Request request = new Request.Builder()
                .url(this.authUrl)
                .addHeader("User-Agent", "Android BiPRO Client")
                .post(body)
                .build();
        final OkHttpClient client = new OkHttpClient.Builder() // Längere Timeouts falls über Mobilnetz
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        final Callback commandCallback = new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.code() == 200) {
                        final String body = response.body().string();
                        Log.d(AppInfo.APP_NAME, "Response: "+body);
                        JSONObject json = new JSONObject(body);
                        String authHeader = json.getString("access_token");
                        BiPROHubAuthenticator.this.onSuccess(authHeader);
                    } else
                        BiPROHubAuthenticator.this.onFailure("Fehler bei Authentifizierung am Hub, HTTP Code " + response.code());
                } catch (Exception e) {
                    Log.e(AppInfo.APP_NAME, "Fehler bei HUB-Anmeldung", e);
                    BiPROHubAuthenticator.this.onFailure(e.getMessage()); // falls bei der Response was schief ging
                } finally {
                    response.close();
                }
            }

            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(AppInfo.APP_NAME, "Fehler bei HUB-Anmeldung", e);
                BiPROHubAuthenticator.this.onFailure("Authentifizierung bei Hub nicht erfolgreich: " + e.getMessage());
            }
        };
        client.newCall(request).enqueue(commandCallback);
    }

    public synchronized void authenticateOAuth2(String clientId, String clientSecret) {
        FormBody body = new FormBody.Builder().add(
                "grant_type", "client_credentials").
                add("client_id", clientId).
                add("client_secret", clientSecret).build();
        Log.d(AppInfo.APP_NAME, "Requesting Key at: "+this.authUrl);
        Log.d(AppInfo.APP_NAME, "Requesting Key with: "+body);
        Request request = new Request.Builder()
                .url(this.authUrl)
                .addHeader("User-Agent", "Android BiPRO Client")
                .post(body)
                .build();
        final OkHttpClient client = new OkHttpClient.Builder() // Längere Timeouts falls über Mobilnetz
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        final Callback commandCallback = new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.code() == 200) {
                        final String body = response.body().string();
                        Log.d(AppInfo.APP_NAME, "Response: "+body);
                        JSONObject json = new JSONObject(body);
                        String authHeader = json.getString("access_token");
                        BiPROHubAuthenticator.this.onSuccess(authHeader);
                    } else
                        BiPROHubAuthenticator.this.onFailure("Fehler bei Authentifizierung am Hub, HTTP Code " + response.code());
                } catch (Exception e) {
                    Log.e(AppInfo.APP_NAME, "Fehler bei HUB-Anmeldung", e);
                    BiPROHubAuthenticator.this.onFailure(e.getMessage()); // falls bei der Response was schief ging
                } finally {
                    response.close();
                }
            }

            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(AppInfo.APP_NAME, "Fehler bei HUB-Anmeldung", e);
                BiPROHubAuthenticator.this.onFailure("Authentifizierung bei Hub nicht erfolgreich: " + e.getMessage());
            }
        };
        client.newCall(request).enqueue(commandCallback);
    }

    public void onSuccess(String auth) {
        if(auth.startsWith("\""))
            auth = auth.substring(1);
        if(auth.endsWith("\""))
            auth = auth.substring(0, auth.length()-1);
        configuration.putString(context.getString(R.string.prefs_biprohub_auth), auth);
        ContextCompat.getMainExecutor(context).execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, R.string.message_got_biprohub_token, Toast.LENGTH_LONG).show();
            }});
    }

    public void onFailure(String msg) {
        ContextCompat.getMainExecutor(context).execute(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Fehler bei Authentifizierung am Hub");
                builder.setMessage(msg);
                builder.setIcon(R.drawable.ic_error_black_24dp);
                builder.setNeutralButton(R.string.cancel, (dialog, which) -> {});
                builder.create().show();
            }
        });
    }

    private @NonNull String cutLastPath(@NonNull String path) {
        int idx = path.lastIndexOf('/');
        if(idx > 0)
            return path.substring(0, idx);
        else return path;
    }
}
