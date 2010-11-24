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

import gen.core.tmx14.Prop;
import gen.core.tmx14.Tu;
import gen.core.tmx14.Tuv;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.omegat.core.Core;
import org.omegat.util.StringUtil;
import org.omegat.util.TMXReader2;

/**
 * 
 * @author alex
 *
 */
public class ProjectTMX {
    protected static final String PROP_FILE = "file";
    protected static final String PROP_ID = "id";
    private Calendar c;

    private Map<String, TransEntry> defaultTranslations = new HashMap<String, TransEntry>();
    private Map<MultipleTranslationKey, TransEntry> multipleTranslations = new HashMap<MultipleTranslationKey, TransEntry>();

    public ProjectTMX(File file) throws Exception {
        ProjectProperties props = Core.getProject().getProjectProperties();

        c = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        c.setTimeInMillis(0);

        TMXReader2.readTMX(file, props.getSourceLanguage(), props.getTargetLanguage(),
                props.isSentenceSegmentingEnabled(), false, loader);

        c = null;
    }

    private TMXReader2.LoadCallback loader = new TMXReader2.LoadCallback() {
        public void onTu(Tu tu, Tuv tuvSource, Tuv tuvTarget) {
            String changeID = StringUtil.nvl(tuvTarget.getChangeid(), tuvTarget.getCreationid(),
                    tu.getChangeid(), tu.getCreationid());
            String changeDT = StringUtil.nvl(tuvTarget.getChangedate(), tuvTarget.getCreationdate(),
                    tu.getChangedate(), tu.getCreationdate());
            TransEntry te = new TransEntry(tuvTarget.getSeg(), changeID, parseISO8601date(changeDT));
            MultipleTranslationKey key = createKeyByProps(tu);
            if (key.file == null) {
                // default translation
                defaultTranslations.put(tuvSource.getSeg(), te);
            } else {
                // multiple translation
                key.src = tuvSource.getSeg();
                multipleTranslations.put(key, te);
            }
        }
    };

    private MultipleTranslationKey createKeyByProps(Tu tu) {
        MultipleTranslationKey k = new MultipleTranslationKey();
        for (int i = 0; i < tu.getNoteOrProp().size(); i++) {
            if (tu.getNoteOrProp().get(i) instanceof Prop) {
                Prop p = (Prop) tu.getNoteOrProp().get(i);
                if (PROP_FILE.equals(p.getType())) {
                    k.file = p.getvalue();
                } else if (PROP_ID.equals(p.getType())) {
                    k.id = p.getvalue();
                }
            }
        }
        return k;
    }

    private long parseISO8601date(String str) {
        return str != null ? DatatypeConverter.parseDateTime(str).getTimeInMillis() : 0;
    }

    public static class MultipleTranslationKey {
        String src;
        String file;
        String id;

        public int hashCode() {
            return src.hashCode() + file.hashCode();
        }

        public boolean equals(Object obj) {
            MultipleTranslationKey o = (MultipleTranslationKey) obj;
            return StringUtil.compare(src, o.src) && StringUtil.compare(file, o.file)
                    && StringUtil.compare(id, o.id);
        }
    }
}
