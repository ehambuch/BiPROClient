package de.erichambuch.biproclient.main.provider;

import android.content.res.Resources;

public class PfefferminziaConfiguration extends ProviderConfiguration {

    public static final int PORT = 55433;

    public PfefferminziaConfiguration(Resources resources) {
        super(resources);
    }

    @Override
    public String getProviderName() {
        return "Pfefferminzia";
    }

    @Override
    public String[] getGDVNummern() { return new String[]{"0000"};}

    @Override
    public String getSTServiceURL() {
        return "http://localhost:55433/service?service=sts";
    }

    @Override
    public String getBipro440ServiceURL() {
        return "http://localhost:55433/service?service=extranet";
    }

    @Override
    public String getTgicProviderServiceId() {
        return "";
    }

    @Override
    public String getVertragServiceURL() {
        return "http://localhost:55433/service?service=vertrag";
    }

    @Override
    public String getTransferServiceURL() {
        return "http://localhost:55433/service?service=transfer";
    }

    @Override
    public String getListServiceURL() {
        return "http://localhost:55433/service?service=listen";
    }

    @Override
    public String getPartnerServiceURL() {
        return "http://localhost:55433/service?service=partner";    }

    @Override
    public String getSchadenServiceURL() {
        return "http://localhost:55433/service?service=schaden";
    }

    @Override
    public String getBipro440ServiceVersion() {
        return "1.0.1.0";
    }

    @Override
    public String getVertragServiceVersion() {
        return "2.6.0.1.0";
    }

    @Override
    public String getTransferServiceVersion() {
        return "2.6.0.1.0";
    }

    @Override
    public String getListServiceVersion() {
        return "2.6.0.1.0";
    }

    @Override
    public String getPartnerServiceVersion() {
        return "2.6.0.1.0";
    }

    @Override
    public String getSchadenServiceVersion() {
        return "2.6.0.1.0";
    }

    @Override
    public String getAPIKey() {
        return "N/A";
    }
}
