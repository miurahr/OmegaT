package org.omegat.gui.preferences;

import javax.swing.JComponent;

/**
 * An interface implemented by views shown in the Preferences window.
 * 
 * TODO: Add a facility for indicating when the project needs to be reloaded or
 * the program restarted
 * 
 * @author amake
 *
 */
public interface PreferencesView {
    JComponent getGui();

    void persist();

    void initDefaults();

    default boolean validate() {
        return true;
    };
}
