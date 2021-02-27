package de.erichambuch.biproclient.main;

import java.util.Map;

public interface StaticData {

    /**
     * Liefert eine Map mit GeVo-Schlüsseln und zugehörigen Beschreibungen.
     * @return Map
     */
    public Map<String,String> getGevoMap();

    /**
     * Liefert eine Map mit Aufzählungswerten eines Detentypen.
     * @param datentyp Name des ST_ oder STE_Typen
     * @return Map oder null
     */
    public Map<String,String> getDatentypMap(String datentyp);

}
