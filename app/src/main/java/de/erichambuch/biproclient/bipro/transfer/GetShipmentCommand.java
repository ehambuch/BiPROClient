package de.erichambuch.biproclient.bipro.transfer;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.AbstractDataCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;
import okhttp3.Response;

public class GetShipmentCommand extends AbstractDataCommand {

    public static class Attachment {
        public byte[] data;
        public String contentType;
    }

    public static final String PARAM_ID = "${id}";

    private List<Attachment> attachmentList;

    public GetShipmentCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger,"Lieferung");
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        super.completeParameters(parameters, PARAM_ID);
        String request = XmlUtils.replace(getConfiguration().getBiproTransferGetShipmentTemplate(), parameters);
        super.executePOST(authentication, getUrl(), request, commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:getShipment";
    }

    private String getUrl() {
        return getConfiguration().getTransferServiceURL();
    }

    @Override
    protected synchronized String createResponse(@NotNull Response response) throws IOException {
        Log.d(AppInfo.APP_NAME, "MTOM Response");
        attachmentList = new ArrayList<>(2);
        String contentType = response.header("Content-Type");
        if (contentType.toLowerCase().startsWith("multipart/related")) { // MTOM application/xop+xml
            String xml = "";
            try {
                MimeMultipart mp = new MimeMultipart(new ByteArrayDataSource(response.body().byteStream(), contentType));
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart bp = mp.getBodyPart(i);
                    if ( i == 0)
                        xml = new String(IOUtils.toByteArray(bp.getInputStream()), StandardCharsets.UTF_8);
                    else {
                        Attachment attachment = new Attachment();
                        attachment.data = IOUtils.toByteArray(bp.getInputStream());
                        attachment.contentType = bp.getContentType();
                        attachmentList.add(attachment);
                    }
                }
                return xml;
            } catch(MessagingException e) {
                throw new IOException(e);
            }
        } else
            return response.body().string(); // text/xml
    }

    public synchronized List<Attachment> getAttachments() {
        return attachmentList;
    }
}
