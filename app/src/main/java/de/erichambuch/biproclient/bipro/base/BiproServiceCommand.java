package de.erichambuch.biproclient.bipro.base;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

    private final ProviderConfiguration config;

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

    /**
     * Liefert eine eventuelle Nachricht.
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Complete the HashMap so that for every key there is a value stored (even if "").
     * @param map
     * @param keys
     */
    protected void completeParameters(Map<String,String> map, String... keys) {
        for(String k : keys) {
            String v = map.get(k);
            if (v == null)
                map.put(k, "");
        }
    }

    protected void executePOST(BiproAuthentication authentication, final String url, final String soapRequest, final CommandCallback commandCallback) {
        String finalRequest = soapRequest.replace("${soapHeader}", authentication.createSOAPHeader());
        Log.d(AppInfo.APP_NAME, finalRequest);
        try {
            executeCommand(url, finalRequest, createCallback(authentication, commandCallback));
        } catch(Exception e) {
            commandCallback.onFailure(e);
        }
    }

    protected Callback createCallback(BiproAuthentication authentication, final CommandCallback commandCallback) {
        return new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
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
            return meldungId+": "+text;
        }

    }
}
