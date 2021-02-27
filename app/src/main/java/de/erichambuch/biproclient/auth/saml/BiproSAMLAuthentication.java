package de.erichambuch.biproclient.auth.saml;

import de.erichambuch.biproclient.auth.BiproAuthentication;

public class BiproSAMLAuthentication implements BiproAuthentication {

    private String samlArtefact;

    public BiproSAMLAuthentication(String artefact) {
        this.samlArtefact = artefact;
    }

    @Override
    public String createSOAPHeader() {
        return
                "<soapenv:Header>" +
                        "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"+
                        samlArtefact+
                        "</wsse:Security></soapenv:Header>";
    }

    @Override
    public void invalidate() {
        samlArtefact = null;
    }

    @Override
    public boolean requiresReauthentication() {
        return (samlArtefact == null); // TODO expiry
    }
}
