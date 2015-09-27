/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2012 Didier Briel, Aaron Madlon-Kay
               2015 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.gui.dialogs;

import java.awt.Frame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.DockingUI;
import org.omegat.util.gui.StaticUIUtils;

/**
 * 
 * @author Maxym Mykhalchuk
 * @author Didier Briel
 * @author Aaron Madlon-Kay
 */
@SuppressWarnings("serial")
public class SaveOptionsDialog extends JDialog {
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    /** Creates new form SaveOptionsDialog */
    public SaveOptionsDialog(Frame parent) {
        super(parent, true);

        StaticUIUtils.setEscapeClosable(this);

        initComponents();

        getRootPane().setDefaultButton(okButton);

        // Initializing options
        int saveInterval = Integer.parseInt(Preferences.getPreferenceDefault(
                        Preferences.AUTO_SAVE_INTERVAL,
                        Preferences.AUTO_SAVE_DEFAULT));

        minutesSpinner.setValue(saveInterval / 60);
        secondsSpinner.setValue(saveInterval % 60);

        externalCommandTextArea.setText(Preferences.getPreference(Preferences.EXTERNAL_COMMAND));
        allowProjectCmdCheckBox.setSelected(Preferences.isPreference(Preferences.ALLOW_PROJECT_EXTERN_CMD));
        DockingUI.displayCentered(this);
    }

    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        intervalDescriptionTextArea = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        minutesLabel = new javax.swing.JLabel();
        minutesSpinner = new javax.swing.JSpinner();
        secondsLabel = new javax.swing.JLabel();
        secondsSpinner = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        externalCommandLabel = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        externalCmdDescriptionTextArea = new javax.swing.JTextArea();
        externalCommandScrollPane = new javax.swing.JScrollPane();
        externalCommandTextArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        variablesLabel = new javax.swing.JLabel();
        variablesList = new javax.swing.JComboBox<String>();
        insertButton = new javax.swing.JButton();
        allowProjectCmdCheckBox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle(OStrings.getString("SAVE_DIALOG_TITLE")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        intervalDescriptionTextArea.setEditable(false);
        intervalDescriptionTextArea.setFont(new JLabel().getFont());
        intervalDescriptionTextArea.setLineWrap(true);
        intervalDescriptionTextArea.setText(OStrings.getString("SAVE_DIALOG_DESCRIPTION")); // NOI18N
        intervalDescriptionTextArea.setWrapStyleWord(true);
        intervalDescriptionTextArea.setOpaque(false);
        jPanel1.add(intervalDescriptionTextArea, java.awt.BorderLayout.NORTH);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 20, 0, 0));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel10.setLayout(new java.awt.GridBagLayout());

        minutesLabel.setLabelFor(minutesSpinner);
        org.openide.awt.Mnemonics.setLocalizedText(minutesLabel, OStrings.getString("SAVE_DIALOG_MINUTES")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(minutesLabel, gridBagConstraints);

        minutesSpinner.setValue(90);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 50;
        jPanel10.add(minutesSpinner, gridBagConstraints);

        secondsLabel.setLabelFor(secondsSpinner);
        org.openide.awt.Mnemonics.setLocalizedText(secondsLabel, OStrings.getString("SAVE_DIALOG_SECONDS")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(secondsLabel, gridBagConstraints);

        secondsSpinner.setValue(90);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 50;
        jPanel10.add(secondsSpinner, gridBagConstraints);

        jPanel5.add(jPanel10, java.awt.BorderLayout.WEST);

        jPanel1.add(jPanel5, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));
        jPanel2.setLayout(new java.awt.BorderLayout());

        externalCommandLabel.setLabelFor(externalCommandTextArea);
        org.openide.awt.Mnemonics.setLocalizedText(externalCommandLabel, OStrings.getString("EXTERNAL_COMMAND_LABEL")); // NOI18N
        jPanel2.add(externalCommandLabel, java.awt.BorderLayout.NORTH);

        jPanel8.setLayout(new java.awt.BorderLayout());

        externalCmdDescriptionTextArea.setEditable(false);
        externalCmdDescriptionTextArea.setFont(new JLabel().getFont());
        externalCmdDescriptionTextArea.setLineWrap(true);
        externalCmdDescriptionTextArea.setText(OStrings.getString("EXTERNAL_COMMAND_DESCRIPTION")); // NOI18N
        externalCmdDescriptionTextArea.setWrapStyleWord(true);
        externalCmdDescriptionTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1));
        externalCmdDescriptionTextArea.setOpaque(false);
        jPanel8.add(externalCmdDescriptionTextArea, java.awt.BorderLayout.NORTH);

        externalCommandTextArea.setColumns(20);
        externalCommandTextArea.setLineWrap(true);
        externalCommandTextArea.setRows(5);
        externalCommandScrollPane.setViewportView(externalCommandTextArea);

        jPanel8.add(externalCommandScrollPane, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(variablesLabel, OStrings.getString("EXT_TMX_MATCHES_TEMPLATE_VARIABLES")); // NOI18N
        jPanel4.add(variablesLabel, java.awt.BorderLayout.WEST);

        variablesList.setModel(new DefaultComboBoxModel<String>((String[])org.omegat.core.data.CommandVarExpansion.COMMAND_VARIABLES));
        jPanel4.add(variablesList, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(insertButton, OStrings.getString("BUTTON_INSERT")); // NOI18N
        insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButtonActionPerformed(evt);
            }
        });
        jPanel4.add(insertButton, java.awt.BorderLayout.EAST);

        jPanel8.add(jPanel4, java.awt.BorderLayout.SOUTH);

        jPanel2.add(jPanel8, java.awt.BorderLayout.CENTER);

        allowProjectCmdCheckBox.setFont(new JLabel().getFont());
        org.openide.awt.Mnemonics.setLocalizedText(allowProjectCmdCheckBox, OStrings.getString("ALLOW_PROJECT_EXTERN_CMD")); // NOI18N
        jPanel2.add(allowProjectCmdCheckBox, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(okButton, OStrings.getString("BUTTON_OK")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel9.add(okButton);

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, OStrings.getString("BUTTON_CANCEL")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel9.add(cancelButton);

        jPanel3.add(jPanel9, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void insertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertButtonActionPerformed
        externalCommandTextArea.replaceSelection(variablesList.getSelectedItem().toString());
    }//GEN-LAST:event_insertButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_okButtonActionPerformed
    {
        int saveMinutes = 0;
        int saveSeconds = 0;
        
        try {
            saveMinutes = Integer.parseInt(minutesSpinner.getValue().toString());
        } catch (NumberFormatException nfe) {
            // Eat exception silently            
        }
        
        try {
            saveSeconds = Integer.parseInt(secondsSpinner.getValue().toString());
        } catch (NumberFormatException nfe) {
            // Eat exception silently            
        }
        
        int saveInterval = saveMinutes * 60 + saveSeconds;

        if (saveInterval < 10) {
            saveInterval = 10; // 10 seconds minimum
        }
        
        Preferences.setPreference(Preferences.AUTO_SAVE_INTERVAL, saveInterval);

        Preferences.setPreference(Preferences.EXTERNAL_COMMAND, externalCommandTextArea.getText());
        Preferences.setPreference(Preferences.ALLOW_PROJECT_EXTERN_CMD, allowProjectCmdCheckBox.isSelected());

        doClose(RET_OK);
    }// GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_cancelButtonActionPerformed
    {
        doClose(RET_CANCEL);
    }// GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt)// GEN-FIRST:event_closeDialog
    {
        doClose(RET_CANCEL);
    }// GEN-LAST:event_closeDialog

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowProjectCmdCheckBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea externalCmdDescriptionTextArea;
    private javax.swing.JLabel externalCommandLabel;
    private javax.swing.JScrollPane externalCommandScrollPane;
    private javax.swing.JTextArea externalCommandTextArea;
    private javax.swing.JButton insertButton;
    private javax.swing.JTextArea intervalDescriptionTextArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel minutesLabel;
    private javax.swing.JSpinner minutesSpinner;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel secondsLabel;
    private javax.swing.JSpinner secondsSpinner;
    private javax.swing.JLabel variablesLabel;
    private javax.swing.JComboBox<String> variablesList;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
