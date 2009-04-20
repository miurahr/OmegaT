package org.omegat.gui.editor;

import java.awt.Font;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Position;

public class Document3 extends DefaultStyledDocument {
    enum ORIENTATION {
        /** Both segments is left aligned. */
        LTR,
        /** Both segments is right aligned. */
        RTL,
        /** Segments have different alignment, depends of language alignment. */
        DIFFER
    };

    protected final EditorController3 controller;
    protected Position activeTranslationBeginM1, activeTranslationEndP1;

   
    /**
     * Flag for check internal schanges of content, which should be always
     * acceptable.
     */
    protected boolean trustedChangesInProgress = false;

    public Document3(final EditorController3 controller) {
        this.controller=controller;
    }

    /**
     * Calculate the position of the start of the current translation
     */
    protected int getTranslationStart() {
        return activeTranslationBeginM1.getOffset() + 1;
    }

    /**
     * Calculcate the position of the end of the current translation
     */
    protected int getTranslationEnd() {
        return activeTranslationEndP1.getOffset() - 1;
    }

    protected void hideMisspelledWord(final String word) {
        // TODO
    }

    public Font getFont(AttributeSet attr) {
        if (attr.containsAttribute(SegmentBuilder.SEGMENT_MARK_ATTRIBUTE, SegmentBuilder.SEGMENT_MARK_ATTRIBUTE)) {
            return controller.boldFont;
        }else {
            return controller.baseFont;
        }
    }

    protected String extractTranslation() {
        int start = getTranslationStart();
        int end = getTranslationEnd();
        try {
            return getText(start, end - start);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
