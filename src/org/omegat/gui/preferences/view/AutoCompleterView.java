package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class AutoCompleterView implements PreferencesView {

    private AutoCompleterPanel panel;

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
        return "Auto-Completion";
    }

    private void initGui() {
        panel = new AutoCompleterPanel();
    }

    @Override
    public void initDefaults() {
        panel.automaticCheckBox.setSelected(Preferences.isPreference(Preferences.AC_SHOW_SUGGESTIONS_AUTOMATICALLY));
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.AC_SHOW_SUGGESTIONS_AUTOMATICALLY, panel.automaticCheckBox.isSelected());
    }
}
