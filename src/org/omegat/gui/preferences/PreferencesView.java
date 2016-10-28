package org.omegat.gui.preferences;

import javax.swing.JComponent;

public interface PreferencesView {
    JComponent getGui();

    void persist();

    void initDefaults();

    default boolean validate() {
        return true;
    };
}
