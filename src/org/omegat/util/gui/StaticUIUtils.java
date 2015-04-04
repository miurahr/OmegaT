/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2006 Henry Pijffers
               2014-2015 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.util.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Aaron Madlon-Kay
 */
public class StaticUIUtils {

    private static final KeyStroke ESC_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
    
    /**
     * Make a dialog closeable by pressing the Esc key.
     * {@link JDialog#dispose()} will be called.
     * 
     * @param dialog
     */
    public static void setEscapeClosable(JDialog dialog) {
        setEscapeAction(dialog.getRootPane(), makeCloseAction(dialog));
    }
    
    /**
     * Make a dialog closeable by pressing the Esc key.
     * {@link JFrame#dispose()} will be called.
     * 
     * @param frame
     */
    public static void setEscapeClosable(JFrame frame) {
        setEscapeAction(frame.getRootPane(), makeCloseAction(frame));
    }
    
    /**
     * Create an action that sends a {@link WindowEvent#WINDOW_CLOSING} event
     * to the supplied window. This mimics closing by clicking the window close button.
     * @param window
     * @return action
     */
    public static AbstractAction makeCloseAction(final Window window) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
        };
    }
    
    /**
     * Associate a custom action to be called when the Esc key is pressed.
     * 
     * @param dialog
     * @param action 
     */
    public static void setEscapeAction(JDialog dialog, Action action) {
        setEscapeAction(dialog.getRootPane(), action);
    }
    
    /**
     * Associate a custom action to be called when the Esc key is pressed.
     * 
     * @param frame
     * @param action 
     */
    public static void setEscapeAction(JFrame frame, Action action) {
        setEscapeAction(frame.getRootPane(), action);
    }

    /**
     * Associate a custom action to be called when the Esc key is pressed.
     * 
     * @param pane
     * @param action 
     */
    public static void setEscapeAction(JRootPane pane, Action action) {
        // Handle escape key to close the window
        pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ESC_KEYSTROKE, "ESCAPE");
        pane.getActionMap().put("ESCAPE", action);
    }
}
