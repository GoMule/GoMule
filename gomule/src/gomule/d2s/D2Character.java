/*******************************************************************************
 * 
 * Copyright 2007 Andy Theuninck & Randall
 * 
 * This file is part of gomule.
 * 
 * gomule is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * gomule is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * gomlue; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 *  
 ******************************************************************************/

package gomule.d2s;

import gomule.item.*;
import gomule.util.*;

import java.util.*;

import randall.d2files.*;

// a character class
// manages one character file
// stores a filename, a bitreader
// to read from that file, and
// a vector of items
public class D2Character
{
    public static final int BODY_INV_CONTENT   = 1;
    public static final int BODY_BELT_CONTENT  = 2; // the belt content
    public static final int BODY_CUBE_CONTENT  = 4;
    public static final int BODY_STASH_CONTENT = 5;
    public static final int BODY_CURSOR        = 10;
    public static final int BODY_HEAD          = 11;
    public static final int BODY_NECK          = 12;
    public static final int BODY_TORSO         = 13;
    public static final int BODY_RARM          = 14;
    public static final int BODY_LARM          = 15;
    public static final int BODY_LRING         = 16;
    public static final int BODY_RRING         = 17;
    public static final int BODY_BELT          = 18; // the belt itself
    public static final int BODY_BOOTS         = 19;
    public static final int BODY_GLOVES        = 20;
    public static final int BODY_RARM2         = 21;
    public static final int BODY_LARM2         = 22;

    private String          iFileName;
    private D2BitReader     iReader;
    private ArrayList       iCharItems;
    private ArrayList       iMercItems;

    private boolean[][]     iStashGrid;
    private boolean[][]     iInventoryGrid;
    private boolean[][]     iCubeGrid;
    private boolean[][]     iBeltGrid;
    private boolean[]       iEquipped;

    private boolean[]       iMerc;

    //    private int itemlist_start;
    //    private int itemlist_end;

    private boolean         iHC;

    //    private int iCharItemCountPos;
    //    private int iMercItemCountPos;
    private boolean         iHasMerc;
    private byte            iBeforeStats[];
    private byte            iBeforeItems[];
    private byte            iBetweenItems[];
    private byte            iAfterItems[];

    private String          iTitleString;
    private long			iCharLevel;
    
    private int				iStats[] = new int[16];
    private int				iGF; 
    private int				iIF; 

    public D2Character(String pFileName) throws Exception
    {
        iFileName = pFileName;
        if ( iFileName == null || !iFileName.toLowerCase().endsWith(".d2s") )
        {
            throw new Exception("Incorrect Character file name");
        }
        iCharItems = new ArrayList();
        iMercItems = new ArrayList();
        iReader = new D2BitReader(iFileName);
        readChar();
    }
    
    public ArrayList getItemList()
    {
        ArrayList lList = new ArrayList();
        
        if ( iCharItems != null )
        {
            lList.addAll( iCharItems );
        }
        if ( iMercItems != null )
        {
            lList.addAll( iMercItems );
        }
        
        return lList;
    }

    private void readChar() throws Exception
    {
        iReader.set_byte_pos(4);
        long lVersion = iReader.read(32);
        //        System.err.println("Version: " + lVersion );
        if (lVersion != 96)
        {
            throw new Exception("Incorrect Character version: " + lVersion);
        }

        iReader.set_byte_pos(8);
        long lSize = iReader.read(32);
        //        System.err.println("FileSize: " + lSize );
        if (iReader.get_length() != lSize)
        {
            throw new Exception("Incorrect FileSize: " + lSize);
        }

        //        long lCheckSum1 = br.read(32);
        long lCheckSum2 = calculateCheckSum();

        iReader.set_byte_pos(12);
        boolean lChecksum = false;
        byte lCalcByte3 = (byte) ((0xff000000 & lCheckSum2) >>> 24);
        byte lCalcByte2 = (byte) ((0x00ff0000 & lCheckSum2) >>> 16);
        byte lCalcByte1 = (byte) ((0x0000ff00 & lCheckSum2) >>> 8);
        byte lCalcByte0 = (byte) (0x000000ff & lCheckSum2);

        byte lFileByte0 = (byte) iReader.read(8);
        byte lFileByte1 = (byte) iReader.read(8);
        byte lFileByte2 = (byte) iReader.read(8);
        byte lFileByte3 = (byte) iReader.read(8);

        if (lFileByte0 == lCalcByte0)
        {
            if (lFileByte1 == lCalcByte1)
            {
                if (lFileByte2 == lCalcByte2)
                {
                    if (lFileByte3 == lCalcByte3)
                    {
                        lChecksum = true;
                    }
                }
            }
        }
        if (!lChecksum)
        {
            throw new Exception("Incorrect Checksum");
        }

        long lWeaponSet = iReader.read(32);
        StringBuffer lCharName = new StringBuffer();
        for (int i = 0; i < 16; i++)
        {
            long lChar = iReader.read(8);
            if (lChar != 0)
            {
                lCharName.append((char) lChar);
            }
        }
        //        System.err.println("Char: " + lCharName.toString());

        iReader.set_byte_pos(36);
        iReader.skipBits(2);
        iHC = iReader.read(1) == 1;
        //        System.err.println("Died: " + br.read(1) );
        //        br.skipBits(1);
        //        System.err.println("Expansion: " + br.read(1) );

        iReader.set_byte_pos(37);
        long lCharTitle = iReader.read(8);
        //        System.err.println("Char Title (?): " + lCharTitle );

        iReader.set_byte_pos(40);
        long lCharCode = iReader.read(8);
        //        System.err.println("Char Class: " + getCharacterCode((int) lCharCode)
        // );

        iReader.set_byte_pos(43);
        iCharLevel = iReader.read(8);
        //        System.err.println("Char Level: " + lCharLevel );
        if ( iCharLevel < 1 || iCharLevel > 99 )
        {
            throw new Exception("Invalid char level: " + iCharLevel + " (should be between 1-99)");
        }

        iTitleString = " Lvl " + iCharLevel + " " + getCharacterCode((int) lCharCode);

        // For the stats, determine the block
        iGF = iReader.findNextFlag("gf", 700);
        if ( iGF == -1 )
        {
            throw new Exception("Error: stats block not found");
        }
        iIF = iReader.findNextFlag("if", 700);
        if ( iIF == -1 )
        {
            throw new Exception("Error: Skills block not found");
        }

        // now copy the block into the Flavie bitreader 
        // (it can read integers unaligned to bytes which is needed here) 
        iReader.set_byte_pos(iGF);
        byte lInitialBytes[] = iReader.get_bytes(iIF - iGF);
        D2FileReader lReader = new D2FileReader(lInitialBytes);
        if ( lReader.getCounterInt(8) != 103 )
        {
            throw new Exception("Stats Section not found");
        }
        if ( lReader.getCounterInt(8) != 102 )
        {
            throw new Exception("Stats Section not found");
        }
//        lReader.getCounterString(2);

        boolean lHasStats = true;
        while ( lHasStats )
        {
	        // read the stats
            int lID = lReader.getCounterInt(9);
            if ( lID == 0x1ff )
            {
                // all read
                lHasStats = false;
            }
            else
            {
                D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST.getRow(lID);
                int lBits = Integer.parseInt( lItemStatCost.get("CSvBits") );
                int lValue = lReader.getCounterInt(lBits);
//                System.err.println( "" + lItemStatCost.get("Stat") + ": " + lValue + "("+lBits+")");
                iStats[lID] = lValue;
            }
        }
        
        // check writer (just to be sure)
        byte lWritenBytes[] = getCurrentStats();
        
        if ( lInitialBytes.length != lWritenBytes.length )
        {
            throw new Exception("Stats writer check at reading: incorrect length");
        }
        for ( int i = 0 ; i < lInitialBytes.length ; i++ )
        {
            if ( lInitialBytes[i] != lWritenBytes[i] )
            {
                throw new Exception("Stats writer check at reading: incorrect byte at nr: " + i);
            }
        }
        
        iStashGrid = new boolean[8][6];
        iInventoryGrid = new boolean[4][10];
        iBeltGrid = new boolean[4][4];
        iCubeGrid = new boolean[4][3];
        iEquipped = new boolean[13];
        iMerc = new boolean[13];
        clearGrid();
        readItems();
    }

    public String getTitleString()
    {
        return iTitleString;
    }

    public static String getCharacterCode(int pChar)
    {
        switch (pChar)
        {
        case 0:
            return "Amazon";
        case 1:
            return "Sorceress";
        case 2:
            return "Necromancer";
        case 3:
            return "Paladin";
        case 4:
            return "Barbarian";
        case 5:
            return "Druid";
        case 6:
            return "Assasin";
        }
        return "<none>";
    }

    // read in all items:
    // find the first "JM" flag in the file
    // the byte immediately following it
    // indicates the number of items
    // copy the set of bytes representing each
    // item from the character file and use
    // them to construct a new item object.
    // store the items in the vector
    public void readItems() throws Exception
    {
        int lFirstPos = iReader.findNextFlag("JM", 0);
        if (lFirstPos == -1)
        {
            throw new Exception("Character items not found");
        }
        int lLastItemEnd = lFirstPos + 2;
        iReader.set_byte_pos(lLastItemEnd);
        int num_items = (int) iReader.read(16);

        int lCharStart = lFirstPos + 4;
        int lCharEnd = lCharStart;

        for (int i = 0; i < num_items; i++)
        {
            int lItemStart = iReader.findNextFlag("JM", lLastItemEnd);
            if (lItemStart == -1)
            {
                throw new Exception("Character item " + (i + 1) + " not found.");
            }

            D2Item lItem = new D2Item(iFileName, iReader, lItemStart, iCharLevel);

            lLastItemEnd = lItemStart + lItem.getItemLength();
            lCharEnd = lLastItemEnd;
            addCharItem(lItem);
            markCharGrid(lItem);
        }

        //        System.err.println("Read Char: " + lCharStart + " - " + lCharEnd );

        int lMercStart = -1;
        int lMercEnd = -1;

        lFirstPos = iReader.findNextFlag("jfJM", lCharEnd);
        if (lFirstPos != -1) // iReader.getFileContent()
        {
            iHasMerc = true;
            lLastItemEnd = lFirstPos + 4;
            iReader.set_byte_pos(lLastItemEnd);
            num_items = (int) iReader.read(16);

            lMercStart = lFirstPos + 6;
            lMercEnd = lMercStart;

            for (int i = 0; i < num_items; i++)
            {
                int lItemStart = iReader.findNextFlag("JM", lLastItemEnd);
                if (lItemStart == -1)
                {
                    throw new Exception("Merc item " + (i + 1) + " not found.");
                }

                D2Item lItem = new D2Item(iFileName, iReader, lItemStart, iCharLevel);

                lLastItemEnd = lItemStart + lItem.getItemLength();
                lMercEnd = lLastItemEnd;
                addMercItem(lItem);
                markMercGrid(lItem);
            }
            //	        System.err.println("Read Char: " + lMercStart + " - " + lMercEnd
            // );
        }

        iReader.set_byte_pos(0); // iReader.getFileContent()
        
        // before stats
        iBeforeStats = iReader.get_bytes(iGF);
        
        // goto after stats
        iReader.set_byte_pos(iIF);
        
        // before items
        iBeforeItems = iReader.get_bytes(lCharStart-iIF);
        
        if (lMercStart == -1)
        {
            // between
            iBetweenItems = new byte[0];

            // goto after char
            iReader.set_byte_pos(lCharEnd);
            // after items
            iAfterItems = iReader.get_bytes(iReader.get_length() - lCharEnd);
        }
        else
        {
            // goto after char
            iReader.set_byte_pos(lCharEnd);
            // between
            iBetweenItems = iReader.get_bytes(lMercStart - lCharEnd);

            // goto after merc
            iReader.set_byte_pos(lMercEnd);
            // after items
            iAfterItems = iReader.get_bytes(iReader.get_length() - lMercEnd);
        }
    }

    public boolean hasMerc()
    {
        return iHasMerc;
    }

    // clear all the grids
    // grids keep track of which spots that items
    // can be place are occupied
    public void clearGrid()
    {
        for (int i = 0; i < iEquipped.length; i++)
        {
            iEquipped[i] = false;
        }
        for (int i = 0; i < iMerc.length; i++)
        {
            iMerc[i] = false;
        }
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                iBeltGrid[i][j] = false;
            }
        }
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 6; j++)
            {
                iStashGrid[i][j] = false;
            }
        }
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                iInventoryGrid[i][j] = false;
            }
        }
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                iCubeGrid[i][j] = false;
            }
        }
    }

    public boolean markCharGrid(D2Item i)
    {
        short panel = i.get_panel();
        // pre-declarations because java scopes
        // switches strangely
        int row, col, width, height, j, k;
        switch (panel)
        {
        case 0: // equipped or on belt
            int location = (int) i.get_location();
            // on the belt
            if (location == 2)
            {
                col = (int) i.get_col();
                row = col / 4;
                col = col % 4;
                width = (int) i.get_width();
                height = (int) i.get_height();
                if ((row + height) > 4)
                    return false;
                if ((col + width) > 4)
                    return false;
                for (j = row; j < row + height; j++)
                {
                    for (k = col; k < col + width; k++)
                        iBeltGrid[j][k] = true;
                }
            }
            // in a socket, not handled yet
            else if (location == 6)
            {

            }
            // not on the belt
            else
            {
                int body_position = (int) i.get_body_position();
                if (iEquipped[body_position] == true)
                {
                    return false;
                }
                else
                {
                    iEquipped[body_position] = true;
                }
            }
            break;
        case BODY_INV_CONTENT: // inventory
            row = (int) i.get_row();
            col = (int) i.get_col();
            width = (int) i.get_width();
            height = (int) i.get_height();
            if ((row + height) > 4)
                return false;
            if ((col + width) > 10)
                return false;
            for (j = row; j < row + height; j++)
            {
                for (k = col; k < col + width; k++)
                    iInventoryGrid[j][k] = true;
            }
            break;
        case BODY_CUBE_CONTENT: // cube
            row = (int) i.get_row();
            col = (int) i.get_col();
            width = (int) i.get_width();
            height = (int) i.get_height();
            if ((row + height) > 4)
                return false;
            if ((col + width) > 3)
                return false;
            for (j = row; j < row + height; j++)
            {
                for (k = col; k < col + width; k++)
                    iCubeGrid[j][k] = true;
            }
            break;
        case BODY_STASH_CONTENT: // stash
            row = (int) i.get_row();
            col = (int) i.get_col();
            width = (int) i.get_width();
            height = (int) i.get_height();
            if ((row + height) > 8)
                return false;
            if ((col + width) > 6)
                return false;
            for (j = row; j < row + height; j++)
            {
                for (k = col; k < col + width; k++)
                    iStashGrid[j][k] = true;
            }
            break;
        }
        return true;
    }

    public boolean markMercGrid(D2Item i)
    {
        short panel = i.get_panel();
        // pre-declarations because java scopes
        // switches strangely
        int row, col, width, height, j, k;
        if (panel == 0)
        {
            int location = (int) i.get_location();
            int body_position = (int) i.get_body_position();
            if (iMerc[body_position - 1] == true)
            {
                return false;
            }
            else
            {
                iMerc[body_position - 1] = true;
            }
        }
        return true;
    }

    public boolean unmarkCharGrid(D2Item i)
    {
        short panel = i.get_panel();
        // pre-declarations because java scopes
        // switches strangely
        int row, col, width, height, j, k;
        switch (panel)
        {
        case 0: // equipped or on belt
            int location = (int) i.get_location();
            // on the belt
            if (location == 2)
            {
                col = (int) i.get_col();
                row = col / 4;
                col = col % 4;
                width = (int) i.get_width();
                height = (int) i.get_height();
                if ((row + height) > 4)
                    return false;
                if ((col + width) > 4)
                    return false;
                for (j = row; j < row + height; j++)
                {
                    for (k = col; k < col + width; k++)
                        iBeltGrid[j][k] = false;
                }
            }
            // in a socket, not handled yet
            else if (location == 6)
            {

            }
            // not on the belt
            else
            {
                int body_position = (int) i.get_body_position();
                iEquipped[body_position] = false;
            }
            break;
        case BODY_INV_CONTENT: // inventory
            row = (int) i.get_row();
            col = (int) i.get_col();
            width = (int) i.get_width();
            height = (int) i.get_height();
            if ((row + height) > 4)
                return false;
            if ((col + width) > 10)
                return false;
            for (j = row; j < row + height; j++)
            {
                for (k = col; k < col + width; k++)
                    iInventoryGrid[j][k] = false;
            }
            break;
        case BODY_CUBE_CONTENT: // cube
            row = (int) i.get_row();
            col = (int) i.get_col();
            width = (int) i.get_width();
            height = (int) i.get_height();
            if ((row + height) > 4)
                return false;
            if ((col + width) > 3)
                return false;
            for (j = row; j < row + height; j++)
            {
                for (k = col; k < col + width; k++)
                    iCubeGrid[j][k] = false;
            }
            break;
        case BODY_STASH_CONTENT: // stash
            row = (int) i.get_row();
            col = (int) i.get_col();
            width = (int) i.get_width();
            height = (int) i.get_height();
            if ((row + height) > 8)
                return false;
            if ((col + width) > 6)
                return false;
            for (j = row; j < row + height; j++)
            {
                for (k = col; k < col + width; k++)
                    iStashGrid[j][k] = false;
            }
            break;
        }
        return true;
    }

    public boolean unmarkMercGrid(D2Item i)
    {
        short panel = i.get_panel();
        // pre-declarations because java scopes
        // switches strangely
        int row, col, width, height, j, k;
        if (panel == 0)
        {
            int location = (int) i.get_location();
            // on the belt
            // not on the belt
            int body_position = (int) i.get_body_position();
            iMerc[body_position - 1] = false;
        }
        return true;
    }

    // insert an item into the vector
    public void addCharItem(D2Item pItem)
    {
        iCharItems.add(pItem);
        pItem.setCharLvl(iCharLevel);
    }

    // insert an item into the vector
    public void addMercItem(D2Item pItem)
    {
        iMercItems.add(pItem);
        pItem.setCharLvl(iCharLevel);
    }

    public void removeCharItem(int i)
    {
        iCharItems.remove(i);
    }

    public void removeMercItem(int i)
    {
        iMercItems.remove(i);
    }

    // get an item from the vector
    public D2Item getCharItem(int i)
    {
        return (D2Item) iCharItems.get(i);
    }

    public int getCharItemNr()
    {
        return iCharItems.size();
    }

    // get an item from the vector
    public D2Item getMercItem(int i)
    {
        return (D2Item) iMercItems.get(i);
    }

    public int getMercItemNr()
    {
        return iMercItems.size();
    }

    public boolean checkCharGrid(int panel, int x, int y, D2Item pItem)
    {
        int i, j;
        int w = pItem.get_width();
        int h = pItem.get_height();
        switch (panel)
        {
        case BODY_INV_CONTENT:
            for (i = x; i < x + w; i++)
            {
                for (j = y; j < y + h; j++)
                {
                    if (j >= iInventoryGrid.length || i >= iInventoryGrid[j].length || iInventoryGrid[j][i])
                        return false;
                }
            }
            break;
        case BODY_BELT_CONTENT:
            if (!pItem.isBelt())
            {
                return false;
            }
            for (i = x; i < x + w; i++)
            {
                for (j = y; j < y + h; j++)
                {
                    if (j >= iBeltGrid.length || i >= iBeltGrid[j].length || iBeltGrid[j][i])
                        return false;
                }
            }
            break;
        case BODY_CUBE_CONTENT:
            for (i = x; i < x + w; i++)
            {
                for (j = y; j < y + h; j++)
                {
                    if (j >= iCubeGrid.length || i >= iCubeGrid[j].length || iCubeGrid[j][i])
                        return false;
                }
            }
            break;
        case BODY_STASH_CONTENT:
            for (i = x; i < x + w; i++)
            {
                for (j = y; j < y + h; j++)
                {
                    if (j >= iStashGrid.length || i >= iStashGrid[j].length || iStashGrid[j][i])
                        return false;
                }
            }
            break;
        }
        return true;
    }

    public boolean checkCharPanel(int panel, int x, int y, D2Item pItem)
    {
        if (panel >= 10)
        {
            if (pItem == null)
            {
                return iEquipped[panel - 10];
            }
            if (iEquipped[panel - 10])
            {
                return true;
            }
            switch (panel)
            {
            case BODY_HEAD:
                // head
                if (pItem.isBodyHead())
                {
                    return false;
                }
                break;
            case BODY_NECK:
                // neck
                if (pItem.isBodyNeck())
                {
                    return false;
                }
                break;
            case BODY_LARM:
            case BODY_LARM2:
                // neck
                if (pItem.isBodyLArm())
                {
                    return false;
                }
                break;
            case BODY_TORSO:
                // neck
                if (pItem.isBodyTors())
                {
                    return false;
                }
                break;
            case BODY_RARM:
            case BODY_RARM2:
                // neck
                if (pItem.isBodyRArm())
                {
                    return false;
                }
                break;
            case BODY_GLOVES:
                // neck
                if (pItem.isBodyGlov())
                {
                    return false;
                }
                break;
            case BODY_RRING:
                // neck
                if (pItem.isBodyRRin())
                {
                    return false;
                }
                break;
            case BODY_BELT:
                // neck
                if (pItem.isBodyBelt())
                {
                    return false;
                }
                break;
            case BODY_LRING:
                // neck
                if (pItem.isBodyLRin())
                {
                    return false;
                }
                break;
            case BODY_BOOTS:
                // neck
                if (pItem.isBodyFeet())
                {
                    return false;
                }
                break;
            case BODY_CURSOR:
                // allow all type of items on the cursor
                return false;
            }
            return true;
        }
        switch (panel)
        {
        case BODY_INV_CONTENT:
            if (y >= 0 && y < iInventoryGrid.length)
            {
                if (x >= 0 && x < iInventoryGrid[y].length)
                {
                    return iInventoryGrid[y][x];
                }
            }
            return false;
        case BODY_BELT_CONTENT:
            if (y >= 0 && y < iBeltGrid.length)
            {
                if (x >= 0 && x < iBeltGrid[y].length)
                {
                    return iBeltGrid[y][x];
                }
            }
            return false;
        case BODY_CUBE_CONTENT:
            if (y >= 0 && y < iCubeGrid.length)
            {
                if (x >= 0 && x < iCubeGrid[y].length)
                {
                    return iCubeGrid[y][x];
                }
            }
            return false;
        case BODY_STASH_CONTENT:
            if (y >= 0 && y < iStashGrid.length)
            {
                if (x >= 0 && x < iStashGrid[y].length)
                {
                    return iStashGrid[y][x];
                }
            }
            return false;
        }
        return true;
    }

    public boolean checkMercPanel(int panel, int x, int y, D2Item pItem)
    {
        if (panel > 10)
        {
            if (pItem == null)
            {
                return iMerc[panel - 10 - 1];
            }
            if (iMerc[panel - 10 - 1])
            {
                return true;
            }
            switch (panel)
            {
            case BODY_HEAD:
                // head
                if (pItem.isBodyHead())
                {
                    return false;
                }
                break;
            case BODY_LARM:
                // left arm
                if (pItem.isBodyLArm())
                {
                    return false;
                }
                break;
            case BODY_TORSO:
                // body
                if (pItem.isBodyTors())
                {
                    return false;
                }
                break;
            case BODY_RARM:
                // right arm
                if (pItem.isBodyRArm())
                {
                    return false;
                }
                break;
            }
            return true;
        }
        return true;
    }

    public int getCharItemIndex(int panel, int x, int y)
    {
        if (panel == BODY_BELT_CONTENT)
        {
            for (int i = 0; i < iCharItems.size(); i++)
            {
                D2Item temp_item = (D2Item) iCharItems.get(i);
                if (temp_item.get_location() == panel)
                {
                    if (temp_item.get_col() == 4 * y + x)
                        return i;
                }
            }
        }
        else if (panel >= 10)
        {
            for (int i = 0; i < iCharItems.size(); i++)
            {
                D2Item temp_item = (D2Item) iCharItems.get(i);
                if (temp_item.get_location() != 0 && temp_item.get_location() != 2)
                {
                    if (temp_item.get_panel() == 0)
                    {
                        if (temp_item.get_body_position() == panel - 10)
                        {
                            return i;
                        }
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < iCharItems.size(); i++)
            {
                D2Item temp_item = (D2Item) iCharItems.get(i);
                if (temp_item.get_panel() == panel)
                {
                    int row = temp_item.get_col();
                    int col = temp_item.get_row();
                    if (x >= row && x <= row + temp_item.get_width() - 1 && y >= col && y <= col + temp_item.get_height() - 1)
                        return i;
                }
            }
        }
        return -1;
    }

    public int getMercItemIndex(int panel, int x, int y)
    {
        if (panel >= 10)
        {
            for (int i = 0; i < iMercItems.size(); i++)
            {
                D2Item temp_item = (D2Item) iMercItems.get(i);
                if (temp_item.get_panel() == 0)
                {
                    if (temp_item.get_body_position() == panel - 10)
                        return i;
                }
            }
        }
        return -1;
    }

    public void save()
    {
        // see how many backups exist
        int backup_max = -1;
        for (int i = 0; i <= 9; i++)
        {
            if (new java.io.File(iFileName + "." + i).exists())
                backup_max = i;
            else
                break;
        }

        // shift the backups down
        // (backup 9 is overwritten, 0 comes free)
        for (int i = backup_max; i >= 0; i--)
        {
            if (i < 9)
            {
                D2BitReader src = new D2BitReader(iFileName + "." + i);
                src.save(iFileName + "." + (i + 1));
            }
        }

        // save the file to backup 0
        iReader.save(iFileName + ".0");

        // build an a byte array that contains the
        // entire item list and insert it into
        // the open file in place of its current item list

        int lCharSize = 0;
        for (int i = 0; i < iCharItems.size(); i++)
        {
            lCharSize += ((D2Item) iCharItems.get(i)).get_bytes().length;
        }

        int lMercSize = 0;
        if (hasMerc())
        {
            for (int i = 0; i < iMercItems.size(); i++)
            {
                lMercSize += ((D2Item) iMercItems.get(i)).get_bytes().length;
            }
        }

        byte lWritenBytes[] = getCurrentStats();
        
        byte[] lNewbytes = new byte[iBeforeStats.length + lWritenBytes.length + iBeforeItems.length + lCharSize + iBetweenItems.length + lMercSize + iAfterItems.length];

        int lPos = 0;
        System.arraycopy(iBeforeStats, 0, lNewbytes, lPos, iBeforeStats.length);
        lPos += iBeforeStats.length;

        System.arraycopy(lWritenBytes, 0, lNewbytes, lPos, lWritenBytes.length);
        lPos += lWritenBytes.length;
        
        System.arraycopy(iBeforeItems, 0, lNewbytes, lPos, iBeforeItems.length);
        lPos += iBeforeItems.length;

        //        System.err.println("Save char " + lPos + " - " + (lPos+lCharSize) );
        int lCharItemCountPos = lPos - 2; // iCharItemCountPos
        int lMercItemCountPos = -1;

        for (int i = 0; i < iCharItems.size(); i++)
        {
            byte[] item_bytes = ((D2Item) iCharItems.get(i)).get_bytes();
            System.arraycopy(item_bytes, 0, lNewbytes, lPos, item_bytes.length);
            lPos += item_bytes.length;
        }

        if (hasMerc())
        {
            System.arraycopy(iBetweenItems, 0, lNewbytes, lPos, iBetweenItems.length);
            lPos += iBetweenItems.length;

            lMercItemCountPos = lPos - 2;

            //	        System.err.println("Save merc " + lPos + " - " + (lPos+lMercSize)
            // );

            for (int i = 0; i < iMercItems.size(); i++)
            {
                byte[] item_bytes = ((D2Item) iMercItems.get(i)).get_bytes();
                System.arraycopy(item_bytes, 0, lNewbytes, lPos, item_bytes.length);
                lPos += item_bytes.length;
            }
        }

        if (iAfterItems.length > 0)
        {
            System.arraycopy(iAfterItems, 0, lNewbytes, lPos, iAfterItems.length);
        }

        iReader.setBytes(lNewbytes); // iReader.getFileContent()

        iReader.set_byte_pos(lCharItemCountPos);
        iReader.write(iCharItems.size(), 16);

        if (hasMerc())
        {
            iReader.set_byte_pos(lMercItemCountPos);
            iReader.write(iMercItems.size(), 16);
        }

        //        iReader.set_byte_pos(12);
        //        long original = iReader.read(32);

        // get all the bytes
        iReader.set_byte_pos(0);
        byte[] data = iReader.get_bytes(iReader.get_length());

        byte[] oldchecksum = { data[12], data[13], data[14], data[15] };

        // clear the current checksum
        byte[] checksum = { 0, 0, 0, 0 }; // byte checksum
        iReader.setBytes(12, checksum);
        //        br.replace_bytes(12, 15, checksum);
        //br.set_byte_pos(12);
        //br.write(0,32);

        // correct the file length
        byte[] length = new byte[4];
        length[3] = (byte) ((0xff000000 & data.length) >>> 24);
        length[2] = (byte) ((0x00ff0000 & data.length) >>> 16);
        length[1] = (byte) ((0x0000ff00 & data.length) >>> 8);
        length[0] = (byte) (0x000000ff & data.length);
        iReader.setBytes(8, length);
        //        br.replace_bytes(8, 11, length);

        iReader.set_byte_pos(0);
        // calculate a new checksum
        // do checksumming
        long lCheckSum = calculateCheckSum();
        //System.out.println(original + " " + ucs);

        //br.set_byte_pos(8);
        //System.out.println("size " + br.read(32) + " " + data.length);

        // put checksum into a byte array
        checksum[3] = (byte) ((0xff000000 & lCheckSum) >>> 24);
        checksum[2] = (byte) ((0x00ff0000 & lCheckSum) >>> 16);
        checksum[1] = (byte) ((0x0000ff00 & lCheckSum) >>> 8);
        checksum[0] = (byte) (0x000000ff & lCheckSum);

        // write checksum
        //        br.replace_bytes(12, 15, checksum);
        iReader.setBytes(12, checksum);

        //for (int i = 0; i < 4; i++)
        //System.out.println((0x0000ff & checksum[i])
        //		    + " " +
        //		   (0x0000ff & oldchecksum[i]));
        iReader.save();
    }
    
    public int getGold()
    {
        return iStats[14];
    }
    
    public int getGoldMax()
    {
        return 10000*((int) iCharLevel);
    }

    public void setGold(int pGold) throws Exception
    {
        if ( pGold < 0 )
        {
            throw new Exception("gold must be greater than zero");
        }
        if ( pGold > getGoldMax() )
        {
            throw new Exception("gold must be smaller than max" + getGoldMax() );
        }
        iStats[14] = pGold;
    }

    public int getGoldBank()
    {
        return iStats[15];
    }

    public void setGoldBank(int pGoldBank) throws Exception
    {
        if ( pGoldBank < 0 )
        {
            throw new Exception("gold must be greater than zero");
        }
        if ( pGoldBank > getGoldBankMax() )
        {
            throw new Exception("gold must be smaller than max" + getGoldBankMax() );
        }
        iStats[15] = pGoldBank;
    }
    
    public int getGoldBankMax()
    {
        int lMaxGold = 50000;
        for ( int lLvl = 9 ; lLvl <=29 ; lLvl+=10 )
        {
            if ( iCharLevel < lLvl )
            {
                return lMaxGold;
            }
            lMaxGold += 50000;
        }
        
        if ( iCharLevel == 30 )
        {
            return 200000;
        }
        if ( iCharLevel == 31 )
        {
            return 800000;
        }
        lMaxGold = 850000;
        
        for ( int lLvl = 33 ; lLvl <=99 ; lLvl+=2 )
        {
            if ( iCharLevel <= lLvl )
            {
                return lMaxGold;
            }
            lMaxGold += 50000;
        }
        return 0;
    }

    private byte[] getCurrentStats()
    {
        D2FileWriter lWriter = new D2FileWriter();
        lWriter.setCounterInt(8, 103);
        lWriter.setCounterInt(8, 102);
        for (int lStatNr = 0; lStatNr < iStats.length; lStatNr++)
        {
            if (iStats[lStatNr] != 0)
            {
                lWriter.setCounterInt(9, lStatNr);
                D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST.getRow(lStatNr);
                int lBits = Integer.parseInt(lItemStatCost.get("CSvBits"));
                lWriter.setCounterInt(lBits, iStats[lStatNr]);
            }
        }
        lWriter.setCounterInt(9, 0x1FF);
        return lWriter.getCurrentContent();
    }

    private long calculateCheckSum()
    {
        iReader.set_byte_pos(0);
        long lCheckSum = 0; // unsigned integer checksum
        for (int i = 0; i < iReader.get_length(); i++)
        {
            long lByte = iReader.read(8);
            if (i >= 12 && i <= 15)
            {
                lByte = 0;
            }

            long upshift = lCheckSum << 33 >>> 32;
            long add = lByte + ((lCheckSum >>> 31) == 1 ? 1 : 0);

            lCheckSum = upshift + add;
        }
        return lCheckSum;
    }

    public boolean isSC()
    {
        return !iHC;
    }

    public boolean isHC()
    {
        return iHC;
    }
}