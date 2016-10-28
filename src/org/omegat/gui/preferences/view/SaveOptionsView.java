package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class SaveOptionsView implements PreferencesView {

    private SaveOptionsPanel panel;

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
        return "Saving and Output";
    }

    private void initGui() {
        panel = new SaveOptionsPanel();

        panel.insertButton.addActionListener(
                e -> panel.externalCommandTextArea.replaceSelection(panel.variablesList.getSelectedItem().toString()));
    }

    @Override
    public void initDefaults() {
        int saveInterval = Preferences.getPreferenceDefault(Preferences.AUTO_SAVE_INTERVAL,
                Preferences.AUTO_SAVE_DEFAULT);

        panel.minutesSpinner.setValue(saveInterval / 60);
        panel.secondsSpinner.setValue(saveInterval % 60);

        panel.externalCommandTextArea.setText(Preferences.getPreference(Preferences.EXTERNAL_COMMAND));
        panel.allowProjectCmdCheckBox.setSelected(Preferences.isPreference(Preferences.ALLOW_PROJECT_EXTERN_CMD));

    }

    @Override
    public Runnable getPersistenceLogic() {
        return () -> {
            int saveMinutes = 0;
            int saveSeconds = 0;

            try {
                saveMinutes = Integer.parseInt(panel.minutesSpinner.getValue().toString());
            } catch (NumberFormatException nfe) {
                // Eat exception silently
            }

            try {
                saveSeconds = Integer.parseInt(panel.secondsSpinner.getValue().toString());
            } catch (NumberFormatException nfe) {
                // Eat exception silently
            }

            int saveInterval = saveMinutes * 60 + saveSeconds;

            if (saveInterval < 10) {
                saveInterval = 10; // 10 seconds minimum
            }

            Preferences.setPreference(Preferences.AUTO_SAVE_INTERVAL, saveInterval);

            Preferences.setPreference(Preferences.EXTERNAL_COMMAND, panel.externalCommandTextArea.getText());
            Preferences.setPreference(Preferences.ALLOW_PROJECT_EXTERN_CMD, panel.allowProjectCmdCheckBox.isSelected());
        };
    }
}
