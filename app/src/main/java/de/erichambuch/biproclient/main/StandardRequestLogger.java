package de.erichambuch.biproclient.main;

import java.util.ArrayList;
import java.util.List;

public class StandardRequestLogger implements RequestLogger {

    private final List<String> logs = new ArrayList<>(12);

    @Override
    public void logRequest(String request) {
        logs.add(request);
        if ( logs.size() > 10 )
            logs.remove(0);
    }

    @Override
    public void logResponse(String response) {
        logs.add(response);
        if ( logs.size() > 10 )
            logs.remove(0);
    }

    @Override
    public void clearLog() {
        logs.clear();
    }

    public List<String> iterateLog() {
        return logs;
    }
}
