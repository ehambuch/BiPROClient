package de.erichambuch.biproclient.bipro.base;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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
import okhttp3.ResponseBody;

public abstract class SOAPCommand {

    private final RequestLogger logger;

    /**
     * Static client: wird von allen Verbindungen genutzt.
     */
    private static final OkHttpClient client = new OkHttpClient.Builder() // Längere Timeouts falls über Mobilnetz
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    protected SOAPCommand(RequestLogger logger) {
        this.logger = logger;
    }

    /**
     * Liefert die SOAP-Action.
     * @return SOAP Action
     */
    protected abstract String getSOAPAction();

    protected void executeCommand(final String url, final String soapRequest, final Callback commandCallback) throws Exception {
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
        final ResponseBody body = response.body();
        if (body != null) {
            try {
                final String contentType = body.contentType() != null ? body.contentType().toString() : "";
                Log.d(AppInfo.APP_NAME, "Response with " + contentType);
                if (contentType.startsWith("multipart")) { // MTOM application/xop+xml
                    try {
                        StringBuilder buffer = new StringBuilder(10240);
                        MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(body.byteStream(), contentType));
                        int count = mp.getCount();
                        for (int i = 0; i < count; i++) {
                            BodyPart bp = mp.getBodyPart(i);
                            buffer.append(new String(IOUtils.toByteArray(bp.getInputStream()), StandardCharsets.UTF_8));
                        }
                        return logResponse(buffer.toString());
                    } catch (MessagingException e) {
                        throw new IOException(e);
                    }
                } else {
                    return logResponse(body.string()); // text/xml
                }
            } finally {
                body.close();
            }
        } else
            throw new IOException("Leere Response mit HTTP Code "+response.code());
    }

    private String logResponse(String response) {
        if (logger != null)
            logger.logResponse(response);
        return response;
    }
}
