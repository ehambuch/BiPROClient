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
        super.completeParameters(parameters, PARAM_SCHADENNR, PARAM_VUNR);
        super.executePOST(authentication, getUrl(), XmlUtils.replace(getConfiguration().getSchadenServiceGetDataTemplate(), parameters), commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:getData";
    }

    private String getUrl() {
        return getConfiguration().getSchadenServiceURL();
    }
}
