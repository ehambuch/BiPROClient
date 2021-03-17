package de.erichambuch.biproclient.main.provider;

import android.content.res.Resources;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Configuration für jeweiligen Service-Provider (VU).
 */
public abstract class ProviderConfiguration {

    public enum AuthMethode {
        STS, SAML
    }

    private final Resources resources;

    public ProviderConfiguration(Resources resources)
    {
        this.resources = resources;
    }

    public AuthMethode getAuthMethode()  {
        return AuthMethode.STS;
    }

    public abstract String getProviderName();

    public abstract String[] getGDVNummern();

    public abstract String getVuPortalURL();

    public abstract String getSTServiceURL();

    public abstract String getBipro440ServiceURL();

    public abstract String getTgicProviderServiceId();

    public abstract String getVertragServiceURL();

    public abstract String getTransferServiceURL();

    public abstract String getListServiceURL();

    public abstract String getPartnerServiceURL();

    public abstract String getSchadenServiceURL();

    public abstract String getBipro440ServiceVersion();

    public abstract String getVertragServiceVersion();

    public abstract String getTransferServiceVersion();

    public abstract String getListServiceVersion();

    public abstract String getPartnerServiceVersion();

    public abstract String getSchadenServiceVersion();

    public String getBipro440KundensucheServiceTemplate() {
        try(InputStream inputStream = resources.getAssets().open("soap/request_bipro440-kunde.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getBipro440VertragsucheServiceTemplate() {
        try(InputStream inputStream = resources.getAssets().open("soap/request_bipro440-vertrag.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBiproTransferListShipmentsTemplate() {
        try(InputStream inputStream = resources.getAssets().open("soap/request_bipro430-list.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBiproTransferGetShipmentTemplate() {
        try(InputStream inputStream = resources.getAssets().open("soap/request_bipro430-get.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRequestSTSTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_sts.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRequestTGICUserPasswordTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_saml_issue.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String  getRequestTGICmTanTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_saml_challenge.xml" )) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getVertragServiceGetDataTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_bipro502-getdata.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPartnerServiceGetDataTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_bipro501-getdata.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSchadenServiceGetDataTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_bipro503-getdata.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getListServiceEnumPartnerTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_bipro480-partner.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getListServiceEnumVertragTemplate() {
        try (InputStream inputStream = resources.getAssets().open("soap/request_bipro480-vertrag.xml")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
