package de.erichambuch.biproclient.bipro.vertrag;

import java.util.Map;

import de.erichambuch.biproclient.auth.BiproAuthentication;
import de.erichambuch.biproclient.bipro.base.AbstractDataCommand;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.main.RequestLogger;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.utils.XmlUtils;

public class VertragGetDataCommand extends AbstractDataCommand {
    public static final String PARAM_VSNR = "${vsnr}";
    public static final String PARAM_VUNR = "${vunr}";

    public VertragGetDataCommand(ProviderConfiguration configuration, RequestLogger logger) {
        super(configuration, logger, "Vertrag");
    }

    @Override
    public void execute(BiproAuthentication authentication, Map<String, String> parameters, final CommandCallback commandCallback)  {
        cleanupEmptyParameters(parameters);
        super.executePOST(authentication, getUrl(),
                XmlUtils.replace(XmlUtils.processIfs(getConfiguration().getVertragServiceGetDataTemplate(), parameters), parameters),
                commandCallback);
    }

    @Override
    protected String getSOAPAction() {
        return "urn:getData";
    }

    private String getUrl() {
        return getConfiguration().getVertragServiceURL();
    }


}
