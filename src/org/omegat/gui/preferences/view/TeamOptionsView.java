package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class TeamOptionsView implements PreferencesView {

    private TeamOptionsPanel panel;

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
        return "Team";
    }

    private void initGui() {
        panel = new TeamOptionsPanel();
    }

    @Override
    public void initDefaults() {
        panel.authorText
                .setText(Preferences.getPreferenceDefault(Preferences.TEAM_AUTHOR, System.getProperty("user.name")));

    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.TEAM_AUTHOR, panel.authorText.getText());
    }
}
