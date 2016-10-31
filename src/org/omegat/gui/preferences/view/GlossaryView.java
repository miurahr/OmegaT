package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class GlossaryView implements PreferencesView {

    private GlossaryPanel panel;

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
        panel = new GlossaryPanel();
    }

    @Override
    public void initDefaults() {
        panel.displayContextCheckBox.setSelected(Preferences.isPreference(Preferences.GLOSSARY_TBX_DISPLAY_CONTEXT));
        panel.useSeparateTermsCheckBox.setSelected(Preferences.isPreference(Preferences.GLOSSARY_NOT_EXACT_MATCH));
        panel.useStemmingCheckBox.setSelected(Preferences.isPreference(Preferences.GLOSSARY_STEMMING));
        panel.replaceHitsCheckBox.setSelected(Preferences.isPreference(Preferences.GLOSSARY_REPLACE_ON_INSERT));
    }


    @Override
    public void persist() {
        Preferences.setPreference(Preferences.GLOSSARY_TBX_DISPLAY_CONTEXT, panel.displayContextCheckBox.isSelected());
        Preferences.setPreference(Preferences.GLOSSARY_NOT_EXACT_MATCH, panel.useSeparateTermsCheckBox.isSelected());
        Preferences.setPreference(Preferences.GLOSSARY_STEMMING, panel.useStemmingCheckBox.isSelected());
        Preferences.setPreference(Preferences.GLOSSARY_REPLACE_ON_INSERT, panel.replaceHitsCheckBox.isSelected());
    }
}
