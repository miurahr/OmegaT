/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2015 Aaron Madlon-Kay
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

package org.omegat.util.gui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Aaron Madlon-Kay
 */
public class TableColumnSizer {
    
    private int[] optimalColWidths;
    private int cutoverWidth = -1;
    private boolean didManuallyAdjustCols;
    private int remainderColumn = -1;
    
    private final JTable table;

    public TableColumnSizer(JTable table) {
        this(table, -1);
    }
    
    public TableColumnSizer(JTable table, int remainderColumn) {
        this.table = table;
        this.remainderColumn = remainderColumn;
        table.getColumnModel().addColumnModelListener(colListener);
        table.addPropertyChangeListener("columnModel", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object val = evt.getNewValue();
                if (!(val instanceof TableColumnModel)) {
                    return;
                }
                ((TableColumnModel) val).addColumnModelListener(colListener);
            }
        });
    }
    
    TableColumnModelListener colListener = new TableColumnModelListener() {
        @Override
        public void columnAdded(TableColumnModelEvent e) {
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
            TableColumn col = table.getTableHeader().getResizingColumn();
            if (col != null) {
                // User has manually resized a column. Don't try auto-sizing.
                didManuallyAdjustCols = true;
            }
        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
        }

        @Override
        public void columnSelectionChanged(ListSelectionEvent e) {
        }
    };
    
    /**
     * Calculate each column's ideal width, based on header and cells.
     * Results are cached.
     */
    private void calculateOptimalColWidths() {
        if (optimalColWidths != null) {
            return;
        }
        optimalColWidths = new int[table.getColumnCount()];
        
        // See: https://tips4java.wordpress.com/2008/11/10/table-column-adjuster/
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn col = table.getColumnModel().getColumn(column);
            int preferredWidth = col.getMinWidth();
            int maxWidth = col.getMaxWidth();

            for (int row = -1; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer;
                Component c;
                int margin = 5;
                if (row == -1) {
                    cellRenderer = col.getHeaderRenderer();
                    if (cellRenderer == null) {
                        cellRenderer = col.getCellRenderer();
                    }
                    if (cellRenderer == null) {
                        cellRenderer = table.getDefaultRenderer(table.getModel().getColumnClass(column));
                    }
                    c = cellRenderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, column);
                    // Add somewhat arbitrary margin to header because it gets truncated at a smaller width
                    // than a regular cell does (Windows LAF more than OS X LAF).
                    margin = 10;
                } else {
                    cellRenderer = table.getCellRenderer(row, column);
                    c = table.prepareRenderer(cellRenderer, row, column);
                }
                
                c.setBounds(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width + margin;
                preferredWidth = Math.max(preferredWidth, width);

                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }
            optimalColWidths[column] = preferredWidth;
        }
    }
    
    public void reset() {
        optimalColWidths = null;
        cutoverWidth = -1;
    }
    
    /**
     * Adjust the columns of the table.
     * 
     * If possible, this optimally sizes the columns such that columns greater
     * than 0 are only as big as necessary, and the rest of the space goes to
     * column 0.
     * 
     * This auto-sizing only happens if it represents an improvement over the
     * default sizing (gives more space to column 0), and only if the user has
     * not manually adjusted column widths.
     * 
     * Once auto-sizing is invoked, the width at which it was first invoked is
     * recorded as a boundary below which default sizing is used again.
     */
    public void adjustTableColumns() {
        calculateOptimalColWidths();
        
        int otherCols = 0;
        for (int i = 0; i < optimalColWidths.length; i++) {
            if (i == remainderColumn) {
                continue;
            }
            otherCols += optimalColWidths[i];
        }
        
        int remainderColWidth = table.getParent().getWidth() - otherCols;
                        
        if (shouldAutoSize(remainderColWidth)) {
            if (cutoverWidth == -1) {
                cutoverWidth = table.getParent().getWidth();
            }
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.getColumnModel().getColumn(0).setPreferredWidth(remainderColWidth);
            for (int width, i = 0; i < optimalColWidths.length; i++) {
                width = optimalColWidths[i];
                if (i == remainderColumn) {
                    width = remainderColWidth;
                }
                table.getColumnModel().getColumn(i).setPreferredWidth(width);
            }
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        }
    }
    
    private boolean shouldAutoSize(int proposedRemainderWidth) {
        if (didManuallyAdjustCols) {
            return false;
        }
        if (remainderColumn == -1) {
            return true;
        }
        if (proposedRemainderWidth > optimalColWidths[remainderColumn]) {
            return true;
        }
        if (cutoverWidth != -1) {
            return table.getParent().getWidth() >= cutoverWidth;
        }
        return proposedRemainderWidth > table.getColumnModel().getColumn(remainderColumn).getWidth();
    }
}
