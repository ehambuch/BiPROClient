package de.erichambuch.biproclient.auth.sts;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.base.SOAPCommand;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UsernameLoginCommand extends SOAPCommand {

    private final String requestXml;
    private final String stsURL;

    public UsernameLoginCommand(ProviderConfiguration configuration) {
        stsURL = configuration.getSTServiceURL();
        requestXml = configuration.getRequestSTSTemplate();
    }

    public void execute(String userName, String password, final CommandCallback commandCallback)  {
        Map<String,String> valueMap = new HashMap<>();
        valueMap.put("${authUser}", userName);
        valueMap.put("${authPassword}", password);
        try {
            executeCommand(stsURL, XmlUtils.replace(requestXml, valueMap),
                    new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful())
                        commandCallback.onSuccess(XmlUtils.getValueFromElement(createResponse(response), "Identifier")); // BiPro Token);
                    else {
                        commandCallback.onFailure(new IOException("Authentifizierung nicht erfolgreich: HTTP: " + response.code()));
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
        return "urn:RequestSecurityToken";
    }
}
