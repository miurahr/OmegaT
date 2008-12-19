package org.omegat.gui.editor;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.View;
import javax.swing.text.Position.Bias;

/**
 * View for represent eod-of-segment.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ViewEmptyLine extends View {
    public ViewEmptyLine(Element elem) {
        super(elem);
    }
    
    @Override
    public float getPreferredSpan(int axis) {
        return 30;
    }
    
    @Override
    public Shape modelToView(int pos, Shape a, Bias b)
            throws BadLocationException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void paint(Graphics g, Shape allocation) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public int viewToModel(float x, float y, Shape a, Bias[] biasReturn) {
        // TODO Auto-generated method stub
        return 0;
    }
}
