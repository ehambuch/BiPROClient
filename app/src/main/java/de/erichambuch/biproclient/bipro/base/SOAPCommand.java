package de.erichambuch.biproclient.bipro.base;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.main.RequestLogger;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class SOAPCommand {

    private final RequestLogger logger;

    protected SOAPCommand(RequestLogger logger) {
        this.logger = logger;
    }

    protected SOAPCommand() {
        this(null);
    }

    /**
     * Liefert die SOAP-Action.
     * @return
     */
    protected abstract String getSOAPAction();

    protected void executeCommand(final String url, final String soapRequest, final Callback commandCallback) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Log.d(AppInfo.APP_NAME, soapRequest);
        if (logger != null)
            logger.logRequest(soapRequest);
        RequestBody body = RequestBody.create(soapRequest, MediaType.parse("text/xml"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Android BiPRO Client")
                .addHeader("SOAPAction", getSOAPAction())
                .post(body)
                .build();
        client.newCall(request).enqueue(commandCallback);
    }

    /**
     * Erstellt einen String aus der Response.
     * <p>Dabei wird sowohl text/xml als auch binär codierte Multipart-Messages gehandelt.</p>
     * @param response HTTP Response
     * @return Antwort als String
     * @throws IOException
     */
    protected String createResponse(Response response) throws IOException {
        Log.d(AppInfo.APP_NAME, "Response with "+response.body().contentType().toString());
        if (response.body().contentType().toString().startsWith("multipart/related")) { // MTOM application/xop+xml
            try {
                StringBuilder buffer = new StringBuilder(10240);
                MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(response.body().byteStream(), response.body().contentType().toString()));
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart bp = mp.getBodyPart(i);
                    buffer.append(new String(IOUtils.toByteArray(bp.getInputStream()), StandardCharsets.UTF_8));
                }
                return logResponse(buffer.toString());
            } catch(MessagingException e) {
                throw new IOException(e);
            }
        } else
            return logResponse(response.body().string()); // text/xml
    }

    private String logResponse(String response) {
        if (logger != null)
            logger.logResponse(response);
        return response;
    }
}
