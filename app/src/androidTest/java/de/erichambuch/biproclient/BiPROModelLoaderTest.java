package de.erichambuch.biproclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import de.erichambuch.biproclient.utils.BiPROModelLoader;

public class BiPROModelLoaderTest {

    @Test
    public void testLoadDatentypen() throws IOException {
        try(InputStream in = InstrumentationRegistry.getInstrumentation().getTargetContext().getAssets().open("bipromodell/bipro-dt.xsd")) {
            Map<String, Map<String, String>> csv = new BiPROModelLoader().loadDatentypenXML(in);
            assertEquals(1, csv.size());
            assertEquals(2, csv.get("ST_XYZ").size() );
            assertEquals("Wert 01", csv.get("ST_XYZ").get("01") );
            assertEquals("Wert 02", csv.get("ST_XYZ").get("02") );
        }
    }

    @Test
    public void testLoadGevos() throws IOException {
        try(InputStream in = InstrumentationRegistry.getInstrumentation().getTargetContext().getAssets().open("bipromodell/gevos.csv")) {
            Map<String, String> gevos = new BiPROModelLoader().loadGeVosFromCSV(in);
            assertTrue(gevos.size() > 600);
            assertEquals("Ablehnung", gevos.get("100004000"));
        }
    }
}
