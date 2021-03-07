package de.erichambuch.biproclient;

import org.junit.Assert;
import org.junit.Test;

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
}
