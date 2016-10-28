/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2013 Zoltan Bartko
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

package org.omegat.gui.preferences.view;

import org.omegat.util.OStrings;

/**
 *
 * @author bartkoz
 */
@SuppressWarnings("serial")
public class GlossaryAutoCompleterOptionsPanel extends javax.swing.JPanel {
    
    /**
     * Creates new form GlossaryAutoCompleterOptionsDialog
     */
    public GlossaryAutoCompleterOptionsPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceButtonGroup = new javax.swing.ButtonGroup();
        enabledCheckBox = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        optionsPanel = new javax.swing.JPanel();
        displaySourceCheckBox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        sourceFirstRadioButton = new javax.swing.JRadioButton();
        targetFirstRadioButton = new javax.swing.JRadioButton();
        sortBySourceCheckBox = new javax.swing.JCheckBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel1 = new javax.swing.JPanel();
        longerFirstCheckBox = new javax.swing.JCheckBox();
        sortEntriesCheckBox = new javax.swing.JCheckBox();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        enabledCheckBox.setText(OStrings.getString("AC_GLOSSARY_ENABLED")); // NOI18N
        enabledCheckBox.setAlignmentY(0.0F);
        add(enabledCheckBox);
        add(filler1);

        optionsPanel.setAlignmentX(0.0F);
        optionsPanel.setAlignmentY(0.0F);
        optionsPanel.setLayout(new javax.swing.BoxLayout(optionsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        displaySourceCheckBox.setText(OStrings.getString("AC_OPTIONS_DISPLAY_SOURCE")); // NOI18N
        displaySourceCheckBox.setToolTipText(OStrings.getString("AC_OPTIONS_DISPLAY_SOURCE_TOOLTIP")); // NOI18N
        displaySourceCheckBox.setAlignmentY(0.0F);
        optionsPanel.add(displaySourceCheckBox);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0));
        jPanel3.setAlignmentX(0.0F);
        jPanel3.setAlignmentY(0.0F);
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        sourceButtonGroup.add(sourceFirstRadioButton);
        sourceFirstRadioButton.setText(OStrings.getString("AC_OPTIONS_SOURCE_FIRST")); // NOI18N
        sourceFirstRadioButton.setToolTipText(OStrings.getString("AC_OPTIONS_SOURCE_FIRST")); // NOI18N
        jPanel2.add(sourceFirstRadioButton);

        sourceButtonGroup.add(targetFirstRadioButton);
        targetFirstRadioButton.setText(OStrings.getString("AC_OPTIONS_TARGET_FIRST")); // NOI18N
        targetFirstRadioButton.setToolTipText(OStrings.getString("AC_OPTIONS_TARGET_FIRST_TOOLTIP")); // NOI18N
        jPanel2.add(targetFirstRadioButton);

        jPanel3.add(jPanel2);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/omegat/Bundle"); // NOI18N
        sortBySourceCheckBox.setText(bundle.getString("AC_OPTIONS_SORT_SOURCE_ALPHABETICALLY")); // NOI18N
        sortBySourceCheckBox.setAlignmentY(0.0F);
        jPanel3.add(sortBySourceCheckBox);

        optionsPanel.add(jPanel3);

        add(optionsPanel);
        add(filler2);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(OStrings.getString("AC_GLOSSARY_TARGET_PANEL"))); // NOI18N
        jPanel1.setAlignmentY(0.0F);
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        longerFirstCheckBox.setText(OStrings.getString("AC_OPTIONS_SORT_BY_LENGTH")); // NOI18N
        longerFirstCheckBox.setToolTipText(OStrings.getString("AC_OPTIONS_SORT_BY_LENGTH_TOOLTIP")); // NOI18N
        jPanel1.add(longerFirstCheckBox);

        sortEntriesCheckBox.setText(OStrings.getString("AC_OPTIONS_SORT_TARGET_ALPHABETICALLY")); // NOI18N
        sortEntriesCheckBox.setToolTipText(OStrings.getString("AC_OPTIONS_SORT_ALPHABETICALLY_TOOLTIP")); // NOI18N
        jPanel1.add(sortEntriesCheckBox);

        add(jPanel1);
        add(filler3);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox displaySourceCheckBox;
    javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    javax.swing.JCheckBox longerFirstCheckBox;
    javax.swing.JPanel optionsPanel;
    javax.swing.JCheckBox sortBySourceCheckBox;
    javax.swing.JCheckBox sortEntriesCheckBox;
    private javax.swing.ButtonGroup sourceButtonGroup;
    javax.swing.JRadioButton sourceFirstRadioButton;
    javax.swing.JRadioButton targetFirstRadioButton;
    // End of variables declaration//GEN-END:variables
}
