/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2007 Zoltan Bartko
               2008-2011 Didier Briel
               2012 Martin Fleurke, Didier Briel
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
 * @author Zoltan Bartko
 * @author Didier Briel
 * @author Martin Fleurke
 * @author Aaron Madlon-Kay
 */
@SuppressWarnings("serial")
public class SpellcheckerConfigurationPanel extends javax.swing.JPanel {

    /**
     * The dictionary manager
     */

    /**
     * Creates new form SpellcheckerConfigurationDialog
     */
    public SpellcheckerConfigurationPanel() {
        initComponents();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        autoSpellcheckCheckBox = new javax.swing.JCheckBox();
        detailPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        directoryLabel = new javax.swing.JLabel();
        directoryTextField = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        directoryChooserButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        contentLabel = new javax.swing.JLabel();
        languageScrollPane = new javax.swing.JScrollPane();
        languageList = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        uninstallButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        dictionaryUrlLabel = new javax.swing.JLabel();
        dictionaryUrlTextField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        installButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(autoSpellcheckCheckBox, OStrings.getString("GUI_SPELLCHECKER_AUTOSPELLCHECKCHECKBOX")); // NOI18N
        autoSpellcheckCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 10));
        autoSpellcheckCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        add(autoSpellcheckCheckBox, java.awt.BorderLayout.NORTH);

        detailPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 5, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(directoryLabel, OStrings.getString("GUI_SPELLCHECKER_DICTIONARYLABEL")); // NOI18N
        jPanel1.add(directoryLabel, java.awt.BorderLayout.NORTH);
        jPanel1.add(directoryTextField, java.awt.BorderLayout.CENTER);

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(directoryChooserButton, OStrings.getString("GUI_SPELLCHECKER_DIRECTORYCHOOSERBUTTON")); // NOI18N
        jPanel8.add(directoryChooserButton);

        jPanel1.add(jPanel8, java.awt.BorderLayout.EAST);

        detailPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        jPanel3.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(contentLabel, OStrings.getString("GUI_SPELLCHECKER_AVAILABLE_LABEL")); // NOI18N
        jPanel3.add(contentLabel, java.awt.BorderLayout.NORTH);

        languageScrollPane.setViewportView(languageList);

        jPanel3.add(languageScrollPane, java.awt.BorderLayout.CENTER);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        jPanel4.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(uninstallButton, OStrings.getString("GUI_SPELLCHECKER_UNINSTALLBUTTON")); // NOI18N
        jPanel4.add(uninstallButton, java.awt.BorderLayout.NORTH);

        jPanel3.add(jPanel4, java.awt.BorderLayout.EAST);

        detailPanel.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
        jPanel5.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(dictionaryUrlLabel, OStrings.getString("GUI_SPELLCHECKER_URL_LABEL")); // NOI18N
        jPanel5.add(dictionaryUrlLabel, java.awt.BorderLayout.NORTH);
        jPanel5.add(dictionaryUrlTextField, java.awt.BorderLayout.CENTER);

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 0, 0));
        jPanel6.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(installButton, OStrings.getString("GUI_SPELLCHECKER_INSTALLBUTTON")); // NOI18N
        jPanel6.add(installButton, java.awt.BorderLayout.WEST);

        jPanel5.add(jPanel6, java.awt.BorderLayout.SOUTH);

        detailPanel.add(jPanel5, java.awt.BorderLayout.SOUTH);

        add(detailPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox autoSpellcheckCheckBox;
    private javax.swing.JLabel contentLabel;
    javax.swing.JPanel detailPanel;
    private javax.swing.JLabel dictionaryUrlLabel;
    javax.swing.JTextField dictionaryUrlTextField;
    javax.swing.JButton directoryChooserButton;
    private javax.swing.JLabel directoryLabel;
    javax.swing.JTextField directoryTextField;
    javax.swing.JButton installButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    javax.swing.JList<String> languageList;
    private javax.swing.JScrollPane languageScrollPane;
    javax.swing.JButton uninstallButton;
    // End of variables declaration//GEN-END:variables

}
