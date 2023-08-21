package de.erichambuch.biproclient.main.provider;

import android.content.Context;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.SettingsDataStore;

/**
 * Configuration, die per Settings einstellbar ist.
 */
public class SettingsConfiguration extends ProviderConfiguration {

    private final SettingsDataStore dataStore;
    private final String defaultVersion;

    private final String defaultConsumerID;

    private final Context appContext;

    public SettingsConfiguration(SettingsDataStore dataStore, Context context) {
        super(context.getResources());
        this.appContext = context.getApplicationContext();
        this.dataStore = dataStore;
        defaultVersion = context.getString(R.string.default_version);
        defaultConsumerID = "BiPRO Android Client";
    }

    @Override
    public AuthMethode getAuthMethode() {
        if("saml".equals(dataStore.getString("prefs_auth_verfahren", "sts"))) // TODO: referenziert auf prefs_authverfahren_values
            return AuthMethode.SAML;
        else
            return AuthMethode.STS;
    }

    @Override
    public String getProviderName() {
        return dataStore.getString("prefs_provider_name", "");
    }

    @Override
    public String[] getGDVNummern() { return dataStore.getString("prefs_provider_gdvnr", "0000").split(",");}

    @Override
    public String getSTServiceURL() {
        return dataStore.getString("prefs_sts_url", "");
    }

    @Override
    public String getBipro440ServiceURL() {
        return dataStore.getString("prefs_extranetservice_url", "");
    }

    @Override
    public String getTgicProviderServiceId() {
        return dataStore.getString("prefs_saml_service_id", "");
    }

    @Override
    public String getVertragServiceURL() {
        return dataStore.getString("prefs_vertragservice_url", "");
    }

    @Override
    public String getTransferServiceURL() {
        return dataStore.getString(appContext.getString(R.string.prefs_transferservice_url), "");
    }

    @Override
    public String getListServiceURL() {
        return dataStore.getString("prefs_listservice_url", "");
    }

    @Override
    public String getPartnerServiceURL() {
        return dataStore.getString("prefs_partnerservice_url", "");
    }

    @Override
    public String getSchadenServiceURL() {
        return dataStore.getString("prefs_schadenservice_url", "");
    }

    @Override
    public String getBipro440ServiceVersion() {
        return dataStore.getString("prefs_extranetservice_version", "1.4.1.0");
    }

    @Override
    public String getVertragServiceVersion() {
        return dataStore.getString("prefs_vertragservice_version", defaultVersion);
    }

    @Override
    public String getTransferServiceVersion() {
        return dataStore.getString("prefs_transferservice_version", defaultVersion);
    }

    @Override
    public String getListServiceVersion() {
        return dataStore.getString("prefs_listservice_version", defaultVersion);
    }

    @Override
    public String getPartnerServiceVersion() {
        return dataStore.getString("prefs_partnerservice_version", defaultVersion);
    }

    @Override
    public String getSchadenServiceVersion() {
        return dataStore.getString("prefs_schadenservice_version", defaultVersion);
    }

    @Override
    public String getConsumerID() {
        return dataStore.getString("prefs_consumer_id", defaultConsumerID);
    }

    @Override
    public String getAPIKey() {
        return dataStore.getString(appContext.getString(R.string.prefs_biprohub_apikey), "unbekannt");
    }

    @Override
    public void saveAuthentication(String auth) {
        super.saveAuthentication(auth);
        dataStore.putString(appContext.getString(R.string.prefs_biprohub_auth), auth);
    }

    @Override
    public String getHubAuthentication() {
        return dataStore.getString(appContext.getString(R.string.prefs_biprohub_auth), "");
    }
}
