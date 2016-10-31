package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.core.Core;
import org.omegat.gui.main.MainWindow;
import org.omegat.gui.main.MainWindowUI;
import org.omegat.gui.preferences.PreferencesView;

public class AppearanceView implements PreferencesView {

    private AppearancePanel panel;

    @Override
    public JComponent getGui() {
        if (panel == null) {
            initGui();
            initDefaults();
        }
        return panel;
    }

    @Override
    public String toString() {
        return "Appearance";
    }

    private void initGui() {
        panel = new AppearancePanel();
        // TODO: Properly abstract the restore function
        panel.restoreWindowButton
                .addActionListener(e -> MainWindowUI.resetDesktopLayout((MainWindow) Core.getMainWindow()));
    }

    @Override
    public void initDefaults() {
    }

    @Override
    public void persist() {
    }
}
