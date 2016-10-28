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
        panel.historyCompletionCheckBox
                .setSelected(Preferences.isPreference(Preferences.AC_HISTORY_COMPLETION_ENABLED));
        panel.historyPredictionCheckBox
                .setSelected(Preferences.isPreference(Preferences.AC_HISTORY_PREDICTION_ENABLED));
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.AC_SHOW_SUGGESTIONS_AUTOMATICALLY, panel.automaticCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_HISTORY_COMPLETION_ENABLED,
                panel.historyCompletionCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_HISTORY_PREDICTION_ENABLED,
                panel.historyPredictionCheckBox.isSelected());
    }
}
