package de.erichambuch.biproclient.bipro.extranet;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.BiproServiceCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;

/**
 * Klasse zum Ausführen eines <code>getLinks</code> auf dem BiPRO-ExtranetService (Norm 440).
 */
public class ExtranetGetLinksCommand extends BiproServiceCommand {

    public static final String PARAM_NAME = "${name}";
    public static final String PARAM_VORNAME = "${vorname}";
    public static final String PARAM_STRASSE = "${strasse}";
    public static final String PARAM_PLZ = "${plz}";
    public static final String PARAM_ORT = "${ort}";
    public static final String PARAM_GEBURTSDATUM = "${gebDatum}";
    public static final String PARAM_VSNR = "${vsnr}";
    public static final String PARAM_PARTNERNR = "${partnernummerVu}";

    public ExtranetGetLinksCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger);
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        super.completeParameters(parameters, PARAM_PARTNERNR, PARAM_NAME, PARAM_VORNAME, PARAM_STRASSE, PARAM_PLZ, PARAM_ORT, PARAM_GEBURTSDATUM, PARAM_VSNR);
        final String template;
        if (parameters.get(PARAM_VSNR).length() > 1) {
            template = getConfiguration().getBipro440VertragsucheServiceTemplate();
        } else {
            template = getConfiguration().getBipro440KundensucheServiceTemplate();
        }
        String request = XmlUtils.replace(template, parameters);
        super.executePOST(authentication, getUrl(), request, commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:getLinks";
    }

    private String getUrl() {
        return getConfiguration().getBipro440ServiceURL();
    }

    /**
     * Extrahiert die Links aus der Response des Serviceaufrufs.
     * <p>
     *     <pre>
     *         &lt;Link&gt;
     *          &lt;URL&gt;... &lt;/URL&gt;
     *          &lt;Beschreibung&gt;...&lt;/Beschreibung&gt;
     *         &lt;/Link&gt;
     *     </pre>
     * </p>
     * @param xmlResponse SOAP-Response
     * @return List der Links
     * @throws Exception Fehler beim Parsing
     */
    public List<ExtranetLink> parseData(String xmlResponse) throws Exception {
        List<ExtranetLink> urlList = new ArrayList<>();
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        xmlFactoryObject.setNamespaceAware(true);
        XmlPullParser myParser = xmlFactoryObject.newPullParser();
        myParser.setInput(new StringReader(xmlResponse));
        int event = myParser.getEventType();
        // einfachster Parser: suche nach </URL>
        String url = null;
        String beschreibung = null;
        while (event != XmlPullParser.END_DOCUMENT)  {
            String name=myParser.getName();
            switch (event){
                case XmlPullParser.START_TAG:
                    if(name.equals("URL")){
                        url = myParser.nextText();
                    } else if(name.equals("Beschreibung")) {
                        beschreibung = myParser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (name.equals("Link")) {
                        urlList.add(new ExtranetLink(beschreibung, url));
                    }
                    break;
                default: break;
            }
            event = myParser.next();
        }

        Log.d(AppInfo.APP_NAME, urlList.toString());
        return urlList;
    }
}
