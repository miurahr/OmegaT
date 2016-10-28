package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;
import org.omegat.util.gui.StaticUIUtils;

public class GlossaryAutoCompleterOptionsView implements PreferencesView {

    private GlossaryAutoCompleterOptionsPanel panel;

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
        return "Glossary";
    }

    private void initGui() {
        panel = new GlossaryAutoCompleterOptionsPanel();
        panel.displaySourceCheckBox
                .addActionListener(e -> activateSourceItems(panel.displaySourceCheckBox.isSelected()));
        panel.enabledCheckBox.addActionListener(e -> updateEnabledness());
    }

    private void activateSourceItems(boolean activate) {
        panel.sortBySourceCheckBox.setEnabled(activate);
        panel.sourceFirstRadioButton.setEnabled(activate);
        panel.targetFirstRadioButton.setEnabled(activate);
    }

    @Override
    public void initDefaults() {
        panel.displaySourceCheckBox.setSelected(Preferences.isPreference(Preferences.AC_GLOSSARY_SHOW_SOURCE));
        activateSourceItems(panel.displaySourceCheckBox.isSelected());
        panel.sourceFirstRadioButton
                .setSelected(!Preferences.isPreference(Preferences.AC_GLOSSARY_SHOW_TARGET_BEFORE_SOURCE));
        panel.targetFirstRadioButton
                .setSelected(Preferences.isPreference(Preferences.AC_GLOSSARY_SHOW_TARGET_BEFORE_SOURCE));
        panel.sortBySourceCheckBox.setSelected(Preferences.isPreference(Preferences.AC_GLOSSARY_SORT_BY_SOURCE));
        panel.longerFirstCheckBox.setSelected(Preferences.isPreference(Preferences.AC_GLOSSARY_SORT_BY_LENGTH));
        panel.sortEntriesCheckBox.setSelected(Preferences.isPreference(Preferences.AC_GLOSSARY_SORT_ALPHABETICALLY));
        panel.enabledCheckBox.setSelected(Preferences.isPreferenceDefault(Preferences.AC_GLOSSARY_ENABLED, true));

        updateEnabledness();
    }

    private void updateEnabledness() {
        StaticUIUtils.setHierarchyEnabled(panel.optionsPanel, panel.enabledCheckBox.isSelected());
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.AC_GLOSSARY_SHOW_SOURCE, panel.displaySourceCheckBox.isSelected());
        if (panel.displaySourceCheckBox.isSelected()) {
            Preferences.setPreference(Preferences.AC_GLOSSARY_SHOW_TARGET_BEFORE_SOURCE,
                    panel.targetFirstRadioButton.isSelected());
            Preferences.setPreference(Preferences.AC_GLOSSARY_SORT_BY_SOURCE, panel.sortBySourceCheckBox.isSelected());
        }
        Preferences.setPreference(Preferences.AC_GLOSSARY_SORT_BY_LENGTH, panel.longerFirstCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_GLOSSARY_SORT_ALPHABETICALLY, panel.sortEntriesCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_GLOSSARY_ENABLED, panel.enabledCheckBox.isSelected());
    }
}
