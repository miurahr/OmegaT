package org.omegat.gui.preferences.view;

import java.io.File;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;
import org.omegat.util.StaticUtils;
import org.omegat.util.gui.StaticUIUtils;

public class GeneralOptionsView implements PreferencesView {

    private GeneralOptionsPanel panel;

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
        return "General";
    }

    private void initGui() {
        panel = new GeneralOptionsPanel();
        panel.accessConfigDirButton.addActionListener(e -> {
            File configDir = new File(StaticUtils.getConfigDir());
            StaticUIUtils.openFileWithDesktop(configDir);
        });
    }

    @Override
    public void initDefaults() {
        panel.tabAdvanceCheckBox.setSelected(Preferences.isPreference(Preferences.USE_TAB_TO_ADVANCE));
        panel.confirmQuitCheckBox.setSelected(Preferences.isPreference(Preferences.ALWAYS_CONFIRM_QUIT));
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.USE_TAB_TO_ADVANCE, panel.tabAdvanceCheckBox.isSelected());
        Preferences.setPreference(Preferences.ALWAYS_CONFIRM_QUIT, panel.confirmQuitCheckBox.isSelected());
    }
}
