package org.omegat.gui.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.omegat.util.gui.ResourcesUtil;

public interface ISegmentPropertiesView {
    static Icon SETTINGS_ICON = new ImageIcon(ResourcesUtil.getBundledImage("appbar.settings.active.png"));
    static Icon SETTINGS_ICON_INACTIVE = new ImageIcon(ResourcesUtil.getBundledImage("appbar.settings.inactive.png"));
    static Icon SETTINGS_ICON_INVISIBLE = new Icon() {
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
		}
		@Override
		public int getIconWidth() {
			return SETTINGS_ICON.getIconWidth();
		}
		@Override
		public int getIconHeight() {
			return SETTINGS_ICON.getIconHeight();
		}
	};
    
    static final String PROPERTY_TRANSLATION_KEY = "SEGPROP_KEY_";    
    static final Color ROW_HIGHLIGHT_COLOR = new Color(245, 245, 245);
    static final Border FOCUS_BORDER = new MatteBorder(1, 1, 1, 1, new Color(0x76AFE8));
    static final Border MARGIN_BORDER = new EmptyBorder(1, 5, 1, 5);
    static final Border FOCUS_COMPOUND_BORDER = new CompoundBorder(MARGIN_BORDER, FOCUS_BORDER);
    
    public void update();
    public JComponent getViewComponent();
    public void notifyUser(List<Integer> notify);
    public void install(SegmentPropertiesArea parent);
    public String getKeyAtPoint(Point p);
}
