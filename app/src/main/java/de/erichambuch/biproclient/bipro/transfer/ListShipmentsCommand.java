package de.erichambuch.biproclient.bipro.transfer;

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

public class ListShipmentsCommand extends BiproServiceCommand {

    public static final String PARAM_GEVO = "${geVo}";
    public static final String PARAM_ACKNOWLEDGED = "${acks}";

    public ListShipmentsCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger);
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        super.completeParameters(parameters, PARAM_ACKNOWLEDGED);
        String request = XmlUtils.replace(getConfiguration().getBiproTransferListShipmentsTemplate(), parameters);
        super.executePOST(authentication, getUrl(), request, commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:listShipments";
    }

    @Override
    protected String getUrl() {
        return getConfiguration().getTransferServiceURL();
    }

    @Override
    protected String getVersion() {
        return getConfiguration().getTransferServiceVersion();
    }


    public List<TransferEntry> parseData(String xmlResponse) throws Exception {
        List<TransferEntry> urlList = new ArrayList<>();
        XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
        xmlFactoryObject.setNamespaceAware(true);
        XmlPullParser myParser = xmlFactoryObject.newPullParser();
        myParser.setInput(new StringReader(xmlResponse));
        int event = myParser.getEventType();
        // einfachster Parser: suche nach </Lieferung>
        String id = null;
        String gevo = null;
        String art = null;
        int counter = 0; // maximale Anzahl
        while (event != XmlPullParser.END_DOCUMENT && counter < 100)  {
            String name=myParser.getName();
            switch (event){
                case XmlPullParser.START_TAG:
                    if(name.equals("ID")){
                        id = myParser.nextText();
                    } else if(name.equals("Kategorie")) {
                        gevo = myParser.nextText();
                    } else if(name.equals("ArtDerLieferung")) {
                        art = myParser.nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (name.equals("Lieferung")) {
                        urlList.add(new TransferEntry(id, gevo, art));
                        id = null;
                        gevo = null;
                        art = null;
                        counter++;
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
