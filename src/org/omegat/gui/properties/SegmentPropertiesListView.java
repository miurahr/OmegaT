package org.omegat.gui.properties;

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.omegat.core.Core;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;

@SuppressWarnings("serial")
public class SegmentPropertiesListView implements ISegmentPropertiesView {

    private SegmentPropertiesArea parent;
    private FlashableList list;
    private PropertiesListModel model;

    public void install(SegmentPropertiesArea parent) {
        this.parent = parent;
        model = new PropertiesListModel();
        list = new FlashableList(model);
        list.setForeground(parent.getForeground());
        list.setBackground(parent.getBackground());
        list.addMouseListener(parent.contextMenuListener);
        list.setCellRenderer(new MultilineCellRenderer());
        list.setFont(Core.getMainWindow().getApplicationFont());
        parent.setViewportView(list);
    }
    
    @Override
    public void update() {
        list.clearSelection();
        list.clearHighlight();
        model.fireModelChanged();
    }

    @Override
    public JComponent getViewComponent() {
        return list;
    }

    @Override
    public void notifyUser(List<Integer> notify) {
        notify = translateIndices(notify);
        list.clearSelection();
        list.scrollRectToVisible(list.getCellBounds(notify.get(0), notify.get(notify.size() - 1)));
        list.flash(notify);
    }

    private List<Integer> translateIndices(List<Integer> indices) {
        List<Integer> result = new ArrayList<Integer>(indices.size());
        for (int i : indices) {
            result.add(i * 2);
        }
        return result;
    }
    
    @Override
    public void uninstall() {
        // Nothing
    }

    private class PropertiesListModel extends DefaultListModel {

        @Override
        public Object getElementAt(int i) {
            return parent.properties.get(i);
        }

        @Override
        public int getSize() {
            return parent.properties.size();
        }
        
        public void fireModelChanged() {
            fireContentsChanged(parent, 0, getSize() - 1);
        }
    }
    
    private class FlashableList extends JList {
        private Flasher flasher;
        
        public FlashableList(ListModel model) {
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
    
    private static class MultilineCellRenderer extends JTextArea implements ListCellRenderer {
        
        private final Border noFocusBorder = new EmptyBorder(FOCUS_BORDER.getBorderInsets(this));
        private final Border noFocusCompoundBorder = new CompoundBorder(MARGIN_BORDER, noFocusBorder);
        
        public MultilineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            boolean isKeyRow = index % 2 == 0;
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(isKeyRow ? ROW_HIGHLIGHT_COLOR : list.getBackground());
                setForeground(list.getForeground());
            }
            if (cellHasFocus) {
                setBorder(FOCUS_COMPOUND_BORDER);
            } else {
                setBorder(noFocusCompoundBorder);
            }
            Flasher flasher = ((FlashableList) list).getFlasher();
            if (flasher != null) {
                flasher.mark();
                if (flasher.isHighlightedIndex(index - 1) && !isSelected) {
                    setBackground(flasher.getColor());
                }
            }
            setFont(isKeyRow ? UIManager.getFont("Label.font") : list.getFont());
            setText(getText(value, isKeyRow));
            int width = list.getParent().getWidth();
            if (width > 0) {
                setSize(width, Short.MAX_VALUE);
            }
            return this;
        }
        
        private String getText(Object value, boolean isKeyRow) {
            if (!isKeyRow || Preferences.isPreference(Preferences.SEGPROPS_SHOW_RAW_KEYS)) {
                return value.toString();
            }
            try {
                return OStrings.getString(PROPERTY_TRANSLATION_KEY + value.toString().toUpperCase());
            } catch (MissingResourceException ex) {
                return value.toString();
            }
        }
    }
}
