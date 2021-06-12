/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2016 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: https://omegat.org/support

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

package org.omegat.gui.preferences.view;

import java.net.URI;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.omegat.Main;
import org.omegat.core.Core;
import org.omegat.core.plugins.PluginInstaller;
import org.omegat.gui.dialogs.ChoosePluginFile;
import org.omegat.gui.main.MainWindow;
import org.omegat.gui.main.MainWindowMenuHandler;
import org.omegat.gui.preferences.BasePreferencesController;
import org.omegat.util.OStrings;
import org.omegat.util.gui.DesktopWrapper;
import org.omegat.util.gui.TableColumnSizer;

/**
 * @author Aaron Madlon-Kay
 */
public class PluginsPreferencesController extends BasePreferencesController {

    public static final String PLUGINS_WIKI_URL = "https://sourceforge.net/p/omegat/wiki/Plugins/";
    private PluginsPreferencesPanel panel;

    @Override
    public JComponent getGui() {
        if (panel == null) {
            initGui();
            initFromPrefs();
        }
        return panel;
    }

    @Override
    public String toString() {
        return OStrings.getString("PREFS_TITLE_PLUGINS");
    }

    private void initGui() {
        panel = new PluginsPreferencesPanel();
        TableColumnSizer.autoSize(panel.tablePluginsInfo, 0, true);
        panel.browsePluginsButton.addActionListener(e -> {
            try {
                DesktopWrapper.browse(URI.create(PLUGINS_WIKI_URL));
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(panel, ex.getLocalizedMessage(),
                        OStrings.getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.restartOmegatButton.setText(OStrings.getString("PREFS_PLUGINS_RESTART"));
        panel.restartOmegatButton.addActionListener(e -> {
            String projectDir = Core.getProject().isProjectLoaded()
                    ? Core.getProject().getProjectProperties().getProjectRoot()
                    : null;
            MainWindowMenuHandler.prepareForExit((MainWindow) Core.getMainWindow(), () -> {
                Main.restartGUI(projectDir);
            });

        });
        panel.restartOmegatButton.setEnabled(false);

        panel.installFromDiskButton.setText(OStrings.getString("PREFS_PLUGINS_INSTALL_FROM_DISK"));
        panel.installFromDiskButton.addActionListener(e -> {
            ChoosePluginFile choosePluginFile = new ChoosePluginFile();
            int choosePluginFileResult = choosePluginFile.showOpenDialog(Core.getMainWindow().getApplicationFrame());
            if (choosePluginFileResult == JFileChooser.APPROVE_OPTION) {
                Boolean result = PluginInstaller.install(choosePluginFile.getSelectedFile());
                if (result) {
                    panel.restartOmegatButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void initFromPrefs() {
    }

    @Override
    public void restoreDefaults() {
    }

    @Override
    public void persist() {
    }
}
