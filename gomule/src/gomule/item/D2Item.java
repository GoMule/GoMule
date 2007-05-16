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

package gomule.item;

import gomule.gui.*;
import gomule.util.*;

import java.util.*;

import randall.d2files.*;
import randall.flavie.*;

// an item class
// manages one item
// keeps the a copy of the bytes representing
// an item and a bitreader to manipulate them
// also stores most data from the item in
//
// this class is NOT designed to edit items
// any methods that allow the item's bytes
// to be written only exist to facillitate
// moving items. writing other item fields
// is not supported by this class
public class D2Item implements Comparable, D2ItemInterface
{
    private ArrayList               iProperties = new ArrayList();
    private ArrayList               iSet1;
    private ArrayList               iSet2;
    private ArrayList               iSet3;
    private ArrayList               iSet4;
    private ArrayList               iSet5;
    private ArrayList               iSocketedItems;

    // general item data
    private int                     flags;
    private short                   version;
    private short                   location;
    private short                   body_position;
    private short                   row;
    private short                   col;
    private short                   panel;
    private String                  item_type;

    // additional data for complex items
    private long                    iSocketNrFilled    = 0;
    private long                   	iSocketNrTotal    = 0;
    private long                    fingerprint;
    private short                   ilvl;
    private short                   quality;
    private short                   gfx_num;
    private short                   class_info;
    private short                   low_quality;
    private short                   hi_quality;
    private short                   magic_prefix;
    private short                   magic_suffix;
    private short                   set_id;
    private short                   rare_name_1;
    private short                   rare_name_2;
    private short[]                 rare_prefixes;
    private short[]                 rare_suffixes;
    private short                   unique_id;
    private int                     runeword_id;
    private String                  personalization;
    private short                   tome;

    // useful item data to keep on hand
    // that's in txt files rather than
    // in the character file
    private short                   width;
    private short                   height;
    private String                  image_file;
    private String                  name;

    private D2TxtFileItemProperties iItemType;
    private String					iType;
    private String					iType2;
    private boolean                 iEthereal;
    private boolean                 iSocketed;
    private boolean                 iMagical;
    private boolean                 iRare;
    private boolean                 iCrafted;
    private boolean                 iSet;
    private boolean                 iUnique;
    private boolean                 iRuneWord;

    private boolean                 iSmallCharm;
    private boolean                 iLargeCharm;
    private boolean                 iGrandCharm;
    private boolean                 iJewel;
    private boolean                 iGem;

    private boolean                 iTypeMisc;
    private boolean                 iTypeWeapon;
    private boolean                 iTypeArmor;

    protected String                iItemName;
    protected String                iItemNameNoPersonalising;
    private String                  iItemNameWithSockets;
    //    private int iLvl;
    private String                  iFP;

    private boolean                 iBody       = false;
    private String                  iBodyLoc1   = null;
    private String                  iBodyLoc2   = null;
    private boolean                 iBelt       = false;

    private D2BitReader             iItem;
    
    private String					iFileName;
    private boolean					iIsChar;
    
    private long					iCharLvl;
    
    private int						iReqLvl = -1;
    private int						iReqStr = -1;
    private int						iReqDex = -1;
    
    //    private int iPossibleItemLength = 0;

    public D2Item(String pFileName, D2BitReader pFile, int pPos, long pCharLvl) throws Exception
    {
        iFileName = pFileName;
        iIsChar = iFileName.endsWith(".d2s");
        iCharLvl = pCharLvl;
        try
        {
            //        bytedata = b;
            //        br = new D2BitReader(bytedata);
            pFile.set_byte_pos(pPos);
            read_item(pFile);

            int lCurrentReadLength = pFile.get_pos() - pPos * 8;
            int lNextJMPos = pFile.findNextFlag("JM", pFile.get_byte_pos());
            int lLengthToNextJM = lNextJMPos - pPos;

            if (lLengthToNextJM < 0)
            {
                int lNextKFPos = pFile.findNextFlag("kf", pFile.get_byte_pos());
                if (lNextKFPos >= 0)
                {
                    lLengthToNextJM = lNextKFPos - pPos;
                }
                else
                {
                    // last item (for stash only)
                    lLengthToNextJM = pFile.get_length() - pPos;
                }
            }
            // pFile.findNextFlag("kf", pFile.get_byte_pos()) - pPos;
            int lDiff = ((lLengthToNextJM * 8) - lCurrentReadLength);
            if (lDiff > 7)
            {
                throw new D2ItemException("Item not read complete, missing bits: " + lDiff + getExStr());
                //            System.err.println("Test: " + lCurrentReadLength + " - " +
                // lLengthToNextJM*8 + ": " + lDiff);
            }

            //        System.err.println("Current read length: " + lCurrentReadLength +
            // " - " + lLengthToNextJM );

            pFile.set_byte_pos(pPos);
            iItem = new D2BitReader(pFile.get_bytes(lLengthToNextJM));
            pFile.set_byte_pos(pPos + lLengthToNextJM);
        }
        catch (D2ItemException pEx)
        {
            throw pEx;
        }
        catch (Exception pEx)
        {
            pEx.printStackTrace();
            throw new D2ItemException("Error: " + pEx.getMessage() + getExStr());
        }
        //        System.err.println("Test: " + getItemName() + " - " + getItemLength()
        // );
    }
    
    public String getFileName()
    {
        return iFileName;
    }
    
    public boolean isCharacter()
    {
        return iIsChar;
    }
    
    // bit checker
    // if the specified bit of flags is set,
    // return true
    private boolean check_flag(int bit)
    {
        if (((flags >>> (32 - bit)) & 1) == 1)
            return true;
        else
            return false;
    }

    // read basic information from the bytes
    // common to all items, then split based on
    // whether the item is an ear
    private void read_item(D2BitReader pFile) throws Exception
    {
        pFile.skipBytes(2);

        flags = (int) pFile.unflip(pFile.read(32), 32); // 4 bytes

        iSocketed = check_flag(12); //12
        iEthereal = check_flag(23); //23
        iRuneWord = check_flag(27); //27

        version = (short) pFile.read(8); // 1 byte
        pFile.skipBits(2);
        location = (short) pFile.read(3);
        body_position = (short) pFile.read(4);
        col = (short) pFile.read(4);
        row = (short) pFile.read(4);
        panel = (short) pFile.read(3); // 20 bits -> 2,5 byte

        // flag 17 is an ear
        if (!check_flag(17))
        {
            readExtend(pFile);
        }
        else
        {
            read_ear(pFile);
        }
        if ( personalization == null )
        {
            iItemNameNoPersonalising = iItemName;
        }
        //        System.err.println("Item read: " + iItemName );

    }

    // read ear related data from the bytes
    private void read_ear(D2BitReader pFile)
    { // br.getFileContent()
        height = 1;
        width = 1;
        iItemName = "? Ear (?)";

        pFile.read(3); // Ear Class
        pFile.read(7); // Ear Lvl
        for (int i = 0; i < 18; i++)
        {
            pFile.read(7); // name
        }
    }

    // read non ear data from the bytes,
    // setting class variables for easier access
    private void readExtend(D2BitReader pFile) throws Exception
    {
        // 9,5 bytes already read (common data)
        item_type = "";
        // skip spaces or hashing won't work
        for (int i = 0; i < 4; i++)
        {
            char c = (char) pFile.read(8); // 4 bytes
            if (c != 32)
            {
                item_type += c;
            }
        }

        iItemType = D2TxtFile.search(item_type);
        height = Short.parseShort(iItemType.get("invheight"));
        width = Short.parseShort(iItemType.get("invwidth"));
        image_file = iItemType.get("invfile");
        name = iItemType.get("name");

        String lD2TxtFileName = iItemType.getFileName();
        if (lD2TxtFileName != null)
        {
            iTypeMisc = ("Misc".equals(lD2TxtFileName));
            iTypeWeapon = ("weapons".equals(lD2TxtFileName));
            iTypeArmor = ("armor".equals(lD2TxtFileName));
        }

        iType = iItemType.get("type");
        iType2 = iItemType.get("type2");
        
        // Requerements
        if ( iTypeMisc )
        {
            iReqLvl = getReq(iItemType.get("levelreq"));
        }
        else if ( iTypeArmor )
        {
            iReqLvl = getReq(iItemType.get("levelreq"));
            iReqStr = getReq(iItemType.get("reqstr"));
        }
        else if ( iTypeWeapon )
        {
            iReqLvl = getReq(iItemType.get("levelreq"));
            iReqStr = getReq(iItemType.get("reqstr"));
            iReqDex = getReq(iItemType.get("reqdex"));
            
//            System.err.println("Weapon: " + item_type + " - " + iItemType.get("type") + " - " + iItemType.get("type2") + " - " + iItemType.get("code") );
        }
        
        String lItemName = D2TblFile.getString(item_type);
        if (lItemName != null)
        {
            iItemName = lItemName;
            iItemNameWithSockets = iItemName;
        }

        // flag 22 is a simple item (extend1)
        if (!check_flag(22))
        {
            readExtend1(pFile);
        }

        // gold (?)
        if ("gold".equals(item_type))
        {
            if (pFile.read(1) == 0)
            {
                pFile.read(12);
            }
            else
            {
                pFile.read(32);
            }
        }

        long lHasRand = pFile.read(1);

        if (lHasRand == 1)
        { // GUID ???
            if (!check_flag(22))
            {
	            pFile.read(32);
	            pFile.read(32);
	            pFile.read(32);
            }
            //                pFile.read(32);
        }

        // flag 22 is a simple item (extend2)
        if (!check_flag(22))
        {
            readExtend2(pFile);
        }

        if (iType != null && iType2 != null && iType.startsWith("gem"))
        {
            if (iType2.equals("gem0") || iType2.equals("gem1") || iType2.equals("gem2") || iType2.equals("gem3") || iType2.equals("gem4"))
            {
                iGem = true;
            }
        }
        D2TxtFileItemProperties lItemType = D2TxtFile.ITEM_TYPES.searchColumns("Code", iType);
        if (lItemType == null)
        {
            lItemType = D2TxtFile.ITEM_TYPES.searchColumns("Equiv1", iType);
            if (lItemType == null)
            {
                lItemType = D2TxtFile.ITEM_TYPES.searchColumns("Equiv2", iType);
            }
        }
        
        if ("1".equals(lItemType.get("Body")))
        {
            iBody = true;
            iBodyLoc1 = lItemType.get("BodyLoc1");
            iBodyLoc2 = lItemType.get("BodyLoc2");
        }
        if ("1".equals(lItemType.get("Beltable")))
        {
            iBelt = true;
        }

        int lLastItem = pFile.get_byte_pos();
        if (iSocketNrFilled > 0)
        {
            iSocketedItems = new ArrayList();
            for (int i = 0; i < iSocketNrFilled; i++)
            {
                int lStartNewItem = pFile.findNextFlag("JM", lLastItem);
                D2Item lSocket = new D2Item(iFileName, pFile, lStartNewItem, iCharLvl);
                lLastItem = lStartNewItem + lSocket.getItemLength();
                iSocketedItems.add(lSocket);
                if ( lSocket.iReqLvl > iReqLvl )
                {
                    iReqLvl = lSocket.iReqLvl;
                }
            }
        }

        if (iRuneWord)
        {
            ArrayList lList = new ArrayList();
            for (int i = 0; i < iSocketedItems.size(); i++)
            {
                lList.add(((D2Item) iSocketedItems.get(i)).getRuneCode());
            }

            D2TxtFileItemProperties lRuneWord = D2TxtFile.RUNES.searchRuneWord(lList);
            if (lRuneWord != null)
            {
                //		        iItem += " Runeword found " + D2TblFile.getString(
                // lRuneWord.get("Name") ) + " - " + D2TblFile.getString(
                // lRuneWord.get("Rune Name") ) + "\n";
                iItemName = D2TblFile.getString(lRuneWord.get("Name"));
                //				if ( iItemName == null )
                //				{
                //				    System.err.println("Runeword: " + iItemName );
                //				}
            }
        }

        if (iSocketNrFilled > 0 && isNormal())
        {
            iItemName = "Gemmed " + iItemName;
        }

        if (iItemName != null)
        {
            iItemName = iItemName.trim();
        }
        
        if ( iEthereal )
        {
            if ( iReqStr != -1 )
            {
                iReqStr -= 10;
            }
            if ( iReqDex != -1 )
            {
                iReqDex -= 10;
            }
        }
    }
    
    private int getReq(String pReq)
    {
        if ( pReq != null )
        {
            String lReq = pReq.trim();
            if ( !lReq.equals("") && !lReq.equals("0") )
            {
                try
                {
                    return Integer.parseInt( lReq );
                }
                catch( Exception pEx )
                {
                    // do nothing, no req
                }
            }
        }
        return -1;
    }

    private void readExtend1(D2BitReader pFile) throws Exception
    {
        // extended item
        iSocketNrFilled = (short) pFile.read(3);
        fingerprint = pFile.read(32);
        iFP = "0x" + Integer.toHexString((int) fingerprint);
        ilvl = (short) pFile.read(7);
        quality = (short) pFile.read(4);
        // check variable graphic flag
        gfx_num = -1;
        if (pFile.read(1) == 1)
        {
            gfx_num = (short) pFile.read(3);
            if (iItemType.get("namestr").compareTo("cm1") == 0)
            {
                iSmallCharm = true;
                image_file = "invch" + ((gfx_num) * 3 + 1);
            }
            else if (iItemType.get("namestr").compareTo("cm2") == 0)
            {
                iLargeCharm = true;
                image_file = "invch" + ((gfx_num) * 3 + 2);
            }
            else if (iItemType.get("namestr").compareTo("cm3") == 0)
            {
                iGrandCharm = true;
                image_file = "invch" + ((gfx_num) * 3 + 3);
            }
            else if (iItemType.get("namestr").compareTo("jew") == 0)
            {
                iJewel = true;
                image_file = "invjw" + (gfx_num + 1);
            }
            else
            {
                image_file += (gfx_num + 1);
            }
        }
        // check class info flag
        if (pFile.read(1) == 1)
        {
            class_info = (short) pFile.read(11);
        }

        // path determined by item quality
        switch (quality)
        {
        case 1: // low quality item
        {
            low_quality = (short) pFile.read(3);
            break;
        }
        case 3: // high quality item
        {
            iItemName = "Superior " + iItemName;
            hi_quality = (short) pFile.read(3);
            break;
        }
        case 4: // magic item
        {
            iMagical = true;
            magic_prefix = (short) pFile.read(11);
            magic_suffix = (short) pFile.read(11);

            D2TxtFileItemProperties lPrefix = D2TxtFile.PREFIX.getRow(magic_prefix);
            String lPreName = lPrefix.get("Name");
            if ( lPreName != null && !lPreName.equals(""))
            {
                iItemName = lPreName + " " + iItemName;
                int lPreReq = getReq(lPrefix.get("levelreq"));
                if ( lPreReq > iReqLvl )
                {
                    iReqLvl = lPreReq;
                }
            }
            
            D2TxtFileItemProperties lSuffix = D2TxtFile.SUFFIX.getRow(magic_suffix);
            String lSufName = lSuffix.get("Name");
            if ( lSufName != null && !lSufName.equals(""))
            {
                iItemName = iItemName + " " + lSufName;
                int lSufReq = getReq(lSuffix.get("levelreq"));
                if ( lSufReq > iReqLvl )
                {
                    iReqLvl = lSufReq;
                }
            }

            break;
        }
        case 5: // set item
        {
            iSet = true;
            set_id = (short) pFile.read(12);
            if (gfx_num == -1)
            {
                String s = (String) iItemType.get("setinvfile");
                if (s.compareTo("") != 0)
                    image_file = s;
            }

            D2TxtFileItemProperties lSet = D2TxtFile.SETITEMS.getRow(set_id);
            iItemName = D2TblFile.getString(lSet.get("index"));
            
            int lSetReq = getReq(lSet.get("lvl req"));
            if ( lSetReq != -1 )
            {
                iReqLvl = lSetReq;
            }
//            else
//            {
//                System.err.println("Set Lvl: " + iItemName + " - " + lSetReq );
//            }
            break;
        }
        case 7: // unique item
        {
            iUnique = true;
            unique_id = (short) pFile.read(12);
            String s = iItemType.get("uniqueinvfile");
            if (s.compareTo("") != 0)
            {
                image_file = s;
            }

            D2TxtFileItemProperties lUnique = D2TxtFile.UNIQUES.getRow(unique_id);
            String lNewName = D2TblFile.getString(lUnique.get("index"));
            if ( lNewName != null )
            {
                iItemName = lNewName;
            }

            int lUniqueReq = getReq(lUnique.get("lvl req"));
            if ( lUniqueReq != -1 )
            {
                iReqLvl = lUniqueReq;
            }
//            else
//            {
//                System.err.println("Unique Lvl: " + iItemName + " - " + lUniqueReq );
//            }
            
            break;
        }
        case 6: // rare item
        {
            iRare = true;
            iItemName = "Rare " + iItemName;
        }
        case 8: // also a rare item, do the same (one's probably crafted)
        {
            if (quality == 8)
            {
                iCrafted = true;
                iItemName = "Crafted " + iItemName;
            }
        }
        {
            rare_name_1 = (short) pFile.read(8);
            rare_name_2 = (short) pFile.read(8);
            rare_prefixes = new short[3];
            rare_suffixes = new short[3];
            short pre_count = 0;
            short suf_count = 0;
            for (int i = 0; i < 3; i++)
            {
                if (pFile.read(1) == 1)
                {
                    rare_prefixes[pre_count] = (short) pFile.read(11);
                    D2TxtFileItemProperties lPrefix = D2TxtFile.PREFIX.getRow(rare_prefixes[pre_count]);
                    pre_count++;
                    String lPreName = lPrefix.get("Name");
                    if ( lPreName != null && !lPreName.equals(""))
                    {
                        int lPreReq = getReq(lPrefix.get("levelreq"));
                        if ( lPreReq > iReqLvl )
                        {
                            iReqLvl = lPreReq;
                        }
                    }
                    
                }
                if (pFile.read(1) == 1)
                {
                    rare_suffixes[suf_count] = (short) pFile.read(11);
                    D2TxtFileItemProperties lSuffix = D2TxtFile.SUFFIX.getRow(rare_suffixes[suf_count]);
                    suf_count++;
                    String lSufName = lSuffix.get("Name");
                    if ( lSufName != null && !lSufName.equals(""))
                    {
                        int lSufReq = getReq(lSuffix.get("levelreq"));
                        if ( lSufReq > iReqLvl )
                        {
                            iReqLvl = lSufReq;
                        }
                    }
                }
            }
            break;
        }
        case 2:
        {
            readTypes(pFile);
            break;
        }
        }

        // rune word
        if (check_flag(27))
        {
            runeword_id = (int) pFile.read(16);
        }
        iItemNameNoPersonalising = iItemName;
        // personalized
        if (check_flag(25))
        {
            personalization = "";
            boolean lNotEnded = true;
            for (int i = 0; i < 15 && lNotEnded; i++)
            {
                char c = (char) pFile.read(7);
                if (c == 0)
                {
                    lNotEnded = false;
                }
                else
                {
                    personalization += c;
                }
            }
        }
    }

    // MBR: unknown, but should be according to file format
    private void readTypes(D2BitReader pFile)
    {
        // charms ??
        if (isCharm())
        {
            long lCharm1 = pFile.read(1);
            long lCharm2 = pFile.read(11);
            //            System.err.println("Charm (?): " + lCharm1 );
            //            System.err.println("Charm (?): " + lCharm2 );
        }

        // books / scrolls ??
        if ("tbk".equals(item_type) || "ibk".equals(item_type))
        {
            long lTomb = pFile.read(5);
            //            System.err.println("Tome ID: " + lTomb );
        }

        if ("tsc".equals(item_type) || "isc".equals(item_type))
        {
            long lTomb = pFile.read(5);
            //            System.err.println("Tome ID: " + lTomb );
        }

        // body ??
        if ("body".equals(item_type))
        {
            long lMonster = pFile.read(10);
            //            System.err.println("Monster ID: " + lMonster );
        }
    }

    private void readExtend2(D2BitReader pFile) throws Exception
    {
        if (isTypeArmor())
        {
            //            pFile.read(1);
            long lDef = (pFile.read(11) - 10); // -10 ???
            //            System.err.println("Defense: " + lDef );

            long lMaxDur = pFile.read(8);
            //            System.err.println("Armor Max Dur.: " + lMaxDur );

            if (lMaxDur != 0)
            {
                long lCurDur = pFile.read(9);
            }
            //            System.err.println("Armor Cur Dur.: " + lCurDur );
        }
        else if (isTypeWeapon())
        {
            long lMaxDur = pFile.read(8);
            //            System.err.println("Weapon Max Dur/Quantity: " + lMaxDur );

            if (lMaxDur != 0)
            {
                long lCurDur = pFile.read(9);
                //                System.err.println("Weapon Cur Dur/Quantity: " + lCurDur );
            }

            if ("1".equals(iItemType.get("stackable")))
            {
                //	            System.err.println("Test: " + iItemType.get("stackable") + "
                // - " + iItemType.get("minstack")
                //	                    + " - " + iItemType.get("maxstack") + " - " +
                // iItemType.get("spawnstack") );

                long lQuantity = pFile.read(9);
                //                System.err.println("Weapon Cur Dur/Quantity: " + lQuantity );
            }
        }
        else if (isTypeMisc())
        {
            if ("1".equals(iItemType.get("stackable")))
            {
                long lQuantity = pFile.read(9);
            }
        }

        if (iSocketed)
        {
            iSocketNrTotal = pFile.read(4);
            //            System.err.println("Nr Sockets: " + lNrSockets );
        }

        long lSet1 = 0;
        long lSet2 = 0;
        long lSet3 = 0;
        long lSet4 = 0;
        long lSet5 = 0;

        if (quality == 5)
        {
            lSet1 = pFile.read(1);
            lSet2 = pFile.read(1);
            lSet3 = pFile.read(1);
            lSet4 = pFile.read(1);
            lSet5 = pFile.read(1);
        }

        readProperties(pFile, iProperties);

        if (quality == 5)
        {
            if (lSet1 == 1)
            {
                iSet1 = new ArrayList();
                readProperties(pFile, iSet1);
            }
            if (lSet2 == 1)
            {
                iSet2 = new ArrayList();
                readProperties(pFile, iSet2);
            }
            if (lSet3 == 1)
            {
                iSet3 = new ArrayList();
                readProperties(pFile, iSet3);
            }
            if (lSet4 == 1)
            {
                iSet4 = new ArrayList();
                readProperties(pFile, iSet4);
            }
            if (lSet5 == 1)
            {
                iSet5 = new ArrayList();
                readProperties(pFile, iSet5);
            }
        }

        //        if ( iRuneWord )
        //        {
        //            long lProp7 = pFile.read(9);
        //        }

    }

    private String getExStr()
    {
        return " (" + iItemName + ", " + iFP + ")";
    }

    private void readProperties(D2BitReader pFile, ArrayList pProperties)
    {
        //        System.err.println("readProperties of item: " + iItemName + " - " +
        // iFP );
        int lProp = (int) pFile.read(9);

        while (lProp != 511)
        {
            D2ItemProperty lProperty = new D2ItemProperty(lProp, iCharLvl, iItemName);
            pProperties.add( lProperty );
            int lRead[] = lProperty.getPropNrs();

            for (int i = 0; i < lRead.length; i++)
            {
                D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST.getRow(lRead[i]);

                String lItemStatCostList[];
                
                if ( lProp == 201 )
                {
                    // extra splitup
                    lItemStatCostList = new String[] { "6", "10", lItemStatCost.get("Save Bits") };
                }
                else if ( lProp == 204 )
                {
                    lItemStatCostList = new String[] { "6", "10", "8", "8" };
                }
                else
                {
                    lItemStatCostList = new String[] { lItemStatCost.get("Save Param Bits"), lItemStatCost.get("Save Bits") };
                }

                for (int k = 0; k < lItemStatCostList.length; k++)
                {
                    if (!("".equals(lItemStatCostList[k])))
                    {
                        int lBits = Integer.parseInt(lItemStatCostList[k]);
                        long lValue = pFile.read(lBits);
//                        System.err.println("Property " + lItemStatCost.get("Stat") + ": " + lValue + " - " + lItemStatCost.get("Save Add") + " - " + lBits);
                        String lSaveAdd = lItemStatCost.get("Save Add");
                        if (lSaveAdd != null && !"".equals(lSaveAdd))
                        {
                            try
                            {
                                long lLoadSubtract = Long.parseLong(lSaveAdd);
                                lValue -= lLoadSubtract;
                            }
                            catch (Exception pEx)
                            {
                                D2FileManager.displayErrorDialog(pEx);
                            }
                        }
                        lProperty.set(lRead[i], lItemStatCost, k, lValue);
                    }
                }
            }
            lProp = (int) pFile.read(9);
        }
    }
    
    public boolean isBodyLArm()
    {
        return isBodyLocation("larm");
    }

    public boolean isBodyRRin()
    {
        return isBodyLocation("rrin");
    }

    public boolean isBodyLRin()
    {
        return isBodyLocation("lrin");
    }
    
    public boolean isWeaponType(D2WeaponTypes pType)
    {
        if ( iTypeWeapon )
        {
            if ( pType.isType( iType ) )
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean isBodyLocation(D2BodyLocations pLocation)
    {
        if ( iBody )
        {
            if (pLocation.getLocation().equals(iBodyLoc1))
            {
                return true;
            }
            if (pLocation.getLocation().equals(iBodyLoc2))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isBodyLocation(String pLocation)
    {
        if (iBody)
        {
            if (pLocation.equals(iBodyLoc1))
            {
                return true;
            }
            if (pLocation.equals(iBodyLoc2))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isBelt()
    {
        return iBelt;
    }

    public boolean isCharm()
    {
        return (iSmallCharm || iLargeCharm || iGrandCharm);
    }

    public boolean isCharmSmall()
    {
        return iSmallCharm;
    }

    public boolean isCharmLarge()
    {
        return iLargeCharm;
    }

    public boolean isCharmGrand()
    {
        return iGrandCharm;
    }

    public boolean isJewel()
    {
        return iJewel;
    }

    // accessor for the row
    public short get_row()
    {
        return row;
    }

    // accessor for the column
    public short get_col()
    {
        return col;
    }

    // setter for the row
    // necessary for moving items
    public void set_row(short r)
    {
        iItem.set_byte_pos(7);
        iItem.skipBits(13);
        iItem.write((long) r, 4);
        row = r;
    }

    // setter for the column
    // necessary for moving items
    public void set_col(short c)
    {
        iItem.set_byte_pos(7);
        iItem.skipBits(9);
        iItem.write((long) c, 4);
        col = c;
    }

    public void set_location(short l)
    {
        iItem.set_byte_pos(7);
        iItem.skipBits(2);
        iItem.write((long) l, 3);
        location = l;
    }

    public void set_body_position(short bp)
    {
        iItem.set_byte_pos(7);
        iItem.skipBits(5);
        iItem.write((long) bp, 4);
        body_position = bp;
    }

    public void set_panel(short p)
    {
        iItem.set_byte_pos(7);
        iItem.skipBits(17);
        iItem.write((long) p, 3);
        panel = p;
    }

    public short get_location()
    {
        return location;
    }

    public short get_body_position()
    {
        return body_position;
    }

    public short get_panel()
    {
        return panel;
    }

    public short get_width()
    {
        return width;
    }

    public short get_height()
    {
        return height;
    }

    public String get_image()
    {
        return image_file;
    }

    public String get_name()
    {
        return name;
    }

    public long getSocketNrFilled()
    {
        return iSocketNrFilled;
    }

    public long getSocketNrTotal()
    {
        return iSocketNrFilled;
    }

    public byte[] get_bytes()
    {
        return iItem.getFileContent();
    }

    public int getItemLength()
    {
        return iItem.get_length();
    }

    public String getItemName()
    {
        return iItemName;
    }
    
    public String getName()
    {
        return iItemNameNoPersonalising;
    }

    public String getFingerprint()
    {
        return iFP;
    }

    public String getILvl()
    {
        return Short.toString(ilvl);
    }

    public String toString()
    {
        String lReturn = toStringHtml();

        lReturn = lReturn.replaceAll("<HTML>", "");
        lReturn = lReturn.replaceAll("<BR>", "\n");

        return lReturn;
    }

    private String getProperties(String pProps, ArrayList pProperties)
    {
        String lReturn = "";

        if (pProperties != null)
        {
            if ( pProps != null )
            {
                lReturn += "<BR>" + pProps;
            }
            for (int i = 0; i < pProperties.size(); i++)
            {
                D2ItemProperty lValue = (D2ItemProperty) pProperties.get(i);
                if ( !lValue.hasNoValue() )
                {
                    lReturn += "<BR>" + lValue.getValue();
                }
            }
        }

        return lReturn;
    }

    public String toStringHtml()
    {
        return "<HTML>" + toStringHtmlInternal();
    }

    protected String toStringHtmlInternal()
    {
        String lReturn = iItemName;

        if ( iReqLvl > 0 )
        {
            lReturn += "<BR>Required Level: " + iReqLvl;
        }
        if ( iReqStr > 0 )
        {
            lReturn += "<BR>Required Strength: " + iReqStr;
        }
        if ( iReqDex > 0 )
        {
            lReturn += "<BR>Required Dexterity: " + iReqDex;
        }
        
        if (iFP != null)
        {
            lReturn += "<BR>FP: " + iFP;
        }

        if (ilvl != 0)
        {
            lReturn += "<BR>iLvl: " + ilvl;
        }

        lReturn += getProperties("Properties: ", iProperties);
        lReturn += getProperties("Set (1 item): ", iSet1);
        lReturn += getProperties("Set (2 items): ", iSet2);
        lReturn += getProperties("Set (3 items): ", iSet3);
        lReturn += getProperties("Set (4 items): ", iSet4);
        lReturn += getProperties("Set (5 items): ", iSet5);
        
        if ( iEthereal )
        {
            lReturn += "<BR>Ethereal";
        }
        if ( iSocketNrTotal > 0 )
        {
            lReturn += "<BR>" + iSocketNrTotal + " Sockets (" + iSocketNrFilled + " used)" ;
            if ( iSocketedItems != null )
            {
	            for ( int i = 0 ; i < iSocketedItems.size() ; i++ )
	            {
	                D2Item lSocket = ((D2Item) iSocketedItems.get(i));
	                lReturn += "<BR>Socketed: " + lSocket.getItemName();
	                if ( lSocket.isJewel() )
	                {
	                    lReturn += lSocket.getProperties(null, lSocket.iProperties);
	                }
	            }
            }
        }

        return lReturn;
    }
    
    //	public boolean isCharacter()
    //	{
    //	    return iCharacter;
    //	}
    
    public boolean isUnique()
    {
        return iUnique;
    }

    public boolean isSet()
    {
        return iSet;
    }

    public boolean isRuneWord()
    {
        return iRuneWord;
    }

    public boolean isCrafted()
    {
        return iCrafted;
    }

    public boolean isRare()
    {
        return iRare;
    }

    public boolean isMagical()
    {
        return iMagical;
    }

    public boolean isNormal()
    {
        return !(iMagical || iRare || iCrafted || iRuneWord || isRune() || iSet || iUnique);
    }

    public boolean isSocketFiller()
    {
        return isRune() || isJewel() || isGem();
    }

    public boolean isGem()
    {
        return iGem;
    }

    public boolean isRune()
    {
        return getRuneCode() != null;
    }

    public String getRuneCode()
    {
        if (iItemType != null)
        {
            if ("rune".equals(iItemType.get("type")))
            {
                return iItemType.get("code");
            }
        }
        return null;
    }

    public boolean isEthereal()
    {
        return iEthereal;
    }

    public boolean isSocketed()
    {
        return iSocketed;
    }

    public boolean isTypeMisc()
    {
        return iTypeMisc;
    }

    public boolean isTypeArmor()
    {
        return iTypeArmor;
    }

    public boolean isTypeWeapon()
    {
        return iTypeWeapon;
    }
    
    public void setCharLvl(long pCharLvl)
    {
        iCharLvl = pCharLvl;
        setCharLvl(iProperties, pCharLvl);
        setCharLvl(iSet1, pCharLvl);
        setCharLvl(iSet2, pCharLvl);
        setCharLvl(iSet3, pCharLvl);
        setCharLvl(iSet4, pCharLvl);
        setCharLvl(iSet5, pCharLvl);
    }
    
    private void setCharLvl(ArrayList pProperties, long pCharLvl)
    {
        if ( pProperties != null )
        {
            for ( int i = 0 ; i < pProperties.size() ; i++ )
            {
                ((D2ItemProperty) pProperties.get(i)).setCharLvl(pCharLvl);
            }
        }
    }

    public int compareTo(Object pObject)
    {
        if (pObject instanceof D2Item)
        {
            String lItemName = ((D2Item) pObject).iItemName;
            if (iItemName == lItemName)
            {
                // also both "null"
                return 0;
            }
            if (iItemName == null)
            {
                return -1;
            }
            if (lItemName == null)
            {
                return 1;
            }
            return iItemName.compareTo(lItemName);
        }
        return -1;
    }
    
}

