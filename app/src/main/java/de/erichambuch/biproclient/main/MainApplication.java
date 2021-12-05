package de.erichambuch.biproclient.main;

import android.app.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.erichambuch.biproclient.utils.CsvReader;

public class MainApplication extends Application implements StaticData  {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Map<String,String> gevoMap = Collections.emptyMap();

    private Map<String,Map<String,String>> datentypenMap = Collections.emptyMap();

    /**
     * Lade statische Daten im Hintergrund.
     */
    public void loadStaticData() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Liste der GeVos einlesen
                try(InputStream inputStream = getApplicationContext().getAssets().open("bipromodell/gevos.csv")) {
                    Map<String,String> gevos = new HashMap<>();
                    for(String[] s: CsvReader.readCsvFile(inputStream)) {
                        if(s.length >= 2) {
                            gevos.put(s[0],s[1]);
                        }
                    }
                    setGeVoMap(gevos);
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }

                // Liste der Datentypen
                try(InputStream inputStream = getApplicationContext().getAssets().open("bipromodell/datentypen.csv")) {
                    Map<String,Map<String,String>> dts = new HashMap<>();
                    for(String[] s: CsvReader.readCsvFile(inputStream)) {
                        if(s.length >= 3) {
                            Map<String,String> map = dts.get(s[0]);
                            if (map == null)
                                dts.put(s[0], map = new HashMap<>());
                            map.put(s[1],s[2]);
                        }
                    }
                    setDatentypenMap(dts);
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public synchronized void setGeVoMap(Map<String,String> geVoMap) {
        this.gevoMap = Collections.unmodifiableMap(geVoMap);
    }

    public synchronized void setDatentypenMap(Map<String,Map<String,String>> dtMap) {
        this.datentypenMap = Collections.unmodifiableMap(dtMap);
    }

    @Override
    public synchronized Map<String,String> getGevoMap() {
        return gevoMap;
    }

    @Override
    public synchronized Map<String, String> getDatentypMap(String datentyp) {
        return datentypenMap.get(datentyp);
    }
}
