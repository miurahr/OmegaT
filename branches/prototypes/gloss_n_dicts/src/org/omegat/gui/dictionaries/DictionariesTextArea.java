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

package org.omegat.gui.dictionaries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

import org.omegat.core.Core;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.data.StringEntry;
import org.omegat.core.dictionaries.DictionariesManager;
import org.omegat.core.dictionaries.DictionaryEntry;
import org.omegat.core.matching.ITokenizer;
import org.omegat.gui.common.EntryInfoPane;
import org.omegat.gui.common.EntryInfoSearchThread;
import org.omegat.gui.main.DockableScrollPane;
import org.omegat.util.OStrings;
import org.omegat.util.Token;
import org.omegat.util.gui.UIThreadsUtil;

/**
 * This is a Dictionaries pane that displays dictionaries entries.
 * 
 * @author Alex Buloichik <alex73mail@gmail.com>
 */
public class DictionariesTextArea extends EntryInfoPane<List<DictionaryEntry>> {

    protected final DictionariesManager manager = new DictionariesManager(this);
    
    protected Map<String,Integer> words=new TreeMap<String, Integer>();

    public DictionariesTextArea() {
        super(true);

      //  setEditable(false);
        String title = OStrings
                .getString("GUI_MATCHWINDOW_SUBWINDOWTITLE_Dictionary");
        Core.getMainWindow().addDockable(
                new DockableScrollPane("DICTIONARY", title, this, true));
    }

    @Override
    protected void onProjectOpen() {
        clear();
        manager
                .start(Core.getProject().getProjectProperties()
                        .getProjectRoot());
    }

    @Override
    protected void onProjectClose() {
        clear();
        manager.stop();
    }

    /** Clears up the pane. */
    protected void clear() {
        UIThreadsUtil.mustBeSwingThread();

        setText("");
    }

    /**
     * Hack only for prototype.
     * TODO
     */
    public void callDictionary(String word) {
        final Integer pos=words.get(word);
        if (pos!=null) {
            setCaretPosition(getDocument().getLength());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setCaretPosition(pos);
                };
            });
        }
    }
    
    @Override
    protected void startSearchThread(StringEntry newEntry) {
        new DictionaryEntriesSearchThread(newEntry).start();
    }

    /**
     * Refresh content on dictionary file changed.
     */

    public void refresh() {
        SourceTextEntry ste = Core.getEditor().getCurrentEntry();
        if (ste != null) {
            startSearchThread(ste.getStrEntry());
        }
    }

    @Override
    protected void setFoundResult(List<DictionaryEntry> data) {
        UIThreadsUtil.mustBeSwingThread();
        
        words.clear();

        setContentType("text/html");

        StringBuilder txt = new StringBuilder();
        boolean wasPrev = false;
        for (DictionaryEntry de : data) {
            if (wasPrev) {
                txt.append("<br><hr>");
            } else {
                wasPrev = true;
            }
            setText(txt.toString());
            words.put(de.getWord(), getDocument().getLength());
            txt.append("<b>").append(de.getWord()).append("</b>").append(" - ");
            txt.append(de.getArticle());
            setText(txt.toString());
        }
        setCaretPosition(0);
    }

    /**
     * Thread for search data in dictionaries.
     */
    public class DictionaryEntriesSearchThread extends
            EntryInfoSearchThread<List<DictionaryEntry>> {
        protected final String src;

        public DictionaryEntriesSearchThread(final StringEntry newEntry) {
            super(DictionariesTextArea.this, newEntry);
            src = newEntry.getSrcText();
        }

        @Override
        protected List<DictionaryEntry> search() {
            List<DictionaryEntry> result = new ArrayList<DictionaryEntry>();
            Token[] tokenList = Core.getTokenizer().tokenizeWords(src,
                    ITokenizer.StemmingMode.NONE);
            for (Token tok : tokenList) {
                if (isEntryChanged()) {
                    return null;
                }
                if (tok.getLength() < 3) {
                    // disable to find less then 3-chars words
                    continue;
                }
                String w = src.substring(tok.getOffset(), tok.getOffset()
                        + tok.getLength());
                result.addAll(manager.findWord(w));
            }

            Collections.sort(result, new Comparator<DictionaryEntry>() {
                public int compare(DictionaryEntry o1, DictionaryEntry o2) {
                    return o1.getWord().compareTo(o2.getWord());
                }
            });
            return result;
        }
    }
}
