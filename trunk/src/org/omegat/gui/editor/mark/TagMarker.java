package org.omegat.gui.editor.mark;

import java.util.List;

import org.omegat.util.OStrings;
import org.omegat.util.PatternConsts;
import org.omegat.util.gui.Styles;

/**
 * Marker for all tags in segments.
 * 
 * @author Martin Fleurke
 */
public class TagMarker extends AbstractMarker {

    public TagMarker() throws Exception {
        //PAINTER = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Styles.COLOR_PLACEHOLDER);
        PAINTER = new TransparentHighlightPainter(Styles.COLOR_PLACEHOLDER, 0.2F);
        toolTip = OStrings.getString("MARKER_TAG");
        pattern = PatternConsts.getPlaceholderPattern();
    }
    
    public List<Mark> getMarksForEntry(String sourceText, String translationText, boolean isActive)
            throws Exception {
        pattern = PatternConsts.getPlaceholderPattern();
        return super.getMarksForEntry(sourceText, translationText, isActive);
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }
}
