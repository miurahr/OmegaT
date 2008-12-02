package org.omegat.gui.editor;

import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;

/**
 * Class for use some protected properties.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ViewParagraph extends ParagraphView {
    public ViewParagraph(final Element elem, final boolean isRTL) {
        super(elem);
        setJustification(isRTL ? StyleConstants.ALIGN_RIGHT
                : StyleConstants.ALIGN_LEFT);
    }
}
