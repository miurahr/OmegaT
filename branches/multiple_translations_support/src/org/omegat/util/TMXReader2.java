/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2010 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **************************************************************************/
package org.omegat.util;

import gen.core.tmx14.Tmx;
import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Helper for read TMX files, using JAXB.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class TMXReader2 {

    static final JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(Tmx.class);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Read TMX file.
     */
    public static void readTMX(File file, final Language sourceLanguage, final Language targetLanguage,
            boolean isSegmentingEnabled, boolean isProjectTMX, final LoadCallback callback) throws Exception {
        Unmarshaller un = CONTEXT.createUnmarshaller();

        // create a new XML parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(un.getUnmarshallerHandler());

        reader.setEntityResolver(TMX_DTD_RESOLVER);

        // install the callback on all PurchaseOrders instances
        un.setListener(new Unmarshaller.Listener() {
            public void beforeUnmarshal(Object target, Object parent) {
            }

            public void afterUnmarshal(Object target, Object parent) {
                if (target instanceof Tu) {
                    Tu tu = (Tu) target;
                    Tuv s = getTuv(tu, sourceLanguage);
                    Tuv t = getTuv(tu, targetLanguage);
                    if (s != null && t != null) {
                        callback.onTu(tu, s, t);
                    }
                }
            }
        });

        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            reader.parse(new InputSource(in));
        } finally {
            in.close();
        }
    }

    /**
     * Returns Tuv from Tu for specific language.
     * 
     * Language choosed by:<br>
     * - with the same language+country<br>
     * - if not exist, then with the same language but without country<br>
     * - if not exist, then with the same language with whatever country<br>
     */
    private static Tuv getTuv(Tu tu, Language lang) {
        String langLanguage = lang.getLanguageCode();
        String langCountry = lang.getCountryCode();
        Tuv tuvLC = null; // Tuv with the same language+country
        Tuv tuvL = null; // Tuv with the same language only, without country
        Tuv tuvLW = null; // Tuv with the same language+whatever country
        for (int i = 0; i < tu.getTuv().size(); i++) {
            Tuv tuv = tu.getTuv().get(i);
            String tuvLang = tuv.getXmlLang();
            if (tuvLang == null) {
                tuvLang = tuv.getLang();
            }
            if (!langLanguage.regionMatches(true, 0, tuvLang, 0, 2)) {
                // language not equals - there is no sense to processing
                continue;
            }
            if (tuvLang.length() < 3) {
                // language only, without country
                tuvL = tuv;
            } else if (langCountry.regionMatches(true, 0, tuvLang, 3, 2)) {
                // the same country
                tuvLC = tuv;
            } else {
                // other country
                tuvLW = tuv;
            }
        }
        if (tuvLC != null) {
            return tuvLC;
        }
        if (tuvL != null) {
            return tuvL;
        }
        return tuvLW;
    }

    /**
     * Callback for receive data from TMX.
     */
    public interface LoadCallback {
        void onTu(Tu tu, Tuv tuvSource, Tuv tuvTarget);
    }

    protected static final EntityResolver TMX_DTD_RESOLVER = new EntityResolver() {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (systemId.endsWith("tmx11.dtd")) {
                return new InputSource(TMXReader2.class.getResourceAsStream("/schemas/tmx11.dtd"));
            } else if (systemId.endsWith("tmx14.dtd")) {
                return new InputSource(TMXReader2.class.getResourceAsStream("/schemas/tmx14.dtd"));
            } else {
                return null;
            }
        }
    };
}
