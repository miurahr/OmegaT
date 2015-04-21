package org.omegat.gui.properties;

import java.awt.Color;
import java.util.List;

import org.omegat.util.gui.Styles;

public class Flasher {
    private static final double FLASH_DURATION = 300.0;
    
    private final long startTime;
    private final List<Integer> indices;
    volatile private long mark;
    
    private final Color colorMin;
    private final Color colorMax;

    public Flasher(List<Integer> indices) {
        startTime = System.currentTimeMillis();
        this.indices = indices;
        this.colorMin = Styles.EditorColor.COLOR_NOTIFICATION_MIN.getColor();
        this.colorMax = Styles.EditorColor.COLOR_NOTIFICATION_MAX.getColor();
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
            return colorMin;
        }
        double intensity = getIntensity(elapsed);
        double r = colorMin.getRed() + (colorMax.getRed() - colorMin.getRed()) * intensity;
        double g = colorMin.getGreen() + (colorMax.getGreen() - colorMin.getGreen()) * intensity;
        double b = colorMin.getBlue() + (colorMax.getBlue() - colorMin.getBlue()) * intensity;
        return new Color((int) r, (int) g, (int) b);
    }
}
