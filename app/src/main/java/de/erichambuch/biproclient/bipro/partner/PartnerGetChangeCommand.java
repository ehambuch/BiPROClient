package de.erichambuch.biproclient.bipro.partner;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathException;

import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.AbstractDataCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;

public class PartnerGetChangeCommand extends AbstractDataCommand {

    public static final String PARAM_PARTNERNR = "${partnernr}";
    public static final String PARAM_IBAN = "${iban}";
    public static final String PARAM_BIC = "${bic}";
    public static final String PARAM_KONTOINHABER = "${kontoInhaber}";
    public static final String PARAM_REFERENZ = "${referenz}";
    public static final String PARAM_GUELTIGAB = "${gueltigAb}";
    public static final String PARAM_ARTID = "${artId}";

    public PartnerGetChangeCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger,"Partner");
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        cleanupEmptyParameters(parameters);
        super.executePOST(authentication, getUrl(),
                XmlUtils.replace(XmlUtils.processIfs(getConfiguration().getPartnerServiceGetChangeTemplate(), parameters), parameters),
                commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:getChange";
    }

    @Override
    protected String getUrl() {
        return getConfiguration().getPartnerServiceURL();
    }

    @Override
    protected String getVersion() {
        return getConfiguration().getPartnerServiceVersion();
    }

    @Nullable
    public static Map<String,String> getBankverbindung(String xml) {
        try {
            String iban = XmlUtils.getXmlPathNSValue(xml, "//pz-partner:Partner/partner:Bankverbindung/partner:IBAN");
            String bic = XmlUtils.getXmlPathNSValue(xml, "//pz-partner:Partner/partner:Bankverbindung/partner:BIC");
            String referenz = XmlUtils.getXmlPathNSValue(xml, "//pz-partner:Partner/partner:Bankverbindung/partner:Referenz");
            String inhaber = XmlUtils.getXmlPathNSValue(xml, "//pz-partner:Partner/partner:Bankverbindung/partner:Kontoinhaber");
            if (referenz != null && iban != null) {
                Map<String, String> params = new HashMap<>();
                params.put(PARAM_IBAN, iban);
                params.put(PARAM_BIC, bic);
                params.put(PARAM_REFERENZ, referenz);
                params.put(PARAM_KONTOINHABER, inhaber != null ? inhaber : "");
                return params;
            } else
                return null; // no found
        } catch(XPathException e) {
            throw new RuntimeException("Problem analyzing XML response",e);
        }
    }
}
