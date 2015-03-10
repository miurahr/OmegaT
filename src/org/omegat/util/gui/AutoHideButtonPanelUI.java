package org.omegat.util.gui;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;


public class AutoHideButtonPanelUI extends com.vlsolutions.swing.docking.ui.AutoHideButtonPanelUI {

    private static AutoHideButtonPanelUI instance = new AutoHideButtonPanelUI();
    private Color originalBackground;

    public static ComponentUI createUI(JComponent c) {
        return instance;
    }
    
    @Override
    public void installUI(JComponent comp) {
        super.installUI(comp);
        Color color = UIManager.getColor("OmegaTMainWindow.backgroundBottom");
        if (color != null) {
            originalBackground = comp.getBackground();
            comp.setBackground(color);
        }
    }
    
    @Override
    public void uninstallUI(JComponent comp) {
        super.uninstallUI(comp);
        comp.setBackground(originalBackground);
    }
}
