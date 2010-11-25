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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

import org.omegat.core.Core;
import org.omegat.util.StaticUtils;
import org.omegat.util.StringUtil;
import org.omegat.util.TMXDateParser;
import org.omegat.util.TMXReader2;
import org.omegat.util.TMXWriter2;

/**
 * Class for store data from project_save.tmx.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ProjectTMX {
    protected static final String PROP_FILE = "file";
    protected static final String PROP_ID = "id";

    /**
     * Storage for translation for current project.
     */
    private final Map<String, TransEntry> translationDefault;

    private final Map<EntryKey, TransEntry> translationMultiple;

    /**
     * Storage for orphaned segments.
     */
    private final Map<String, TransEntry> orphanedDefault;

    private final Map<EntryKey, TransEntry> orphanedMultiple;

    public ProjectTMX(File file, CheckOrphanedCallback callback) throws Exception {

        ProjectProperties props = Core.getProject().getProjectProperties();

        translationMultiple = new HashMap<EntryKey, TransEntry>();
        orphanedMultiple = new HashMap<EntryKey, TransEntry>();
        if (props.isSupportDefaultTranslations()) {
            translationDefault = new HashMap<String, TransEntry>();
            orphanedDefault = new HashMap<String, TransEntry>();
        } else {
            // Do not even create default storage if not required. It will
            // allow to see errors.
            translationDefault = null;
            orphanedDefault = null;
        }

        TMXReader2.readTMX(file, props.getSourceLanguage(), props.getTargetLanguage(),
                props.isSentenceSegmentingEnabled(), false, new Loader(callback));
    }

    public void save(File outFile, final boolean forceValidTMX) {
        ProjectProperties props = Core.getProject().getProjectProperties();

    }

    public TransEntry getTranslation(SourceTextEntry ste) {
        TransEntry r = translationMultiple.get(ste.getKey());
        if (r == null && translationDefault != null) {
            r = translationDefault.get(ste.getKey().sourceText);
        }
        return r;
    }

    private class Loader implements TMXReader2.LoadCallback {
        private final CheckOrphanedCallback callback;

        public Loader(CheckOrphanedCallback callback) {
            this.callback = callback;
        }

        public void onTu(Tu tu, Tuv tuvSource, Tuv tuvTarget) {
            String changeID = StringUtil.nvl(tuvTarget.getChangeid(), tuvTarget.getCreationid(),
                    tu.getChangeid(), tu.getCreationid());
            String changeDT = StringUtil.nvl(tuvTarget.getChangedate(), tuvTarget.getCreationdate(),
                    tu.getChangedate(), tu.getCreationdate());
            TransEntry te = new TransEntry(tuvTarget.getSeg(), changeID, parseISO8601date(changeDT));
            EntryKey key = createKeyByProps(tuvSource.getSeg(), tu);
            if (key.file == null) {
                // default translation
                if (callback.existSourceInProject(tuvSource.getSeg())) {
                    translationDefault.put(tuvSource.getSeg(), te);
                } else {
                    orphanedDefault.put(tuvSource.getSeg(), te);
                }
            } else {
                // multiple translation
                if (callback.existEntryInProject(key)) {
                    translationMultiple.put(key, te);
                } else {
                    orphanedMultiple.put(key, te);
                }
            }
        }
    };

    private EntryKey createKeyByProps(String src, Tu tu) {
        String file = null;
        String id = null;
        for (int i = 0; i < tu.getNoteOrProp().size(); i++) {
            if (tu.getNoteOrProp().get(i) instanceof Prop) {
                Prop p = (Prop) tu.getNoteOrProp().get(i);
                if (PROP_FILE.equals(p.getType())) {
                    file = p.getvalue();
                } else if (PROP_ID.equals(p.getType())) {
                    id = p.getvalue();
                }
            }
        }
        return new EntryKey(file, src, id);
    }

    private long parseISO8601date(String str) {
        return str != null ? DatatypeConverter.parseDateTime(str).getTimeInMillis() : 0;
    }

    public interface CheckOrphanedCallback {
        boolean existEntryInProject(EntryKey key);

        boolean existSourceInProject(String src);
    }

    private class SaveCallback implements TMXWriter2.SaveCallback {
        private final boolean forceValidTMX;
        private final Iterator<Map.Entry<String, TransEntry>> itTD;
        private final Iterator<Map.Entry<EntryKey, TransEntry>> itTM;
        private final Iterator<Map.Entry<String, TransEntry>> itOD;
        private final Iterator<Map.Entry<EntryKey, TransEntry>> itOM;

        /**
         * DateFormat with format YYYYMMDDThhmmssZ able to display a date in UTC time.
         * 
         * SimpleDateFormat IS NOT THREAD SAFE !!!
         */
        private final SimpleDateFormat tmxDateFormat;

        public SaveCallback(boolean forceValidTMX) {
            this.forceValidTMX = forceValidTMX;

            // we need to put entries into TreeMap for have sorted TMX
            itTD = new TreeMap<String, TransEntry>(translationDefault).entrySet().iterator();
            itTM = new TreeMap<EntryKey, TransEntry>(translationMultiple).entrySet().iterator();
            itOD = new TreeMap<String, TransEntry>(orphanedDefault).entrySet().iterator();
            itOM = new TreeMap<EntryKey, TransEntry>(orphanedMultiple).entrySet().iterator();

            tmxDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
            tmxDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        public Tu getNextTu() {
            Tu tu = new Tu();
            Tuv s = new Tuv();
            Tuv t = new Tuv();
            tu.getTuv().add(s);
            tu.getTuv().add(t);

            Map.Entry<String, TransEntry> ed = null;
            Map.Entry<EntryKey, TransEntry> em = null;

            // find next entry
            if ((ed = itTD.next()) != null) {
            } else if ((em = itTM.next()) != null) {
            } else if ((ed = itOD.next()) != null) {
            } else if ((em = itOM.next()) != null) {
            } else {
                return null;
            }

            TransEntry te = null;
            if (ed != null) {
                s.setSeg(ed.getKey());
                t.setSeg(ed.getValue().translation);
                te = ed.getValue();
            } else if (em != null) {
                s.setSeg(em.getKey().sourceText);
                t.setSeg(em.getValue().translation);
                te = em.getValue();

                addProp(tu, PROP_FILE, em.getKey().file);
                addProp(tu, PROP_ID, em.getKey().id);
            }

            if (!StringUtil.isEmpty(te.changeId)) {
                t.setChangeid(te.changeId);
            }
            if (te.changeDate > 0) {
                t.setChangedate(tmxDateFormat.format(new Date(te.changeDate)));
            }

            if (forceValidTMX) {
                s.setSeg(StaticUtils.stripTags(s.getSeg()));
                t.setSeg(StaticUtils.stripTags(t.getSeg()));
            }

            return tu;
        }
    }

    private static void addProp(Tu tu, String propName, String propValue) {
        if (StringUtil.isEmpty(propValue)) {
            return;
        }
        Prop p = new Prop();
        p.setType(propName);
        p.setvalue(propValue);
    }
}
