package de.erichambuch.biproclient;

/**
 * Autor: Eric Hambuch (erichambuch@googlemail.com)
 * Lizenz: <a href="https://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>
 * <p>
 *     Versionen: 1.0 (07.03.2021) erstes Release
 *     1.0.1 (11) (13.03.2021) - Bugfix für Scrollbar
 *     1.1.0 (12) (17.03.2021) - Transferservice nimmt auch Base64-Encoding, Versionsnummern für alle Services
 *     1.1.1 (13) (18.03.2021) - Validierung des Inputs
 *     1.2.0 (14) (19.03.2021) - Fehlerkorrekturen, Expand All bei TreeView
 *     1.3.0 (15) () - Updated Google Libs
 *     1.3.1 (16) (15.08.2021) - Inkompatibilität behoben bzgl. appcompat
 *     1.4.0 (17) - Update Android 12
 *     1.4.1 (18) - Anzeige OSS Lizenzen
 *     1.4.2 (19) - Update Libs, Bugfixes
 *     1.5.0 (20) - Verbessertes Logging bei Anmeldung, Android 8 als Minimum
 *     1.5.1 (21/22) - NetworkPolicy lässt auch HTTP zu
 *     1.5.2 (23) - Fehlerkorrektur Parsing von ISO 8601 Datum
 *     1.6.0 (24) - Update Android 13/Libs
 *     1.7.0 (25/26) - BiPRO Hub (für Tests), Neues Material3 Design
 *     1.8.0 (27/28) - Android 14, Hub, Pfefferminzia nun Test-Versicherung, Impressum als local.config
 *     1.9.0 - Laden des BiPRO-Modells (XSD, CSV)
 * </p>
 * <p>
 *     Offene Themen:<ol>
 *         <li>VU-spezifische Befüllung BiPRO</li>
 *         <li>Timeout erhöhen</li>
 *         <li>k2SOAP einsetzen</li>
 *         <li>Fehler beim Einstellung laden</li>
 *     </ol>
 * </p>
 */
public class AppInfo {

    public static final String APP_NAME = "BiPROClient";
}
