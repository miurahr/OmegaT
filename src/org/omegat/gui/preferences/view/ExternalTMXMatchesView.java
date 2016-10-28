package org.omegat.gui.preferences.view;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;

import org.omegat.core.matching.NearString.SORT_KEY;
import org.omegat.gui.matches.MatchesVarExpansion;
import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.DelegatingComboBoxRenderer;

public class ExternalTMXMatchesView implements PreferencesView {

    private ExternalTMXMatchesPanel panel;

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
        return "TM Matches";
    }

    private void initGui() {
        panel = new ExternalTMXMatchesPanel();
        panel.sortMatchesList.setModel(new DefaultComboBoxModel<>(
                new SORT_KEY[] { SORT_KEY.SCORE, SORT_KEY.SCORE_NO_STEM, SORT_KEY.ADJUSTED_SCORE }));
        panel.sortMatchesList.setRenderer(new DelegatingComboBoxRenderer<SORT_KEY, String>() {
            @Override
            protected String getDisplayText(SORT_KEY value) {
                return OStrings.getString("EXT_TMX_SORT_KEY_" + value.name());
            }
        });
        panel.insertButton
                .addActionListener(
                        e -> panel.matchesTemplate.replaceSelection(panel.variablesList.getSelectedItem().toString()));
    }

    @Override
    public void initDefaults() {
        panel.sortMatchesList
                .setSelectedItem(Preferences.getPreferenceEnumDefault(Preferences.EXT_TMX_SORT_KEY, SORT_KEY.SCORE));
        panel.displayLevel2Tags.setSelected(Preferences.isPreference(Preferences.EXT_TMX_SHOW_LEVEL2));
        panel.useSlash.setSelected(Preferences.isPreference(Preferences.EXT_TMX_USE_SLASH));
        panel.matchesTemplate.setText(Preferences.getPreferenceDefault(Preferences.EXT_TMX_MATCH_TEMPLATE,
                MatchesVarExpansion.DEFAULT_TEMPLATE));
        panel.matchesTemplate.setCaretPosition(0);
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.EXT_TMX_SORT_KEY, (SORT_KEY) panel.sortMatchesList.getSelectedItem());
        Preferences.setPreference(Preferences.EXT_TMX_SHOW_LEVEL2, panel.displayLevel2Tags.isSelected());
        Preferences.setPreference(Preferences.EXT_TMX_USE_SLASH, panel.useSlash.isSelected());
        Preferences.setPreference(Preferences.EXT_TMX_MATCH_TEMPLATE, panel.matchesTemplate.getText());
    }
}
