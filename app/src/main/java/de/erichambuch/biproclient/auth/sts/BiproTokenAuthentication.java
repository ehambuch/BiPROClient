package de.erichambuch.biproclient.auth.sts;

import de.erichambuch.biproclient.auth.BiproAuthentication;

public class BiproTokenAuthentication implements BiproAuthentication {

    private String biproToken;

    public BiproTokenAuthentication(String token) {
        this.biproToken = token;
    }

    public String getBiproToken() {
        return biproToken;
    }

    @Override
    public String createSOAPHeader() {
        return
        "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
            "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"+
                "<wsc:SecurityContextToken xmlns:wsc=\"http://schemas.xmlsoap.org/ws/2005/02/sc\">"+
                    "<wsc:Identifier>"+biproToken+"</wsc:Identifier>"+
                "</wsc:SecurityContextToken>"+
            "</wsse:Security>"+
        "</soapenv:Header>";
    }

    @Override
    public void invalidate() {
        biproToken = null;
    }

    @Override
    public boolean requiresReauthentication() {
        return biproToken == null;
        // TODO: expiry prüfen
    }


}
