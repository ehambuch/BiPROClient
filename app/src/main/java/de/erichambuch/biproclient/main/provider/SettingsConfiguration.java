package de.erichambuch.biproclient.main.provider;

import android.content.Context;

import de.erichambuch.biproclient.main.SettingsDataStore;

/**
 * Configuration, die per Settings einstellbar ist.
 */
public class SettingsConfiguration extends ProviderConfiguration {

    private final SettingsDataStore dataStore;

    public SettingsConfiguration(SettingsDataStore dataStore, Context context) {
        super(context.getResources());
        this.dataStore = dataStore;
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
        return dataStore.getString("prefs_transferservice_url", "");
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
}
