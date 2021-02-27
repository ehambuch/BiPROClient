package de.erichambuch.biproclient.main;

public interface RequestLogger {
    public void logRequest(String request);
    public void logResponse(String response);
    public void clearLog();
}
