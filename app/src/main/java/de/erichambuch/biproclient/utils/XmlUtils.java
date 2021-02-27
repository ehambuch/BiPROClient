package de.erichambuch.biproclient.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

// TODO: Ersetzen durch Template Engine (z.B. https://de.wikipedia.org/wiki/FreeMarker)
// und XPath bzw. DOM-Parser,
public class XmlUtils {

    /**
     * Ersetzt Tokens in einem String durch vorgegebene Werte. Dabei darf jedes Token nur 1x vorkommen.
     * @param input Eingabe mit Tokens
     * @param valueMap Map mit Token,Wert
     * @return ersetzte Eingabe
     */
    public static String replace(String input, Map<String,String> valueMap) {
        for(Map.Entry<String,String> entry : valueMap.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue());
        }
        return input;
    }

    /**
     * Einfache Methode, um den Wert eines XML-Elements auszulesen.
     *
     * @param input XML-Datenstrom
     * @param element Name des XML-Elements (ohne Namespace)
     * @return der Wert oder null
     */
    public static String getValueFromElement(String input, String element) {
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(true);
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(new StringReader(input));
            int event = myParser.getEventType();;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                if (event == XmlPullParser.START_TAG) {
                    if (name.equals(element))
                        return myParser.nextText();
                }
                event = myParser.next();
            }
            return null;
        }
        catch(XmlPullParserException | IOException e) {
            throw new IllegalArgumentException("Ungültiges XML", e);
        }
    }

    public static boolean containsElement(String input, String element) {
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(true);
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(new StringReader(input));
            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                if (event == XmlPullParser.START_TAG) {
                    if (name.equals(element))
                        return true;
                }
                event = myParser.next();
            }
            return false;
        }
        catch(XmlPullParserException | IOException e) {
            throw new IllegalArgumentException("Ungültiges XML", e);
        }
    }

    public static String getXmlBlock(String input, String node) {
        int from = input.indexOf("<"+node);
        if (from < 0 )
            return null;
        int to = input.indexOf("</"+node, from);
        if (to < 0)
            return null;
        int to2 = input.indexOf('>', to);
        if (to2 < 0 )
            return null;
        return input.substring(from, to2+1); // <node>...</node>
    }
}
