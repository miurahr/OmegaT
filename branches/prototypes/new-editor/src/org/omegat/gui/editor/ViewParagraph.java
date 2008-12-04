package org.omegat.gui.editor;

import javax.swing.text.Element;
import javax.swing.text.FlowView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;

/**
 * Class for use some protected properties.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ViewParagraph extends ParagraphView {
    public ViewParagraph(final Element elem, final boolean isRTL, final boolean isRightAligned) {
        super(elem);
        strategy = new LayoutStrategy(isRTL);
        setJustification(isRightAligned ? StyleConstants.ALIGN_RIGHT
                : StyleConstants.ALIGN_LEFT);
    }
    
    @Override
    protected View createRow() {
        // TODO Auto-generated method stub
        return super.createRow();
    }
    
    public static class LayoutStrategy extends FlowView.FlowStrategy {
        protected boolean isRTL;

        public LayoutStrategy(final boolean isRTL) {
            this.isRTL = isRTL;
        }

        @Override
        protected int layoutRow(FlowView fv, int rowIndex, int pos) {
            int res = super.layoutRow(fv, rowIndex, pos);

            if (isRTL) {
                // Need to swap "begin" and "end" mark view, because paragraph
                // is RTL
                View row = fv.getView(rowIndex);
                int n = row.getViewCount();
                if (n > 1) {
                    // only when more than one view, because nothing to do if
                    // there
                    // is no 2 view
                    if (row.getView(0) instanceof ViewSegmentMark) {
                        // the first view should always be mark
                        int secondIndex = -1;
                        View[] replace = new View[n];
                        for (int i = 0; i < n; i++) {
                            replace[i] = row.getView(i);
                            if (replace[i] instanceof ViewSegmentMark) {
                                /*
                                 * index of latest segment view, length-2 in
                                 * most cases
                                 */
                                secondIndex = i;
                            }
                        }
                        if (secondIndex >= 0) {
                            // swap it
                            System.out.println("replace");
                            View firstView = replace[0];
                            replace[0] = replace[secondIndex];
                            replace[secondIndex] = firstView;
                            row.replace(0, n, replace);
                        }
                    }
                }
            }

            return res;
        }
    }
}
