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
package org.omegat.core.data;

import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.omegat.core.Core;
import org.omegat.util.TMXReader2;

/**
 * Class for store data from TMX from /tm/ folder. They are used only for fuzzy matches.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ExternalTMX {

    private final String name;

    String[] source;
    String[] target;

    public ExternalTMX(String name) {
        this.name = name;
    }

    public ExternalTMX(File file) throws Exception {
        this.name = file.getName();
        ProjectProperties props = Core.getProject().getProjectProperties();

        final List<String> s = new ArrayList<String>();
        final List<String> t = new ArrayList<String>();
        TMXReader2.LoadCallback loader = new TMXReader2.LoadCallback() {
            public void onTu(Tu tu, Tuv tuvSource, Tuv tuvTarget) {
                s.add(tuvSource.getSeg());
                t.add(tuvTarget.getSeg());
            }
        };

        TMXReader2.readTMX(file, props.getSourceLanguage(), props.getTargetLanguage(),
                props.isSentenceSegmentingEnabled(), false, loader);
        source = s.toArray(new String[s.size()]);
        target = t.toArray(new String[t.size()]);
    }

    public String getName() {
        return name;
    }
}
