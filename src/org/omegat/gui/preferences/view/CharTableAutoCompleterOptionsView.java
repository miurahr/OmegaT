package org.omegat.gui.preferences.view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.omegat.gui.editor.chartable.CharTableModel;
import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;
import org.omegat.util.gui.StaticUIUtils;

public class CharTableAutoCompleterOptionsView implements PreferencesView {

    private final CharTableModel allCharModel = new CharTableModel(null);
    private final CharTableModel selCharModel = new CharTableModel("");
    private CharTableAutoCompleterOptionsPanel panel;

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
        return "Character Table";
    }

    private void initGui() {
        panel = new CharTableAutoCompleterOptionsPanel();
        panel.allCharTable.setModel(allCharModel);
        panel.allCharTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_INSERT) {
                    int col = panel.allCharTable.getSelectedColumn();
                    int row = panel.allCharTable.getSelectedRow();
                    selCharModel.appendChar((Character) allCharModel.getValueAt(row, col),
                            panel.uniqueCheckBox.isSelected());
                }
            }
        });
        panel.allCharTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (panel.allCharTable.isEnabled() && evt.getClickCount() == 2) {
                    JTable target = (JTable) evt.getSource();
                    int row = target.getSelectedRow();
                    int col = target.getSelectedColumn();
                    selCharModel.appendChar((Character) allCharModel.getValueAt(row, col),
                            panel.uniqueCheckBox.isSelected());
                }
            }
        });
        panel.selCharTable.setModel(selCharModel);
        panel.selCharTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (panel.selCharTable.getSelectedColumnCount() < 1) {
                        return;
                    }
                    int row1 = panel.selCharTable.getSelectedRow();
                    int col1 = panel.selCharTable.getSelectedColumn();
                    int row2 = panel.selCharTable.getSelectedRowCount() + row1 - 1;
                    int col2 = panel.selCharTable.getSelectedColumnCount() + col1 - 1;

                    selCharModel.removeSelection(row1, col1, row2, col2);
                }
            }
        });
        panel.clearButton.addActionListener(e -> selCharModel.setData(""));
        panel.uniqueCheckBox.addActionListener(e -> {
            if (panel.uniqueCheckBox.isSelected()) {
                selCharModel.allowOnlyUnique();
            }
        });
        panel.selectedCharsCheckBox.addActionListener(
                e -> StaticUIUtils.setHierarchyEnabled(panel.customPanel, panel.selectedCharsCheckBox.isSelected()));
        panel.enabledCheckBox.addActionListener(e -> updateEnabledness());
    }

    private void updateEnabledness() {
        StaticUIUtils.setHierarchyEnabled(panel.customPanel,
                panel.enabledCheckBox.isSelected() && panel.selectedCharsCheckBox.isSelected());
        panel.selectedCharsCheckBox.setEnabled(panel.enabledCheckBox.isSelected());
    }

    @Override
    public void initDefaults() {
        panel.selectedCharsCheckBox.setSelected(Preferences.isPreference(Preferences.AC_CHARTABLE_USE_CUSTOM_CHARS));
        panel.uniqueCheckBox.setSelected(Preferences.isPreference(Preferences.AC_CHARTABLE_UNIQUE_CUSTOM_CHARS));
        selCharModel.setData(Preferences.getPreference(Preferences.AC_CHARTABLE_CUSTOM_CHAR_STRING));
        panel.enabledCheckBox.setSelected(Preferences.isPreferenceDefault(Preferences.AC_CHARTABLE_ENABLED, true));

        updateEnabledness();
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.AC_CHARTABLE_USE_CUSTOM_CHARS, panel.selectedCharsCheckBox.isSelected());
        String customCharString = selCharModel.getData();
        Preferences.setPreference(Preferences.AC_CHARTABLE_CUSTOM_CHAR_STRING, customCharString);
        if (customCharString.isEmpty()) {
            Preferences.setPreference(Preferences.AC_CHARTABLE_USE_CUSTOM_CHARS, false);
        }
        Preferences.setPreference(Preferences.AC_CHARTABLE_UNIQUE_CUSTOM_CHARS, panel.uniqueCheckBox.isSelected());
        Preferences.setPreference(Preferences.AC_CHARTABLE_ENABLED, panel.enabledCheckBox.isSelected());
    }
}
