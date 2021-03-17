package de.erichambuch.biproclient.bipro.listen;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.BiproServiceCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;

public class ListVertragCommand extends BiproServiceCommand {

    public static final String PARAM_VSNR = "${vsnr}";
    public static final String PARAM_PARTNERID = "${partner}";
    public static final String PARAM_VORGANGID = "${vorgang}";
    public static final String PARAM_VUNR = "${vunr}";

    public ListVertragCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger);
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        cleanupEmptyParameters(parameters); // wir löschen leere Parameter ganz raus
        String request = XmlUtils.replace(XmlUtils.processIfs(getConfiguration().getListServiceEnumVertragTemplate(), parameters), parameters);
        super.executePOST(authentication, getUrl(), request, commandCallback);
    }

    @Override
    protected String getUrl() {
        return  getConfiguration().getListServiceURL();
    }

    @Override
    protected String getVersion() {
        return getConfiguration().getListServiceVersion();
    }


    @Override
    protected String getSOAPAction() {
        return "http://www.w3.org/2011/03/ws-enu/Enumerate";
    }

    protected boolean isOK(String xmlResponse) { // keine normale BiPRO-Statusmessage
        return XmlUtils.containsElement(xmlResponse, "EnumerateResponse");
    }

    public List<ListResultData> parseData(String data) throws Exception {
        List<ListResultData> resultList = new ArrayList<>();
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        xmlFactoryObject.setNamespaceAware(true);
        XmlPullParser myParser = xmlFactoryObject.newPullParser();
        myParser.setInput(new StringReader(data));
        int event = myParser.getEventType();
        String vsnr = null, partnerID = null, strasse = null, plz = null, ort= null, vorname= null, geburtsdatum = null, anrede = null, name=null;
        int counter = 0; // maximale Anzahl
        while (event != XmlPullParser.END_DOCUMENT && counter < 100) {
            final String tagName = myParser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equals("Versicherungsscheinnummer")) {
                        vsnr = myParser.nextText();
                    } else if (tagName.equals("Name")) {  // TODO: ggf. meherer PArtner im Ergebnis
                        name = myParser.nextText();
                    } else if (tagName.equals("PartnerID")) {
                        partnerID = myParser.nextText();
                    } else if (tagName.equals("Strasse")) {
                        strasse = myParser.nextText();
                    } else if (tagName.equals("Postleitzahl")) {
                        plz = myParser.nextText();
                    } else if (tagName.equals("Ort")) {
                        ort = myParser.nextText();
                    } else if (tagName.equals("Vorname")) {
                        vorname = myParser.nextText();
                    } else if (tagName.equals("Geburtsdatum")) {
                        geburtsdatum = myParser.nextText();
                    } else if( tagName.equals("Anrede")) {
                        anrede = myParser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equals("Ergebniseintrag")) {
                        ListResultData entry = new ListResultData();
                        entry.vsnr = vsnr;
                        entry.anrede = anrede;
                        entry.name = vorname != null ? (vorname + " " + name) : name;
                        entry.anschrift = emptystring(strasse) + ", "+emptystring(plz)+" "+emptystring(ort);
                        entry.geburtsdatum = geburtsdatum;
                        entry.partnerId = partnerID;
                        resultList.add(entry);
                        vsnr = null; partnerID = null; strasse = null; plz = null; ort= null; vorname= null; geburtsdatum = null; anrede = null;
                        counter++;
                    }
                    break;
                default:
                    break;
            }
            event = myParser.next();
        }
        return resultList;
    }

    private static String emptystring(String s) {
        return s != null ? s : "";
    }
}
