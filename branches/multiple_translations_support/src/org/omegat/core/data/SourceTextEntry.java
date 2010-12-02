/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2009-2010 Alex Buloichik
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

/*
 * Source text entry represents an individual segment for
 * translation pulled directly from the input files.
 * There can be many SourceTextEntries having identical source
 * language strings
 *
 * @author Keith Godfrey
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class SourceTextEntry {
    /** Storage for full entry's identifiers, including source text. */
    private EntryKey key;

    /** If entry with the same source already exist in project. */
    boolean duplicateSource;

    /** Holds the number of this entry in a project. */
    private int m_entryNum;

    /**
     * Creates a new source text entry.
     * 
     * @param file
     *            Source file name
     * @param id
     *            ID in source file
     * @param str
     *            unique StringEntry that holds source and translation of this
     *            entry.
     * @param entryNum
     *            the number of this entry in a project.
     */
    public SourceTextEntry(EntryKey key, int entryNum) {
        this.key = key;
        m_entryNum = entryNum;
    }

    public EntryKey getKey() {
        return key;
    }

    /**
     * Returns the source text (shortcut for
     * <code>getStrEntry().getSrcText()</code>).
     */
    public String getSrcText() {
        return key.sourceText;
    }

    /** Returns the number of this entry in a project. */
    public int entryNum() {
        return m_entryNum;
    }

    /** If entry with the same source already exist in project. */
    public boolean isDuplicateSource() {
        return duplicateSource;
    }
}
