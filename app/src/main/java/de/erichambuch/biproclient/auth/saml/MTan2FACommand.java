package de.erichambuch.biproclient.auth.saml;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.base.SOAPCommand;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Kommando zum Ausführen der <em>Challenge</em> (mTAN) in der 2-Faktor-Authentifizierung.
 */
public class MTan2FACommand extends SOAPCommand {

    private final String requestXml;
    private final String stsURL;

    public MTan2FACommand(ProviderConfiguration configuration) {
        super(null); // kein Logging
        stsURL = configuration.getSTServiceURL();
        requestXml = configuration.getRequestTGICmTanTemplate();
    }

    public void execute(String requestId, String mTAN, final CommandCallback commandCallback)  {
        Map<String,String> valueMap = new HashMap<>();
        valueMap.put("${url}", stsURL);
        valueMap.put("${mTAN}", mTAN);
        valueMap.put("${requestId}", requestId);
        valueMap.put("${messageId}", "uuid:"+java.util.UUID.randomUUID().toString());
        try {
            executeCommand(stsURL, XmlUtils.replace(requestXml, valueMap), null, new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String xml = createResponse(response);
                    Log.d(AppInfo.APP_NAME, xml);
                    if (response.isSuccessful()) {
                        response.close();
                        commandCallback.onSuccess(XmlUtils.getXmlBlock(xml, "saml2:EncryptedAssertion"));
                    }
                    else {
                        String reason = XmlUtils.getValueFromElement(xml, "Reason");
                        final int code = response.code();
                        response.close();
                        commandCallback.onFailure(new IOException("Authentifizierung nicht erfolgreich: "+reason+" (HTTP: " + code+")"));
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    commandCallback.onFailure(new IOException("Authentifizierung nicht erfolgreich: " + e.getMessage(), e));
                }
            });
        } catch(Exception e) {
            commandCallback.onFailure(e);
        }
    }

    @Override
    protected String getSOAPAction() {
        return "http://docs.oasis-open.org/ws-sx/ws-trust/200512/RSTR/Issue";
    }
}
