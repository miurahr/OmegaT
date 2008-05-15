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

import java.io.File;

import org.omegat.core.Core;
import org.omegat.core.data.CommandThread;
import org.omegat.core.data.ProjectProperties;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.OmegaTFileChooser;
import org.omegat.util.gui.OpenProjectFileChooser;
import org.omegat.util.gui.SwingWorker;
import org.omegat.util.gui.UIThreadsUtil;

/**
 * Handler for project UI commands, like open, save, compile, etc.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ProjectUICommands {
    public static void projectCreate() {
        UIThreadsUtil.mustBeSwingThread();
        if (Core.getDataEngine().isProjectLoaded()) {
            // it shoudn't happen because menu item for open project must be
            // disabled in this case
            Core.getMainWindow().displayError(
                    "Please close the project first!",
                    new Exception("FATAL: Another project is open"));
            return;
        }

        Core.getDataEngine().createProject();

        final String projectRoot = CommandThread.core.getProjectProperties()
                .getProjectRoot();

        if (projectRoot != null && projectRoot.length() > 0) {
            new SwingWorker<Object>() {
                protected Object doInBackground() throws Exception {
                    Core.getDataEngine().loadProject(
                            projectRoot + File.separator);
                    return null;
                }

                protected void done() {
                    Core.getEditor().setFirstEntry();
                    Core.getEditor().loadDocument();
                    Core.getEditor().activateEntry();
                }
            }.execute();
        }
    }
    
    /**
     * Open project.
     */
    public static void projectOpen() {
        UIThreadsUtil.mustBeSwingThread();
        if (Core.getDataEngine().isProjectLoaded()) {
            // it shoudn't happen because menu item for open project must be
            // disabled in this case
            Core.getMainWindow().displayError(
                    "Please close the project first!",
                    new Exception("FATAL: Another project is open"));
            return;
        }

        // select existing project file - open it
        OmegaTFileChooser pfc = new OpenProjectFileChooser();
        if (OmegaTFileChooser.APPROVE_OPTION != pfc.showOpenDialog(Core
                .getMainWindow().getApplicationFrame())) {
            return;
        }

        final File projectRootFolder = pfc.getSelectedFile();
        Core.getMainWindow().clear();

        new SwingWorker<Object>() {
            protected Object doInBackground() throws Exception {
                Core.getDataEngine().loadProject(
                        projectRootFolder.getAbsolutePath() + File.separator);
                return null;
            }

            protected void done() {
                Core.getEditor().setFirstEntry();
                Core.getEditor().loadDocument();
                Core.getEditor().activateEntry();
                
              //  m_projWin.uiUpdateImportButtonStatus();
                
               // m_projWin.setVisible(true);

            }
        }.execute();
    }

    public static void projectReload() {
        if (!Core.getDataEngine().isProjectLoaded()) {
            // it shoudn't happen because menu item for save project must be
            // disabled in this case
            Core.getMainWindow().displayError("FATAL: Project not opened yet!",
                    new Exception("Project not opened"));
            return;
        }
        
        ProjectProperties config = CommandThread.core.getProjectProperties();
        final String projectRoot = config.getProjectRoot();

        new SwingWorker<Object>() {
            protected Object doInBackground() throws Exception {
                Core.getDataEngine().saveProject();

                Core.getMainWindow().clear();

                Core.getDataEngine().loadProject(
                        projectRoot + File.separator);
                return null;
            }

            protected void done() {
                Core.getEditor().setFirstEntry();
                Core.getEditor().loadDocument();
                Core.getEditor().activateEntry();
            }
        }.execute();
    }

    public static void projectSave() {
        // commit the current entry first
        Core.getEditor().commitEntry(true);
        Core.getEditor().activateEntry();

        if (!Core.getDataEngine().isProjectLoaded()) {
            // it shoudn't happen because menu item for save project must be
            // disabled in this case
            Core.getMainWindow().displayError("FATAL: Project not opened yet!",
                    new Exception("Project not opened"));
            return;
        }

        new SwingWorker<Object>() {
            protected Object doInBackground() throws Exception {
                Core.getMainWindow().showStatusMessage(
                        OStrings.getString("MW_STATUS_SAVING"));

                Core.getDataEngine().saveProject();

                Core.getMainWindow().showStatusMessage(
                        OStrings.getString("MW_STATUS_SAVED"));

                return null;
            }

            protected void done() {
            }
        }.execute();
    }

    public static void projectClose() {
        if (!Core.getDataEngine().isProjectLoaded()) {
            // it shoudn't happen because menu item for save project must be
            // disabled in this case
            Core.getMainWindow().displayError("FATAL: Project not opened yet!",
                    new Exception("Project not opened"));
            return;
        }

        new SwingWorker<Object>() {
            protected Object doInBackground() throws Exception {
                Core.getMainWindow().showStatusMessage(
                        OStrings.getString("MW_STATUS_SAVING"));

                Preferences.save();

                Core.getDataEngine().saveProject();

                Core.getMainWindow().showStatusMessage(
                        OStrings.getString("MW_STATUS_SAVED"));

                return null;
            }

            protected void done() {
                Core.getMainWindow().clear();                
                Core.getEditor().showIntoduction();
                                
                Core.getDataEngine().closeProject();
               // showProgressMessage(OStrings.getString("MW_PROGRESS_DEFAULT"));
               // showLengthMessage(OStrings.getString("MW_SEGMENT_LENGTH_DEFAULT"));

            }
        }.execute();
    }
}
