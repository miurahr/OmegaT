/**************************************************************************
 OmegaT - Java based Computer Assisted Translation (CAT) tool
 Copyright (C) 2002-2005  Keith Godfrey et al
                          keithgodfrey@users.sourceforge.net
                          907.223.2039

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
**************************************************************************/

package org.omegat.gui.dialogs;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.omegat.util.OStrings;

/**
 * About dialog, showing OmegaT version and information on contributors.
 *
 * @author Maxym Mykhalchuk
 */
public class AboutDialog extends JDialog
{
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    
    /** Creates new form AboutDialog */
    public AboutDialog(Frame parent)
    {
        super(parent, true);
        initComponents();
        versionLabel.setText(  OStrings.getString("ABOUTDIALOG_VERSION_PREFIX")+OStrings.VERSION);
        abouttext.setCaretPosition(1);
    }
    
    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus()
    {
        return returnStatus;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        buttonPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        licenseButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        versionLabel = new javax.swing.JLabel();
        scroll = new javax.swing.JScrollPane();
        abouttext = new javax.swing.JTextArea();

        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        setTitle(OStrings.getString("ABOUTDIALOG_TITLE"));
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                closeDialog(evt);
            }
        });

        buttonPanel.setLayout(new java.awt.BorderLayout());

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, OStrings.getString("ABOUTDIALOG_COPYRIGHT"));
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.add(jLabel2, java.awt.BorderLayout.WEST);

        org.openide.awt.Mnemonics.setLocalizedText(licenseButton, OStrings.getString("ABOUTDIALOG_LICENSE_BUTTON"));
        licenseButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                licenseButtonActionPerformed(evt);
            }
        });

        jPanel1.add(licenseButton);

        org.openide.awt.Mnemonics.setLocalizedText(okButton, OStrings.getString("BUTTON_OK"));
        okButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okButtonActionPerformed(evt);
            }
        });

        jPanel1.add(okButton);

        buttonPanel.add(jPanel1, java.awt.BorderLayout.EAST);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        versionLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/omegat/gui/resources/OmegaT.gif")));
        versionLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        getContentPane().add(versionLabel, java.awt.BorderLayout.NORTH);

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        abouttext.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        abouttext.setEditable(false);
        abouttext.setFont(versionLabel.getFont());
        abouttext.setLineWrap(true);
        abouttext.setText(OStrings.getString("ABOUTDIALOG_CONTRIBUTORS"));
        abouttext.setWrapStyleWord(true);
        scroll.setViewportView(abouttext);

        getContentPane().add(scroll, java.awt.BorderLayout.CENTER);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-515)/2, (screenSize.height-451)/2, 515, 451);
    }
    // </editor-fold>//GEN-END:initComponents

    private void licenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenseButtonActionPerformed
        new LicenseDialog(this).setVisible(true);
    }//GEN-LAST:event_licenseButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed
        
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
    {
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog
    
    private void doClose(int retStatus)
    {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea abouttext;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton licenseButton;
    private javax.swing.JButton okButton;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
    
    private int returnStatus = RET_CANCEL;

}
