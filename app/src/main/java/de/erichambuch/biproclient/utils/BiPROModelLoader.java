package de.erichambuch.biproclient.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse lädt notwendige Dateien des aktuellen BiPRO-Modells.
 */
public class BiPROModelLoader {


    /**
     * Liest die GeVo CSV Datei mit allen numerischen Gevos.
     * @param inputStream
     * @return Map
     */
    public Map<String,String> loadGeVosFromCSV(InputStream inputStream) throws IOException {
        final Map<String,String> gevos = new HashMap<>(1000);
        final List<String[]> csvs = CsvReader.readCsvFile(inputStream);
        for(String[] csvLine : csvs) {
            if(csvLine.length >= 2) {
                gevos.put(csvLine[0],csvLine[1]);
            }
        }
        return gevos;
    }

    /**
     * Liest die Datei biprodatentypen-x.y.z.xsd und wandelt diese in eine CSV um.
     *
     * @param inputStream
     * @throws IOException
     * @return Map der Enums
     */
    public Map<String, Map<String,String>> loadDatentypenXML(InputStream inputStream) throws IOException {
        Map<String,Map<String,String>> csv = new HashMap<>(1000);
        try {
            Map<String,String> stMap = null;
            String enumKey = null;
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            xmlFactoryObject.setNamespaceAware(true);
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                if (event == XmlPullParser.START_TAG) {
                    if (name.equals("simpleType")) {
                        int anzAttribute = myParser.getAttributeCount();
                        if(anzAttribute < 1)
                            continue;
                        if(!"name".equals(myParser.getAttributeName(0)))
                            continue;
                        final String stName = myParser.getAttributeValue(0); // <xsd:simpleType name="ST_XYZ">
                        if(stName.startsWith("ST_"))
                            csv.put(stName, stMap = new HashMap<>());
                        else {
                            stMap = null; }
                    } else if (name.equals("enumeration")) {
                        int anzAttribute = myParser.getAttributeCount();
                        if(anzAttribute < 1)
                            continue;
                        if(!"value".equals(myParser.getAttributeName(0)))
                            continue;
                        enumKey = myParser.getAttributeValue(0); // <xsd:enumeration value="01">
                    } else if( name.equals("documentation")) {
                        if(enumKey != null) {
                            if(stMap != null)
                                stMap.put(enumKey, myParser.nextText());
                        }
                    }
                }
                // reset nach Ende des Tags
                if(event == XmlPullParser.END_TAG && name.equals("simpleType")) {
                    stMap = null;
                }
                if(event == XmlPullParser.END_TAG && name.equals("enumeration"))
                    enumKey = null;
                event = myParser.next();
            }
            return csv;
        }
        catch(XmlPullParserException e) {
            throw new IOException("Ungültiges XML", e);
        }
    }
}
