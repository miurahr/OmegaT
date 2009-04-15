package org.omegat.gui.editor;

import java.awt.Font;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;

public class Document3 extends DefaultStyledDocument {
    enum ORIENTATION {
        /** Both segments is left aligned. */
        LTR,
        /** Both segments is right aligned. */
        RTL,
        /** Segments have different alignment, depends of language alignment. */
        DIFFER
    };
    
    protected Position activeTranslationBeginM1, activeTranslationEndP1;
    
    /**
     * Flag for check internal schanges of content, which should be always
     * acceptable.
     */
    protected boolean trustedChangesInProgress = false;
    
    public Document3(final EditorController3 controller) {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Check if specified position inside active segment, i.e. between segment's
     * marks.
     */
    protected boolean isInsideActiveSegPart(int pos) {
        return true;//TODO
//        if (activeSegmentIndex < 0) {
//            // there is no active segment
//            return false;
//        }
//        OmElementSegment seg = (OmElementSegment) root
//                .getElement(activeSegmentIndex);
//        OmElementSegPart segPart = (OmElementSegPart) seg.getElement(1);
//        return segPart.getStartOffset() <= pos && segPart.getEndOffset() >= pos;
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

    
    protected void setOrientation(final ORIENTATION newOrientation) {
        //TODO
    }
    
    protected void hideMisspelledWord(final String word) {
        //TODO
    }
    
    protected void setFont(Font f) {
    }
    
    protected int getSegmentAtLocation(int location) {
        return 0;
    }
    
    protected String extractTranslation() {
        return "ttt";//TODO
    }
}
