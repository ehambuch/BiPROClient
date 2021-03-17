package de.erichambuch.biproclient.bipro.base;

import android.webkit.WebView;

import java.nio.charset.StandardCharsets;

import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.auth.saml.BiproSAMLAuthentication;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;

public class OpenVuPortalCommand {

    private final ProviderConfiguration configuration;

    public OpenVuPortalCommand(ProviderConfiguration configuration) {
        this.configuration = configuration;
    }

    public void execute(BiproAuthentication authentication, WebView webView, CommandCallback commandCallback) {
        if ( authentication instanceof BiproSAMLAuthentication) { // SAML POST Binding ?
            webView.postUrl(configuration.getVuPortalURL(), ((BiproSAMLAuthentication) authentication).getSamlArtefact().getBytes(StandardCharsets.UTF_8));
        } else {
            webView.loadUrl(configuration.getVuPortalURL());
        }
    }
}
