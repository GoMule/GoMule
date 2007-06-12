/*
 * Created on 5-jun-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gomule.gui;

import java.util.*;

import gomule.item.*;
import gomule.util.*;

/**
 * @author Marco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface D2ItemList
{
    public boolean containsItem(D2Item pItem);
    public void removeItem(D2Item pItem);
    public ArrayList getItemList();
    public int getNrItems();
    public String getFilename();
    
    public boolean isModified();
    public void addD2ItemListListener(D2ItemListListener pListener);
    public void removeD2ItemListListener(D2ItemListListener pListener);
    public boolean hasD2ItemListListener();
    public void save(D2Project pProject);
    
    public boolean isSC();
    public boolean isHC();
}
