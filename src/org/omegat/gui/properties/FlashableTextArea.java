package org.omegat.gui.properties;

import java.awt.Graphics;
import javax.swing.JTextArea;

/**
 *
 * @author Aaron Madlon-Kay <aaron@madlon-kay.com>
 */
public class FlashableTextArea extends JTextArea {
    private Flasher flasher;

    public void flash() {
        flasher = new Flasher();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (flasher != null && flasher.isFlashing()) {
            flasher.mark();
            setBackground(flasher.getColor());
            repaint();
        }
    }
}
