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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.omegat.core.Core;
import org.omegat.core.segmentation.Segmenter;
import org.omegat.util.Language;
import org.omegat.util.StaticUtils;
import org.omegat.util.StringUtil;
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
    protected static final String PROP_PREV = "prev";
    protected static final String PROP_NEXT = "next";

    /**
     * Storage for translation for current project. Will be null if default translation disabled.
     */
    final Map<String, TMXEntry> translationDefault;

    final Map<EntryKey, TMXEntry> translationMultiple;

    /**
     * Storage for orphaned segments.
     */
    final Map<String, TMXEntry> orphanedDefault;

    final Map<EntryKey, TMXEntry> orphanedMultiple;

    public ProjectTMX(ProjectProperties props, File file, CheckOrphanedCallback callback) throws Exception {
        translationMultiple = new HashMap<EntryKey, TMXEntry>();
        orphanedMultiple = new HashMap<EntryKey, TMXEntry>();
        orphanedDefault = new HashMap<String, TMXEntry>();
        if (props.isSupportDefaultTranslations()) {
            translationDefault = new HashMap<String, TMXEntry>();
        } else {
            // Do not even create default storage if not required. It will
            // allow to see errors.
            translationDefault = null;
        }

        TMXReader2.readTMX(
                file,
                props.getSourceLanguage(),
                props.getTargetLanguage(),
                props.isSentenceSegmentingEnabled(),
                false,
                new Loader(callback, props.getSourceLanguage(), props.getTargetLanguage(), props
                        .isSentenceSegmentingEnabled()));
    }

    public void save(File outFile, final boolean forceValidTMX, final boolean levelTwo) throws Exception {
        ProjectProperties props = Core.getProject().getProjectProperties();

        TMXWriter2.writeTMX(outFile, props.getSourceLanguage(), props.getTargetLanguage(),
                props.isSentenceSegmentingEnabled(), levelTwo, new SaveCallback(forceValidTMX));
    }

    /**
     * Get default translation or null if not exist.
     */
    public TMXEntry getDefaultTranslation(String source) {
        return translationDefault != null ? translationDefault.get(source) : null;
    }

    /**
     * Get multiple translation or null if not exist.
     */
    public TMXEntry getMultipleTranslation(EntryKey ek) {
        return translationMultiple.get(ek);
    }

    /**
     * Set new translation.
     */
    public void setTranslation(SourceTextEntry ste, TMXEntry te, boolean isDefault) {
        // TODO review default
        if (te == null) {
            if (isDefault) {
                translationDefault.remove(ste.getKey().sourceText);
            } else {
                translationMultiple.remove(ste.getKey());
            }
        } else {
            if (isDefault) {
                translationDefault.put(ste.getKey().sourceText, te);
            } else {
                translationMultiple.put(ste.getKey(), te);
            }
        }
    }

    /**
     * Store translation from source file.
     */
    void putFromSourceFile(EntryKey key, TMXEntry te) {
        // TODO review default
        translationMultiple.put(key, te);
    }

    private class Loader implements TMXReader2.LoadCallback {
        private final CheckOrphanedCallback callback;
        private final Language sourceLang;
        private final Language targetLang;
        private final boolean sentenceSegmentingEnabled;

        public Loader(CheckOrphanedCallback callback, Language sourceLang, Language targetLang,
                boolean sentenceSegmentingEnabled) {
            this.callback = callback;
            this.sourceLang = sourceLang;
            this.targetLang = targetLang;
            this.sentenceSegmentingEnabled = sentenceSegmentingEnabled;
        }

        public void onTu(Tu tu, Tuv tuvSource, Tuv tuvTarget, boolean isParagraphSegtype) {
            String changer = StringUtil.nvl(tuvTarget.getChangeid(), tuvTarget.getCreationid(),
                    tu.getChangeid(), tu.getCreationid());
            String dt = StringUtil.nvl(tuvTarget.getChangedate(), tuvTarget.getCreationdate(),
                    tu.getChangedate(), tu.getCreationdate());

            List<String> sources = new ArrayList<String>();
            List<String> targets = new ArrayList<String>();
            Segmenter.segmentEntries(sentenceSegmentingEnabled && isParagraphSegtype, sourceLang,
                    tuvSource.getSeg(), targetLang, tuvTarget.getSeg(), sources, targets);

            for (int i = 0; i < sources.size(); i++) {
                TMXEntry te = new TMXEntry(sources.get(i), targets.get(i), changer,
                        TMXReader2.parseISO8601date(dt));
                EntryKey key = createKeyByProps(tuvSource.getSeg(), tu);
                if (key.file == null) {
                    // default translation
                    if (translationDefault != null && callback.existSourceInProject(tuvSource.getSeg())) {
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
        }
    };

    private EntryKey createKeyByProps(String src, Tu tu) {
        String file = null;
        String id = null;
        String prev = null;
        String next = null;
        for (int i = 0; i < tu.getNoteOrProp().size(); i++) {
            if (tu.getNoteOrProp().get(i) instanceof Prop) {
                Prop p = (Prop) tu.getNoteOrProp().get(i);
                if (PROP_FILE.equals(p.getType())) {
                    file = p.getvalue();
                } else if (PROP_ID.equals(p.getType())) {
                    id = p.getvalue();
                } else if (PROP_PREV.equals(p.getType())) {
                    prev = p.getvalue();
                } else if (PROP_NEXT.equals(p.getType())) {
                    next = p.getvalue();
                }
            }
        }
        return new EntryKey(file, src, id, prev, next);
    }

    public interface CheckOrphanedCallback {
        boolean existEntryInProject(EntryKey key);

        boolean existSourceInProject(String src);
    }

    private class SaveCallback implements TMXWriter2.SaveCallback {
        private final boolean forceValidTMX;
        private final Iterator<Map.Entry<String, TMXEntry>> itTD;
        private final Iterator<Map.Entry<EntryKey, TMXEntry>> itTM;
        private final Iterator<Map.Entry<String, TMXEntry>> itOD;
        private final Iterator<Map.Entry<EntryKey, TMXEntry>> itOM;

        /**
         * DateFormat with format YYYYMMDDThhmmssZ able to display a date in UTC time.
         * 
         * SimpleDateFormat IS NOT THREAD SAFE !!!
         */
        private final SimpleDateFormat tmxDateFormat;

        public SaveCallback(boolean forceValidTMX) {
            this.forceValidTMX = forceValidTMX;

            // we need to put entries into TreeMap for have sorted TMX
            if (translationDefault != null) {
                itTD = new TreeMap<String, TMXEntry>(translationDefault).entrySet().iterator();
            } else {
                itTD = null;
            }
            itTM = new TreeMap<EntryKey, TMXEntry>(translationMultiple).entrySet().iterator();
            itOD = new TreeMap<String, TMXEntry>(orphanedDefault).entrySet().iterator();
            itOM = new TreeMap<EntryKey, TMXEntry>(orphanedMultiple).entrySet().iterator();

            tmxDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
            tmxDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        public Tu getNextTu() {
            Tu tu = new Tu();
            Tuv s = new Tuv();
            Tuv t = new Tuv();
            tu.getTuv().add(s);
            tu.getTuv().add(t);

            Map.Entry<String, TMXEntry> ed = null;
            Map.Entry<EntryKey, TMXEntry> em = null;

            // find next entry
            if (itTD != null && itTD.hasNext()) {
                ed = itTD.next();
            } else if (itTM.hasNext()) {
                em = itTM.next();
            } else if (itOD.hasNext()) {
                ed = itOD.next();
            } else if (itOM.hasNext()) {
                em = itOM.next();
            } else {
                return null;
            }

            TMXEntry te = null;
            if (ed != null) {
                s.setSeg(ed.getKey());
                t.setSeg(ed.getValue().translation);
                te = ed.getValue();
            } else if (em != null) {
                EntryKey emKey = em.getKey();
                s.setSeg(emKey.sourceText);
                t.setSeg(em.getValue().translation);
                te = em.getValue();

                addProp(tu, PROP_FILE, emKey.file);
                addProp(tu, PROP_ID, emKey.id);
                addProp(tu, PROP_PREV, emKey.prev);
                addProp(tu, PROP_NEXT, emKey.next);
            }

            if (!StringUtil.isEmpty(te.changer)) {
                t.setChangeid(te.changer);
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
        tu.getNoteOrProp().add(p);
    }
}
