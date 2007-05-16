/*
 * Created on 11-mei-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gomule.item;

import java.util.*;

/**
 * @author Marco
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class D2BodyLocations
{
    private String iLocation;
    private String iDisplay;
    
    public static final ArrayList sListAll = new ArrayList();
    
    public static final D2BodyLocations BODY_HEAD = new D2BodyLocations("head", "Head", sListAll);
    public static final D2BodyLocations BODY_TORS = new D2BodyLocations("tors", "Body", sListAll);
    public static final D2BodyLocations BODY_GLOV = new D2BodyLocations("glov", "Gloves", sListAll);
    public static final D2BodyLocations BODY_BELT = new D2BodyLocations("belt", "Belt", sListAll);
    public static final D2BodyLocations BODY_FEET = new D2BodyLocations("feet", "Boots", sListAll);
    public static final D2BodyLocations BODY_RARM = new D2BodyLocations("rarm", "Shield", sListAll);
    
    public static final D2BodyLocations BODY_ALL = new D2BodyLocations("all", "All", sListAll);
    
    public static final D2BodyLocations BODY_NECK = new D2BodyLocations("neck", "Amulet", null);
    public static final D2BodyLocations BODY_LRIN = new D2BodyLocations("lrin", "Ring", null);
    
    private D2BodyLocations(String pLocation, String pDisplay, ArrayList pListAll)
    {
        iLocation = pLocation;
        iDisplay = pDisplay;
        if ( pListAll != null )
        {
            pListAll.add(this);
        }
    }
    
    public String getLocation()
    {
        return iLocation;
    }
    
    public String toString()
    {
        return iDisplay;
    }
    
    public static ArrayList getArmorFilterList()
    {
        return sListAll;
    }
    
}
