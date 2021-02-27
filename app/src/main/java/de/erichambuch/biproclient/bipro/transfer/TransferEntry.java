package de.erichambuch.biproclient.bipro.transfer;

public class TransferEntry {

    private final String shipmentID;
    private final String gevo;
    private final String art;

    private byte[] document;
    private String documentName;
    private String contentType;

    public TransferEntry(String id, String gevo, String art) {
        this.shipmentID = id;
        this.gevo = gevo;
        this.art = art;
    }

    public String getShipmentID() {
        return shipmentID;
    }

    public String getGevo() {
        return gevo;
    }

    public String getArt()  { return art; }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
