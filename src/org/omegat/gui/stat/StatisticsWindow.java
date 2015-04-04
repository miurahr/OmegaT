/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2009 Alex Buloichik
               2012 Thomas Cordonnier
               2015 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.gui.stat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.omegat.core.Core;
import org.omegat.core.statistics.CalcMatchStatistics;
import org.omegat.core.statistics.CalcStandardStatistics;
import org.omegat.core.threads.LongProcessThread;
import org.omegat.util.OStrings;
import org.omegat.util.gui.DockingUI;
import org.omegat.util.gui.StaticUIUtils;
import org.omegat.util.gui.UIThreadsUtil;

/**
 * Display match statistics window and save data to file.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Thomas Cordonnier
 * @author Aaron Madlon-Kay
 */
@SuppressWarnings("serial")
public class StatisticsWindow extends JDialog {

    public static enum STAT_TYPE {
        STANDARD, MATCHES, MATCHES_PER_FILE
    };

    private JProgressBar progressBar;
    private JComponent output;
    private LongProcessThread thread;

    public StatisticsWindow(STAT_TYPE statType) {
        super(Core.getMainWindow().getApplicationFrame(), true);

        progressBar = new JProgressBar();
        output = null;

        switch (statType) {
        case STANDARD:
            setTitle(OStrings.getString("CT_STATSSTANDARD_WindowHeader"));
            StatisticsPanel panel = new StatisticsPanel(this);
            thread = new CalcStandardStatistics(panel);
            output = panel;
            break;
        case MATCHES:
            setTitle(OStrings.getString("CT_STATSMATCH_WindowHeader"));
            PlainTextPanel panel1 = new PlainTextPanel(this);
            thread = new CalcMatchStatistics(panel1, false);
            output = panel1;
            break;
        case MATCHES_PER_FILE:
            setTitle(OStrings.getString("CT_STATSMATCH_PER_FILE_WindowHeader"));
            PlainTextPanel panel2 = new PlainTextPanel(this);
            thread = new CalcMatchStatistics(panel2, true);
            output = panel2;
            break;
        }

        // Run calculation
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

        // Prepare UI
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(p);

        progressBar.setStringPainted(true);
        p.add(progressBar, BorderLayout.SOUTH);

        p.add(output, BorderLayout.CENTER);

        StaticUIUtils.setEscapeAction(this, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread.fin();
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thread.fin();
            }
        });

        setSize(800, 400);
        DockingUI.displayCentered(this);
    }

    public void showProgress(final int percent) {
        UIThreadsUtil.mustBeSwingThread();
        progressBar.setValue(percent);
        progressBar.setString(percent + "%");
    }

    public void finishData() {
        UIThreadsUtil.mustBeSwingThread();
        progressBar.setValue(100);
        progressBar.setString("");
        progressBar.setVisible(false);
    }
}
