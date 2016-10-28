package org.omegat.gui.preferences;

import java.io.IOException;

import org.omegat.core.Core;
import org.omegat.core.data.NotLoadedProject;
import org.omegat.util.TestPreferencesInitializer;

public class PreferencesWindowTest {
    public static void main(String[] args) throws IOException {
        TestPreferencesInitializer.init();
        Core.setProject(new NotLoadedProject());
        new PreferencesWindowController().show(null);
    }
}
