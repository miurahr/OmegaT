package org.omegat.gui.properties;

import java.awt.Color;
import java.util.List;

public class Flasher {
    private static final double FLASH_DURATION = 300.0;
    private static final Color HIGHLIGHT_COLOR = new Color(0xFFE8E8);
    
    private final long startTime;
    private final List<Integer> indices;
    volatile private long mark;

    public Flasher(List<Integer> indices) {
        startTime = System.currentTimeMillis();
        this.indices = indices;
    }
    
    public void mark() {
        mark = System.currentTimeMillis();
    }
        
    public boolean isHighlightedIndex(int index) {
        return indices.contains(index);
    }
    
    public boolean isFlashing() {
        return mark - startTime <= FLASH_DURATION;
    }
    
    private double getIntensity(long elapsed) {
        double x = elapsed / FLASH_DURATION;
        return -4 * x * x + 4 * x;
    }
    
    public Color getColor() {
        long elapsed = mark - startTime;
        if (elapsed >= FLASH_DURATION) {
            return HIGHLIGHT_COLOR;
        }
        int blueAndGreen = 255 - (int)(255 * getIntensity(elapsed));
        return new Color(255, blueAndGreen, blueAndGreen);
    }
}
