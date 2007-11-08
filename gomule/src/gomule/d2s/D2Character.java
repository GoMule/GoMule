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

import gomule.gui.*;
import gomule.item.*;
import gomule.util.*;

import java.awt.Point;
import java.io.*;
import java.util.*;

import randall.d2files.*;

// a character class
// manages one character file
// stores a filename, a bitreader
// to read from that file, and
// a vector of items
public class D2Character extends D2ItemListAdapter
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

//    private String          iFileName;
    private D2BitReader     iReader;
    private ArrayList       iCharItems;
    private D2Item			iCharCursorItem;
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

    private String			iCharName;
    private String          iTitleString;
    private long			iCharLevel;
    private ArrayList			iCharSkillsA = new ArrayList();
    private ArrayList			iCharSkillsB = new ArrayList();
    private ArrayList			iCharSkillsC = new ArrayList();
    private Point[] iSkillLocs = new Point[30];
    
    //16 is DEF
    //17 AR
    //18 FR
    ///19 LR
    //20 CR
    //21 PR
    //22 MF
    //23 F/RW
    private int				iInitStats[] = new int[24];
    private int				iCharStats[] = new int[24];
    private int				iGF; 
    private int				iIF;
	private String iMercName = " ";
	private String iMercRace = " ";
	private String iMercType = " ";
	private int iMercLevel;
	private long iMercExp;
	private boolean iMercDead = false; 
	private int iMercInitStr;
	private int iMercInitDex;
	private int iMercInitHP;
	private long iMercInitDef;
	private int iMercInitFireRes;
	private int iMercInitColdRes;
	private int iMercInitLightRes;
	private int iMercInitPoisRes;
	private int iMercStr;
	private int iMercDex;
	private int iMercHP;
	private long iMercDef;
	private int testCounter = 0;
	private boolean fullChanged = false;
	private int iMercAR;
	private int iMercFireRes;
	private int iMercColdRes;
	private int iMercPoisRes;
	private int iMercLightRes;
	public int[] mercStatArray = new int[338];
	public int[] charStatArray = new int[340];
	private ArrayList partialSetProps = new ArrayList();
	private ArrayList fullSetProps = new ArrayList();
	private int iCurWepSlot = 1;
	
	private int iMercInitAR;
	private String iCharClass;
	private ArrayList equippedSetItems = new ArrayList();
	private int[] iReadStats = new int[16];
	private long lCharCode;
    
    public D2Character(String pFileName) throws Exception
    {
        super( pFileName );
        if ( iFileName == null || !iFileName.toLowerCase().endsWith(".d2s") )
        {
            throw new Exception("Incorrect Character file name");
        }
        iCharItems = new ArrayList();
        iMercItems = new ArrayList();
        iReader = new D2BitReader(iFileName);
        readChar();
        // clear status
        setModified(false);
    }
    
    public String getFilename()
    {
        return iFileName;
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
    
    public int getNrItems()
    {
        return iCharItems.size() + iMercItems.size();
    }
    
    public boolean itemContainsProp(int pProp, D2Item pItem){
    	
//    	if(pItem.get)
    	
    	return true;
    	
    }
    
    public boolean containsItem(D2Item pItem)
    {
        if ( iCharItems.contains(pItem) )
        {
            return true;
        }
        if ( iMercItems.contains(pItem) )
        {
            return true;
        }
        return false;
    }
    
    public void removeItem(D2Item pItem)
    {
        if ( iCharItems.remove(pItem) )
        {
            unmarkCharGrid(pItem);
        }
        else
        {
            unmarkMercGrid(pItem);
            iMercItems.remove(pItem);
        }
        setModified(true);
    }

    private void readChar() throws Exception
    {
        iReader.set_byte_pos(4);
        long lVersion = iReader.read(32);
                System.err.println("Version: " + lVersion );
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
        iCharName = lCharName.toString();
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
        lCharCode = iReader.read(8);
        //        System.err.println("Char Class: " + getCharacterCode((int) lCharCode)
        // );

        iReader.set_byte_pos(43);
        iCharLevel = iReader.read(8);
        //        System.err.println("Char Level: " + lCharLevel );
        if ( iCharLevel < 1 || iCharLevel > 99 )
        {
            throw new Exception("Invalid char level: " + iCharLevel + " (should be between 1-99)");
        }
        iCharClass =  getCharacterCode((int) lCharCode);
        iTitleString = " Lvl " + iCharLevel + " " + getCharacterCode((int) lCharCode);

//        System.err.println("Test Woo: " + iReader.findNextFlag("Woo!", 0) );
//        System.err.println("Test W4: " + iReader.findNextFlag("W4", 0) );
        iReader.set_byte_pos(177);
        //MERC INFO: 0 IF HAS EVER HAD A MERC
//        System.out.println(iReader.read(32));
        if(iReader.read(8) == 1){
        	iMercDead = true;
        }
        
        iReader.skipBits(8);
        
        if(iReader.read(32) != 0){
        	iHasMerc = true;
        	iReader.skipBits(16);
        	setMercType(iReader.read(16));
        	iReader.skipBits(-32);
        	setMercName(iReader.read(16));
        	iReader.skipBits(16);
        	iMercExp = iReader.read(32);
        	setMercLevel();
//        	System.out.println(iMercExp);
        	generateMercStats();
        }else{
        	iReader.skipBits(64);
        }
        

        
        int lWoo = iReader.findNextFlag("Woo!", 0);
        if ( lWoo == -1 )
        {
            throw new Exception("Error: Act Quests block not found");
        }
        if ( lWoo != 335 )
        {
            System.err.println("Warning: Act Quests block not on expected position");
        }

        int lW4 = iReader.findNextFlag("w4", lWoo);
        if ( lW4 == -1 )
        {
            throw new Exception("Error: NPC State control block not found");
        }
        if ( lW4 != 714 )
        {
            System.err.println("Warning: NPC State control block not on expected position");
        }
        
        // For the stats, determine the block
        iGF = iReader.findNextFlag("gf", lW4);
        if ( iGF == -1 )
        {
            throw new Exception("Error: Stats block not found");
        }
        if ( iGF != 765 )
        {
            System.err.println("Warning: Stats block not on expected position");
        }
        
        iIF = iReader.findNextFlag("if", iGF);
        if ( iIF == -1 )
        {
            throw new Exception("Error: Skills block not found");
        }
        
//        System.err.println("Test: " + lWoo + " - " + lW4 + " - " + iGF + " - " + iIF );
        
        if ( iIF < iGF )
        {
            throw new Exception("Error: Stats / Skills not correct");
        }

        // now copy the block into the Flavie bitreader 
        // (it can read integers unaligned to bytes which is needed here) 
        iReader.set_byte_pos(iGF);
        byte lInitialBytes[] = iReader.get_bytes(iIF - iGF);
//        System.out.println(iIF - iGF);
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
                iReadStats[lID] = lValue;
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
        
        
        readSkills();
        
        
        
//        System.out.println(iReader.getByte());
       
        
        iStashGrid = new boolean[8][6];
        iInventoryGrid = new boolean[4][10];
        iBeltGrid = new boolean[4][4];
        iCubeGrid = new boolean[4][3];
        iEquipped = new boolean[13];
        iMerc = new boolean[13];
        clearGrid();
        readItems( iIF );
        
//        for(int x = 0;x<iCharItems.size();x=x+1){
//        	if(((D2Item)iCharItems.get(x)).isEquipped() && ((D2Item)iCharItems.get(x)).isSet()){
//            		equippedSetItems.add(iCharItems.get(x));
//        	}
//            }
//        
//        if(equippedSetItems.size() > 0){
//        setSetProps();
//        }
        
        for(int x = 0;x<iMercItems.size();x=x+1){
    	updateMercStats("D", (D2Item)iMercItems.get(x));
        }
        
        generateCharStats();
        
//    	System.out.println(weaponSlot);
//    	
//    	if(weaponSlot == 1){
//    	
//    	if(pItem.get_body_position() == 4 || pItem.get_body_position() == 5 ){
//    		System.out.println(pItem.getName());
//    	}
//    	}else{
//        	if(pItem.get_body_position() == 11 || pItem.get_body_position() == 12 ){
//        		System.out.println(pItem.getName());
//        	}
//    	}
        
        for(int x = 0;x<iCharItems.size();x=x+1){
        	if(((D2Item)iCharItems.get(x)).isEquipped()){

//        		if(((D2Item)iCharItems.get(x)).isSet()){
//            		System.out.print("NAME: " + ((D2Item)iCharItems.get(x)).getName());
//            		System.out.println(" ID: " + ((D2Item)iCharItems.get(x)).getSetID());
//            		equippedSetItems.add(iCharItems.get(x));
//        		}
//        		System.out.println(((D2Item)iCharItems.get(x)).getName()+" , " + ((D2Item)iCharItems.get(x)).get_panel() + " , " + ((D2Item)iCharItems.get(x)).get_row()+" , " + ((D2Item)iCharItems.get(x)).get_col());
        		if(((D2Item)iCharItems.get(x)).get_body_position() != 11 &&((D2Item)iCharItems.get(x)).get_body_position() != 12 ){
//        			if(pItem.get_body_position() == 4 || pItem.get_body_position() == 5 ){
        			
        			
        	updateCharStats("D", (D2Item)iCharItems.get(x));
        		}
        	}
            }

    }
    
    private void readSkills() {
		
    	String cClass = "";
    	
    	switch((int)lCharCode){
    	

        case 0:
             cClass = "ama";
             
             break;
        case 1:
        	cClass = "sor";
        	break;
        case 2:
        	cClass = "nec";
        	break;
        case 3:
        	cClass = "pal";
        	break;
        case 4:
        	cClass = "bar";
        	break;
        case 5:
        	cClass = "dru";
        	break;
        case 6:
        	cClass = "ass";
        	break;

    	
    	}
    	
//    	System.out.println(cClass);
    	generateSkillLocs();
    	D2TxtFileItemProperties initRow = D2TxtFile.SKILLS.searchColumns("charclass", cClass);
    	System.out.println(initRow.get("skill"));
    	
    	iReader.set_byte_pos(iIF);
      byte skillInitialBytes[] = iReader.get_bytes(32);
      D2FileReader skillReader = new D2FileReader(skillInitialBytes);
     skillReader.getCounterInt(8);
     skillReader.getCounterInt(8);
     int tree = 0;
     for(int x =0;x<30;x=x+1){
    	 tree = Integer.parseInt((D2TxtFile.SKILL_DESC.searchColumns("skilldesc", D2TxtFile.SKILLS.getRow(initRow.getRowNum() + x).get("skilldesc"))).get("SkillPage"));
//    	 System.out.println(tree);
    	 switch (tree){
    	 
    	 case 1:
    		 iCharSkillsA.add(new Integer(skillReader.getCounterInt(8)));
//    		 iCharSkillsA.add(new Integer(20));

    		 break;
    		 
    	 case 2:
    		 iCharSkillsB.add(new Integer(skillReader.getCounterInt(8)));
//    		 iCharSkillsB.add(new Integer(20));
    		 break;
    		 
    	 case 3:
    		 iCharSkillsC.add(new Integer(skillReader.getCounterInt(8)));
//    		 iCharSkillsC.add(new Integer(20));
    		 break;
    	 
    	 }
    	 
//      System.out.println(skillReader.getCounterInt(8) + " , " + tree);
     }
     	
		
	}

	private void generateSkillLocs() {
		
    	switch((int)lCharCode){
    	

        case 0:
//          cClass = "ama";
            iSkillLocs[0] = new Point(112,64);
            iSkillLocs[1] = new Point(173,64); 
            iSkillLocs[2] = new Point(50,125); 
            iSkillLocs[3] = new Point(112,125); 
            iSkillLocs[4] = new Point(173,187); 
            iSkillLocs[5] = new Point(50,248); 
            iSkillLocs[6] = new Point(112,248); 
            iSkillLocs[7] = new Point(112,307); 
            iSkillLocs[8] = new Point(173,307); 
            iSkillLocs[9] = new Point(50,370);
            
            iSkillLocs[10] = new Point(50,64); 
            iSkillLocs[11] = new Point(173,64); 
            iSkillLocs[12] = new Point(112,125); 
            iSkillLocs[13] = new Point(50,187); 
            iSkillLocs[14] = new Point(112,187); 
            iSkillLocs[15] = new Point(173,248); 
            iSkillLocs[16] = new Point(50,307); 
            iSkillLocs[17] = new Point(112,307); 
            iSkillLocs[18] = new Point(50,370); 
            iSkillLocs[19] = new Point(173,370); 
              
            iSkillLocs[20] = new Point(50,64); 
            iSkillLocs[21] = new Point(112,125); 
            iSkillLocs[22] = new Point(173,125); 
            iSkillLocs[23] = new Point(50,187); 
            iSkillLocs[24] = new Point(173,187); 
            iSkillLocs[25] = new Point(112,248); 
            iSkillLocs[26] = new Point(173,248); 
            iSkillLocs[27] = new Point(50,307); 
            iSkillLocs[28] = new Point(112,370); 
            iSkillLocs[29] = new Point(173,370); 
             break;
        case 1:
//        	cClass = "sor";
            iSkillLocs[0] = new Point(112,64);
            iSkillLocs[1] = new Point(173,64); 
            iSkillLocs[2] = new Point(50,125); 
            iSkillLocs[3] = new Point(50,187);
            iSkillLocs[4] = new Point(112,187);
            iSkillLocs[5] = new Point(50,248); 
            iSkillLocs[6] = new Point(173,248); 
            iSkillLocs[7] = new Point(112,307); 
            iSkillLocs[8] = new Point(112,370); 
            iSkillLocs[9] = new Point(173,370); 
            
            iSkillLocs[10] = new Point(112,64); 
            iSkillLocs[11] = new Point(50,125); 
            iSkillLocs[12] = new Point(173,125); 
            iSkillLocs[13] = new Point(50,187); 
            iSkillLocs[14] = new Point(112,187); 
            iSkillLocs[15] = new Point(112,248); 
            iSkillLocs[16] = new Point(173,248); 
            iSkillLocs[17] = new Point(50,307); 
            iSkillLocs[18] = new Point(173,307); 
            iSkillLocs[19] = new Point(112,370); 
              
            iSkillLocs[20] = new Point(112,64); 
            iSkillLocs[21] = new Point(173,64); 
            iSkillLocs[22] = new Point(50,125); 
            iSkillLocs[23] = new Point(112,125); 
            iSkillLocs[24] = new Point(173,187); 
            iSkillLocs[25] = new Point(112,248); 
            iSkillLocs[26] = new Point(50,307); 
            iSkillLocs[27] = new Point(173,307); 
            iSkillLocs[28] = new Point(50,370); 
            iSkillLocs[29] = new Point(112,370); 
        	break;
        case 2:
//        	cClass = "nec";
            iSkillLocs[0] = new Point(112,64);
            iSkillLocs[1] = new Point(50,125); 
            iSkillLocs[2] = new Point(173,125); 
            iSkillLocs[3] = new Point(112,187);
            iSkillLocs[4] = new Point(173,187);
            iSkillLocs[5] = new Point(50,248); 
            iSkillLocs[6] = new Point(112,248); 
            iSkillLocs[7] = new Point(50,307); 
            iSkillLocs[8] = new Point(173,307); 
            iSkillLocs[9] = new Point(112,370); 
            
            iSkillLocs[10] = new Point(112,64); 
            iSkillLocs[11] = new Point(173,64); 
            iSkillLocs[12] = new Point(50,125); 
            iSkillLocs[13] = new Point(112,125); 
            iSkillLocs[14] = new Point(173,187); 
            iSkillLocs[15] = new Point(50,248); 
            iSkillLocs[16] = new Point(112,248); 
            iSkillLocs[17] = new Point(173,307); 
            iSkillLocs[18] = new Point(50,370); 
            iSkillLocs[19] = new Point(112,370); 
              
            iSkillLocs[20] = new Point(50,64); 
            iSkillLocs[21] = new Point(173,64); 
            iSkillLocs[22] = new Point(112,125); 
            iSkillLocs[23] = new Point(50,187); 
            iSkillLocs[24] = new Point(173,187); 
            iSkillLocs[25] = new Point(112,248); 
            iSkillLocs[26] = new Point(50,307); 
            iSkillLocs[27] = new Point(112,307); 
            iSkillLocs[28] = new Point(112,370); 
            iSkillLocs[29] = new Point(173,370); 
        	break;
        case 3:
//        	cClass = "pal";
            iSkillLocs[0] = new Point(50,64);
            iSkillLocs[1] = new Point(173,64); 
            iSkillLocs[2] = new Point(112,125); 
            iSkillLocs[3] = new Point(50,187);
            iSkillLocs[4] = new Point(173,187);
            iSkillLocs[5] = new Point(50,248); 
            iSkillLocs[6] = new Point(112,248); 
            iSkillLocs[7] = new Point(50,307); 
            iSkillLocs[8] = new Point(173,307); 
            iSkillLocs[9] = new Point(112,370); 
            
            iSkillLocs[10] = new Point(50,64); 
            iSkillLocs[11] = new Point(112,125); 
            iSkillLocs[12] = new Point(173,125); 
            iSkillLocs[13] = new Point(50,187); 
            iSkillLocs[14] = new Point(50,248); 
            iSkillLocs[15] = new Point(112,248); 
            iSkillLocs[16] = new Point(112,307); 
            iSkillLocs[17] = new Point(173,307); 
            iSkillLocs[18] = new Point(50,370); 
            iSkillLocs[19] = new Point(173,370); 
              
            iSkillLocs[20] = new Point(50,64); 
            iSkillLocs[21] = new Point(173,64); 
            iSkillLocs[22] = new Point(112,125); 
            iSkillLocs[23] = new Point(173,125); 
            iSkillLocs[24] = new Point(50,187); 
            iSkillLocs[25] = new Point(173,187); 
            iSkillLocs[26] = new Point(112,248); 
            iSkillLocs[27] = new Point(50,307); 
            iSkillLocs[28] = new Point(112,370); 
            iSkillLocs[29] = new Point(173,370); 
        	break;
        case 4:
//        	cClass = "bar";
            iSkillLocs[0] = new Point(112,64);
            iSkillLocs[1] = new Point(50,125); 
            iSkillLocs[2] = new Point(173,125); 
            iSkillLocs[3] = new Point(112,187);
            iSkillLocs[4] = new Point(173,187);
            iSkillLocs[5] = new Point(50,248); 
            iSkillLocs[6] = new Point(112,248); 
            iSkillLocs[7] = new Point(173,307); 
            iSkillLocs[8] = new Point(50,370); 
            iSkillLocs[9] = new Point(112,370); 
            
            iSkillLocs[10] = new Point(50,64); 
            iSkillLocs[11] = new Point(112,64); 
            iSkillLocs[12] = new Point(173,64); 
            iSkillLocs[13] = new Point(50,125); 
            iSkillLocs[14] = new Point(112,125); 
            iSkillLocs[15] = new Point(173,125); 
            iSkillLocs[16] = new Point(50,187); 
            iSkillLocs[17] = new Point(173,248); 
            iSkillLocs[18] = new Point(50,307); 
            iSkillLocs[19] = new Point(173,370); 
              
            iSkillLocs[20] = new Point(50,64); 
            iSkillLocs[21] = new Point(173,64); 
            iSkillLocs[22] = new Point(50,125); 
            iSkillLocs[23] = new Point(112,125); 
            iSkillLocs[24] = new Point(173,187); 
            iSkillLocs[25] = new Point(50,248); 
            iSkillLocs[26] = new Point(112,307); 
            iSkillLocs[27] = new Point(173,307); 
            iSkillLocs[28] = new Point(50,370); 
            iSkillLocs[29] = new Point(112,370); 
        	break;
        case 5:
//        	cClass = "dru";
            iSkillLocs[0] = new Point(112,64);
            iSkillLocs[1] = new Point(173,64); 
            iSkillLocs[2] = new Point(50,125); 
            iSkillLocs[3] = new Point(112,125);
            iSkillLocs[4] = new Point(173,187);
            iSkillLocs[5] = new Point(50,248); 
            iSkillLocs[6] = new Point(112,248); 
            iSkillLocs[7] = new Point(173,307); 
            iSkillLocs[8] = new Point(50,370); 
            iSkillLocs[9] = new Point(112,370); 
            
            iSkillLocs[10] = new Point(50,64); 
            iSkillLocs[11] = new Point(112,64); 
            iSkillLocs[12] = new Point(173,125); 
            iSkillLocs[13] = new Point(50,187); 
            iSkillLocs[14] = new Point(173,187); 
            iSkillLocs[15] = new Point(50,248); 
            iSkillLocs[16] = new Point(112,248); 
            iSkillLocs[17] = new Point(112,307); 
            iSkillLocs[18] = new Point(173,307); 
            iSkillLocs[19] = new Point(50,370); 
              
            iSkillLocs[20] = new Point(50,64); 
            iSkillLocs[21] = new Point(50,125); 
            iSkillLocs[22] = new Point(173,125); 
            iSkillLocs[23] = new Point(50,187); 
            iSkillLocs[24] = new Point(173,187); 
            iSkillLocs[25] = new Point(112,248); 
            iSkillLocs[26] = new Point(50,307); 
            iSkillLocs[27] = new Point(112,307); 
            iSkillLocs[28] = new Point(50,370); 
            iSkillLocs[29] = new Point(112,370); 
        	break;
        case 6:
//        	cClass = "ass";
            iSkillLocs[0] = new Point(112,64);
            iSkillLocs[1] = new Point(50,125); 
            iSkillLocs[2] = new Point(173,125); 
            iSkillLocs[3] = new Point(50,187);
            iSkillLocs[4] = new Point(112,187);
            iSkillLocs[5] = new Point(173,248); 
            iSkillLocs[6] = new Point(50,307); 
            iSkillLocs[7] = new Point(112,307); 
            iSkillLocs[8] = new Point(50,370); 
            iSkillLocs[9] = new Point(173,370); 
            
            iSkillLocs[10] = new Point(112,64); 
            iSkillLocs[11] = new Point(173,64); 
            iSkillLocs[12] = new Point(50,125); 
            iSkillLocs[13] = new Point(112,187); 
            iSkillLocs[14] = new Point(173,187); 
            iSkillLocs[15] = new Point(50,248); 
            iSkillLocs[16] = new Point(112,248); 
            iSkillLocs[17] = new Point(173,307); 
            iSkillLocs[18] = new Point(50,370); 
            iSkillLocs[19] = new Point(112,370); 
              
            iSkillLocs[20] = new Point(112,64); 
            iSkillLocs[21] = new Point(173,64); 
            iSkillLocs[22] = new Point(50,125); 
            iSkillLocs[23] = new Point(173,125); 
            iSkillLocs[24] = new Point(112,187); 
            iSkillLocs[25] = new Point(50,248); 
            iSkillLocs[26] = new Point(173,248); 
            iSkillLocs[27] = new Point(50,307); 
            iSkillLocs[28] = new Point(173,307); 
            iSkillLocs[29] = new Point(112,370); 
        	break;

    	
    	}
		
	}

	private void setSetProps() {
		
    	    	
    	int counter = 0;
    	ArrayList itemsToMod = new ArrayList();
    	ArrayList equippedSetItems = new ArrayList(); 
    	equippedSetItems.addAll(this.equippedSetItems);
    	partialSetProps.clear();
    	fullSetProps.clear();
    	for(int x = 0;x<equippedSetItems.size();x=x+1){
    		 D2Item thisSetItem = ((D2Item)equippedSetItems.get(x));

    		 itemsToMod.clear();
    		 counter = 1;
    		 itemsToMod.add(thisSetItem);
    		 equippedSetItems.remove(x);
    		 x=x-1;
    		 for(int y = x+1;y<equippedSetItems.size();y=y+1){
    			 if(!((D2Item)equippedSetItems.get(y)).equals(thisSetItem)){
    				 if(((D2Item)equippedSetItems.get(y)).getSetName().equals(thisSetItem.getSetName())){
    					 counter = counter +1;
    					 itemsToMod.add((D2Item)equippedSetItems.get(y));
    		    		 equippedSetItems.remove(y);
    		    		 y=y-1;
    				 }
    			 }
    		 }
    		for(int z = 0;z<itemsToMod.size();z=z+1){
    			((D2Item)itemsToMod.get(z)).setSetProps(counter);
    		}
    		
    		if(itemsToMod.size() > 0 ){
    			
//    			System.out.println(thisSetItem.getName());
    			if(counter == thisSetItem.getSetSize()){
    				setFullSet(thisSetItem);
    			}
    			
    			setPartialSet(counter, thisSetItem);
    			
    		}
    		
//    		if(counter == thisSetItem.getFullSetNum()){
//    			BLABLALBLALBLBLDgioklao;sidhFU
//    		}
    	 }
    	
    	
    	
    	
	}

	private void setPartialSet(int counter, D2Item thisSetItem) {
		
		if(counter > 5){
			counter = 5;
		}
		
		D2TxtFileItemProperties setRow = D2TxtFile.FULLSET.searchColumns("name", thisSetItem.getSetName());
		
		int replaceChar = 2;
		
		String[] replaceStr = new String[4];
		String codeStr;
		while(replaceChar < counter +1){
			
			replaceStr[0] = "PCode" + replaceChar + "a";
			replaceStr[1] = "PParam" + replaceChar + "a";
			replaceStr[2] = "PMin" + replaceChar + "a";
			replaceStr[3] = "PMax" + replaceChar + "a";
			 codeStr = setRow.get(replaceStr[0]);
			if(codeStr.equals("")){
				break;
			}
			//System.out.println((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0]))).get(pKey));
			
			String[] propStats = {
					(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat1"))).get("ID")//,
				//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat2"))).get("ID"),
				//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat3"))).get("ID"),
				//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat4"))).get("ID")
					};

				

				
				D2ItemProperty lProperty = new D2ItemProperty(Integer.parseInt(propStats[0]), (long)thisSetItem.getReqLvl(),
						thisSetItem.getName());
				
				D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST
				.getRow(lProperty.getPropNrs()[0]);
				
				lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long.parseLong(setRow.get(replaceStr[2])));
				
				
				if( setRow.get(replaceStr[0]).equals("res-all")){
					lProperty = new D2ItemProperty(1337,
							(long)thisSetItem.getReqLvl(), thisSetItem.getName());
					lProperty.set(1337, lItemStatCost, 0, Long
							.parseLong(setRow.get(replaceStr[3])));
				}
				if (! setRow.get(replaceStr[3]).equals("")) {
					lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long
							.parseLong( setRow.get(replaceStr[3])));
				}

				if (! setRow.get(replaceStr[1]).equals("")) {

					lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long
							.parseLong( setRow.get(replaceStr[1])));
				}
				
				
				partialSetProps.add(lProperty);
				
//				System.out.println(lProperty.getValue());
				
			replaceChar = replaceChar +1;
		}
		
	}

	private void setFullSet(D2Item thisSetItem) {
		

D2TxtFileItemProperties setRow = D2TxtFile.FULLSET.searchColumns("name", thisSetItem.getSetName());
		
		int replaceChar = 1;
		
		String[] replaceStr = new String[4];
		String codeStr = "";
		
		while(replaceStr.length == 4){
			
			replaceStr[0] = "FCode" + replaceChar;
			replaceStr[1] = "FParam" + replaceChar;
			replaceStr[2] = "FMin" + replaceChar;
			replaceStr[3] = "FMax" + replaceChar;
			codeStr= setRow.get(replaceStr[0]);
			
			if(codeStr.equals("") || codeStr.equals("lifesteal")){
				break;
			}
			 
//			 System.out.println(codeStr);
			
			//System.out.println((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0]))).get(pKey));
			
			String[] propStats = {
					(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat1"))).get("ID")//,
				//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat2"))).get("ID"),
				//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat3"))).get("ID"),
				//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat4"))).get("ID")
					};

				

				
				D2ItemProperty lProperty = new D2ItemProperty(Integer.parseInt(propStats[0]), (long)thisSetItem.getReqLvl(),
						thisSetItem.getName());
				
				D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST
				.getRow(lProperty.getPropNrs()[0]);
				
				lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long.parseLong(setRow.get(replaceStr[2])));
				
				
				if( setRow.get(replaceStr[0]).equals("res-all")){
					lProperty = new D2ItemProperty(1337,
							(long)thisSetItem.getReqLvl(), thisSetItem.getName());
					lProperty.set(1337, lItemStatCost, 0, Long
							.parseLong(setRow.get(replaceStr[3])));
				}
				if (! setRow.get(replaceStr[3]).equals("")) {
					lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long
							.parseLong( setRow.get(replaceStr[3])));
				}

				if (! setRow.get(replaceStr[1]).equals("") && !setRow.get(replaceStr[1]).equals("fullsetgeneric")&& !setRow.get(replaceStr[1]).equals("monsterset") && !setRow.get(replaceStr[1]).equals("Fire Mastery")) {
					lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long  

							.parseLong( setRow.get(replaceStr[1])));
				}
				
				
				fullSetProps.add(lProperty);
				
//				System.out.println("FULL: " + lProperty.getValue());
				
			replaceChar = replaceChar +1;
			
		}
		
	}

	private void generateCharStats(){
    	
    	
    	
    	

    	
    	
    	
    	//NEED TO GENERATE 
        //16 is DEF
        //17 AR
        //18 FR
        ///19 CR
        //20 LR
        //21 PR
    	
    	//DEAL WITH XP (?)
    	//DEAL WITH LIFE AND MANA AND STAMINA

    	
    	
    	iInitStats[16] = (int)(Math.floor((double)iInitStats[2]/(double)4));
    	iInitStats[17] = ((iInitStats[2] * 5) - 35) + getARClassBonus(); 
    	
    	
    	
    	for(int i = 0;i<iInitStats.length;i=i+1){
    		iCharStats[i] = iInitStats[i];
    	}
    	
    	int[] manaHpStam = setHPManaStam();
    	iInitStats[7] =manaHpStam[0];
    	iInitStats[9] = manaHpStam[1];
    	iInitStats[11] = manaHpStam[2];
    	
    }
    
    public int getARClassBonus(){
    	if(getCharClass().equals("Barbarian") || getCharClass().equals("Paladin")){
    		return 20;
    	}else if(getCharClass().equals("Assasin")){
    		return 15;
    	}else if(getCharClass().equals("Amazon")|| getCharClass().equals("Druid")){
    		return 5;
    	}else if(getCharClass().equals("Necromancer")){
    		return -10;
    	}else if(getCharClass().equals("Sorceress")){
    		return -15;
    	}else{
    		return 99999999;
    	}
    }
    
    public int[] setHPManaStam(){
    	
    	//HEALTH - MANA - STAM
    	int[] out = new int[3];
    	
    	
    	if(getCharClass().equals("Barbarian")){
    		out[0] = (4*(iCharStats[3]-25))+(2*(int)(iCharLevel-1)) + 55;
    		out[2] = (1*(iCharStats[3]-25))+(1*(int)(iCharLevel-1)) + 92;
    		out[1] = (1*(iCharStats[1]-10))+(1*(int)(iCharLevel-1)) + 10;
    	}else if(getCharClass().equals("Paladin")){
    		out[0] = (3*(iCharStats[3]-25))+(2*(int)(iCharLevel-1)) + 55;
    		out[2] = (1*(iCharStats[3]-25))+(1*(int)(iCharLevel-1)) + 89;
    		out[1] = ((int)(Math.floor(1.5*(iCharStats[1]-15))))+((int)(Math.floor(1.5*(int)(iCharLevel-1)))) + 15;
    	}else if(getCharClass().equals("Assasin")){
    		out[0] = (3*(iCharStats[3]-20))+(2*(int)(iCharLevel-1)) + 50;
    		out[2] = ((int)Math.floor(1.5*(iCharStats[3]-20)))+(1*(int)(iCharLevel-1)) + 95;
    		out[1] = ((int)Math.floor(1.5*(iCharStats[1]-25)))+((int)Math.floor(1.5*(int)(iCharLevel-1))) + 25;
    	}else if(getCharClass().equals("Amazon")){
    		out[0] = (3*(iCharStats[3]-20))+(2*(int)(iCharLevel-1)) + 50;
    		out[2] = (1*(iCharStats[3]-20))+(1*(int)(iCharLevel-1)) + 84;
    		out[1] = ((int)Math.floor(1.5*(iCharStats[1]-15)))+((int)Math.floor(1.5*(int)(iCharLevel-1))) + 15;
    	}else if(getCharClass().equals("Druid")){
    		out[0] = (2*(iCharStats[3]-25))+((int)(Math.floor(1.5*(int)(iCharLevel-1)))) + 55;
    		out[2] = (1*(iCharStats[3]-25))+(1*(int)(iCharLevel-1)) + 84;
    		out[1] = (2*(iCharStats[1]-20))+(2*(int)(iCharLevel-1)) + 20;
    	}else if(getCharClass().equals("Necromancer")){
    		out[0] = (2*(iCharStats[3]-15))+((int)Math.floor(1.5*(int)(iCharLevel-1))) + 45;
    		out[2] = (1*(iCharStats[3]-15))+(1*(int)(iCharLevel-1)) + 79;
    		out[1] = (2*(iCharStats[1]-25))+(2*(int)(iCharLevel-1)) + 25;
    	}else if(getCharClass().equals("Sorceress")){
    		
    		out[0] = (2*(iCharStats[3]-10))+(1*(int)(iCharLevel-1)) + 40;
    		out[2] = (1*(iCharStats[3]-10))+(1*(int)(iCharLevel-1)) + 74;
    		out[1] = (2*(iCharStats[1]-35))+(2*(int)(iCharLevel-1)) + 35;
    	}
    	
    	return out;
    	
    }
    
    private void generateMercStats() {
		
    	D2TxtFileItemProperties hireCol = null;
    	ArrayList hireArr = D2TxtFile.HIRE.searchColumnsMultipleHits("SubType", iMercType);
    	
    	for(int x = 0;x<hireArr.size();x =x+1){
//			System.out.println(Integer.parseInt(((D2TxtFileItemProperties)hireArr.get(x)).get("Level")));
    		if(((D2TxtFileItemProperties)hireArr.get(x)).get("Version").equals("100") && Integer.parseInt(((D2TxtFileItemProperties)hireArr.get(x)).get("Level")) <= iMercLevel){

    			hireCol = (D2TxtFileItemProperties)hireArr.get(x);

    	}
    	}
    	
    	if(hireCol == null){
    		
        	for(int x = 0;x<hireArr.size();x =x+1){
        		if(((D2TxtFileItemProperties)hireArr.get(x)).get("Version").equals("100") && Integer.parseInt(((D2TxtFileItemProperties)hireArr.get(x)).get("Level")) > iMercLevel){

        			hireCol = (D2TxtFileItemProperties)hireArr.get(x);
        			break;
        	}
    		
        	}
//    		iMercStr = iMercInitStr = (int)Math.floor((Integer.parseInt(hireCol.get("Str"))+ ((Double.parseDouble(hireCol.get("Str/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//        	iMercDex = iMercInitDex = (int)Math.floor((Integer.parseInt(hireCol.get("Dex"))+ ((Double.parseDouble(hireCol.get("Dex/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//        	iMercHP = iMercInitHP =  (int)Math.floor((Integer.parseInt(hireCol.get("HP"))+ ((Double.parseDouble(hireCol.get("HP/Lvl")))*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//        	iMercDef =iMercInitDef = (long)(Integer.parseInt(hireCol.get("Defense"))+ (Integer.parseInt(hireCol.get("Def/Lvl"))*(iMercLevel - Integer.parseInt(hireCol.get("Level")))));
//        	iMercFireRes = iMercInitFireRes = (int)Math.floor((Integer.parseInt(hireCol.get("Resist"))+ ((Double.parseDouble(hireCol.get("Resist/Lvl"))/(double)4)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//        	iMercAR = iMercInitAR = (int)Math.floor((Integer.parseInt(hireCol.get("AR"))+ ((Double.parseDouble(hireCol.get("AR/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//        	iMercColdRes = iMercInitColdRes = iMercInitFireRes;
//        	iMercLightRes = iMercInitLightRes = iMercInitFireRes;
//        	iMercPoisRes = iMercInitPoisRes = iMercInitFireRes;
        	
    	}
    //MATHS ARGH"GJ:LIJGODISH!
    	//System.out.println("STR: "+(Integer.parseInt(hireCol.get("Str"))+ ((Double.parseDouble(hireCol.get("Str/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
    	iMercStr = iMercInitStr = (int)Math.floor((Integer.parseInt(hireCol.get("Str"))+ ((Double.parseDouble(hireCol.get("Str/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
    	iMercDex = iMercInitDex = (int)Math.floor((Integer.parseInt(hireCol.get("Dex"))+ ((Double.parseDouble(hireCol.get("Dex/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
    	iMercHP = iMercInitHP =  (int)Math.floor((Integer.parseInt(hireCol.get("HP"))+ ((Double.parseDouble(hireCol.get("HP/Lvl")))*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
    	iMercDef =iMercInitDef = (long)(Integer.parseInt(hireCol.get("Defense"))+ (Integer.parseInt(hireCol.get("Def/Lvl"))*(iMercLevel - Integer.parseInt(hireCol.get("Level")))));
    	iMercFireRes = iMercInitFireRes = (int)Math.floor((Integer.parseInt(hireCol.get("Resist"))+ ((Double.parseDouble(hireCol.get("Resist/Lvl"))/(double)4)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
    	iMercAR = iMercInitAR = (int)Math.floor((Integer.parseInt(hireCol.get("AR"))+ ((Double.parseDouble(hireCol.get("AR/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
    	iMercColdRes = iMercInitColdRes = iMercInitFireRes;
    	iMercLightRes = iMercInitLightRes = iMercInitFireRes;
    	iMercPoisRes = iMercInitPoisRes = iMercInitFireRes;
    	
		
	}

	private void setMercLevel() {
    	
    	D2TxtFileItemProperties hireCol = (D2TxtFile.HIRE.searchColumns("SubType", iMercType));
    	int xpPLev = Integer.parseInt(hireCol.get("Exp/Lvl"));
    	long xpOut = 0;
    	int lev = 0;
    	
    	do{
    		xpOut = xpPLev * lev*lev*(lev+1);    		
    		if(xpOut > iMercExp){
    			lev = lev -1;
    			break;
    		}else{
    			lev = lev+1;
    		}
    	}while (1==1);

    	iMercLevel = lev;
    
	}

	private void setMercType(long bitsIn) {
//		System.out.println("MERC TYPE: "+ bitsIn);
		D2TxtFileItemProperties hireCol = (D2TxtFile.HIRE.searchColumns("Id", Long.toString(bitsIn)));
		iMercRace = (hireCol.get("Hireling"));
		iMercType = hireCol.get("SubType");
		
//		System.out.println("RACE: "+iMercRace + "\n" + "TYPE: " + iMercType);
				
	}

	private void setMercName(long bitsIn) {
//		System.out.println("MERC NAME: "+bitsIn);
		D2TxtFileItemProperties hireCol = (D2TxtFile.HIRE.searchColumns("Hireling", iMercRace));
		String nameStr = hireCol.get("NameFirst");
//		System.out.println("MERC NAME: "+nameStr);
		
		int curNum = Integer.parseInt(nameStr.substring(nameStr.length() - 2, nameStr.length()));
		nameStr = nameStr.substring(0, nameStr.length() - 2);
		curNum = curNum + (int)bitsIn;
		
		
		if(curNum < 10){
			nameStr = nameStr + "0" + curNum;
		}else{
			nameStr = nameStr + curNum;
		}
		
		iMercName = D2TblFile.getString(nameStr);
		
		
	}

	public String getCharName()
    {
        return iCharName;
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
    
    
    public void updateCharStats(String string, D2Item pItem){
    	
//    	iWeaponSlotiWeaponSlotiWeaponSlotiWeaponSlotiWeaponSlotiWeaponSlotiWeaponSlotiWeaponSlot
//    	iCurWepSlotiCurWepSlotiCurWepSlotiCurWepSlotiCurWepSlotiCurWepSlotiCurWepSlotiCurWepSlotiCurWepSlot
    	

    	
    	if(! pItem.isEquipped()){
    		return;
    	}
    	


    	
    	
    	
    	if(partialSetProps.size() > 0  || fullSetProps.size() > 0){
    		
    		ArrayList set = new ArrayList();
    		
    		set.addAll(partialSetProps);
    		set.addAll(fullSetProps);
    		
    		for(int y = 0;y<set.size();y=y+1){
    			
    			if(!pItem.isTypeArmor()){
    			if(((D2ItemProperty)set.get(y)).getiProp() == 31){   				
    				charStatArray[338] = charStatArray[338] - ((D2ItemProperty)set.get(y)).getRealValue();
    			}
    			if(((D2ItemProperty)set.get(y)).getiProp() == 16){
    				charStatArray[339] = charStatArray[339] - ((D2ItemProperty)set.get(y)).getRealValue();
    			}
    			}
    			
    		
    		if(((D2ItemProperty)set.get(y)).getiProp() < 340){
    			charStatArray[((D2ItemProperty)set.get(y)).getiProp()] = charStatArray[((D2ItemProperty)set.get(y)).getiProp()] - ((D2ItemProperty)set.get(y)).getRealValue();
    		}else{
    			if(((D2ItemProperty)set.get(y)).getiProp() == 1337){
    				//ALL RESISTANCES
    				charStatArray[39] = charStatArray[39] - ((D2ItemProperty)set.get(y)).getRealValue();
    				charStatArray[41] = charStatArray[41] - ((D2ItemProperty)set.get(y)).getRealValue();
    				charStatArray[43] = charStatArray[43] - ((D2ItemProperty)set.get(y)).getRealValue();
    				charStatArray[45] = charStatArray[45] - ((D2ItemProperty)set.get(y)).getRealValue();
    				
    			}else if(((D2ItemProperty)set.get(y)).getiProp() == 1338){
    				//ALL STATS
    				charStatArray[0] = charStatArray[0] - ((D2ItemProperty)set.get(y)).getRealValue();
    				charStatArray[1] = charStatArray[1] - ((D2ItemProperty)set.get(y)).getRealValue();
    				charStatArray[2] = charStatArray[2] - ((D2ItemProperty)set.get(y)).getRealValue();
    				charStatArray[3] = charStatArray[3] - ((D2ItemProperty)set.get(y)).getRealValue();
    			}
    		} 
    		
    		}
    		
    	}
    	
//    	if(pItem.isSet()){
    		equippedSetItems.clear();
        for(int x = 0;x<iCharItems.size();x=x+1){
        	if(((D2Item)iCharItems.get(x)).isEquipped() && ((D2Item)iCharItems.get(x)).isSet()){
            		equippedSetItems.add(iCharItems.get(x));
        	}
            }
//    		 if(string.equals("D")){
//    			 equippedSetItems.add(pItem);
//    		 }else{
//    			 equippedSetItems.remove(pItem);
//    		 }
    		 
    		 

    	        if(equippedSetItems.size() > 0){
    	            setSetProps();
    	            }
//    	}
//    	07773913719
        if(string.equals("D")){
        	if(pItem.isTypeArmor()){
        		charStatArray[338] = charStatArray[338] + pItem.getiDef();
        	}
        	
            ArrayList bla = pItem.getAllProps();
//            System.out.println(testCounter);
//            testCounter = testCounter +1;
            bla.addAll(partialSetProps);
            bla.addAll(fullSetProps);
//            
            
            
            
            
        	for(int y = 0;y<bla.size();y=y+1){
        		
//        		if(((D2ItemProperty)bla.get(y)).getiProp() == 80){
//        			System.out.println(pItem.getName());
//        		}
        		
    			if(!pItem.isTypeArmor()){
        			if(((D2ItemProperty)bla.get(y)).getiProp() == 31){   				
        				charStatArray[338] = charStatArray[338] + ((D2ItemProperty)bla.get(y)).getRealValue();
        			}
        			if(((D2ItemProperty)bla.get(y)).getiProp() == 16){
        				charStatArray[339] = charStatArray[339] + ((D2ItemProperty)bla.get(y)).getRealValue();
        			}
        			}
        		

        		if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
        			charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] + ((D2ItemProperty)bla.get(y)).getRealValue();
        		}else{
        			if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
        				//ALL RESISTANCES
        				charStatArray[39] = charStatArray[39] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				charStatArray[41] = charStatArray[41] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				charStatArray[43] = charStatArray[43] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				charStatArray[45] = charStatArray[45] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				
        			}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
        				//ALL STATS
        				charStatArray[0] = charStatArray[0] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				charStatArray[1] = charStatArray[1] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				charStatArray[2] = charStatArray[2] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				charStatArray[3] = charStatArray[3] + ((D2ItemProperty)bla.get(y)).getRealValue();
        			}
        		}        	
    }
        	
        }else{
        	if(pItem.isTypeArmor()){
        		charStatArray[338] = charStatArray[338] - pItem.getiDef();
        	}
        	
        	if(partialSetProps.size() > 0  || fullSetProps.size() > 0){
        		
        		ArrayList set = new ArrayList();
        		
        		set.addAll(partialSetProps);
        		set.addAll(fullSetProps);
        		
        		for(int y = 0;y<set.size();y=y+1){
        			
        			if(!pItem.isTypeArmor()){
            			if(((D2ItemProperty)set.get(y)).getiProp() == 31){   				
            				charStatArray[338] = charStatArray[338] + ((D2ItemProperty)set.get(y)).getRealValue();
            			}
            			if(((D2ItemProperty)set.get(y)).getiProp() == 16){
            				charStatArray[339] = charStatArray[339] + ((D2ItemProperty)set.get(y)).getRealValue();
            			}
            			}
        		
        		if(((D2ItemProperty)set.get(y)).getiProp() < 340){
        			charStatArray[((D2ItemProperty)set.get(y)).getiProp()] = charStatArray[((D2ItemProperty)set.get(y)).getiProp()] + ((D2ItemProperty)set.get(y)).getRealValue();
        		}else{
        			if(((D2ItemProperty)set.get(y)).getiProp() == 1337){
        				//ALL RESISTANCES
        				charStatArray[39] = charStatArray[39] + ((D2ItemProperty)set.get(y)).getRealValue();
        				charStatArray[41] = charStatArray[41] + ((D2ItemProperty)set.get(y)).getRealValue();
        				charStatArray[43] = charStatArray[43] + ((D2ItemProperty)set.get(y)).getRealValue();
        				charStatArray[45] = charStatArray[45] + ((D2ItemProperty)set.get(y)).getRealValue();
        				
        			}else if(((D2ItemProperty)set.get(y)).getiProp() == 1338){
        				//ALL STATS
        				charStatArray[0] = charStatArray[0] + ((D2ItemProperty)set.get(y)).getRealValue();
        				charStatArray[1] = charStatArray[1] + ((D2ItemProperty)set.get(y)).getRealValue();
        				charStatArray[2] = charStatArray[2] + ((D2ItemProperty)set.get(y)).getRealValue();
        				charStatArray[3] = charStatArray[3] + ((D2ItemProperty)set.get(y)).getRealValue();
        			}
        		} 
        		
        		}
        		
        	}
        	
            ArrayList bla = pItem.getAllProps();
//            bla.addAll(partialSetProps);
//            bla.addAll(fullSetProps);
        	for(int y = 0;y<bla.size();y=y+1){
        		
    			if(!pItem.isTypeArmor()){
        			if(((D2ItemProperty)bla.get(y)).getiProp() == 31){   				
        				charStatArray[338] = charStatArray[338] - ((D2ItemProperty)bla.get(y)).getRealValue();
        			}
        			if(((D2ItemProperty)bla.get(y)).getiProp() == 16){
        				charStatArray[339] = charStatArray[339] - ((D2ItemProperty)bla.get(y)).getRealValue();
        			}
        			}
        		
        		if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
        			charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] - ((D2ItemProperty)bla.get(y)).getRealValue();
        		
    		}else{
    			if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
    				//ALL RESISTANCES
    				charStatArray[39] = charStatArray[39] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				charStatArray[41] = charStatArray[41] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				charStatArray[43] = charStatArray[43] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				charStatArray[45] = charStatArray[45] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				
    			}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
    				//ALL STATS
    				charStatArray[0] = charStatArray[0] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				charStatArray[1] = charStatArray[1] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				charStatArray[2] = charStatArray[2] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				charStatArray[3] = charStatArray[3] - ((D2ItemProperty)bla.get(y)).getRealValue();
    			}
    		}     
        }
        }
        
        
        iCharStats[0] = iInitStats[0] +  (charStatArray[220]*(int)iCharLevel) + charStatArray[0];
        iCharStats[2] = iInitStats[2] +  (charStatArray[221]*(int)iCharLevel)+ charStatArray[2];
        iCharStats[1] = iInitStats[1] +  (charStatArray[222]*(int)iCharLevel)+ charStatArray[1];
        iCharStats[3] = iInitStats[3] +  (charStatArray[223]*(int)iCharLevel)+ charStatArray[3];
//        iCharStats[7] = ((int)Math.floor(((double)(iInitStats[7] + charStatArray[7])/(double)100) * charStatArray[76]))+(iInitStats[7] + charStatArray[7])+(charStatArray[216]*(int)iCharLevel);
        
        int[] manaHpStam = setHPManaStam();
        
//        iCharStats[7] =(manaHpStam[0] + charStatArray[7])+(charStatArray[216]*(int)iCharLevel);
        
//        System.out.println("LIFE: " +(manaHpStam[1] + charStatArray[9]/*+ (int)Math.floor((charStatArray[217] * 0.125)*(int)iCharLevel)*/));
//        System.out.println("CHAR STAT ARRAY: ")
        iCharStats[7] = ((int)Math.floor(((double)(manaHpStam[0] + charStatArray[7])/(double)100) * charStatArray[76]))+(manaHpStam[0] + charStatArray[7])+ (int)Math.floor((charStatArray[216] * 0.125)*(int)iCharLevel);
//        iCharStats
        
//        OFF ARMOR DEF
        iCharStats[9] = ((int)Math.floor(((double)(manaHpStam[1] + charStatArray[9])/(double)100) * charStatArray[77]))+(manaHpStam[1] + charStatArray[9])+(int)Math.floor((charStatArray[217]* 0.125)*(int)iCharLevel);
        iCharStats[11] = (manaHpStam[2] + charStatArray[11])+(charStatArray[242]*(int)iCharLevel);

//        Sorceress: (1 * 85 + 2 * 311 + 19 + 177) * (1 + 0/100) + 2 * 54 + 127

//        Sorceress: (1 * 85 + 2 * 311 + 19 + (50)) * (1 + 0/100) + 2 * 54 + 127
//(1 * 85 + 2 * 311 + 19 + (50)) * (1 + 0/100) + 2 * 52 + 127
//        iInitStats[16] =
        iCharStats[16] = (int)(Math.floor((((double)((int)(Math.floor((double)iCharStats[2]/(double)4))) + charStatArray[338])/(double)100)*charStatArray[339])) + (((int)(Math.floor((double)iCharStats[2]/(double)4))) + charStatArray[338]);
//       System.out.println("DEXARM: "+ ( (int)(Math.floor((double)iCharStats[2]/(double)4))));
//       System.out.println("ARM: " + calcCharArmor());
//       System.out.println()
//        iCharStats[16] = (int) ((int)Math.floor(((double)(iInitStats[16] + (calcCharArmor()) + (charStatArray[214] * iCharLevel))/(double)100)*(charStatArray[215] * iCharLevel)) +  iInitStats[16] + (calcCharArmor()) + (charStatArray[214] * iCharLevel))  ;
    	
     
        
//        iCharStats[17] = (int)Math.floor(((double)(iMercInitAR + (charStatArray[19]) + (charStatArray[224] * iMercLevel))/(double)100)* (charStatArray[225] * iMercLevel)) +  iMercInitAR + (charStatArray[19]) + (charStatArray[224] * iMercLevel)  ;
        iCharStats[18] = iInitStats[18] + charStatArray[39];
        iCharStats[19] = iInitStats[19] + charStatArray[41];
        iCharStats[20] = iInitStats[20] + charStatArray[43];
        iCharStats[21] = iInitStats[21] + charStatArray[45];
    	iCharStats[22] = charStatArray[80];
    	iCharStats[23] = charStatArray[96];
    	    	
//    	iCharStats[2] = iInitStats[2] +  (charStatArray[221]*(int)iCharLevel)+ charStatArray[2];
//    	iCharStats[16] = (int)calcCharArmor() + (int)(Math.floor((double)iCharStats[2]/(double)4));
    	
        //16 is DEF
        //17 AR
        //18 FR
        ///19 CR
        //20 LR
        //21 PR
    	
//    	System.out.println("START");
//    	
//    	for(int x = 0;x<partialSetProps.size();x=x+1){
//    		
//    		
//    		System.out.println(((D2ItemProperty)partialSetProps.get(x)).getValue());
//    	}
    		
    		
    }
    
    public void updateMercStats(String string, D2Item pItem){
    	
//    	statArray = new int[338];
    	
        if(string.equals("D")){
            ArrayList bla = pItem.getAllProps();
        	for(int y = 0;y<bla.size();y=y+1){
        		if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
        			mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] + ((D2ItemProperty)bla.get(y)).getRealValue();
        		}else{
        			if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
        				//ALL RESISTANCES
        				mercStatArray[39] = mercStatArray[39] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				mercStatArray[41] = mercStatArray[41] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				mercStatArray[43] = mercStatArray[43] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				mercStatArray[45] = mercStatArray[45] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				
        			}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
        				//ALL STATS
        				mercStatArray[0] = mercStatArray[0] + ((D2ItemProperty)bla.get(y)).getRealValue();
        				mercStatArray[2] = mercStatArray[2] + ((D2ItemProperty)bla.get(y)).getRealValue();
        			}
        		}        	
    }
        	
        }else{
            ArrayList bla = pItem.getAllProps();
        	for(int y = 0;y<bla.size();y=y+1){
        		if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
        			mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] - ((D2ItemProperty)bla.get(y)).getRealValue();
        		
    		}else{
    			if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
    				//ALL RESISTANCES
    				mercStatArray[39] = mercStatArray[39] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				mercStatArray[41] = mercStatArray[41] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				mercStatArray[43] = mercStatArray[43] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				mercStatArray[45] = mercStatArray[45] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				
    			}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
    				//ALL STATS
    				mercStatArray[0] = mercStatArray[0] - ((D2ItemProperty)bla.get(y)).getRealValue();
    				mercStatArray[2] = mercStatArray[2] - ((D2ItemProperty)bla.get(y)).getRealValue();
    			}
    		}     
        }
        }
        
        
    	iMercStr = iMercInitStr +  (mercStatArray[220]*iMercLevel) + mercStatArray[0];
    	iMercDex = iMercInitDex +  (mercStatArray[221]*iMercLevel)+ mercStatArray[2];
    	iMercHP = ((int)Math.floor(((double)(iMercInitHP + mercStatArray[7])/(double)100) * mercStatArray[76]))+(iMercInitHP + mercStatArray[7])+(mercStatArray[216]*iMercLevel);
    	iMercDef = (int)Math.floor(((double)(iMercInitDef + (calcMercArmor()) + (mercStatArray[214] * iMercLevel))/(double)100)*(mercStatArray[215] * iMercLevel)) +  iMercInitDef + (calcMercArmor()) + (mercStatArray[214] * iMercLevel)  ;
    	iMercAR = (int)Math.floor(((double)(iMercInitAR + (mercStatArray[19]) + (mercStatArray[224] * iMercLevel))/(double)100)* (mercStatArray[225] * iMercLevel)) +  iMercInitAR + (mercStatArray[19]) + (mercStatArray[224] * iMercLevel)  ;
    	iMercFireRes = iMercInitFireRes + mercStatArray[39];
    	iMercLightRes = iMercInitLightRes + mercStatArray[41];
    	iMercColdRes = iMercInitColdRes + mercStatArray[43];
    	iMercPoisRes = iMercInitPoisRes + mercStatArray[45];
    	
    }
    

    private long calcMercArmor() {
		
    	int out = 0;
    	
    	for(int y = 0;y<iMercItems.size();y=y+1){
    		if(((D2Item)iMercItems.get(y)).isTypeArmor()){
    			out = out + ((D2Item)iMercItems.get(y)).getiDef();
    		}
    	}
    	
    	
		return out;
	}
    
    private int calcCharArmor() {
    	
    	
    	
    	int out = 0;
    	
    	for(int y = 0;y<iCharItems.size();y=y+1){
    		if(((D2Item)iCharItems.get(y)).isTypeArmor()){
    			if(((D2Item)iCharItems.get(y)).isEquipped()){
    			out = out + ((D2Item)iCharItems.get(y)).getiDef();
//    			System.out.println("ITEML: "+ ((D2Item)iCharItems.get(y)).getiDef());
    			}
    		}
    	}
    	
    	
		return out;
	}


	// read in all items:
    // find the first "JM" flag in the file
    // the byte immediately following it
    // indicates the number of items
    // copy the set of bytes representing each
    // item from the character file and use
    // them to construct a new item object.
    // store the items in the vector
    public void readItems(int pStartSearch) throws Exception
    {
        int lFirstPos = iReader.findNextFlag("JM", pStartSearch);
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
            if ( lItem.isCursorItem() )
            {
                if ( iCharCursorItem != null )
                {
                    throw new Exception("Double cursor item found");
                }
                iCharCursorItem = lItem;
            }
            else
            {
                addCharItem(lItem);
                markCharGrid(lItem);
            }
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
    
    public void setCursorItem(D2Item pItem)
    {
        iCharCursorItem = pItem;
        setModified(true);
        // cursor item
        if ( iCharCursorItem != null )
        {
            iCharCursorItem.set_location((short) 4);
            iCharCursorItem.set_body_position((short) 0);
        }
    }
    
    public D2Item getCursorItem()
    {
        return iCharCursorItem;
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
        setModified(true);
    }

    // insert an item into the vector
    public void addMercItem(D2Item pItem)
    {
        iMercItems.add(pItem);
        pItem.setCharLvl(iCharLevel);
        setModified(true);
    }

    public void removeCharItem(int i)
    {
        iCharItems.remove(i);
        setModified(true);
    }

    public void removeMercItem(int i)
    {
        iMercItems.remove(i);
        setModified(true);
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
                if (pItem.isBodyLocation(D2BodyLocations.BODY_HEAD) )
                {
                    return false;
                }
                break;
            case BODY_NECK:
                // neck
                if (pItem.isBodyLocation(D2BodyLocations.BODY_NECK) )
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
                if (pItem.isBodyLocation(D2BodyLocations.BODY_TORS) )
                {
                    return false;
                }
                break;
            case BODY_RARM:
            case BODY_RARM2:
                // neck
                if (pItem.isBodyLocation(D2BodyLocations.BODY_RARM) )
                {
                    return false;
                }
                break;
            case BODY_GLOVES:
                // neck
                if (pItem.isBodyLocation(D2BodyLocations.BODY_GLOV) )
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
                if ( pItem.isBodyLocation(D2BodyLocations.BODY_BELT) )
                {
                    return false;
                }
                break;
            case BODY_LRING:
                // neck
                if (pItem.isBodyLocation(D2BodyLocations.BODY_LRIN) )
                {
                    return false;
                }
                break;
            case BODY_BOOTS:
                // neck
                if (pItem.isBodyLocation(D2BodyLocations.BODY_FEET) )
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
                if (pItem.isBodyLocation(D2BodyLocations.BODY_HEAD) )
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
                if (pItem.isBodyLocation(D2BodyLocations.BODY_TORS) )
                {
                    return false;
                }
                break;
            case BODY_RARM:
                // right arm
                if (pItem.isBodyLocation(D2BodyLocations.BODY_RARM) )
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
    
    public void saveInternal(D2Project pProject)
    {
        // backup file
        D2Backup.backup(pProject, iFileName, iReader);

        // build an a byte array that contains the
        // entire item list and insert it into
        // the open file in place of its current item list

        int lCharSize = 0;
        for (int i = 0; i < iCharItems.size(); i++)
        {
            lCharSize += ((D2Item) iCharItems.get(i)).get_bytes().length;
        }
        if ( iCharCursorItem != null )
        {
            lCharSize += iCharCursorItem.get_bytes().length;
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
        if ( iCharCursorItem != null )
        {
            byte[] item_bytes = iCharCursorItem.get_bytes();
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
        int lCharItemsCount = iCharItems.size();
        if ( iCharCursorItem != null )
        {
            lCharItemsCount++;
        }
        iReader.write(lCharItemsCount, 16);

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
        setModified(false);
    }
    
    public int getGold()
    {
        return iReadStats[14];
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
        iReadStats[14] = pGold;
        setModified(true);
    }

    public int getGoldBank()
    {
        return iReadStats[15];
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
        iReadStats[15] = pGoldBank;
        setModified(true);
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
        for (int lStatNr = 0; lStatNr < iReadStats.length; lStatNr++)
        {
            if (iReadStats[lStatNr] != 0)
            {
                lWriter.setCounterInt(9, lStatNr);
                D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST.getRow(lStatNr);
                int lBits = Integer.parseInt(lItemStatCost.get("CSvBits"));
                lWriter.setCounterInt(lBits, iReadStats[lStatNr]);
            }
        }
        
        for(int x = 0; x< iReadStats.length;x=x+1){
        	  iInitStats[x] = iReadStats[x];
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
    
    public Point[] getSkillLocs(){
    	return iSkillLocs;
    }
    
    public ArrayList getSkillListA(){
    	return iCharSkillsA;
    }
    
    public ArrayList getSkillListB(){
    	return iCharSkillsB;
    }
    
    public ArrayList getSkillListC(){
    	return iCharSkillsC;
    }
    
    public void fullDump(PrintWriter pWriter)
    {
        pWriter.println( iFileName );
        pWriter.println( "Level: " + iCharLevel );
	    pWriter.println();
        
        if ( iCharItems != null )
        {
            for ( int i = 0 ; i < iCharItems.size() ; i++ )
            {
                D2Item lItem = (D2Item) iCharItems.get(i);
                lItem.toWriter(pWriter);
            }
        }
        if ( iMercItems != null )
        {
            pWriter.println();
            pWriter.println("Mercenary");
            for ( int i = 0 ; i < iMercItems.size() ; i++ )
            {
                D2Item lItem = (D2Item) iMercItems.get(i);
                lItem.toWriter(pWriter);
            }
        }
        pWriter.println( "Finished: " + iFileName );
        pWriter.println();
    }

	public String getMercName() {
		// TODO Auto-generated method stub
		return iMercName;
	}

	public String getMercType() {
		// TODO Auto-generated method stub
		return iMercType;
	}

	public long getMercExp() {
		// TODO Auto-generated method stub
		return iMercExp;
	}

	public String getMercRace() {
		// TODO Auto-generated method stub
		return iMercRace;
	}

	public int getMercLevel() {
		// TODO Auto-generated method stub
		return iMercLevel;
	}

	public boolean getMercDead() {
		// TODO Auto-generated method stub
		return iMercDead;
	}

	public int getMercStr() {
		// TODO Auto-generated method stub
		return iMercStr;
	}

	public int getMercDex() {
		// TODO Auto-generated method stub
		return iMercDex;
	}

	public int getMercHP() {
		// TODO Auto-generated method stub
		return iMercHP;
	}

	public long getMercDef() {
		// TODO Auto-generated method stub
		return iMercDef;
	}

	public int getMercAR() {
		// TODO Auto-generated method stub
		return iMercAR;
	}
	
	public int getMercFireRes() {
		// TODO Auto-generated method stub
		return iMercFireRes;
	}
	
	public int getMercColdRes() {
		// TODO Auto-generated method stub
		return iMercColdRes;
	}
	
	public int getMercLightRes() {
		// TODO Auto-generated method stub
		return iMercLightRes;
	}
	
	public int getMercPoisRes() {
		// TODO Auto-generated method stub
		return iMercPoisRes;
	}
	
	public int getMercInitStr() {
		// TODO Auto-generated method stub
		return iMercInitStr;
	}

	public int getMercInitDex() {
		// TODO Auto-generated method stub
		return iMercInitDex;
	}

	public int getMercInitHP() {
		// TODO Auto-generated method stub
		return iMercInitHP;
	}

	public long getMercInitDef() {
		// TODO Auto-generated method stub
		return iMercInitDef;
	}

	public int getMercInitAR() {
		// TODO Auto-generated method stub
		return iMercInitAR;
	}
	
	public int getMercInitFireRes() {
		// TODO Auto-generated method stub
		return iMercInitFireRes;
	}
	
	public int getMercInitColdRes() {
		// TODO Auto-generated method stub
		return iMercInitColdRes;
	}
	
	public int getMercInitLightRes() {
		// TODO Auto-generated method stub
		return iMercInitLightRes;
	}
	
	public int getMercInitPoisRes() {
		// TODO Auto-generated method stub
		return iMercInitPoisRes;
	}

	public String getCharClass() {
		// TODO Auto-generated method stub
		return iCharClass;
	}
	
	public long getCharExp() {
		// TODO Auto-generated method stub
		return iInitStats[13];
	}

	public boolean getCharDead() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getCharStr() {
		// TODO Auto-generated method stub
		return iCharStats[0];
	}

	public int getCharDex() {
		// TODO Auto-generated method stub
		return iCharStats[2];
	}
	public int getCharNrg() {
		// TODO Auto-generated method stub
		return iCharStats[1];
	}
	
	public int getCharVit() {
		// TODO Auto-generated method stub
		return iCharStats[3];
	}
	
	public int getCharRemStat() {
		// TODO Auto-generated method stub
		return iCharStats[4];
	}
	
	public int getCharRemSkill() {
		// TODO Auto-generated method stub
		return iCharStats[5];
	}
	
	public int getCharMana() {
		// TODO Auto-generated method stub
		return iCharStats[9];
	}
	public int getCharStam() {
		// TODO Auto-generated method stub
		return iCharStats[11];
	}

	public int getCharHP() {
		// TODO Auto-generated method stub
		return iCharStats[7];
	}

	public long getCharDef() {
		// TODO Auto-generated method stub
		return iCharStats[16];
	}

	public int getCharAR() {
		// TODO Auto-generated method stub
		return iCharStats[17];
	}
	
	public int getCharFireRes() {
		// TODO Auto-generated method stub
		return iCharStats[18];
	}
	
	public int getCharColdRes() {
		// TODO Auto-generated method stub
		return iCharStats[20];
	}
	
	public int getCharLightRes() {
		// TODO Auto-generated method stu
		return iCharStats[19];
	}
	
	public int getCharPoisRes() {
		// TODO Auto-generated method stub
		return iCharStats[21];
	}
	
	public int getCharInitStr() {
		// TODO Auto-generated method stub
		return iInitStats[0];
	}

	public int getCharInitDex() {
		// TODO Auto-generated method stub
		return iInitStats[2];
	}
	
	public int getCharInitNrg() {
		// TODO Auto-generated method stub
		return iInitStats[1];
	}
	
	public int getCharInitVit() {
		// TODO Auto-generated method stub
		return iInitStats[3];
	}
	
	public int getCharInitMana() {
		// TODO Auto-generated method stub
		return iInitStats[9];
	}
	
	public int getCharInitStam() {
		// TODO Auto-generated method stub
		return iInitStats[11];
	}

	public int getCharInitHP() {
		// TODO Auto-generated method stub
		return iInitStats[7];
	}

	public long getCharInitDef() {
		// TODO Auto-generated method stub
		return iInitStats[16];
	}

	public int getCharInitAR() {
		// TODO Auto-generated method stub
		return iInitStats[17];
	}
	
	public int getCharInitFireRes() {
		// TODO Auto-generated method stub
		return iInitStats[18];
	}
	
	public int getCharInitColdRes() {
		// TODO Auto-generated method stub
		return iInitStats[19];
	}
	
	public int getCharInitLightRes() {
		// TODO Auto-generated method stub
		return iInitStats[20];
	}
	
	public int getCharInitPoisRes() {
		// TODO Auto-generated method stub
		return iInitStats[21];
	}

	public int getCharLevel() {
		// TODO Auto-generated method stub
		return (int)iCharLevel;
	}
	
	public int getCharMF(){
		return iCharStats[22];
	}
	public int getCharFRW(){
		return iCharStats[23];
	}
	public int getCharSkillRem(){
		return iInitStats[5];
	}

	public int getCharCode() {
		// TODO Auto-generated method stub
		return (int)lCharCode;
	}

    
}