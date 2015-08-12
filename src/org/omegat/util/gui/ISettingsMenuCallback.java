package org.omegat.util.gui;

import javax.swing.JPopupMenu;

public interface ISettingsMenuCallback {
    String PROPERTY_SETTINGS_MENU_CALLBACK = "settings_button_action_listener";

    public void populateSettingsMenu(JPopupMenu menu);
}