package org.omegat.gui.preferences.view;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import org.omegat.gui.editor.autotext.Autotext;
import org.omegat.gui.editor.autotext.Autotext.AutotextItem;
import org.omegat.gui.editor.autotext.AutotextTableModel;
import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.StaticUIUtils;

public class AutotextAutoCompleterOptionsView implements PreferencesView {

    private final JFileChooser fc = new JFileChooser();
    private AutotextAutoCompleterOptionsPanel panel;

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
        return "Autotext";
    }

    private void initGui() {
        panel = new AutotextAutoCompleterOptionsPanel();

        fc.setDialogType(JFileChooser.FILES_ONLY);
        fc.addChoosableFileFilter(new FileNameExtensionFilter(OStrings.getString("AC_AUTOTEXT_FILE"), "autotext"));
        panel.entryTable.setModel(new AutotextTableModel(Autotext.getItems()));
        panel.saveButton.addActionListener(e -> saveFile());
        panel.loadButton.addActionListener(e -> loadFile());

        TableColumnModel cModel = panel.entryTable.getColumnModel();
        cModel.getColumn(0).setHeaderValue(OStrings.getString("AC_AUTOTEXT_ABBREVIATION")); // NOI18N
        cModel.getColumn(1).setHeaderValue(OStrings.getString("AC_AUTOTEXT_TEXT")); // NOI18N
        cModel.getColumn(2).setHeaderValue(OStrings.getString("AC_AUTOTEXT_COMMENT")); // NOI18N

        panel.sortAlphabeticallyCheckBox.addActionListener(
                e -> panel.sortFullTextCheckBox.setEnabled(panel.sortAlphabeticallyCheckBox.isSelected()));
        panel.addNewRowButton.addActionListener(e -> {
            int newRow = ((AutotextTableModel) panel.entryTable.getModel()).addRow(new AutotextItem(),
                    panel.entryTable.getSelectedRow());
            panel.entryTable.changeSelection(newRow, 0, false, false);
            panel.entryTable.changeSelection(newRow, panel.entryTable.getColumnCount() - 1, false, true);
            panel.entryTable.editCellAt(newRow, 0);
            panel.entryTable.transferFocus();
        });
        panel.removeEntryButton.addActionListener(e -> {
            if (panel.entryTable.getSelectedRow() != -1) {
                ((AutotextTableModel) panel.entryTable.getModel()).removeRow(panel.entryTable.getSelectedRow());
            }
        });
        panel.enabledCheckBox.addActionListener(e -> updateEnabledness());
    }

    private void updateEnabledness() {
        StaticUIUtils.setHierarchyEnabled(panel.displayPanel, panel.enabledCheckBox.isSelected());
        StaticUIUtils.setHierarchyEnabled(panel.entriesPanel, panel.enabledCheckBox.isSelected());
    }

    private void loadFile() {
        if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(panel)) {
            try {
                List<AutotextItem> data = Autotext.load(fc.getSelectedFile());
                panel.entryTable.setModel(new AutotextTableModel(data));
            } catch (IOException ex) {
                Logger.getLogger(AutotextAutoCompleterOptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveFile() {
        if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(panel)) {
            try {
                List<AutotextItem> data = ((AutotextTableModel) panel.entryTable.getModel()).getData();
                Autotext.save(data, fc.getSelectedFile());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(panel, OStrings.getString("AC_AUTOTEXT_UNABLE_TO_SAVE"));
            }
        }

    }

    @Override
    public void initDefaults() {
        panel.sortByLengthCheckBox.setSelected(Preferences.isPreference(Preferences.AC_AUTOTEXT_SORT_BY_LENGTH));
        panel.sortAlphabeticallyCheckBox
                .setSelected(Preferences.isPreference(Preferences.AC_AUTOTEXT_SORT_ALPHABETICALLY));
        panel.sortFullTextCheckBox.setSelected(Preferences.isPreference(Preferences.AC_AUTOTEXT_SORT_FULL_TEXT));
        panel.sortFullTextCheckBox.setEnabled(panel.sortAlphabeticallyCheckBox.isSelected());
        panel.enabledCheckBox.setSelected(Preferences.isPreferenceDefault(Preferences.AC_AUTOTEXT_ENABLED, true));

        updateEnabledness();
    }

    @Override
    public void persist() {
        TableCellEditor editor = panel.entryTable.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }

        Autotext.setList(((AutotextTableModel) panel.entryTable.getModel()).getData());

        try {
            Autotext.save();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(panel, OStrings.getString("AC_AUTOTEXT_UNABLE_TO_SAVE"));
        }

        Preferences.setPreference(Preferences.AC_AUTOTEXT_SORT_BY_LENGTH, panel.sortByLengthCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_AUTOTEXT_SORT_ALPHABETICALLY,
                panel.sortAlphabeticallyCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_AUTOTEXT_SORT_FULL_TEXT, panel.sortFullTextCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_AUTOTEXT_ENABLED, panel.enabledCheckBox.isSelected());
    }
}
