package de.erichambuch.biproclient.bipro.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class BiproNamespaceContext implements NamespaceContext {

    private static final Map<String,String> NAMESPACES = new HashMap<>();

    static {
        NAMESPACES.put("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        NAMESPACES.put("basis", "http://www.bipro.net/namespace/basis");
        NAMESPACES.put("nachr", "http://www.bipro.net/namespace/nachrichten");
        NAMESPACES.put("dt", "http://www.bipro.net/namespace/datentypen");
        NAMESPACES.put("gevo", "http://www.bipro.net/namespace/gevo");
        NAMESPACES.put("allg", "http://www.bipro.net/namespace/allgemein");
        NAMESPACES.put("xmime", "http://www.w3.org/2005/05/xmlmime" );
        NAMESPACES.put("partner", "http://www.bipro.net/namespace/partner");
        NAMESPACES.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        NAMESPACES.put("pz-partner", "http://www.bipro.net/namespace/prozesse/partner");
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return NAMESPACES.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for(Map.Entry<String, String> e : NAMESPACES.entrySet()) {
            if(namespaceURI.equals(e.getValue()))
                return e.getKey();
        }
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        for(Map.Entry<String, String> e : NAMESPACES.entrySet()) {
            if(namespaceURI.equals(e.getValue()))
                return Collections.singletonList(e.getValue()).iterator();
        }
        return Collections.emptyIterator();
    }
}
