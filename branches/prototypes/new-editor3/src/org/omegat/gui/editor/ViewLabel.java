package org.omegat.gui.editor;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.LabelView;

public class ViewLabel extends LabelView {
    private boolean isSegmentMark;

    public ViewLabel(final Element el) {
        super(el);
        isSegmentMark = el.getAttributes().containsAttribute(
                SegmentBuilder.SEGMENT_MARK_ATTRIBUTE,
                SegmentBuilder.SEGMENT_MARK_ATTRIBUTE);
    }

    @Override
    public void paint(Graphics g, Shape a) {
        if (!isSegmentMark) {
            super.paint(g, a);
        } else {
            paintMark(g, a);
        }
    }
    
    protected void paintMark(Graphics g, Shape a) {
        EditorTextArea3 editor = ((EditorTextArea3) getContainer());
        try {
            String text = getElement().getDocument().getText(getStartOffset(),
                    getEndOffset() - getStartOffset());
            JLabel lb = editor.getSegmentMarkLabel(text);
            SwingUtilities.paintComponent(g, lb, editor, a.getBounds());
        } catch (BadLocationException ex) {
        }
    }
}
