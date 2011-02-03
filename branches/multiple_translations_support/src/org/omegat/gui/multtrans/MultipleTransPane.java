/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2011 Alex Buloichik
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
package org.omegat.gui.multtrans;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import org.omegat.core.Core;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.gui.common.EntryInfoPane;
import org.omegat.gui.editor.IPopupMenuConstructor;
import org.omegat.gui.editor.SegmentBuilder;
import org.omegat.gui.main.DockableScrollPane;
import org.omegat.util.OStrings;
import org.omegat.util.gui.UIThreadsUtil;

/**
 * Pane for display information about multiple translations.
 * 
 * @author Alex Buloichik <alex73mail@gmail.com>
 */
public class MultipleTransPane extends EntryInfoPane<List<MultipleTransFoundEntry>> {
    public MultipleTransPane() {
        super(true);

        String title = OStrings.getString("MULT_TITLE");
        Core.getMainWindow().addDockable(new DockableScrollPane("MULTIPLE_TRANS", title, this, true));

        setEditable(false);
        setMinimumSize(new Dimension(100, 50));

        Core.getEditor().registerPopupMenuConstructors(300, new IPopupMenuConstructor() {
            public void addItems(JPopupMenu menu, JTextComponent comp, int mousepos, boolean isInActiveEntry,
                    boolean isInActiveTranslation, final SegmentBuilder sb) {
                if (isInActiveEntry
                        && Core.getProject().getProjectProperties().isSupportDefaultTranslations()) {
                    menu.addSeparator();
                    JMenuItem miDefault = menu.add(OStrings.getString("MULT_MENU_DEFAULT"));
                    JMenuItem miMultiple = menu.add(OStrings.getString("MULT_MENU_MULTIPLE"));
                    miDefault.setEnabled(!sb.isDefaultTranslation());
                    miMultiple.setEnabled(sb.isDefaultTranslation());

                    miDefault.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Core.getProject().setTranslation(sb.getSourceTextEntry(), "", false);
                            sb.setDefaultTranslation(true);
                        }
                    });
                    miMultiple.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            sb.setDefaultTranslation(false);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void setFoundResult(SourceTextEntry processedEntry, List<MultipleTransFoundEntry> data) {
        UIThreadsUtil.mustBeSwingThread();

        String o = "";
        for (MultipleTransFoundEntry e : data) {
            if (o.length() > 0) {
                o += "----------------------------\n";
            }
            if (e.key != null) {
                o += e.entry.translation + '\n';
                o += "(" + e.key.file + "/" + e.key.id + ")\n";
            } else {
                o += e.entry.translation + '\n';
            }
        }

        setText(o);
    }

    @Override
    protected void startSearchThread(SourceTextEntry newEntry) {
        new MultipleTransFindThread(this, Core.getProject(), newEntry).start();
    }
}
