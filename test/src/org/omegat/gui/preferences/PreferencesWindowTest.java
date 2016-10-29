package org.omegat.gui.preferences;

import javax.swing.SwingUtilities;
import junit.framework.TestCase;

import org.omegat.core.Core;
import org.omegat.core.data.NotLoadedProject;
import org.omegat.util.TestPreferencesInitializer;

public class PreferencesWindowTest extends TestCase {
    public void testShowPrefsWindow() throws Exception {
        TestPreferencesInitializer.init();
        Core.setProject(new NotLoadedProject());
        SwingUtilities.invokeAndWait(() -> new PreferencesWindowController().show(null));
    }
}
