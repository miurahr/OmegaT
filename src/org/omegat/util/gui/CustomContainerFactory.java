package org.omegat.util.gui;

import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

import org.omegat.gui.properties.ISegmentPropertiesView;
import org.omegat.util.OStrings;

import com.vlsolutions.swing.docking.DefaultDockableContainerFactory;
import com.vlsolutions.swing.docking.DockViewTitleBar;

/**
 * A custom {@link DockableContainerFactory} to allow us to supply custom
 * {@link DockViewTitleBar}s so that we can insert custom buttons.
 * 
 * @author Aaron Madlon-Kay
 *
 */
public class CustomContainerFactory extends DefaultDockableContainerFactory {
    
    @Override
    public DockViewTitleBar createTitleBar() {
        return new CustomTitleBar();
    }
    
    private static class CustomTitleBar extends DockViewTitleBar {
        
        private JButton settingsButton;
        
        public CustomTitleBar() {
            settingsButton = new JButton(ISegmentPropertiesView.SETTINGS_ICON_INACTIVE);
            settingsButton.setRolloverIcon(ISegmentPropertiesView.SETTINGS_ICON);
            settingsButton.setPressedIcon(ISegmentPropertiesView.SETTINGS_ICON_PRESSED);
            settingsButton.setToolTipText(OStrings.getString("DOCKING_HINT_SETTINGS"));
            
            // These values are set to match defaults in DockViewTitleBarUI
            settingsButton.setRolloverEnabled(true);
            settingsButton.setBorderPainted(false);
            settingsButton.setContentAreaFilled(false);
            settingsButton.setFocusable(false);
            settingsButton.setMargin(new Insets(0, 2, 0, 2));
            settingsButton.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            
            settingsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ISettingsMenuCallback callback = getSettingsCallback();
                    if (callback == null) {
                        return;
                    }
                    JPopupMenu menu = new JPopupMenu();
                    callback.populateSettingsMenu(menu);
                    try {
                        menu.show(settingsButton, 0, 0);
                    } catch (IllegalComponentStateException ignore) {
                        ignore.printStackTrace();
                    }
                }
            });
        }
        
        private ISettingsMenuCallback getSettingsCallback() {
            return (ISettingsMenuCallback) getDockable().getDockKey().getProperty(ISettingsMenuCallback.PROPERTY_SETTINGS_MENU_CALLBACK);
        }
        
        @Override
        public void finishLayout() {
            if (getSettingsCallback() != null) {
                // 4 is the number of default buttons:
                //   CloseButton, MaximizeOrRestoreButton, HideOrDockButton, Float Button
                // We want to insert before all of them, regardless of their visibility.
                add(settingsButton, getComponentCount() - 4);
            }
        }
    }
}
