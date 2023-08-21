package de.erichambuch.biproclient.auth.vdg;

import de.erichambuch.biproclient.bipro.base.SOAPCommand;
import de.erichambuch.biproclient.main.RequestLogger;
// TODO
public class VDGTokenCommand extends SOAPCommand {


    protected VDGTokenCommand(RequestLogger logger) {
        super(logger);
    }

    @Override
    protected String getSOAPAction() {
        return null;
    }
}
