package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class HistoryAutoCompleterOptionsView implements PreferencesView {

    private HistoryAutoCompleterOptionsPanel panel;

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
        return "History";
    }

    private void initGui() {
        panel = new HistoryAutoCompleterOptionsPanel();
    }

    @Override
    public void initDefaults() {
        panel.historyCompletionCheckBox
                .setSelected(Preferences.isPreference(Preferences.AC_HISTORY_COMPLETION_ENABLED));
        panel.historyPredictionCheckBox
                .setSelected(Preferences.isPreference(Preferences.AC_HISTORY_PREDICTION_ENABLED));
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.AC_HISTORY_COMPLETION_ENABLED,
                panel.historyCompletionCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_HISTORY_PREDICTION_ENABLED,
                panel.historyPredictionCheckBox.isSelected());
    }
}
