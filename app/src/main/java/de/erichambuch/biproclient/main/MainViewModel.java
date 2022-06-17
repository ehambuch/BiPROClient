package de.erichambuch.biproclient.main;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.erichambuch.biproclient.auth.AuthenticationManager;
import de.erichambuch.biproclient.bipro.extranet.ExtranetLink;
import de.erichambuch.biproclient.bipro.listen.ListResultData;
import de.erichambuch.biproclient.bipro.transfer.TransferEntry;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import tellh.com.recyclertreeview_lib.TreeNode;

/**
 * ViewModel, welches zwischen allen Fragmenten als gemeinsamer Datenspeicher während der Laufzeit
 * der MainActivity dient.
 */
public class MainViewModel extends ViewModel {

    private AuthenticationManager authenticationManager;
    private ProviderConfiguration configuration;

    private List<ExtranetLink> URLs;

    private TreeNode tree;

    private StaticData staticData;

    private String vsnr;

    public RequestLogger getRequestLogger() {
        return requestLogger;
    }

    private RequestLogger requestLogger;

    public String getSchadennummer() {
        return schadennummer;
    }

    public void setSchadennummer(String schadennummer) {
        this.schadennummer = schadennummer;
    }

    private String schadennummer;

    public String getVsnr() {
        return vsnr;
    }

    public void setVsnr(String vsnr) {
        this.vsnr = vsnr;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    private String partnerId;

    public List<TransferEntry> getShipmentList() {
        return shipmentList;
    }

    public List<ListResultData> getListenResultData() {
        return listenResultData;
    }

    public void setShipmentList(List<TransferEntry> shipmentList) {
        this.shipmentList = shipmentList;
    }

    private List<TransferEntry> shipmentList;

    public void setListenResultData(List<ListResultData> listenResultData) {
        this.listenResultData = listenResultData;
    }

    private List<ListResultData> listenResultData;

    private String responseMessage;

    private String xml;

    public void initConfiguration(ProviderConfiguration configuration) {
        this.configuration = configuration;
        this.requestLogger = new StandardRequestLogger();
        this.authenticationManager = new AuthenticationManager(configuration, getRequestLogger());
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public void setURLs(List<ExtranetLink> urls) {
        this.URLs = urls;
    }

    public List<ExtranetLink> getURLs() {
        return URLs;
    }

    public ProviderConfiguration getConfiguration() {
        return configuration;
    }

    public void setResponseMessage(String msg) {
        this.responseMessage = msg;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public String getXml() { return xml; }
    public void setXml(String s) { this.xml = s;}

    public void setTree(TreeNode root) {
        this.tree = root;
    }

    public TreeNode getTree() { return tree; }

    public StaticData getStaticData() {
        return staticData;
    }

    public void setStaticData(StaticData staticData) {
        this.staticData = staticData;
    }
}