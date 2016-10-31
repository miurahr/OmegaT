package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class DictionaryView implements PreferencesView {

    private DictionaryPanel panel;

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
        return "Dictionary";
    }

    private void initGui() {
        panel = new DictionaryPanel();
    }

    @Override
    public void initDefaults() {
        panel.fuzzyMatchingCheckBox.setSelected(Preferences.isPreference(Preferences.DICTIONARY_FUZZY_MATCHING));
    }


    @Override
    public void persist() {
        Preferences.setPreference(Preferences.DICTIONARY_FUZZY_MATCHING, panel.fuzzyMatchingCheckBox.isSelected());
    }
}
