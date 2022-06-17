package de.erichambuch.biproclient.auth.saml;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.base.SOAPCommand;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Kommando zur Authentifizierung mittels User/Passwort als ersten Schritt in der 2-Faktor-Authentifizierung.
 */
public class Username2FACommand extends SOAPCommand {

    private final String requestXml;
    private final String stsURL;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);

    public Username2FACommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(logger);
        stsURL = configuration.getSTServiceURL();
        requestXml = configuration.getRequestTGICUserPasswordTemplate();
    }

    public void execute(String userName, String password, String serviceId, final CommandCallback commandCallback)  {
        Map<String,String> valueMap = new HashMap<>();
        valueMap.put("${url}", stsURL);
        valueMap.put("${authUser}", userName);
        valueMap.put("${authPassword}", password);
        valueMap.put("${serviceId}", serviceId);
        valueMap.put("${messageId}", "uuid:"+java.util.UUID.randomUUID().toString());
        valueMap.put("${createdDate}", formatter.format(new Date(System.currentTimeMillis())));
        valueMap.put("${expiresDate}", formatter.format(new Date(System.currentTimeMillis() + 5*60*1000))); // 5min
        try {
            executeCommand(stsURL, XmlUtils.replace(requestXml, valueMap), new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String xml = createResponse(response);
                    Log.d(AppInfo.APP_NAME, xml);
                    if (response.isSuccessful()) {
                        String messageId = XmlUtils.getValueFromElement(xml,"MessageID");
                        String titleId = XmlUtils.getValueFromElement(xml,"Title");
                        commandCallback.onSuccess(new String[]{messageId,titleId});
                    }
                    else {
                        String reason = XmlUtils.getValueFromElement(xml, "Reason");
                        commandCallback.onFailure(new IOException("Authentifizierung nicht erfolgreich: "+reason+" (HTTP: " + response.code()+")"));
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
        return "http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue";
    }
}
