package org.omegat.gui.editor.mark;

import java.util.List;

import org.omegat.util.OStrings;
import org.omegat.util.PatternConsts;
import org.omegat.util.gui.Styles;

/**
 * Marker for all parts in segments that have to be removed.
 * 
 * @author Martin Fleurke
 */
public class RemoveTagMarker extends AbstractMarker {

    public RemoveTagMarker() throws Exception {
        PAINTER = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Styles.COLOR_REMOVETEXT_TARGET);
        toolTip = OStrings.getString("MARKER_REMOVETAG");
        pattern = PatternConsts.getRemovePattern();
    }
    
    public List<Mark> getMarksForEntry(String sourceText, String translationText, boolean isActive)
            throws Exception {
        pattern = PatternConsts.getRemovePattern();
        return super.getMarksForEntry(sourceText, translationText, isActive);
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }
}
