package org.omegat.gui.properties;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.BoxLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import org.omegat.core.Core;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.UIThreadsUtil;

@SuppressWarnings("serial")
public class SegmentPropertiesListView implements ISegmentPropertiesView {

    private SegmentPropertiesArea parent;
    private JPanel panel;

    public void install(final SegmentPropertiesArea parent) {
        UIThreadsUtil.mustBeSwingThread();
        this.parent = parent;
        panel = new ReasonablySizedPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setFont(Core.getMainWindow().getApplicationFont());
        panel.setOpaque(true);
        parent.setViewportView(panel);
    }
    
    @Override
    public void update() {
        UIThreadsUtil.mustBeSwingThread();
        panel.removeAll();
        for (int i = 0; i < parent.properties.size(); i += 2) {
            final SegmentPropertiesListCell cell = new SegmentPropertiesListCell();
            String key = parent.properties.get(i);
            cell.key = key;
            cell.label.setText(getDisplayKey(key));
            cell.value.setText(parent.properties.get(i + 1));
            cell.value.setFont(panel.getFont());
            cell.settingsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parent.showContextMenu(SwingUtilities.convertPoint(cell, cell.settingsButton.getLocation(), parent));
                }
            });
            panel.add(cell);
        }
        panel.validate();
        panel.repaint();
    }
    
    private String getDisplayKey(String key) {
        if (Preferences.isPreference(Preferences.SEGPROPS_SHOW_RAW_KEYS)) {
            return key;
        }
        try {
            return OStrings.getString(PROPERTY_TRANSLATION_KEY + key.toUpperCase());
        } catch (MissingResourceException ex) {
            return key;
        }
    }

    @Override
    public JComponent getViewComponent() {
        return panel;
    }

    @Override
    public void notifyUser(List<Integer> notify) {
        UIThreadsUtil.mustBeSwingThread();
        for (int i : notify) {
            ((SegmentPropertiesListCell)panel.getComponent(i / 2)).value.flash();
        }
    }
    
    @Override
    public String getKeyAtPoint(Point p) {
        SegmentPropertiesListCell cell = ((SegmentPropertiesListCell)panel.getComponentAt(p));
        return cell == null ? null : cell.key;
    }
    
    private static class ReasonablySizedPanel extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return getFont().getSize();
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return getFont().getSize();
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
        
        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
        
    }
}
