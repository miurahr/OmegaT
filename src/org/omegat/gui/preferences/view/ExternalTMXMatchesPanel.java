/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2010 Didier Briel
               2014-2015 Aaron Madlon-Kay
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import org.omegat.core.matching.NearString.SORT_KEY;
import org.omegat.gui.matches.MatchesVarExpansion;
import org.omegat.util.OStrings;

/**
 * 
 * @author Maxym Mykhalchuk
 * @author Didier Briel
 * @author Aaron Madlon-Kay
 */
@SuppressWarnings("serial")
public class ExternalTMXMatchesPanel extends JPanel {

    /** Creates new form WorkflowOptionsDialog */
    public ExternalTMXMatchesPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        sortMatchesLabel = new javax.swing.JLabel();
        sortMatchesList = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        tagHandlingLabel = new javax.swing.JLabel();
        displayLevel2Tags = new javax.swing.JCheckBox();
        useSlash = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        templateLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        matchesTemplate = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        variablesLabel = new javax.swing.JLabel();
        variablesList = new javax.swing.JComboBox<>();
        insertButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(sortMatchesLabel, OStrings.getString("EXT_TMX_SORT_KEY")); // NOI18N
        jPanel1.add(sortMatchesLabel);
        jPanel1.add(sortMatchesList);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(tagHandlingLabel, OStrings.getString("EXT_TMX_DESCRIPTION")); // NOI18N
        jPanel7.add(tagHandlingLabel);

        displayLevel2Tags.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(displayLevel2Tags, OStrings.getString("EXT_TMX_SHOW_LEVEL2")); // NOI18N
        jPanel7.add(displayLevel2Tags);

        useSlash.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(useSlash, OStrings.getString("EXT_TMX_USE_XML")); // NOI18N
        jPanel7.add(useSlash);

        jPanel2.add(jPanel7, java.awt.BorderLayout.PAGE_END);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));
        jPanel3.setLayout(new java.awt.BorderLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/omegat/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(templateLabel, bundle.getString("EXT_TMX_MATCHES_TEMPLATE")); // NOI18N
        jPanel3.add(templateLabel, java.awt.BorderLayout.NORTH);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(525, 25));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(446, 96));

        matchesTemplate.setColumns(30);
        matchesTemplate.setRows(5);
        jScrollPane1.setViewportView(matchesTemplate);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(variablesLabel, bundle.getString("EXT_TMX_MATCHES_TEMPLATE_VARIABLES")); // NOI18N
        jPanel4.add(variablesLabel, java.awt.BorderLayout.WEST);

        variablesList.setModel(new DefaultComboBoxModel<>(MatchesVarExpansion.MATCHES_VARIABLES));
        jPanel4.add(variablesList, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(insertButton, bundle.getString("BUTTON_INSERT")); // NOI18N
        jPanel4.add(insertButton, java.awt.BorderLayout.EAST);

        jPanel3.add(jPanel4, java.awt.BorderLayout.PAGE_END);

        add(jPanel3, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox displayLevel2Tags;
    javax.swing.JButton insertButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    javax.swing.JTextArea matchesTemplate;
    private javax.swing.JLabel sortMatchesLabel;
    javax.swing.JComboBox<SORT_KEY> sortMatchesList;
    private javax.swing.JLabel tagHandlingLabel;
    private javax.swing.JLabel templateLabel;
    javax.swing.JCheckBox useSlash;
    private javax.swing.JLabel variablesLabel;
    javax.swing.JComboBox<String> variablesList;
    // End of variables declaration//GEN-END:variables
}
