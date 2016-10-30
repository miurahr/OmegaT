package org.omegat.gui.preferences.view;

import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;

import org.omegat.core.CoreEvents;
import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;
import org.omegat.util.StaticUtils;

public class FontSelectionView implements PreferencesView {

    private FontSelectionPanel panel;
    private Font oldFont;

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
        return "Font";
    }

    private void initGui() {
        panel = new FontSelectionPanel();
        panel.fontComboBox.setModel(new DefaultComboBoxModel<>(StaticUtils.getFontNames()));
        panel.fontComboBox.addActionListener(e -> panel.previewTextArea.setFont(getSelectedFont()));
        panel.sizeSpinner.addChangeListener(e -> panel.previewTextArea.setFont(getSelectedFont()));
    }

    @Override
    public void initDefaults() {
        String fontName = Preferences.getPreferenceDefault(Preferences.TF_SRC_FONT_NAME, Preferences.TF_FONT_DEFAULT);
        int fontSize = Preferences.getPreferenceDefault(Preferences.TF_SRC_FONT_SIZE, Preferences.TF_FONT_SIZE_DEFAULT);
        oldFont = new Font(fontName, Font.PLAIN, fontSize);
        panel.previewTextArea.setFont(oldFont);
        panel.fontComboBox.setSelectedItem(oldFont.getName());
        panel.sizeSpinner.setValue(oldFont.getSize());
        panel.applyToProjectFilesCheckBox.setSelected(Preferences.isPreference(Preferences.PROJECT_FILES_USE_FONT));
    }

    private Font getSelectedFont() {
        return new Font((String) panel.fontComboBox.getSelectedItem(), Font.PLAIN,
                ((Number) panel.sizeSpinner.getValue()).intValue());
    }

    @Override
    public void persist() {
        boolean applyToProjFiles = panel.applyToProjectFilesCheckBox.isSelected();
        Font newFont = getSelectedFont();
        if (!newFont.equals(oldFont)
                || applyToProjFiles != Preferences.isPreference(Preferences.PROJECT_FILES_USE_FONT)) {
            Preferences.setPreference(Preferences.PROJECT_FILES_USE_FONT, applyToProjFiles);
            Preferences.setPreference(Preferences.TF_SRC_FONT_NAME, newFont.getName());
            Preferences.setPreference(Preferences.TF_SRC_FONT_SIZE, newFont.getSize());
            CoreEvents.fireFontChanged(newFont);
        }
    }
}
