/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2008 Alex Buloichik
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

package org.omegat.gui.main;

/**
 * Interface for access to editor functionality.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public interface IEditor {
    enum CHANGE_CASE_TO {
        /** lower case */
        LOWER,
        /** title case */
        TITLE,
        /** upper case */
        UPPER,
        /** cycle between cases */
        CYCLE,
    }

    /**
     * Move to next entry.
     */
    void nextEntry();

    /**
     * Move to previous entry.
     */
    void prevEntry();

    /**
     * Move to next untranslated entry.
     */
    void nextUntranslatedEntry();

    /**
     * Change case of the selected text or if none is selected, of the current
     * word.
     * 
     * @param newCase :
     *            lower, title, upper or cycle
     */
    void changeCase(CHANGE_CASE_TO newCase);
}
