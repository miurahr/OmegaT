package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.editor.ModificationInfoManager;
import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class ViewOptionsView implements PreferencesView {

    private ViewOptionsPanel panel;

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
        return "View";
    }

    private void initGui() {
        panel = new ViewOptionsPanel();
        panel.templateActivator.addActionListener(e -> templatesSetEnabled(panel.templateActivator.isSelected()));
        panel.insertButton.addActionListener(
                e -> panel.modInfoTemplate.replaceSelection(panel.variablesList.getSelectedItem().toString()));
        panel.insertButtonND.addActionListener(
                e -> panel.modInfoTemplateND.replaceSelection(panel.variablesListND.getSelectedItem().toString()));
    }

    @Override
    public void initDefaults() {
        panel.viewSourceAllBold
                .setSelected(Preferences.isPreferenceDefault(Preferences.VIEW_OPTION_SOURCE_ALL_BOLD, true));
        panel.markFirstNonUnique.setSelected(Preferences.isPreference(Preferences.VIEW_OPTION_UNIQUE_FIRST));

        panel.simplifyPPTooltips
                .setSelected(Preferences.isPreferenceDefault(Preferences.VIEW_OPTION_PPT_SIMPLIFY, true));

        panel.templateActivator
                .setSelected(Preferences.isPreferenceDefault(Preferences.VIEW_OPTION_TEMPLATE_ACTIVE, false));
        templatesSetEnabled(panel.templateActivator.isSelected());

        panel.modInfoTemplate.setText(Preferences.getPreferenceDefault(Preferences.VIEW_OPTION_MOD_INFO_TEMPLATE,
                ModificationInfoManager.DEFAULT_TEMPLATE));
        panel.modInfoTemplate.setCaretPosition(0);

        panel.modInfoTemplateND
                .setText(Preferences.getPreferenceDefault(Preferences.VIEW_OPTION_MOD_INFO_TEMPLATE_WO_DATE,
                ModificationInfoManager.DEFAULT_TEMPLATE_NO_DATE));
        panel.modInfoTemplateND.setCaretPosition(0);
    }

    private void templatesSetEnabled(boolean isEnabled) {
        panel.modInfoTemplate.setEnabled(isEnabled);
        panel.templateLabel.setEnabled(isEnabled);
        panel.variablesLabel.setEnabled(isEnabled);
        panel.variablesList.setEnabled(isEnabled);
        panel.insertButton.setEnabled(isEnabled);
        panel.modInfoTemplateND.setEnabled(isEnabled);
        panel.templateLabelND.setEnabled(isEnabled);
        panel.variablesLabelND.setEnabled(isEnabled);
        panel.variablesListND.setEnabled(isEnabled);
        panel.insertButtonND.setEnabled(isEnabled);
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.VIEW_OPTION_SOURCE_ALL_BOLD, panel.viewSourceAllBold.isSelected());
        Preferences.setPreference(Preferences.VIEW_OPTION_UNIQUE_FIRST, panel.markFirstNonUnique.isSelected());
        Preferences.setPreference(Preferences.VIEW_OPTION_PPT_SIMPLIFY, panel.simplifyPPTooltips.isSelected());
        Preferences.setPreference(Preferences.VIEW_OPTION_TEMPLATE_ACTIVE, panel.templateActivator.isSelected());
        Preferences.setPreference(Preferences.VIEW_OPTION_MOD_INFO_TEMPLATE, panel.modInfoTemplate.getText());
        Preferences.setPreference(Preferences.VIEW_OPTION_MOD_INFO_TEMPLATE_WO_DATE, panel.modInfoTemplateND.getText());
        ModificationInfoManager.reset();
    }
}
