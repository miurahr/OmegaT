/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2009 Alex Buloichik
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

package org.omegat.core.dictionaries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.omegat.gui.dictionaries.DictionariesTextArea;
import org.omegat.util.DirectoryMonitor;
import org.omegat.util.Log;

/**
 * Class for load dictionaries.
 * 
 * @author Alex Buloichik <alex73mail@gmail.com>
 */
public class DictionariesManager implements DirectoryMonitor.Callback {
    protected DirectoryMonitor monitor;
    protected final Map<String, DictionaryInfo> infos = new TreeMap<String, DictionaryInfo>();
    private final DictionariesTextArea pane;
    protected static String DICTIONARY_SUBDIR = "dictionary";

    public DictionariesManager(final DictionariesTextArea pane) {
        this.pane = pane;
    }

    public void start(final String projectDir) {
        File dir = new File(projectDir, DICTIONARY_SUBDIR);
        monitor = new DirectoryMonitor(dir, this);
        monitor.start();
    }

    public void stop() {
        monitor.fin();
        synchronized (this) {
            infos.clear();
        }
    }

    public void fileChanged(File file) {
        String fn = file.getPath();
        synchronized (this) {
            infos.remove(fn);
        }
        if (file.exists()) {
            if (fn.endsWith(".ifo")) {
                try {
                    IDictionary dict = new StarDict(file);
                    Map<String, Object> header = dict.readHeader();
                    synchronized (this) {
                        infos.put(fn, new DictionaryInfo(dict, header));
                    }
                    Log.log("Loaded dictionary from " + fn);
                } catch (Exception ex) {
                    Log.log("Error load dictionary: " + ex.getMessage());
                }
            }
        }
        pane.refresh();
    }

    /**
     * Find word in all dictionaries.
     * 
     * @param word
     * @return
     */
    public List<DictionaryEntry> findWord(String word) {
        List<DictionaryInfo> dicts;
        synchronized (this) {
            dicts = new ArrayList<DictionaryInfo>(infos.values());
        }
        List<DictionaryEntry> result = new ArrayList<DictionaryEntry>();
        for (DictionaryInfo di : dicts) {
            try {
                Object data = di.info.get(word);
                if (data == null) {
                    word = word.toLowerCase();
                    data = di.info.get(word);
                }
                if (data != null) {
                    String a = di.dict.readArticle(word, data);
                    result.add(new DictionaryEntry(word, a));
                }
            } catch (Exception ex) {
                Log.log(ex);
            }
        }
        return result;
    }

    protected static class DictionaryInfo {
        public final IDictionary dict;
        public final Map<String, Object> info;

        public DictionaryInfo(final IDictionary dict,
                final Map<String, Object> info) {
            this.dict = dict;
            this.info = info;
        }
    }
}
