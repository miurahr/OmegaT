/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2008 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **************************************************************************/

package org.omegat.gui.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.Position.Bias;

/**
 * Class for display segmentation marks. It better to paint marks by own
 * component, because we will not have problems with RTL writing.
 * 
 * @author Alexander_Buloichik
 */
public class SegmentMarkView extends View {
    private Font font;
    private FontMetrics fontMetrics;
    private Color fg, bg;

    public SegmentMarkView(Element elem) {
        super(elem);
    }

    /**
     * Calculate alignment on the line.
     */
    @Override
    public float getAlignment(int axis) {
        updateDrawSettings();
        switch (axis) {
        case View.X_AXIS:
            return super.getAlignment(axis);
        case View.Y_AXIS:
            float h = fontMetrics.getHeight();
            float d = fontMetrics.getDescent();
            return (h > 0) ? (h - d) / h : 0;
        default:
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    /**
     * Calculate view size.
     */
    @Override
    public float getPreferredSpan(int axis) {
        updateDrawSettings();
        switch (axis) {
        case View.X_AXIS:
            return fontMetrics.stringWidth(getText());
        case View.Y_AXIS:
            return fontMetrics.getHeight();
        default:
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    /**
     * Calculate where position should be displayed. Used for display caret.
     */
    @Override
    public Shape modelToView(int pos, Shape a, Bias b)
            throws BadLocationException {
        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a
                .getBounds();

        Rectangle lineArea = new Rectangle();

        String text = getText();
        int p0 = getElement().getStartOffset();
        // fill in the results and return
        lineArea.x = alloc.x
                + fontMetrics.stringWidth(text.substring(0, pos - p0));
        lineArea.y = alloc.y;
        lineArea.width = 0;
        lineArea.height = fontMetrics.getHeight();
        return lineArea;
    }

    /**
     * Calculate what position should be displayed at point. Used for position
     * caret after mouse click.
     */
    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] biasReturn) {
        String text = getText();
        int p0 = getElement().getStartOffset();

        float prev = 0;
        float beg = x - a.getBounds().x;
        for (int i = 0; i <= text.length(); i++) {
            float pos = fontMetrics.stringWidth(text.substring(0, i)) - beg;
            if (pos > 0) {
                return pos < -prev ? i + p0 : i - 1 + p0;
            }
            prev = pos;
        }
        return p0 + text.length();
    }

    /**
     * Paint this view.
     */
    @Override
    public void paint(Graphics g, Shape a) {
        updateDrawSettings();

        Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a
                .getBounds();

        if (bg != null) {
            g.setColor(bg);
            g.fillRect(alloc.x, alloc.y, alloc.width, alloc.height);
        }

        g.setFont(font);
        g.setColor(fg);
        String text = getText();

        int y = alloc.getBounds().y + fontMetrics.getHeight()
                - fontMetrics.getDescent();

        g.drawChars(text.toCharArray(), 0, text.length(), alloc.getBounds().x,
                y);
    }

    /**
     * Get view's text from document.
     * 
     * @return text
     */
    private String getText() {
        int p0 = getElement().getStartOffset();
        int p1 = getElement().getEndOffset();
        try {
            return getElement().getDocument().getText(p0, p1 - p0);
        } catch (BadLocationException ex) {
            throw new RuntimeException("Invalid location", ex);
        }
    }

    /**
     * Update view draw settings.
     */
    private void updateDrawSettings() {
        if (font == null) {
            AttributeSet attrs = getElement().getAttributes();
            StyledDocument doc = (StyledDocument) getDocument();
            font = doc.getFont(attrs);
            fontMetrics = getContainer().getFontMetrics(font);

            fg = doc.getForeground(attrs);
            if (attrs.isDefined(StyleConstants.Background)) {
                bg = doc.getBackground(attrs);
            } else {
                bg = null;
            }
        }
    }
}
