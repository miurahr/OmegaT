package org.omegat.gui.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.omegat.core.Core;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.UIThreadsUtil;

@SuppressWarnings("serial")
public class SegmentPropertiesListView implements ISegmentPropertiesView {

    private SegmentPropertiesArea parent;
    private FlashableList list;
    private PropertiesListModel model;
    private int mouseoverIndex = -1;

    public void install(final SegmentPropertiesArea parent) {
        UIThreadsUtil.mustBeSwingThread();
        this.parent = parent;
        model = new PropertiesListModel();
        list = new FlashableList(model);
        list.setForeground(parent.getForeground());
        list.setBackground(parent.getBackground());
        list.addMouseListener(parent.contextMenuListener);
        list.setCellRenderer(new MultilineCellRenderer());
        list.setFont(Core.getMainWindow().getApplicationFont());
        list.addMouseListener(mouseAdapter);
        list.addMouseMotionListener(mouseAdapter);
        parent.setViewportView(list);
    }
    
    private final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (mouseoverIndex % 2 == 0) {
                parent.showContextMenu(SwingUtilities.convertPoint(list, e.getPoint(), parent));
            }
        }
        @Override
        public void mouseExited(MouseEvent e) {
            updateRollover();
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            updateRollover();
        }
    };
    
    private void updateRollover() {
        Point point = list.getMousePosition();
        int newIndex = point == null ? -1 : list.locationToIndex(point);
        boolean doRepaint = newIndex != mouseoverIndex;
        mouseoverIndex = newIndex;
        if (doRepaint) {
            list.repaint();
        }
    }
    
    @Override
    public void update() {
        UIThreadsUtil.mustBeSwingThread();
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
        UIThreadsUtil.mustBeSwingThread();
        notify = translateIndices(notify);
        list.clearSelection();
        list.scrollRectToVisible(list.getCellBounds(notify.get(0), notify.get(notify.size() - 1)));
        list.flash(notify);
    }

    private List<Integer> translateIndices(List<Integer> indices) {
        List<Integer> result = new ArrayList<Integer>(indices.size());
        for (int i : indices) {
            result.add(i + 1);
        }
        return result;
    }
    
    @Override
    public String getKeyAtPoint(Point p) {
        int clickedIndex = list.locationToIndex(SwingUtilities.convertPoint(parent, p, list));
        if (clickedIndex == -1) {
            return null;
        }
        int clickedKeyIndex = clickedIndex % 2 == 0 ? clickedIndex : clickedIndex - 1;
        return (String) model.getElementAt(clickedKeyIndex);
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
    
    private class MultilineCellRenderer extends JTextArea implements ListCellRenderer {
        
        private final Border noFocusBorder = new EmptyBorder(FOCUS_BORDER.getBorderInsets(this));
        private final Border noFocusCompoundBorder = new CompoundBorder(MARGIN_BORDER, noFocusBorder);
        private final JLabel button;
        
        public MultilineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setLayout(new BorderLayout());
            button = new JLabel();
            add(button, BorderLayout.EAST);
        }
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            boolean isKeyRow = index % 2 == 0;
            button.setVisible(isKeyRow && (index == mouseoverIndex || index + 1 == mouseoverIndex));
            if (isKeyRow) {
                button.setIcon(index == mouseoverIndex ? SETTINGS_ICON : SETTINGS_ICON_INACTIVE);
            }
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
                if (flasher.isHighlightedIndex(index) && !isSelected) {
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
