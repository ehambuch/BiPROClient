package de.erichambuch.biproclient.bipro.schaden;

import java.util.Map;

import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.AbstractDataCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;

public class SchadenGetDataCommand extends AbstractDataCommand {

    public static final String PARAM_SCHADENNR = "${schadennr}";
    public static final String PARAM_VUNR = "${vunr}";

    public SchadenGetDataCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger,"Schadenauskunft");
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        cleanupEmptyParameters(parameters);
        super.executePOST(authentication, getUrl(),
                XmlUtils.replace(XmlUtils.processIfs(getConfiguration().getSchadenServiceGetDataTemplate(), parameters), parameters),
                commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:getData";
    }

    protected String getUrl() {
        return getConfiguration().getSchadenServiceURL();
    }

    @Override
    protected String getVersion() {
        return getConfiguration().getSchadenServiceVersion();
    }
}
