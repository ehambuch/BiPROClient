package de.erichambuch.biproclient.utils;

import android.os.Build;

import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

/**
 * Diese Klasse enthält einige Hilfsmethoden zum Umgang mit XML.
 * <p>Eigentlich sollte ein richtiges Web-Service-Framework (JAX-WS, KSOAP2) eingesetzt werden; oder eine Template-Engine zum Erzeugen der Dateien
 * (analog JSP). Leider funktioniert z.B. <a href="https://de.wikipedia.org/wiki/FreeMarker">FreeMarker</a> unter Android nicht.</a></p>
 */
public class XmlUtils {

    private static final String IFDEF_START_TOKEN = "<!--#IFDEF ";
    private static final String IFDEF_CLOSE_TOKEN = "-->";
    private static final String IFDEF_END_TOKEN = "<!--/#IFDEF-->";

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
     * Prozessiert die <code>IFDEF</code> Kommandos und löscht ggf. Teile aus dem XML heraus.
     * @param input Template
     * @param valueMap Map mit Token, Wert
     * @return das Ergebnis
     */
    public static String processIfs(String input, Map<String,String> valueMap) {
        StringBuilder output = new StringBuilder(input.length());
        int startIndex = -1;
        int lastIndex = 0;
        while((startIndex = input.indexOf(IFDEF_START_TOKEN, lastIndex)) >= 0) {
            output.append(input.substring(lastIndex, startIndex)); // alles bis zum if
            lastIndex = input.indexOf(IFDEF_CLOSE_TOKEN, startIndex) + IFDEF_CLOSE_TOKEN.length();
            int endIndexIf = input.indexOf(IFDEF_END_TOKEN, lastIndex);
            String theIfToken = input.substring(startIndex+IFDEF_START_TOKEN.length(), lastIndex-IFDEF_CLOSE_TOKEN.length()).trim();
            if(valueMap.get(theIfToken) != null) { // token ist definiert -> true
                output.append(input.substring(lastIndex, endIndexIf)); // alles im If-Block
                lastIndex = endIndexIf+IFDEF_END_TOKEN.length();
            } else { // if-block loeschen
                lastIndex = endIndexIf+IFDEF_END_TOKEN.length();
            }
        }
        output.append(input.substring(lastIndex));
        return output.toString();
    }

    /**
     * Einfache Methode, um den Wert eines XML-Elements auszulesen.
     *
     * @param input XML-Datenstrom
     * @param element Name des XML-Elements (ohne Namespace)
     * @return der Wert oder null
     * @throws IllegalArgumentException fehlerhaftes XML
     */
    public static String getValueFromElement(String input, String element) throws IllegalArgumentException{
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

    /**
     * Liest ein XS:DateTime nach ISO 8601 mit/ohne Zeitzone.
     *
     * <p>Bei einer Zeitzone wird die Zeit in die Systemzeit konvertiert.</p>
     * @param input
     * @return das Datum oder null
     * @throws DateTimeException
     */
    @Nullable
    public static LocalDateTime parseDateTime(String input) throws DateTimeException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TemporalAccessor temp = DateTimeFormatter.ISO_DATE_TIME.parseBest(input, OffsetDateTime::from, LocalDateTime::from);
            if (temp  instanceof LocalDateTime)
                return (LocalDateTime) temp;
            else if (temp instanceof OffsetDateTime)
                return  ((OffsetDateTime)temp).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            else
                throw new DateTimeException("Unbekannter Typ: "+temp);
        } else
            return null;
    }
}
