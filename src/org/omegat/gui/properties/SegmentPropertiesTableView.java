package org.omegat.gui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.omegat.core.Core;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.DataTableStyling;
import org.omegat.util.gui.TableColumnSizer;
import org.omegat.util.gui.UIThreadsUtil;

@SuppressWarnings("serial")
public class SegmentPropertiesTableView implements ISegmentPropertiesView {

    private SegmentPropertiesArea parent;
    private FlashableTable table;
    private PropertiesTableModel model;
    
    @Override
    public void install(SegmentPropertiesArea parent) {
        UIThreadsUtil.mustBeSwingThread();
        this.parent = parent;
        model = new PropertiesTableModel();
        table = new FlashableTable(model);
        table.setForeground(parent.getForeground());
        table.setBackground(parent.getBackground());
        table.addMouseListener(parent.contextMenuListener);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setGridColor(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new MultilineCellRenderer());
        table.getColumnModel().getColumn(0).setCellRenderer(new SingleLineCellRenderer());
        DataTableStyling.applyFont(table, Core.getMainWindow().getApplicationFont());
        TableColumnSizer.autoSize(table, 1, true).addColumnAdjustmentListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustRowHeights();
            }
        });
        parent.setViewportView(table);
    }
    
    @Override
    public JComponent getViewComponent() {
        return table;
    }
    
    @Override
    public void update() {
        UIThreadsUtil.mustBeSwingThread();
        table.clearSelection();
        table.clearHighlight();
        model.fireTableDataChanged();
        adjustRowHeights();
    }
    
    private void adjustRowHeights() {
        int column1Width = table.getColumnModel().getColumn(1).getWidth();
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer cellRenderer = table.getCellRenderer(row, 1);
            Component c = table.prepareRenderer(cellRenderer, row, 1);
            c.setBounds(0, 0, column1Width, Integer.MAX_VALUE);
            int height = c.getPreferredSize().height;
            table.setRowHeight(row, height);
        }
    }
    
    @Override
    public void notifyUser(List<Integer> notify) {
        UIThreadsUtil.mustBeSwingThread();
        notify = translateIndices(notify);
        table.clearSelection();
        table.scrollRectToVisible(table.getCellRect(notify.get(0), notify.get(notify.size() - 1), true));
        table.flash(notify);
    }
    
    private List<Integer> translateIndices(List<Integer> indices) {
        List<Integer> result = new ArrayList<Integer>(indices.size());
        for (int i : indices) {
            result.add(i / 2);
        }
        return result;
    }
    
    @Override
    public String getKeyAtPoint(Point p) {
        int clickedRow = table.rowAtPoint(SwingUtilities.convertPoint(parent, p, table));
        if (clickedRow == -1) {
            return null;
        }
        return (String) model.getValueAt(clickedRow, 0);
    }
    
    private class PropertiesTableModel extends AbstractTableModel {

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
            case 0: return OStrings.getString("SEGPROP_TABLE_HEADER_KEY");
            case 1: return OStrings.getString("SEGPROP_TABLE_HEADER_VALUE");
            }
            return null;
        }

        @Override
        public int getRowCount() {
            return parent.properties.size() / 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return parent.properties.get(rowIndex * 2 + columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
    
    private class FlashableTable extends JTable {
        private Flasher flasher;
        
        public FlashableTable(TableModel model) {
            super(model);
        }
        
        public void flash(List<Integer> rows) {
            flasher = new Flasher(rows);
            repaint();
        }
        
        public void clearHighlight() {
            flasher = null;
        }
        
        public Flasher getFlasher() {
            return flasher;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (flasher != null && flasher.isFlashing()) {
                repaint();
            }
        }
    }
    
    private static class SingleLineCellRenderer extends DefaultTableCellRenderer {

        private final Border noFocusBorder = new EmptyBorder(FOCUS_BORDER.getBorderInsets(this));
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
            if (!(comp instanceof JLabel)) {
                return comp;
            }
            JLabel result = (JLabel) comp;
            result.setVerticalAlignment(SwingConstants.TOP);
            if (isSelected) {
                result.setBackground(table.getSelectionBackground());
                result.setForeground(table.getSelectionForeground());
            } else {
                result.setBackground(row % 2 == 0 ? table.getBackground() : ROW_HIGHLIGHT_COLOR);
                result.setForeground(table.getForeground());
            }
            Border marginBorder = new EmptyBorder(1, column == 0 ? 5 : 1,
                    1, column == table.getColumnCount() - 1 ? 5 : 1);
            if (hasFocus) {
                result.setBorder(new CompoundBorder(marginBorder, FOCUS_BORDER));
            } else {
                result.setBorder(new CompoundBorder(marginBorder, noFocusBorder));
            }
            Flasher flasher = ((FlashableTable) table).getFlasher();
            if (flasher != null) {
                flasher.mark();
                if (flasher.isHighlightedIndex(row) && !isSelected) {
                    setBackground(flasher.getColor());
                }
            }
            result.setFont(table.getFont());
            result.setText(getText(value));
            return result;
        }
        
        private String getText(Object value) {
            if (Preferences.isPreference(Preferences.SEGPROPS_SHOW_RAW_KEYS)) {
                return value.toString();
            }
            try {
                return OStrings.getString(PROPERTY_TRANSLATION_KEY + value.toString().toUpperCase());
            } catch (MissingResourceException ex) {
                return value.toString();
            }
        }
    }
    
    // See: http://esus.com/creating-a-jtable-with-multiline-cells/
    private static class MultilineCellRenderer extends JTextArea implements TableCellRenderer {
        
        private final Border noFocusBorder = new EmptyBorder(FOCUS_BORDER.getBorderInsets(this));
        
        public MultilineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(row % 2 == 0 ? table.getBackground() : ROW_HIGHLIGHT_COLOR);
                setForeground(table.getForeground());
            }
            Border marginBorder = new EmptyBorder(1, column == 0 ? 5 : 1,
                    1, column == table.getColumnCount() - 1 ? 5 : 1);
            if (hasFocus) {
                setBorder(new CompoundBorder(marginBorder, FOCUS_BORDER));
            } else {
                setBorder(new CompoundBorder(marginBorder, noFocusBorder));
            }
            Flasher flasher = ((FlashableTable) table).getFlasher();
            if (flasher != null) {
                flasher.mark();
                if (flasher.isHighlightedIndex(row)) {
                    setBackground(flasher.getColor());
                }
            }
            setFont(table.getFont());
            setText(value.toString());
            return this;
        }
    }
}
