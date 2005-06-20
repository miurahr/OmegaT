
package org.omegat.gui;

/**
 *
 * @author Maxym Mykhalchuk
 */
public interface TransFrameInterface
{
    public void doNextEntry();
    public void doPrevEntry();
    public void doRecycleTrans();
    public void activateEntry();
    public void doGotoEntry(String entry);
    public void setMessageText(String message);
    public void displayWarning(String warning, Throwable throwable);
    public void displayError(String error, Throwable throwable);
    public void fatalError(String error, Throwable throwable);
    public void doCompareN(int n);
    
    public void finishLoadProject();
    public void doUnloadProject();
    
    public boolean isProjectLoaded();
    
    public void toFront();
    
    public void setVisible(boolean show);
    
    /** Returns the window which lists all the source files. */
    public ProjectFrame getProjectFrame();
    
    
}
