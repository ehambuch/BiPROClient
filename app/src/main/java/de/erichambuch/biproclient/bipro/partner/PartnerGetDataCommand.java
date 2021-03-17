package de.erichambuch.biproclient.bipro.partner;

import java.util.Map;

import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.AbstractDataCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;

public class PartnerGetDataCommand extends AbstractDataCommand {

    public static final String PARAM_PARTNERNR = "${partnernr}";
    public static final String PARAM_VUNR = "${vunr}";

    public PartnerGetDataCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger,"Partner");
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        cleanupEmptyParameters(parameters);
        super.executePOST(authentication, getUrl(),
                XmlUtils.replace(XmlUtils.processIfs(getConfiguration().getPartnerServiceGetDataTemplate(), parameters), parameters),
                commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:getData";
    }

    @Override
    protected String getUrl() {
        return getConfiguration().getPartnerServiceURL();
    }

    @Override
    protected String getVersion() {
        return getConfiguration().getPartnerServiceVersion();
    }
}
