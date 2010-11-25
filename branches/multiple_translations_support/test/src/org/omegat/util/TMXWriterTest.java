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

import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;

import java.io.File;

import org.custommonkey.xmlunit.XMLUnit;
import org.omegat.filters.TestFilterBase;

/**
 * @author Alex Buloichik
 */
public class TMXWriterTest extends TestFilterBase {
    public void testLeveL1() throws Exception {
        TMXWriter2.writeTMX(outFile, new Language("en-US"), new Language("be-BY"), false, false,
                new TMXWriter2.SaveCallback() {
                    int i = 0;

                    public Tu getNextTu() {
                        if (i > 0)
                            return null;
                        i++;

                        Tu tu = new Tu();
                        Tuv src = new Tuv();
                        Tuv tar = new Tuv();

                        src.setXmlLang("en-US");
                        tar.setXmlLang("be-BY");
                        src.setSeg("source");
                        tar.setSeg("target");

                        tu.getTuv().add(src);
                        tu.getTuv().add(tar);

                        return tu;
                    }
                });

        XMLUnit.setControlEntityResolver(TMXReader2.TMX_DTD_RESOLVER);
        XMLUnit.setTestEntityResolver(TMXReader2.TMX_DTD_RESOLVER);
        XMLUnit.setIgnoreWhitespace(true);
        try {
            compareXML(outFile, new File("test/data/tmx/test-save-tmx14.tmx"));
        } finally {
            XMLUnit.setControlEntityResolver(null);
            XMLUnit.setTestEntityResolver(null);
        }
    }
}
