package org.omegat.gui.preferences.view;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.omegat.core.statistics.StatisticsSettings;
import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.OStrings;
import org.omegat.util.PatternConsts;
import org.omegat.util.Preferences;

public class TagProcessingOptionsView implements PreferencesView {

    private TagProcessingOptionsPanel panel;

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
        return "Tag Processing";
    }

    private void initGui() {
        panel = new TagProcessingOptionsPanel();
    }

    @Override
    public void initDefaults() {
        panel.noCheckRadio.setSelected(Preferences.isPreference(Preferences.DONT_CHECK_PRINTF_TAGS));
        panel.simpleCheckRadio.setSelected(Preferences.isPreference(Preferences.CHECK_SIMPLE_PRINTF_TAGS));
        panel.fullCheckRadio.setSelected(Preferences.isPreference(Preferences.CHECK_ALL_PRINTF_TAGS));
        panel.javaPatternCheckBox.setSelected(Preferences.isPreference(Preferences.CHECK_JAVA_PATTERN_TAGS));
        panel.customPatternRegExpTF.setText(Preferences.getPreference(Preferences.CHECK_CUSTOM_PATTERN));
        panel.removePatternRegExpTF.setText(Preferences.getPreference(Preferences.CHECK_REMOVE_PATTERN));
        panel.looseTagOrderCheckBox.setSelected(Preferences.isPreference(Preferences.LOOSE_TAG_ORDERING));
        panel.cbTagsValidRequired.setSelected(Preferences.isPreference(Preferences.TAGS_VALID_REQUIRED));
        panel.cbCountingProtectedText
                .setSelected(StatisticsSettings.isCountingProtectedText() || StatisticsSettings.isCountingCustomTags());
    }

    /**
     * Checks text value of JTextField if it is a valid regular expression. If
     * not, focus is set to the text field and an alert is shown.
     * 
     * @param textfield
     *            the textfield with the regular expression
     * @return true if regular expression is valid, false otherwise
     */
    private boolean checkRegExp(JTextField textfield) {
        try {
            Pattern.compile(textfield.getText());
        } catch (PatternSyntaxException e) {
            textfield.setCaretPosition(e.getIndex());
            JOptionPane.showMessageDialog(panel, e.getLocalizedMessage(),
                    OStrings.getString("TV_OPTION_ERROR_CUSTOMREGEXP_TITLE"), JOptionPane.ERROR_MESSAGE);
            textfield.grabFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean validate() {
        return checkRegExp(panel.customPatternRegExpTF) && checkRegExp(panel.removePatternRegExpTF);
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.DONT_CHECK_PRINTF_TAGS, panel.noCheckRadio.isSelected());
        Preferences.setPreference(Preferences.CHECK_SIMPLE_PRINTF_TAGS, panel.simpleCheckRadio.isSelected());
        Preferences.setPreference(Preferences.CHECK_ALL_PRINTF_TAGS, panel.fullCheckRadio.isSelected());
        Preferences.setPreference(Preferences.CHECK_JAVA_PATTERN_TAGS, panel.javaPatternCheckBox.isSelected());
        Preferences.setPreference(Preferences.CHECK_CUSTOM_PATTERN, panel.customPatternRegExpTF.getText());
        Preferences.setPreference(Preferences.CHECK_REMOVE_PATTERN, panel.removePatternRegExpTF.getText());
        Preferences.setPreference(Preferences.LOOSE_TAG_ORDERING, panel.looseTagOrderCheckBox.isSelected());
        Preferences.setPreference(Preferences.TAGS_VALID_REQUIRED, panel.cbTagsValidRequired.isSelected());
        StatisticsSettings.setCountingProtectedText(panel.cbCountingProtectedText.isSelected());
        StatisticsSettings.setCountingCustomTags(panel.cbCountingProtectedText.isSelected());
        PatternConsts.updatePlaceholderPattern();
        PatternConsts.updateRemovePattern();
        PatternConsts.updateCustomTagPattern();
    }
}
