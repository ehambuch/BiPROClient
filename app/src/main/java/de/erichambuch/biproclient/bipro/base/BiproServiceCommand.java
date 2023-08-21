package de.erichambuch.biproclient.bipro.base;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Basisklasse für alle BiPRO-Service-Aufrufe.
 */
public abstract class BiproServiceCommand extends SOAPCommand {

    private static final String PARAM_CONSUMER_ID = "${consumerId}";
    private final ProviderConfiguration config;

    protected final static String PARAM_VERSION = "${version}";

    protected String message = "";

    public BiproServiceCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(logger);
        this.config = configuration;
    }

    /**
     * Ausführung des Kommandos.
     * @param authentication passende Authentifizierung
     * @param parameters Parameter für Aufruf
     * @param commandCallback Callback mit Ergebnis
     */
    public abstract void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback);

    protected ProviderConfiguration getConfiguration() {
        return config;
    }

    protected abstract String getUrl();

    protected abstract String getVersion();

    /**
     * Liefert eine eventuelle Nachricht.
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Complete the HashMap so that for every key there is a value stored (even if "").
     * @param map Parameter
     * @param keys notwendige Schlüssel
     */
    protected void completeParameters(Map<String,String> map, String... keys) {
        for(String k : keys) {
            String v = map.get(k);
            if (v == null)
                map.put(k, "");
        }
    }

    /**
     * Löscht überflüssige Paramter, die mit einem NULL belegt sind heraus.
     * @param map Parameter
     */
    protected void cleanupEmptyParameters(Map<String,String> map) {
        for(Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String,String> entry = iterator.next();
            if (entry.getValue() == null) {
                iterator.remove();
            }
        }
    }

    protected void executePOST(BiproAuthentication authentication, final String url, final String soapRequest, final CommandCallback commandCallback) {
        String finalRequest = soapRequest.
                replace("${soapHeader}", authentication.createSOAPHeader()).
                replace(PARAM_VERSION, getVersion()).
                replace(PARAM_CONSUMER_ID, config.getConsumerID());
        Log.d(AppInfo.APP_NAME, finalRequest);
        Map headers = null;
        try {
            if(getHubAuthentication() != null) {
                headers = new HashMap<>();
                headers.put("Authorization", getHubAuthentication());
            }
            executeCommand(url, finalRequest, headers, createCallback(authentication, commandCallback));
        } catch(Exception e) {
            commandCallback.onFailure(e);
        }
    }

    protected Callback createCallback(BiproAuthentication authentication, final CommandCallback commandCallback) {
        return new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    final String responseXml = createResponse(response);
                    Log.d(AppInfo.APP_NAME, responseXml);
                    if (response.isSuccessful() && isOK(responseXml)) {
                        BiproServiceCommand.this.message = getErrorMessage(responseXml, 200);
                        commandCallback.onSuccess(responseXml);
                    } else {
                        final int code = response.code();
                        if (code == 401 || code == 403)
                            authentication.invalidate();
                        commandCallback.onFailure(new IOException(getErrorMessage(responseXml, code)));
                    }
                } catch(IOException e) {
                    onFailure(call, e); // falls bei der Response was schief ging
                } catch(IllegalArgumentException e) { // fehler beim XML Parsing
                    onFailure(call, new IOException("Fehler im XML", e));
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(AppInfo.APP_NAME, "Fehler beim Aufruf", e);
                commandCallback.onFailure(new IOException("Aufruf des Service nicht erfolgreich: " + e.getMessage(), e));
            }
        };
    }

    /**
     * Liefert das Ergebnis eines BiPRO-Service-Aufrufs
     * @param xmlResponse SOAP-Response
     * @return true, falls Aufruf erfolgreich
     */
    protected boolean isOK(String xmlResponse) {
        String statusID = XmlUtils.getValueFromElement(xmlResponse, "StatusID");
        return "OK".equals(statusID) || "00000".equals(statusID); // für BiPRO 440
    }

    /**
     * Liefert eine lesbare Fehlernachricht, falls Aufruf fehlgeschlagen.
     * @param xmlResponse SOAP-Response oder SOAP-Fault, kann leer sein
     * @param httpCode HTTP-Fehlercode der Response
     * @return lesbare Fehlermeldung
     */
    protected String getErrorMessage(String xmlResponse, int httpCode) {
        String meldungId = XmlUtils.getValueFromElement(xmlResponse, "MeldungID");
        String text = XmlUtils.getValueFromElement(xmlResponse, "Text");
        if (meldungId == null && text == null) { // SOAP:Fault
            text = XmlUtils.getValueFromElement(xmlResponse, "faultstring");
            return text != null ? text : ("Unbekannter Fehler: HTTP "+httpCode);
        } else {
            return meldungId + (text != null ? (": "+text) : "");
        }
    }

    protected String getHubAuthentication() {
        String auth = config.getHubAuthentication();
        return (auth != null && auth.length() > 1) ? auth : null;
    }
}
