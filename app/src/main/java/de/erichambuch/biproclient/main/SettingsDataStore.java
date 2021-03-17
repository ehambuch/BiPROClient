package de.erichambuch.biproclient.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class SettingsDataStore extends PreferenceDataStore {

    private final String prefix;
    private final SharedPreferences sharedPreferences;

    public SettingsDataStore(Context context, int index) {
        super();
        this.prefix = String.valueOf(index);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void putString(String key, @Nullable String value) {
        sharedPreferences.edit().putString(key(key), value).apply();
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        return sharedPreferences.getString(key(key), defValue);
    }

    private String key(String key) {
        return prefix + "_" + key;
    }

    public void readFromJSON(InputStream inStream) throws IOException {
        Reader reader = new InputStreamReader(inStream, StandardCharsets.UTF_8);
        JsonReader json = new JsonReader(reader);
        json.setLenient(true);
        json.beginObject();
        while(json.hasNext()) {
            String name = json.nextName();
            switch (name) {
                case "name":
                    this.putString("prefs_provider_name", json.nextString());
                    break;
                case "stsServiceUrl":
                    this.putString("prefs_sts_url", json.nextString());
                    break;
                case "extranetServiceUrl":
                    this.putString("prefs_extranetservice_url", json.nextString());
                    break;
                case "transferServiceUrl":
                    this.putString("prefs_transferservice_url", json.nextString());
                    break;
                case "vertragServiceUrl":
                    this.putString("prefs_vertragservice_url", json.nextString());
                    break;
                case "listServiceUrl":
                    this.putString("prefs_listservice_url", json.nextString());
                    break;
                case "parterServiceUrl":
                    this.putString("prefs_partnerservice_url", json.nextString());
                    break;
                case "schadenServiceUrl":
                    this.putString("prefs_schadenservice_url", json.nextString());
                    break;
                case "extranetServiceVersion":
                    this.putString("prefs_extranetservice_version", json.nextString());
                    break;
                case "transferServiceVersion":
                    this.putString("prefs_transferservice_version", json.nextString());
                    break;
                case "vertragServiceVersion":
                    this.putString("prefs_vertragservice_version", json.nextString());
                    break;
                case "listServiceVersion":
                    this.putString("prefs_listservice_version", json.nextString());
                    break;
                case "parterServiceVersion":
                    this.putString("prefs_partnerservice_version", json.nextString());
                    break;
                case "schadenServiceVersion":
                    this.putString("prefs_schadenservice_version", json.nextString());
                    break;
                case "vuPortalUrl":
                    this.putString("prefs_vuportal_url", json.nextString());
                    break;
                case "gdvNummern":
                    this.putString("prefs_provider_gdvnr", json.nextString());
                    break;
                case "samlServiceId":
                    this.putString("prefs_saml_service_id", json.nextString());
                    break;
                default:
                    json.skipValue();
                    break;
            }
        }
        json.endObject();
    }

    public void writeToJSON(OutputStream outputStream) throws IOException {
        // TODO: umstrukturieren service { name, url, version }
        Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        JsonWriter json = new JsonWriter(writer);
        json.beginObject().name("name").value(this.getString("prefs_provider_name", ""))
                .name("stsServiceUrl").value(this.getString("prefs_sts_url", ""))
                .name("extranetServiceUrl").value(this.getString("prefs_extranetservice_url", ""))
                .name("transferServiceUrl").value(this.getString("prefs_transferservice_url", ""))
                .name("vertragServiceUrl").value(this.getString("prefs_vertragservice_url", ""))
                .name("listServiceUrl").value(this.getString("prefs_listservice_url", ""))
                .name("partnerServiceUrl").value(this.getString("prefs_partnerservice_url", ""))
                .name("schadenServiceUrl").value(this.getString("prefs_schadenservice_url", ""))
                .name("extranetServiceVersion").value(this.getString("prefs_extranetservice_version", ""))
                .name("transferServiceVersion").value(this.getString("prefs_transferservice_version", ""))
                .name("vertragServiceVersion").value(this.getString("prefs_vertragservice_version", ""))
                .name("listServiceVersion").value(this.getString("prefs_listservice_version", ""))
                .name("partnerServiceVersion").value(this.getString("prefs_partnerservice_version", ""))
                .name("schadenServiceVersion").value(this.getString("prefs_schadenservice_version", ""))
                .name("vuPortalUrl").value(this.getString("prefs_vuportal_url", ""))
                .name("samlServiceId").value(this.getString("prefs_saml_service_id", ""))
                .name("gdvNummern").value(this.getString("prefs_provider_gdvnr", "")).endObject();
        json.flush();
    }
}
