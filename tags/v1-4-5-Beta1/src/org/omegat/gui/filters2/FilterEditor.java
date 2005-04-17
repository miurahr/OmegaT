/*
 * SingleFilterEditor.java
 *
 * Created on 30 ������� 2004 �., 23:40
 */

package org.omegat.gui.filters2;

import java.awt.Dialog;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.omegat.filters2.AbstractFilter;
import org.omegat.filters2.Instance;
import org.omegat.filters2.master.FilterMaster;
import org.omegat.filters2.master.OneFilter;
import org.omegat.filters2.master.Filters;

/**
 *
 * @author  user
 */
public class FilterEditor extends JDialog implements ListSelectionListener
{
    
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    
    private Filters filters;
    private int index;
    private OneFilter filter;    
    
    /** Creates new form SingleFilterEditor */
    public FilterEditor(Dialog parent, Filters filters, int index)
    {
        super(parent, true);
        this.filter = filters.getFilter(index);
        this.filters = filters;
        this.index = index;
        
        initComponents();
        
        getRootPane().setDefaultButton(okButton);

        instances.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        instances.getSelectionModel().addListSelectionListener(this);
        
        TableColumn sourceEnc = instances.getColumnModel().getColumn(1);
        sourceEnc.setCellEditor(new DefaultCellEditor(encodingComboBox()));
        TableColumn targetEnc = instances.getColumnModel().getColumn(2);
        targetEnc.setCellEditor(new DefaultCellEditor(encodingComboBox()));
    }
    
    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus()
    {
        return returnStatus;
    }
    
    private JComboBox encodingComboBox()
    {
        return new JComboBox(new Vector(FilterMaster.getSupportedEncodings()));
    }
    
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting()) return;
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty())
        {
            editButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
        else
        {
            int selectedRow = lsm.getMinSelectionIndex();
            editButton.setEnabled(true);
            removeButton.setEnabled(true);
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonPanel = new javax.swing.JPanel();
        toDefaultsButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        instancesScrollPane = new javax.swing.JScrollPane();
        instances = new javax.swing.JTable();
        removeButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Edit a single file filter");
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                closeDialog(evt);
            }
        });

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        org.openide.awt.Mnemonics.setLocalizedText(toDefaultsButton, "Revert to &Defaults");
        toDefaultsButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                toDefaultsButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(toDefaultsButton);

        org.openide.awt.Mnemonics.setLocalizedText(okButton, "&OK");
        okButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okButton);

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, "&Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(buttonPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "File &Format");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(jLabel1, gridBagConstraints);

        jTextField1.setEditable(false);
        jTextField1.setText("Text Files");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(jTextField1, gridBagConstraints);

        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new JLabel().getFont());
        jTextArea1.setLineWrap(true);
        jTextArea1.setText("Here you may edit filter instances. Each filter instance must have a different source filename pattern and may have different source and target file encoding and target filename pattern.\nNote that not all filters allow you to select an encoding.");
        jTextArea1.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(jTextArea1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        instances.setModel(filter);
        instancesScrollPane.setViewportView(instances);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(instancesScrollPane, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, "&Remove");
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(removeButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(editButton, "&Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                editButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(editButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, "&Add...");
        addButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(addButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(jPanel3, gridBagConstraints);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-381)/2, (screenSize.height-333)/2, 381, 333);
    }//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removeButtonActionPerformed
    {//GEN-HEADEREND:event_removeButtonActionPerformed
        int row = instances.getSelectedRow();
        Instance instance = filter.getInstance(row);
        if( JOptionPane.OK_OPTION ==
                JOptionPane.showConfirmDialog(this, 
                MessageFormat.format("Do you really want to delete a filter instance for input filename mask \"{0}\" ?", new Object[] { instance.getSourceFilenameMask() }),
                "Confirm deletion",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) )
        {
            filter.removeInstance(row);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void toDefaultsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_toDefaultsButtonActionPerformed
    {//GEN-HEADEREND:event_toDefaultsButtonActionPerformed
        try
        {
            Class filterClass = Class.forName(filter.getClassName());
            Constructor filterConstructor = filterClass.getConstructor((Class[])null);
            AbstractFilter filterObject = (AbstractFilter)filterConstructor.newInstance((Object[])null);
            filter = new OneFilter(filterObject);
            filters.setFilter(index, filter);
            instances.setModel(filter);
        }
        catch( Exception e )
        {
            JOptionPane.showMessageDialog(this, "Cannot revert to default the single filter. \n\nException occured: \n" + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_toDefaultsButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addButtonActionPerformed
    {//GEN-HEADEREND:event_addButtonActionPerformed
        InstanceEditor ie = new InstanceEditor(this, filter.isSourceEncodingVariable(), filter.isTargetEncodingVariable());
        ie.setVisible(true);
        if( ie.getReturnStatus()==InstanceEditor.RET_OK )
        {
            filter.addInstance(
                    new Instance(
                    ie.getSourceFilenameMask(),
                    ie.getSourceEncoding(),
                    ie.getTargetEncoding(),
                    ie.getTargetFilenamePattern())
                    );
        }
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editButtonActionPerformed
    {//GEN-HEADEREND:event_editButtonActionPerformed
        int row = instances.getSelectedRow();
        InstanceEditor ie = new InstanceEditor(this, 
                filter.isSourceEncodingVariable(), 
                filter.isTargetEncodingVariable(), 
                instances.getModel().getValueAt(row, 0).toString(),
                instances.getModel().getValueAt(row, 1).toString(), 
                instances.getModel().getValueAt(row, 2).toString(),
                instances.getModel().getValueAt(row, 3).toString());
        ie.setVisible(true);
        if( ie.getReturnStatus()==InstanceEditor.RET_OK )
        {
            filter.setInstance(
                    row, 
                    new Instance(
                    ie.getSourceFilenameMask(),
                    ie.getSourceEncoding(),
                    ie.getTargetEncoding(),
                    ie.getTargetFilenamePattern())
                    );
        }
    }//GEN-LAST:event_editButtonActionPerformed
        
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog
    
    private void doClose(int retStatus)
    {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    // ���������� ���������� - �� ��������� ������ ���//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton editButton;
    private javax.swing.JTable instances;
    private javax.swing.JScrollPane instancesScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton toDefaultsButton;
    // ����� ���������� ����������//GEN-END:variables
    
    private int returnStatus = RET_CANCEL;
    
}
