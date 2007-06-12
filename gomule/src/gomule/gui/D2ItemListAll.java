/*
 * Created on 8-jun-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gomule.gui;

import gomule.item.*;
import gomule.util.*;

import java.util.*;

/**
 * @author Marco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class D2ItemListAll implements D2ItemList
{
    private D2FileManager	iFileManager;
    private ArrayList 		iList = new ArrayList();
    
    public D2ItemListAll(D2FileManager pFileManager)
    {
        iFileManager = pFileManager;
    }
    
    public boolean containsItem(D2Item pItem)
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            if ( lItemList.containsItem(pItem) )
            {
                return true;
            }
        }
        return false;
    }
    
    public void removeItem(D2Item pItem)
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            if ( lItemList.containsItem(pItem) )
            {
                lItemList.removeItem(pItem);
                return;
            }
        }
    }

    public ArrayList getItemList()
    {
        ArrayList lList = new ArrayList();
        
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            lList.addAll( lItemList.getItemList() );
        }
        
        return lList;
    }

    public int getNrItems()
    {
        int  lNrItems = 0;
        
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            lNrItems += lItemList.getNrItems();
        }
        
        return lNrItems;
    }

    public boolean isModified()
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            if ( lItemList.isModified() );
            {
                return true;
            }
        }
        
        return false;
    }

    public void addD2ItemListListener(D2ItemListListener pListener)
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            lItemList.addD2ItemListListener(pListener);
        }
    }

    public void removeD2ItemListListener(D2ItemListListener pListener)
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            lItemList.removeD2ItemListListener(pListener);
        }
    }

    public void save(D2Project pProject)
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            if ( lItemList.isModified() )
            {
                lItemList.save(pProject);
            }
        }
    }

    public boolean isSC()
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            if ( lItemList.isSC() );
            {
                return true;
            }
        }
        
        return false;
    }

    public boolean isHC()
    {
        D2ItemList lItemList;
        for ( int i = 0 ; i < iList.size() ; i++ )
        {
            lItemList = (D2ItemList) iList.get(i);
            if ( lItemList.isHC() );
            {
                return true;
            }
        }
        
        return false;
    }

}
