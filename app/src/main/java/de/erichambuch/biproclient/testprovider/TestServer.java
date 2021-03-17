package de.erichambuch.biproclient.testprovider;

import android.content.res.Resources;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.utils.XmlUtils;
import fi.iki.elonen.NanoHTTPD;

public class TestServer extends NanoHTTPD {

    private static String responseError = "";
    private static String response260 = "<Identifier>bipro:testserver</Identifier>";
    private static String response440 = "";
    private static String response430list = "";
    private static String response430get= "";
    private static String response501get = "";
    private static String response503get = "";
    private static String response502get = "";
    private static String response480vertrag = "";
    private static String response480kunde = "";
    private static ByteArrayInputStream response430get_stream = new ByteArrayInputStream(new byte[10240]);

    public TestServer(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, List<String>> parms = session.getParameters();
        List<String> p = parms.get("service");
        String service = p != null && p.size() > 0 ? p.get(0) : "service?";
        String method = session.getHeaders().getOrDefault("soapaction", "soapaction?");
        Log.d(AppInfo.APP_NAME, "TestServer: "+service+", "+method);
        switch(service) {
            case "sts":
                return newFixedLengthResponse(Response.Status.OK, "text/xml", response260);
            case "extranet":
                return newFixedLengthResponse(Response.Status.OK, "text/xml", response440);
            case "transfer":
                switch(Objects.requireNonNull(method)) {
                    case "urn:listShipments":
                        return newFixedLengthResponse(Response.Status.OK, "text/xml", response430list);
                    case "urn:getShipment":
                        //return newFixedLengthResponse(Response.Status.OK, "text/xml", response430get);
                        response430get_stream.reset();
                        return newChunkedResponse(Response.Status.OK, "Multipart/Related; start-info=\"text/xml\"; type=\"application/xop+xml\"; boundary=\"uuid:_Startxml\"",
                                response430get_stream);
                }
                break;
            case "vertrag":
                return newFixedLengthResponse(Response.Status.OK, "text/xml", response502get);
            case "partner":
                return newFixedLengthResponse(Response.Status.OK, "text/xml", response501get);
            case "schaden":
                return newFixedLengthResponse(Response.Status.OK, "text/xml", response503get);
            case "listen":
                if (peekIntoBody(session, "CT_Vertragssuche"))
                    return newFixedLengthResponse(Response.Status.OK, "text/xml", response480vertrag);
                else
                    return newFixedLengthResponse(Response.Status.OK, "text/xml", response480kunde);
            default:
                break;
        }
        return returnError("Unbekannter Service (Falsche Konfiguration?): "+service);
    }

    private Response returnError(String msg) {
        Log.e(AppInfo.APP_NAME, "Error in TestServer: "+msg);
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/xml", XmlUtils.replace(responseError, Collections.singletonMap("${msg}", msg)));
    }

    private boolean peekIntoBody(IHTTPSession session, String stringToLookFor) {
        try {
            Map<String,String> body = new HashMap();
            session.parseBody(body);
            return body.values().size() > 0 && body.values().iterator().next().contains(stringToLookFor);
        } catch (ResponseException | IOException e) {
            Log.e(AppInfo.APP_NAME, "Fehler beim Lesen des Requests", e);
            return false;
        }
    }

    public void loadMessages(Resources resources) {
        try(InputStream inputStream = resources.getAssets().open("testserver/response_error.xml")) {
            responseError = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro440.xml")) {
            response440 = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro430_list.xml")) {
            response430list = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro430_get.xml")) {
            response430get = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro430_getstream.bin")) {
            response430get_stream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro501.xml")) {
            response501get = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro502.xml")) {
            response502get = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro503.xml")) {
            response503get = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro480_partner.xml")) {
            response480kunde = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        try(InputStream inputStream = resources.getAssets().open("testserver/response_bipro480_vertrag.xml")) {
            response480vertrag = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
