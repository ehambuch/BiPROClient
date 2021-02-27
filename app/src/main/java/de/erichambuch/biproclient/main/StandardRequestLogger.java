package de.erichambuch.biproclient.main;

import java.util.ArrayList;
import java.util.List;

public class StandardRequestLogger implements RequestLogger {

    private final List<String> logs = new ArrayList<>();

    @Override
    public void logRequest(String request) {
        logs.add(request);
    }

    @Override
    public void logResponse(String response) {
        logs.add(response);
    }

    @Override
    public void clearLog() {
        logs.clear();
    }

    public List<String> iterateLog() {
        return logs;
    }
}
