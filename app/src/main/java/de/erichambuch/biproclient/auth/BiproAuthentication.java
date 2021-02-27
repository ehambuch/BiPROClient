package de.erichambuch.biproclient.auth;

public interface BiproAuthentication {

    public String createSOAPHeader();

    public void invalidate();

    public boolean requiresReauthentication();
}
