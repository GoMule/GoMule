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
    public void removeItem(D2Item pItem);
    public ArrayList getItemList();
    public int getNrItems();
    
    public boolean isModified();
    public void save(D2Project pProject);
    
    public boolean isSC();
    public boolean isHC();
}
