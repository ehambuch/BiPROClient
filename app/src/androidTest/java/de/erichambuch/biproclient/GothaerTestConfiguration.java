package de.erichambuch.biproclient;

import android.content.res.Resources;

import de.erichambuch.biproclient.main.provider.ProviderConfiguration;

public class GothaerTestConfiguration extends ProviderConfiguration {

    public GothaerTestConfiguration(Resources resources) {
        super(resources);
    }

    @Override
    public String getProviderName() {
        return "Gothaer-TEST";
    }

    @Override
    public String[] getGDVNummern() {
        return new String[]{"5858", "1108", "4119"};
    }

    @Override
    public String getVuPortalURL() {
        return null;
    }

    @Override
    public String getSTServiceURL() {
        return "https://basicauthsecure.gothaer.de/n410/v1-0/services/UserPasswordLogin";
        // bzw. https://exttest-ists-v2.tgic.de/RST/Issue bzw. PROD https://ists-v2.tgic.de/
    }

    @Override
    public String getBipro440ServiceURL() {
        return "https://public-api-test.gothaer.de/bipro/ExtranetService/ExtranetService";
    }

    @Override
    public String getTgicProviderServiceId() {
        return "http://www.tgic.de/Gothaer/test/easylogin_sso/1.0"; // http://www.tgic.de/Gothaer/prod/easylogin_sso/1.0
    }

    @Override
    public String getVertragServiceURL() {
        return "https://public-api-test.gothaer.de/bipro/VertragService/VertragService_2.6.1.1.0";
    }

    @Override
    public String getTransferServiceURL() {
        return "https://public-api-test.gothaer.de/bipro/TransferService/TransferService_2.6.0.1.0";
    }

    @Override
    public String getListServiceURL() {
        return "https://public-api-test.gothaer.de/bipro/TransferService/ListService_2.6.0.1.0";
    }

    @Override
    public String getPartnerServiceURL() {
        return null;
    }

    @Override
    public String getSchadenServiceURL() {
        return null;
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
}
