package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;

public class WorkflowOptionsView implements PreferencesView {

    private WorkflowOptionsPanel panel;

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
        return "Editing Behaviour";
    }

    private void initGui() {
        panel = new WorkflowOptionsPanel();
        panel.insertFuzzyCheckBox.addActionListener(e -> {
            panel.similarityLabel.setEnabled(panel.insertFuzzyCheckBox.isSelected());
            panel.similaritySpinner.setEnabled(panel.insertFuzzyCheckBox.isSelected());
            panel.prefixLabel.setEnabled(panel.insertFuzzyCheckBox.isSelected());
            panel.prefixText.setEnabled(panel.insertFuzzyCheckBox.isSelected());
        });
    }

    @Override
    public void initDefaults() {
        panel.leaveEmptyRadio.setSelected(Preferences.isPreference(Preferences.DONT_INSERT_SOURCE_TEXT));

        panel.insertFuzzyCheckBox.setSelected(Preferences.isPreference(Preferences.BEST_MATCH_INSERT));
        panel.similarityLabel.setEnabled(panel.insertFuzzyCheckBox.isSelected());
        panel.similaritySpinner.setValue(Preferences.getPreferenceDefault(Preferences.BEST_MATCH_MINIMAL_SIMILARITY,
                Preferences.BEST_MATCH_MINIMAL_SIMILARITY_DEFAULT));
        panel.similaritySpinner.setEnabled(panel.insertFuzzyCheckBox.isSelected());
        panel.prefixLabel.setEnabled(panel.insertFuzzyCheckBox.isSelected());
        if (!Preferences.existsPreference(Preferences.BEST_MATCH_EXPLANATORY_TEXT)) {
            panel.prefixText.setText(OStrings.getString("WF_DEFAULT_PREFIX"));
        } else {
            panel.prefixText.setText(Preferences.getPreference(Preferences.BEST_MATCH_EXPLANATORY_TEXT));
        }
        panel.prefixText.setEnabled(panel.insertFuzzyCheckBox.isSelected());

        panel.allowTranslationEqualToSource.setSelected(Preferences.isPreference(Preferences.ALLOW_TRANS_EQUAL_TO_SRC));
        panel.exportCurrentSegment.setSelected(Preferences.isPreference(Preferences.EXPORT_CURRENT_SEGMENT));
        panel.stopOnAlternativeTranslation
                .setSelected(Preferences.isPreference(Preferences.STOP_ON_ALTERNATIVE_TRANSLATION));
        panel.convertNumbers.setSelected(Preferences.isPreference(Preferences.CONVERT_NUMBERS));
        panel.allowTagEditing.setSelected(Preferences.isPreference(Preferences.ALLOW_TAG_EDITING));
        panel.tagValidateOnLeave.setSelected(Preferences.isPreference(Preferences.TAG_VALIDATE_ON_LEAVE));
        panel.cbSaveAutoStatus.setSelected(Preferences.isPreference(Preferences.SAVE_AUTO_STATUS));
        panel.initialSegCountSpinner.setValue(Preferences.getPreferenceDefault(
                Preferences.EDITOR_INITIAL_SEGMENT_LOAD_COUNT, Preferences.EDITOR_INITIAL_SEGMENT_LOAD_COUNT_DEFAULT));
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.DONT_INSERT_SOURCE_TEXT, panel.leaveEmptyRadio.isSelected());

        Preferences.setPreference(Preferences.BEST_MATCH_INSERT, panel.insertFuzzyCheckBox.isSelected());
        if (panel.insertFuzzyCheckBox.isSelected()) {
            int val = Math.max(0, Math.min(100, (Integer) panel.similaritySpinner.getValue()));
            Preferences.setPreference(Preferences.BEST_MATCH_MINIMAL_SIMILARITY, val);
            Preferences.setPreference(Preferences.BEST_MATCH_EXPLANATORY_TEXT, panel.prefixText.getText());
        }

        Preferences.setPreference(Preferences.ALLOW_TRANS_EQUAL_TO_SRC,
                panel.allowTranslationEqualToSource.isSelected());
        Preferences.setPreference(Preferences.EXPORT_CURRENT_SEGMENT, panel.exportCurrentSegment.isSelected());
        Preferences.setPreference(Preferences.STOP_ON_ALTERNATIVE_TRANSLATION,
                panel.stopOnAlternativeTranslation.isSelected());
        Preferences.setPreference(Preferences.CONVERT_NUMBERS, panel.convertNumbers.isSelected());
        Preferences.setPreference(Preferences.ALLOW_TAG_EDITING, panel.allowTagEditing.isSelected());
        Preferences.setPreference(Preferences.TAG_VALIDATE_ON_LEAVE, panel.tagValidateOnLeave.isSelected());
        Preferences.setPreference(Preferences.SAVE_AUTO_STATUS, panel.cbSaveAutoStatus.isSelected());

        int segCount = Math.max(0, (Integer) panel.initialSegCountSpinner.getValue());
        Preferences.setPreference(Preferences.EDITOR_INITIAL_SEGMENT_LOAD_COUNT, segCount);
    }
}
