package org.omegat.gui.properties;

import java.awt.Color;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public interface ISegmentPropertiesView {
    static final String PROPERTY_TRANSLATION_KEY = "SEGPROP_KEY_";    
    static final Color ROW_HIGHLIGHT_COLOR = new Color(245, 245, 245);
    static final Border FOCUS_BORDER = new MatteBorder(1, 1, 1, 1, new Color(0x76AFE8));
    static final Border MARGIN_BORDER = new EmptyBorder(1, 5, 1, 5);
    static final Border FOCUS_COMPOUND_BORDER = new CompoundBorder(MARGIN_BORDER, FOCUS_BORDER);
    
    public void update();
    public JComponent getViewComponent();
    public void notifyUser(List<Integer> notify);
    public void install(SegmentPropertiesArea parent);
    public void uninstall();
}
