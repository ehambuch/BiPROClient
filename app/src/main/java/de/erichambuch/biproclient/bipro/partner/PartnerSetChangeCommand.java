package de.erichambuch.biproclient.bipro.partner;

import java.util.Map;

import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.AbstractDataCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;

public class PartnerSetChangeCommand extends AbstractDataCommand {

    public PartnerSetChangeCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger,"Partner");
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        cleanupEmptyParameters(parameters);
        super.executePOST(authentication, getUrl(),
                XmlUtils.replace(XmlUtils.processIfs(getConfiguration().getPartnerServiceSetChangeTemplate(), parameters), parameters),
                commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:setChange";
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
