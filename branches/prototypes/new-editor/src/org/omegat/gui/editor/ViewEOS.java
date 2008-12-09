package org.omegat.gui.editor;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.Element;
import javax.swing.text.LabelView;

/**
 * View for represent eod-of-segment.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ViewEOS extends LabelView {
    public ViewEOS(Element elem) {
        super(elem);
    }
    
    public static void main(String[] args) {
        JFrame f=new JFrame();
        
        f.getContentPane().add(new JTextArea());
        f.setSize(300,200);
        f.show();
    }
}
