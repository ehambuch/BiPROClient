package de.erichambuch.biproclient.auth.sts;

import android.os.Build;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

import de.erichambuch.biproclient.auth.BiproAuthentication;

public class BiproTokenAuthentication implements BiproAuthentication {

    public static class BiproToken {
        public String token;
        public LocalDateTime expires;

        public BiproToken(String token, @Nullable LocalDateTime expires) {
            this.token = token;
            this.expires = expires;
        }
    }

    private BiproToken biproToken;

    public BiproTokenAuthentication(BiproToken token) {
        this.biproToken = token;
    }

    public BiproToken getBiproToken() {
        return biproToken;
    }

    @Override
    public String createSOAPHeader() {
        return
        "<soapenv:Header xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
            "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"+
                "<wsc:SecurityContextToken xmlns:wsc=\"http://schemas.xmlsoap.org/ws/2005/02/sc\">"+
                    "<wsc:Identifier>"+biproToken.token+"</wsc:Identifier>"+
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return biproToken == null || (biproToken.expires != null && biproToken.expires.isBefore(LocalDateTime.now()));
        } else
            return true;
    }
}
