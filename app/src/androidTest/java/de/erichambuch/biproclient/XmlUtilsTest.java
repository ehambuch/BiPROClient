package de.erichambuch.biproclient;

import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.util.Collections;

import de.erichambuch.biproclient.utils.XmlUtils;

public class XmlUtilsTest {

    @Test
    public void testReplace() {
        Assert.assertEquals("XersatzX", XmlUtils.replace("X{token}X", Collections.singletonMap("{token}", "ersatz")));
    }

    @Test
    public void testProcessIfs() {
        Assert.assertEquals("ja", XmlUtils.processIfs("<!--#IFDEF {token}-->ja<!--/#IFDEF-->", Collections.singletonMap("{token}", "ersatz")));
        Assert.assertEquals("anfangjaende", XmlUtils.processIfs("anfang<!--#IFDEF {token}-->ja<!--/#IFDEF-->ende", Collections.singletonMap("{token}", "ersatz")));
        Assert.assertEquals("", XmlUtils.processIfs("<!--#IFDEF {token} -->ja<!--/#IFDEF-->", Collections.singletonMap("{tokennein}", "ersatz")));
        Assert.assertEquals("anfangende", XmlUtils.processIfs("anfang<!--#IFDEF {token}-->ja<!--/#IFDEF-->ende", Collections.singletonMap("{tokennein}", "ersatz")));
    }

    @Test
    public void testGetValueFromElement1() {
        Assert.assertEquals("test", XmlUtils.getValueFromElement("<xml>test</xml>", "xml"));
    }

    @Test
    public void testGetValueFromElement2() {
        Assert.assertEquals("test", XmlUtils.getValueFromElement("<voll><myns:xml xmlns:myns=\"testns\">test</myns:xml></voll>", "xml"));
    }

    @Test
    public void testGetValueFromElement3() {
        Assert.assertEquals("", XmlUtils.getValueFromElement("<voll><myns:xml xmlns:myns=\"testns\"></myns:xml></voll>", "xml"));
    }

    @Test
    public void testGetValueFromElement4() {
        Assert.assertNull(XmlUtils.getValueFromElement("<voll><xml></xml></voll>", "unbekannt"));
    }

    @Test
    public void testParseDateTime() {
        // sicherstellen, dass Emulator in der deutschen Zeitzone läuft
        Assert.assertEquals("Europe/Berlin", ZoneId.systemDefault().getId());
        // TODO: funktioniert das auch zur Sommer/Winterzeit? NEIN
        Assert.assertEquals("2021-03-07T01:11:59", XmlUtils.parseDateTime("2021-03-07T01:11:59").toString());
        Assert.assertEquals("2021-03-07T01:11:59.123450", XmlUtils.parseDateTime("2021-03-07T01:11:59.12345").toString());
        Assert.assertEquals("2021-03-07T02:11:59", XmlUtils.parseDateTime("2021-03-07T01:11:59Z").toString());
        Assert.assertEquals("2021-03-07T02:11:59.123450", XmlUtils.parseDateTime("2021-03-07T01:11:59.12345Z").toString());
        // Zeitzone
        Assert.assertEquals("2022-06-21T12:37:58", XmlUtils.parseDateTime("2022-06-21T12:37:58+02:00").toString());
        Assert.assertEquals("2022-06-21T12:37:58.219",XmlUtils.parseDateTime("2022-06-21T12:37:58.219+02:00").toString());

        // und mit verschiedener Zeitzone
        Assert.assertEquals("2022-06-21T10:37:58",XmlUtils.parseDateTime("2022-06-21T12:37:58+04:00").toString());
    }

    @Test
    public void testXmlPath() throws  Exception {
        Assert.assertEquals("wert", XmlUtils.getXmlPathValue("<test><pfad attr=\"xx\">wert</pfad><pfad/></test>", "/test/pfad"));
    }

    @Test
    public void testXmlPathNS() throws  Exception {
        Assert.assertEquals("wert", XmlUtils.getXmlPathNSValue("<head xmlns:dt=\"http://www.bipro.net/namespace/datentypen\"><dt:test><dt:pfad attr=\"xx\">wert</dt:pfad><dt:pfad/></dt:test></head>", "/head/dt:test/dt:pfad"));
    }


    @Test
    public void testXmlPathNotFound() throws  Exception {
        Assert.assertNull(XmlUtils.getXmlPathValue("<test><pfad attr=\"xx\">wert</pfad><pfad/></test>", "/test/xx"));
    }

}
