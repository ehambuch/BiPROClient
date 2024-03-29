package de.erichambuch.biproclient.auth.sts;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.base.SOAPCommand;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UsernameLoginCommand extends SOAPCommand {

    private final String requestXml;
    private final String stsURL;

    public UsernameLoginCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(logger);
        stsURL = configuration.getSTServiceURL();
        requestXml = configuration.getRequestSTSTemplate();
    }

    public void execute(String userName, String password, final CommandCallback commandCallback)  {
        Map<String,String> valueMap = new HashMap<>();
        valueMap.put("${authUser}", userName);
        valueMap.put("${authPassword}", password);
        try {
            executeCommand(stsURL, XmlUtils.replace(requestXml, valueMap), null,
                    new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseXml = createResponse(response);
                        final String token = XmlUtils.getValueFromElement(responseXml, "Identifier");
                        final String expires = XmlUtils.getValueFromElement(responseXml, "Expires");
                        final LocalDateTime expiresDate = parseExpires(expires);
                        response.close();
                        commandCallback.onSuccess(
                                new BiproTokenAuthentication.BiproToken(token, expiresDate));
                    } else {
                        int code = response.code();
                        response.close();
                        commandCallback.onFailure(new IOException("Authentifizierung nicht erfolgreich: HTTP: " + code));
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

    private LocalDateTime parseExpires(String expires) throws IOException {
        if (expires != null && expires.length() > 0 ) {
            try {
                return XmlUtils.parseDateTime(expires); // XML DateTime, kann ohne oder mit Zeitzone sein
            } catch (DateTimeParseException e) {
                throw new IOException("Fehler beim Lesen des Expires: " + expires + " : " + e.getMessage());
            }
        } else {
            return null;
        }
    }
}
