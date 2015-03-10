package org.omegat.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

@SuppressWarnings("serial")
class RoundedCornerBorder extends AbstractBorder {
    private final int radius;
    private final Color color;
    private final boolean upperLeft;
    private final boolean upperRight;
    private final boolean lowerLeft;
    private final boolean lowerRight;
    
    public RoundedCornerBorder() {
        this(-1, Color.GRAY, true, true, true, true);
    }
    
    public RoundedCornerBorder(int radius, Color color, boolean upperLeft, boolean upperRight,
            boolean lowerLeft, boolean lowerRight) {
        this.radius = radius;
        this.color = color;
        this.upperLeft = upperLeft;
        this.upperRight = upperRight;
        this.lowerLeft = lowerLeft;
        this.lowerRight = lowerRight;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int r = radius == -1 ? height - 1 : radius;
        RoundRectangle2D round = new RoundRectangle2D.Float(x, y, width - 1,
                height - 1, r, r);
        Rectangle2D rect = new Rectangle2D.Float(x, y, width - 1, height - 1);
        Area corners = new Area(new Rectangle2D.Float(x, y, width, height));
        corners.subtract(new Area(round));
        Color background = c.getParent() == null ? null : c.getParent().getBackground();
        
        // Upper left
        Area clip = new Area(new Rectangle2D.Float(x, y, width / 2, height / 2));
        if (!upperLeft && !upperRight) {
            clip.subtract(new Area(new Rectangle2D.Float(x + 1, y, width, 2)));
        }
        if (!upperLeft && !lowerLeft) {
            clip.subtract(new Area(new Rectangle2D.Float(x, y + 1, 2, height)));
        }
        g2.setClip(clip);
        drawCorner(g2, upperLeft ? background : null, corners, upperLeft ? round : rect);
        
        // Upper right
        clip = new Area(new Rectangle2D.Float(x + width / 2, y, width, height / 2));
        if (!upperLeft && !upperRight) {
            clip.subtract(new Area(new Rectangle2D.Float(x, y, width - 1, 2)));
        }
        if (!upperRight && !lowerRight) {
            clip.subtract(new Area(new Rectangle2D.Float(width - 2, y + 1, 2, height)));
        }
        g2.setClip(clip);
        drawCorner(g2, upperRight ? background : null, corners, upperRight ? round : rect);
        
        // Lower left
        clip = new Area(new Rectangle2D.Float(x, y + height / 2, width / 2, height));
        if (!lowerLeft && !lowerRight) {
            clip.subtract(new Area(new Rectangle2D.Float(x + 1, height - 2, width, 2)));
        }
        if (!upperLeft && !lowerLeft) {
            clip.subtract(new Area(new Rectangle2D.Float(x, y, 2, height - 1)));
        }
        g2.setClip(clip);
        drawCorner(g2, lowerLeft ? background : null, corners, lowerLeft ? round : rect);
        
        // Lower right
        clip = new Area(new Rectangle2D.Float(x + width / 2, y + height / 2, width, height));
        if (!lowerLeft && !lowerRight) {
            clip.subtract(new Area(new Rectangle2D.Float(x, height - 2, width - 1, 2)));
        }
        if (!upperRight && !lowerRight) {
            clip.subtract(new Area(new Rectangle2D.Float(width - 2, y, 2, height - 2)));
        }
        g2.setClip(clip);
        drawCorner(g2, lowerRight ? background : null, corners, lowerRight ? round : rect);
        
        g2.dispose();
    }
    
    private void drawCorner(Graphics2D g2, Color background, Area corners, Shape shape) {
        if (background != null) {
            g2.setColor(background);
            g2.fill(corners);
        }
        g2.setColor(color);
        g2.draw(shape);
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(4, 8, 4, 8);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(4, 8, 4, 8);
        return insets;
    }
}