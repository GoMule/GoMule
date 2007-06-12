/*
 * Created on 6-jun-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gomule.gui;

import java.util.*;

/**
 * @author Marco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class D2ItemListAdapter implements D2ItemList
{
    private ArrayList	iListeners = new ArrayList();
    private boolean 	iModified;
    
    protected void setModified(boolean pModified)
    {
        iModified = pModified;
        fireD2ItemListEvent();
    }
    
    public boolean isModified()
    {
        return iModified;
    }
    
    public void addD2ItemListListener(D2ItemListListener pListener)
    {
        iListeners.add(pListener);
    }
    public void removeD2ItemListListener(D2ItemListListener pListener)
    {
        iListeners.remove(pListener);
    }
    public boolean hasD2ItemListListener()
    {
        return !iListeners.isEmpty();
    }
    public void fireD2ItemListEvent()
    {
        for ( int i = 0 ; i < iListeners.size() ; i++ )
        {
            ((D2ItemListListener) iListeners.get(i)).itemListChanged();
        }
    }

}
