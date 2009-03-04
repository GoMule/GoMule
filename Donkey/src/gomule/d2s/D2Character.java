/*******************************************************************************
 * 
 * Copyright 2007 Andy Theuninck, Randall & Silospen
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

//a character class
//manages one character file
//stores a filename, a bitreader
//to read from that file, and
//a vector of items
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
	public static final int GOLEM_SLOT         = 23;

	private D2BitReader iReader;
	private ArrayList iCharItems;
	private D2Item iCharCursorItem;
	private D2Item golemItem;
	private ArrayList iMercItems;
	private ArrayList iCorpseItems = new ArrayList();

	private String iCharName;
	private String iTitleString;
	private String cClass;
	private long iCharLevel;
	private int curWep = 0;
	private long lCharCode;
	private String iCharClass;
	private boolean iHC;

	private boolean[][]     iStashGrid;
	private boolean[][]     iInventoryGrid;
	private boolean[][]     iCubeGrid;
	private boolean[][]     iBeltGrid;
	private boolean[]       iEquipped;
	private boolean[]       iMerc;
	private boolean[]       iCorpse;

	private String[][] iQuests = new String[3][5];

	private String[][] iWaypoints = new String[3][5];

	private ArrayList iCharInitSkillsA = new ArrayList();
	private ArrayList iCharInitSkillsB = new ArrayList();
	private ArrayList iCharInitSkillsC = new ArrayList();
	private ArrayList iCharSkillsA = new ArrayList();
	private ArrayList iCharSkillsB = new ArrayList();
	private ArrayList iCharSkillsC = new ArrayList();
	private Point[] iSkillLocs = new Point[30];

	private HashMap cMercInfo;	

//	private String iMercName;
//	private String iMercRace;
//	private String iMercType;
//	private int iMercLevel;
//	private long iMercExp;
//	private boolean iMercDead = false; 
//	private int iMercInitStr;
//	private int iMercInitDex;
//	private int iMercInitHP;
//	private long iMercInitDef;
//	private int iMercInitFireRes;
//	private int iMercInitColdRes;
//	private int iMercInitLightRes;
//	private int iMercInitPoisRes;
//	private int iMercStr;
//	private int iMercDex;
//	private int iMercHP;
//	private long iMercDef;
//	private int iMercAR;
//	private int iMercFireRes;
//	private int iMercColdRes;
//	private int iMercPoisRes;
//	private int iMercLightRes;
//	private int iMercInitAR;

	private int testCounter = 0;
	private boolean fullChanged = false;
	private ArrayList partialSetProps = new ArrayList();
	private ArrayList fullSetProps = new ArrayList();
	private int hpCounter;

	private ArrayList plusSkillArr = new ArrayList();
	private long[] iReadStats = new long[16];
	private int[] cStats = new int[32];	
	private int cDef = 0;

	private int lWoo;
	private int iWS;
	private int	iGF; 
	private int	iIF;
	private int iKF;
	private int iJF;
	private int iItemEnd;
	private byte iBeforeStats[];
	private byte iBeforeItems[];
	private byte iBetweenItems[];
	private byte iAfterItems[];

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
		if (iReader.get_length() != lSize)
		{
			throw new Exception("Incorrect FileSize: " + lSize);
		}

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
		iReader.set_byte_pos(36);
		iReader.skipBits(2);
		iHC = iReader.read(1) == 1;
		iReader.set_byte_pos(37);
		long lCharTitle = iReader.read(8);
		iReader.set_byte_pos(40);
		lCharCode = iReader.read(8);

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

		iReader.set_byte_pos(43);
		iCharLevel = iReader.read(8);
		if ( iCharLevel < 1 || iCharLevel > 99 )
		{
			throw new Exception("Invalid char level: " + iCharLevel + " (should be between 1-99)");
		}
		iCharClass =  D2TxtFile.getCharacterCode((int) lCharCode);
		iTitleString = " Lvl " + iCharLevel + " " + D2TxtFile.getCharacterCode((int) lCharCode);
		iReader.set_byte_pos(177);
		if(iReader.read(8) == 1){
			//MERC IS DEAD?
//			iMercDead = true;
		}
		iReader.skipBits(8);

		if(iReader.read(32) != 0){
			cMercInfo = new HashMap();
			iReader.skipBits(16);
			D2TxtFileItemProperties hireCol = (D2TxtFile.HIRE.searchColumns("Id", Long.toString(iReader.read(16))));
			cMercInfo.put("race", hireCol.get("Hireling"));
			cMercInfo.put("type", hireCol.get("SubType"));
			iReader.skipBits(-32);
			extractMercName(iReader.read(16), hireCol);
			iReader.skipBits(16);
			cMercInfo.put("xp", new Long(iReader.read(32)));
			setMercLevel(hireCol);
		}else{
			iReader.skipBits(64);
		}

		lWoo = iReader.findNextFlag("Woo!", 0);
		if ( lWoo == -1 )
		{
			throw new Exception("Error: Act Quests block not found");
		}
		if ( lWoo != 335 )
		{
			System.err.println("Warning: Act Quests block not on expected position");
		}

		iWS = iReader.findNextFlag("WS", lWoo);
		if ( iWS == -1 )
		{
			throw new Exception("Error: Waypoints not found");
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

		iJF = iReader.findNextFlag("jf", iIF);
		if ( iJF == -1 )
		{
			System.out.println("WTF is going on. Looks like it might be classic? USE WITH CARE!");
		}

		iKF = iReader.findNextFlag("kf", iIF);
		if ( iKF != -1 )
		{
			readGolem();
		}

		if ( iIF < iGF )
		{
			throw new Exception("Error: Stats / Skills not correct");
		}
		readWaypoints();
		readQuests();
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
				long lValue = lReader.getCounterLong(lBits);
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
		iStashGrid = new boolean[8][6];
		iInventoryGrid = new boolean[4][10];
		iBeltGrid = new boolean[4][4];
		iCubeGrid = new boolean[4][3];
		iEquipped = new boolean[13];
		iMerc = new boolean[13];
		iCorpse = new boolean[13];
		clearGrid();
		readItems( iIF );
		readCorpse();
		readSkills();
		resetStats();
		for(int x = 0;x<iCharItems.size();x++){
			equipItem((D2Item)iCharItems.get(x));
		}
	}

	private void resetStats() {

		cStats[0] = getCharInitStr();
		cStats[2] = getCharInitNrg();
		cStats[4] = getCharInitDex();
		cStats[6] = getCharInitVit();
	}

	private void generateCharStats(D2Item cItem, int op) {

		if(!cItem.isEquipped(curWep))return;  
		cItem.getPropCollection().calcStats(cStats, (int)iCharLevel, op);	
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

	private void readWaypoints() {

		for(int y= 0;y<iWaypoints.length;y=y+1){
			for(int u=0;u<iWaypoints[y].length;u=u+1){
				iWaypoints[y][u] = "";
			}
		}
		iReader.set_byte_pos(iWS);
		iReader.skipBytes(10);

		for(int f = 0;f<3;f=f+1){
			for(int y = 0;y<3;y=y+1){
				for(int x = 0;x<9;x=x+1){
					if(iReader.read(1) == 1){
						iWaypoints[f][y] = iWaypoints[f][y] + 1;
					}else{
						iWaypoints[f][y] = iWaypoints[f][y] + 0;
					}
				}
			}

			for(int x = 0;x<3;x=x+1){
				if(iReader.read(1) == 1){
					iWaypoints[f][3] = iWaypoints[f][3] + 1;
				}else{
					iWaypoints[f][3] = iWaypoints[f][3] + 0;
				}
			}

			for(int x = 0;x<9;x=x+1){
				if(iReader.read(1) == 1){
					iWaypoints[f][4] = iWaypoints[f][4] + 1;
				}else{
					iWaypoints[f][4] = iWaypoints[f][4] + 0;
				}
			}
			iReader.skipBits(1);
			iReader.skipBytes(19);
		}
	}

	private void readQuests() {

		for(int y= 0;y<iQuests.length;y=y+1){
			for(int u=0;u<iQuests[y].length;u=u+1){
				iQuests[y][u] = "";
			}
		}
		iReader.set_byte_pos(lWoo);
		iReader.skipBytes(4);

		for(int x = 0;x<4;x=x+1){
			iReader.read(8);
		}

		for(int v = 0;v<3;v=v+1){
			for(int g = 0;g<3;g=g+1){
				iReader.skipBytes(4);

				for(int f = 0;f<6;f=f+1){
					if(iReader.read(1) == 1){
						iQuests[v][g] = iQuests[v][g] + "1";
					}else{
						iQuests[v][g] = iQuests[v][g] + "0";
					}
					iReader.skipBits(15);
				}

			}
			iReader.skipBytes(4);
			for(int f = 0;f<3;f=f+1){
				if(iReader.read(1) == 1){
					iQuests[v][3] = iQuests[v][3] + "1";
				}else{
					iQuests[v][3] = iQuests[v][3] + "0";
				}
				iReader.skipBits(15);
			}
			iReader.skipBytes(2);
			iReader.skipBytes(6);
			iReader.skipBytes(2);
			iReader.skipBytes(4);

			for(int f = 0;f<6;f=f+1){
				if(iReader.read(1) == 1){
					iQuests[v][4] = iQuests[v][4] + "1";
				}else{
					iQuests[v][4] = iQuests[v][4] + "0";
				}
				iReader.skipBits(15);
			}
			iReader.skipBytes(12);
		}
	}

	private byte[] getCurrentStats(){

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
				lWriter.setCounterInt(lBits, (int)iReadStats[lStatNr]);
			}
		}

		lWriter.setCounterInt(9, 0x1FF);
		return lWriter.getCurrentContent();
	}

	private void readSkills() {

		generateSkillLocs();
		D2TxtFileItemProperties initRow = D2TxtFile.SKILLS.searchColumns("charclass", cClass);

		iReader.set_byte_pos(iIF);
		byte skillInitialBytes[] = iReader.get_bytes(32);
		D2FileReader skillReader = new D2FileReader(skillInitialBytes);
		skillReader.getCounterInt(8);
		skillReader.getCounterInt(8);
		int tree = 0;
		for(int x =0;x<30;x=x+1){
			tree = Integer.parseInt((D2TxtFile.SKILL_DESC.searchColumns("skilldesc", D2TxtFile.SKILLS.getRow(initRow.getRowNum() + x).get("skilldesc"))).get("SkillPage"));
			switch (tree){

			case 1:
				iCharInitSkillsA.add(new Integer(skillReader.getCounterInt(8)));
				break;

			case 2:
				iCharInitSkillsB.add(new Integer(skillReader.getCounterInt(8)));
				break;

			case 3:
				iCharInitSkillsC.add(new Integer(skillReader.getCounterInt(8)));
				break;
			}
		}
		resetSkills();
	}

	private void generateSkillLocs() {

		switch((int)lCharCode){

		case 0:
//			cClass = "ama";
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
//			cClass = "sor";
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
//			cClass = "nec";
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
//			cClass = "pal";
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
//			cClass = "bar";
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
//			cClass = "dru";
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
//			cClass = "ass";
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

	private void readCorpse() throws Exception {

		int corpseStart = iReader.findNextFlag("JM", iItemEnd+2);
		if(corpseStart < 0 || corpseStart > iKF || corpseStart > iJF){
			return;
		}
		iReader.set_byte_pos(corpseStart);
		iReader.skipBytes(2);
		int num_items = (int) (iReader.read(8));
		int lLastItemEnd = iReader.get_byte_pos();
		for (int i = 0; i < num_items; i++)
		{
			int lItemStart = iReader.findNextFlag("JM", lLastItemEnd);
			if (lItemStart == -1)
			{
				throw new Exception("Corpse item " + (i + 1) + " not found.");
			}

			D2Item lItem = new D2Item(iFileName, iReader, lItemStart, iCharLevel);
			lLastItemEnd = lItemStart + lItem.getItemLength();
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
				addCorpseItem(lItem);
				markCorpseGrid(lItem);
			}
		}
	}
	// read in all items:
	// find the first "JM" flag in the file
	// the byte immediately following it
	// indicates the number of items
	// copy the set of bytes representing each
	// item from the character file and use
	// them to construct a new item object.
	// store the items in the vector
	private void readItems(int pStartSearch) throws Exception
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
		iItemEnd = lCharEnd;
		//        System.err.println("Read Char: " + lCharStart + " - " + lCharEnd );

		int lMercStart = -1;
		int lMercEnd = -1;

		lFirstPos = iReader.findNextFlag("jfJM", lCharEnd);
		if (lFirstPos != -1) // iReader.getFileContent()
		{
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

	private void readGolem() throws Exception {

		iReader.set_byte_pos(iKF);
		iReader.skipBytes(2);
		switch((int)iReader.read(8)){
		case 0:
			golemItem = null;
			return;		
		}
		int lItemStart = iReader.findNextFlag("JM", iKF);
		if (lItemStart == -1)
		{
			throw new Exception("Golem item not found.");
		}
		golemItem = new D2Item(iFileName, iReader, lItemStart, iCharLevel);
	}

//	private void setSetProps() {


//	int counter = 0;
//	ArrayList itemsToMod = new ArrayList();
//	ArrayList equippedSetItems = new ArrayList(); 
//	equippedSetItems.addAll(this.equippedSetItems);
//	partialSetProps.clear();
//	fullSetProps.clear();
//	for(int x = 0;x<equippedSetItems.size();x=x+1){
//	D2Item thisSetItem = ((D2Item)equippedSetItems.get(x));
//	itemsToMod.clear();
//	counter = 1;
//	itemsToMod.add(thisSetItem);
//	equippedSetItems.remove(x);
//	x=x-1;
//	for(int y = x+1;y<equippedSetItems.size();y=y+1){
//	if(!((D2Item)equippedSetItems.get(y)).equals(thisSetItem)){
//	if(((D2Item)equippedSetItems.get(y)).getSetName().equals(thisSetItem.getSetName())){
//	counter = counter +1;
//	itemsToMod.add((D2Item)equippedSetItems.get(y));
//	equippedSetItems.remove(y);
//	y=y-1;
//	}
//	}
//	}
//	for(int z = 0;z<itemsToMod.size();z=z+1){
//	((D2Item)itemsToMod.get(z)).setSetProps(counter);
//	}

//	if(itemsToMod.size() > 0 ){

//	if(counter == thisSetItem.getSetSize()){
//	setFullSet(thisSetItem);
//	}
//	setPartialSet(counter, thisSetItem);
//	}
//	}
//	}

//	private void setPartialSet(int counter, D2Item thisSetItem) {

//	if(counter > 5){
//	counter = 5;
//	}

//	D2TxtFileItemProperties setRow = D2TxtFile.FULLSET.searchColumns("name", thisSetItem.getSetName());

//	int replaceChar = 2;

//	String[] replaceStr = new String[4];
//	String codeStr;
//	while(replaceChar < counter +1){

//	replaceStr[0] = "PCode" + replaceChar + "a";
//	replaceStr[1] = "PParam" + replaceChar + "a";
//	replaceStr[2] = "PMin" + replaceChar + "a";
//	replaceStr[3] = "PMax" + replaceChar + "a";
//	codeStr = setRow.get(replaceStr[0]);
//	if(codeStr.equals("")){
//	break;
//	}

//	String[] propStats = {
//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat1"))).get("ID")};
//	D2ItemProperty lProperty = new D2ItemProperty(Integer.parseInt(propStats[0]), (long)thisSetItem.getReqLvl(),
//	thisSetItem.getName());

//	D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST
//	.getRow(lProperty.getPropNrs()[0]);

//	lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long.parseLong(setRow.get(replaceStr[2])));


//	if( setRow.get(replaceStr[0]).equals("res-all")){
//	lProperty = new D2ItemProperty(1337,
//	(long)thisSetItem.getReqLvl(), thisSetItem.getName());
//	lProperty.set(1337, lItemStatCost, 0, Long
//	.parseLong(setRow.get(replaceStr[3])));
//	}
//	if (! setRow.get(replaceStr[3]).equals("")) {
//	lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long
//	.parseLong( setRow.get(replaceStr[3])));
//	}

//	if (! setRow.get(replaceStr[1]).equals("")) {

//	lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long
//	.parseLong( setRow.get(replaceStr[1])));
//	}
//	partialSetProps.add(lProperty);
//	replaceChar = replaceChar +1;
//	}

//	}

//	private void setFullSet(D2Item thisSetItem) {

//	D2TxtFileItemProperties setRow = D2TxtFile.FULLSET.searchColumns("name", thisSetItem.getSetName());
//	int replaceChar = 1;
//	String[] replaceStr = new String[4];
//	String codeStr = "";
//	while(replaceStr.length == 4){
//	replaceStr[0] = "FCode" + replaceChar;
//	replaceStr[1] = "FParam" + replaceChar;
//	replaceStr[2] = "FMin" + replaceChar;
//	replaceStr[3] = "FMax" + replaceChar;
//	codeStr= setRow.get(replaceStr[0]);
//	if(codeStr.equals("") || codeStr.equals("lifesteal")){
//	break;
//	}
//	String[] propStats = {
//	(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",((D2TxtFile.PROPS.searchColumns("code", setRow.get(replaceStr[0])))).get("stat1"))).get("ID")};
//	D2ItemProperty lProperty = new D2ItemProperty(Integer.parseInt(propStats[0]), (long)thisSetItem.getReqLvl(),
//	thisSetItem.getName());

//	D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST
//	.getRow(lProperty.getPropNrs()[0]);

//	lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long.parseLong(setRow.get(replaceStr[2])));


//	if( setRow.get(replaceStr[0]).equals("res-all")){
//	lProperty = new D2ItemProperty(1337,
//	(long)thisSetItem.getReqLvl(), thisSetItem.getName());
//	lProperty.set(1337, lItemStatCost, 0, Long
//	.parseLong(setRow.get(replaceStr[3])));
//	}
//	if (! setRow.get(replaceStr[3]).equals("")) {
//	lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long
//	.parseLong( setRow.get(replaceStr[3])));
//	}

//	if (! setRow.get(replaceStr[1]).equals("") && !setRow.get(replaceStr[1]).equals("fullsetgeneric")&& !setRow.get(replaceStr[1]).equals("monsterset") && !setRow.get(replaceStr[1]).equals("Fire Mastery")) {
//	lProperty.set(Integer.parseInt(propStats[0]), lItemStatCost, 0, Long  

//	.parseLong( setRow.get(replaceStr[1])));
//	}
//	fullSetProps.add(lProperty);
//	replaceChar = replaceChar +1;
//	}

//	}

//	private void generateCharStats(){

//	int resCounter = 0;
//	hpCounter = 0;
//	for(int x = 0;x<3;x=x+1){
//	if(iQuests[x][4].charAt(2) == '1'){
//	resCounter = resCounter + 1;
//	}
//	if(iQuests[x][2].charAt(0) == '1'){
//	hpCounter = hpCounter +1;
//	}
//	}
//	charStatArray[39] = resCounter*10;
//	charStatArray[41] =resCounter*10; 
//	charStatArray[43] =resCounter*10; 
//	charStatArray[45] =resCounter*10;
//	//NEED TO GENERATE 
//	//16 is DEF
//	//17 AR
//	//18 FR
//	///19 CR
//	//20 LR
//	//21 PR

//	//DEAL WITH XP (?)
//	//DEAL WITH LIFE AND MANA AND STAMINA
//	iInitStats[16] = (int)(Math.floor((double)iInitStats[2]/(double)4));
//	iInitStats[17] = ((iInitStats[2] * 5) - 35) + getARClassBonus(); 
//	for(int i = 0;i<iInitStats.length;i=i+1){
//	iCharStats[i] = iInitStats[i];
//	}

//	int[] manaHpStam = setHPManaStam();
//	iInitStats[7] =(manaHpStam[0])+(hpCounter*20);
//	iInitStats[9] = manaHpStam[1];
//	iInitStats[11] = manaHpStam[2];

//	}

//	private void generateMercStats() {

//	D2TxtFileItemProperties hireCol = null;
//	ArrayList hireArr = D2TxtFile.HIRE.searchColumnsMultipleHits("SubType", iMercType);

//	for(int x = 0;x<hireArr.size();x =x+1){
//	if(((D2TxtFileItemProperties)hireArr.get(x)).get("Version").equals("100") && Integer.parseInt(((D2TxtFileItemProperties)hireArr.get(x)).get("Level")) <= iMercLevel){
//	hireCol = (D2TxtFileItemProperties)hireArr.get(x);
//	}
//	}

//	if(hireCol == null){

//	for(int x = 0;x<hireArr.size();x =x+1){
//	if(((D2TxtFileItemProperties)hireArr.get(x)).get("Version").equals("100") && Integer.parseInt(((D2TxtFileItemProperties)hireArr.get(x)).get("Level")) > iMercLevel){

//	hireCol = (D2TxtFileItemProperties)hireArr.get(x);
//	break;
//	}
//	}
//	}
//	iMercStr = iMercInitStr = (int)Math.floor((Integer.parseInt(hireCol.get("Str"))+ ((Double.parseDouble(hireCol.get("Str/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//	iMercDex = iMercInitDex = (int)Math.floor((Integer.parseInt(hireCol.get("Dex"))+ ((Double.parseDouble(hireCol.get("Dex/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//	iMercHP = iMercInitHP =  (int)Math.floor((Integer.parseInt(hireCol.get("HP"))+ ((Double.parseDouble(hireCol.get("HP/Lvl")))*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//	iMercDef =iMercInitDef = (long)(Integer.parseInt(hireCol.get("Defense"))+ (Integer.parseInt(hireCol.get("Def/Lvl"))*(iMercLevel - Integer.parseInt(hireCol.get("Level")))));
//	iMercFireRes = iMercInitFireRes = (int)Math.floor((Integer.parseInt(hireCol.get("Resist"))+ ((Double.parseDouble(hireCol.get("Resist/Lvl"))/(double)4)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//	iMercAR = iMercInitAR = (int)Math.floor((Integer.parseInt(hireCol.get("AR"))+ ((Double.parseDouble(hireCol.get("AR/Lvl"))/(double)8)*(iMercLevel - Integer.parseInt(hireCol.get("Level"))))));
//	iMercColdRes = iMercInitColdRes = iMercInitFireRes;
//	iMercLightRes = iMercInitLightRes = iMercInitFireRes;
//	iMercPoisRes = iMercInitPoisRes = iMercInitFireRes;
//	}

	private void setMercLevel(D2TxtFileItemProperties hireCol) {

		int xpPLev = Integer.parseInt(hireCol.get("Exp/Lvl"));
		long xpOut = 0;
		int lev = 0;
		do{
			xpOut = xpPLev * lev*lev*(lev+1);    		
			if(xpOut > ((Long)cMercInfo.get("xp")).longValue()){
				lev = lev -1;
				break;
			}else{
				lev = lev+1;
			}
		}while (true);
		cMercInfo.put("level", Integer.valueOf(lev));
	}

	private void extractMercName(long bitsIn, D2TxtFileItemProperties hireCol) {
		String nameStr = hireCol.get("NameFirst");
		int curNum = Integer.parseInt(nameStr.substring(nameStr.length() - 2, nameStr.length()));
		nameStr = nameStr.substring(0, nameStr.length() - 2);
		curNum = curNum + (int)bitsIn;

		if(curNum < 10){
			nameStr = nameStr + "0" + curNum;
		}else{
			nameStr = nameStr + curNum;
		}
		cMercInfo.put("name", D2TblFile.getString(nameStr));
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
				}
			}
		}
		return out;
	}

	private void dealWithSkills() {

		resetSkills();
		for(int x = 0;x<getPlusSkills().size();x=x+1){
//			188, 126, 97, 107, 83, 127
			int val = 0;
			int ID = (((D2ItemProperty)getPlusSkills().get(x)).getRealValue());
			if(((D2ItemProperty)getPlusSkills().get(x)).getiProp()!=127){
				val = (((D2ItemProperty)getPlusSkills().get(x)).getRealValueTwo());
			}
			switch(((D2ItemProperty)getPlusSkills().get(x)).getiProp()){

			case(127):
				//TO ALL SKILLS

				for(int t = 0;t<10;t=t+1){
					incrementSkills('A',ID, t, 'N');
					incrementSkills('B',ID, t, 'N');
					incrementSkills('C',ID, t, 'N');
				}
			break;

			case(83):
				//TO ALL CLASS SKILLS

				if(ID == this.getCharCode()){
					for(int t = 0;t<10;t=t+1){
						incrementSkills('A',val, t, 'N');
						incrementSkills('B',val, t, 'N');
						incrementSkills('C',val, t, 'N');
					}
				}	

			break;

			case(97):
				//NON CLASS SKILL
				//IGNORE I GUESS....
				D2TxtFileItemProperties lSkill = D2TxtFile.SKILL_DESC.getRow(ID);

			if(!D2TxtFile.SKILLS.getRow(ID).get("charclass").equals(cClass)){
				continue;
			}

			String page = lSkill.get("SkillPage");
			int counter = 0;
			for(int z = ID;z> -1;z=z-1){
				if(D2TxtFile.SKILLS.getRow(z).get("charclass").equals(cClass)){
					if(D2TxtFile.SKILL_DESC.getRow(z).get("SkillPage").equals(page)){
						counter = counter + 1;
					}
				}
			}
			switch(Integer.parseInt(page)){

			case 1:
				incrementSkills('A',val, counter-1, 'Y');
				break;

			case 2:
				incrementSkills('B',val, counter-1, 'Y');
				break;

			case 3:
				incrementSkills('C',val, counter-1, 'Y');
				break;

			}
			break;

			case(107):
				//SINGLE SKILL
				lSkill = D2TxtFile.SKILL_DESC.getRow(ID);

			if(!D2TxtFile.SKILLS.getRow(ID).get("charclass").equals(cClass)){
				continue;
			}

			page = lSkill.get("SkillPage");
			counter = 0;
			for(int z = ID;z> -1;z=z-1){
				if(D2TxtFile.SKILLS.getRow(z).get("charclass").equals(cClass)){
					if(D2TxtFile.SKILL_DESC.getRow(z).get("SkillPage").equals(page)){
						counter = counter + 1;
					}
				}
			}
			switch(Integer.parseInt(page)){

			case 1:
				incrementSkills('A',val, counter-1, 'Y');
				break;

			case 2:
				incrementSkills('B',val, counter-1, 'Y');
				break;

			case 3:
				incrementSkills('C',val, counter-1, 'Y');
				break;
			}

			break;
			case(126):
				//ELEM SKILL

//				sorc fire tree
//				amazon fire arrow skills
//				assassins fire claw skill and 3 fire trap skills, (excludes dsentry and phoenix strike)
//				necro corpse explosion, (not sure about fire golem, probably works)
//				druid firestorm, fissure, molten boulder, volcano, and armmegeddon.
//				barb no skills with fire
//				paladin holy fire skill (not sure on this one, probably does)


				switch((int)lCharCode){
				case 0:
//					cClass = "ama";
					incrementSkills('A',val, 1, 'N');
					incrementSkills('A',val, 4, 'N');
					incrementSkills('A',val, 8, 'N');
					break;
				case 1:
//					cClass = "sor";
					for(int t = 0;t<10;t=t+1){
						incrementSkills('A',val, t, 'N');
					}
					break;
				case 2:
					incrementSkills('B',val, 3, 'N');
					incrementSkills('C',val, 8, 'N');
//					cClass = "nec";
					break;
				case 3:
					incrementSkills('B',val, 1, 'N');
//					cClass = "pal";
					break;
				case 5:
					incrementSkills('B',val, 6, 'N');
					incrementSkills('C',val, 0, 'N');
					incrementSkills('C',val, 1, 'N');
					incrementSkills('C',val, 3, 'N');
					incrementSkills('C',val, 6, 'N');
					incrementSkills('C',val, 8, 'N');
//					cClass = "dru";
					break;
				case 6:
					incrementSkills('C',val, 2, 'N');
					incrementSkills('C',val, 6, 'N');

					incrementSkills('A',val, 0, 'N');
					incrementSkills('A',val, 4, 'N');
					incrementSkills('A',val, 7, 'N');
//					cClass = "ass";
					break;
				}
			break;
			case(188):
				//SKILL TREE
				int selector = ID-(this.getCharCode() * 8);
			switch(selector){

			case 0:
				for(int t = 0;t<10;t=t+1){
					incrementSkills('A',val, t, 'N');
				}
				break;
			case 1:
				for(int t = 0;t<10;t=t+1){
					incrementSkills('B',val, t, 'N');
				}
				break;
			case 2:
				for(int t = 0;t<10;t=t+1){
					incrementSkills('C',val, t, 'N');
				}
				break;

			}
			}
		}
	}

	public void changeWep(){

		switch(curWep){
		case 0:
			curWep = 1;
			return;
		case 1:
			curWep = 0;
			return;
		}
	}

	public void equipItem(D2Item item){
		generateCharStats(item, 1);
	}

	public void unequipItem(D2Item item){
		generateCharStats(item, -1);
	}



	public String getCharName()
	{
		return iCharName;
	}

	public String getTitleString()
	{

		return iTitleString;
	}

//	public void updateCharStats(String string, D2Item pItem){

//	if(! pItem.isEquipped()){
//	return;
//	}
//	if(partialSetProps.size() > 0  || fullSetProps.size() > 0){
//	ArrayList set = new ArrayList();
//	set.addAll(partialSetProps);
//	set.addAll(fullSetProps);
//	for(int y = 0;y<set.size();y=y+1){

//	if(!pItem.isTypeArmor()){
//	if(((D2ItemProperty)set.get(y)).getiProp() == 31){   				
//	charStatArray[338] = charStatArray[338] - ((D2ItemProperty)set.get(y)).getRealValue();
//	}
//	if(((D2ItemProperty)set.get(y)).getiProp() == 16){
//	charStatArray[339] = charStatArray[339] - ((D2ItemProperty)set.get(y)).getRealValue();
//	}
//	}

//	if(((D2ItemProperty)set.get(y)).getiProp() < 340){

//	if(((D2ItemProperty)set.get(y)).getiProp() == 188 || ((D2ItemProperty)set.get(y)).getiProp() == 97||((D2ItemProperty)set.get(y)).getiProp() == 126||((D2ItemProperty)set.get(y)).getiProp() == 107||((D2ItemProperty)set.get(y)).getiProp() == 83||((D2ItemProperty)set.get(y)).getiProp() == 127){
//	plusSkillArr.remove(((D2ItemProperty)set.get(y)));
//	}
//	charStatArray[((D2ItemProperty)set.get(y)).getiProp()] = charStatArray[((D2ItemProperty)set.get(y)).getiProp()] - ((D2ItemProperty)set.get(y)).getRealValue();
//	}else{
//	if(((D2ItemProperty)set.get(y)).getiProp() == 1337){
//	//ALL RESISTANCES
//	charStatArray[39] = charStatArray[39] - ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[41] = charStatArray[41] - ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[43] = charStatArray[43] - ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[45] = charStatArray[45] - ((D2ItemProperty)set.get(y)).getRealValue();

//	}else if(((D2ItemProperty)set.get(y)).getiProp() == 1338){
//	//ALL STATS
//	charStatArray[0] = charStatArray[0] - ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[1] = charStatArray[1] - ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[2] = charStatArray[2] - ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[3] = charStatArray[3] - ((D2ItemProperty)set.get(y)).getRealValue();
//	}
//	} 
//	}
//	}
//	equippedSetItems.clear();
//	for(int x = 0;x<iCharItems.size();x=x+1){
//	if(((D2Item)iCharItems.get(x)).isEquipped() && ((D2Item)iCharItems.get(x)).isSet()){
//	equippedSetItems.add(iCharItems.get(x));

//	}
//	}

//	if(equippedSetItems.size() > 0){
//	setSetProps();
//	}
//	if(string.equals("D")){
//	if(pItem.isTypeArmor()){
//	charStatArray[338] = charStatArray[338] + pItem.getiDef();
//	}

//	if(pItem.isTypeArmor()&& (pItem.get_body_position()== 4 || pItem.get_body_position()== 5 || pItem.get_body_position()== 11 || pItem.get_body_position()== 12)){
//	charStatArray[20] = charStatArray[20] + pItem.getBlock();

//	}
//	ArrayList bla = pItem.getAllProps();
//	bla.addAll(partialSetProps);
//	bla.addAll(fullSetProps);

//	for(int y = 0;y<bla.size();y=y+1){
//	if(!pItem.isTypeArmor()){
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 31){   				
//	charStatArray[338] = charStatArray[338] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 16){
//	charStatArray[339] = charStatArray[339] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	}

//	if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 188 || ((D2ItemProperty)bla.get(y)).getiProp() == 97||((D2ItemProperty)bla.get(y)).getiProp() == 126||((D2ItemProperty)bla.get(y)).getiProp() == 107||((D2ItemProperty)bla.get(y)).getiProp() == 83||((D2ItemProperty)bla.get(y)).getiProp() == 127){

//	plusSkillArr.add(((D2ItemProperty)bla.get(y)));
//	}
//	charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	}else{
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
//	//ALL RESISTANCES
//	charStatArray[39] = charStatArray[39] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[41] = charStatArray[41] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[43] = charStatArray[43] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[45] = charStatArray[45] + ((D2ItemProperty)bla.get(y)).getRealValue();

//	}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
//	//ALL STATS
//	charStatArray[0] = charStatArray[0] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[1] = charStatArray[1] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[2] = charStatArray[2] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[3] = charStatArray[3] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	}        	
//	}

//	}else{
//	if(pItem.isTypeArmor()){
//	charStatArray[338] = charStatArray[338] - pItem.getiDef();
//	}

//	if(pItem.isTypeArmor()&& (pItem.get_body_position()== 4 || pItem.get_body_position()== 5 || pItem.get_body_position()== 11 || pItem.get_body_position()== 12)){
//	charStatArray[20] = charStatArray[20] - pItem.getBlock();

//	}

//	if(partialSetProps.size() > 0  || fullSetProps.size() > 0){

//	ArrayList set = new ArrayList();

//	set.addAll(partialSetProps);
//	set.addAll(fullSetProps);

//	for(int y = 0;y<set.size();y=y+1){

//	if(!pItem.isTypeArmor()){
//	if(((D2ItemProperty)set.get(y)).getiProp() == 31){   				
//	charStatArray[338] = charStatArray[338] + ((D2ItemProperty)set.get(y)).getRealValue();
//	}
//	if(((D2ItemProperty)set.get(y)).getiProp() == 16){
//	charStatArray[339] = charStatArray[339] + ((D2ItemProperty)set.get(y)).getRealValue();
//	}
//	}

//	if(((D2ItemProperty)set.get(y)).getiProp() < 340){
//	if(((D2ItemProperty)set.get(y)).getiProp() == 188 || ((D2ItemProperty)set.get(y)).getiProp() == 97||((D2ItemProperty)set.get(y)).getiProp() == 126||((D2ItemProperty)set.get(y)).getiProp() == 107||((D2ItemProperty)set.get(y)).getiProp() == 83||((D2ItemProperty)set.get(y)).getiProp() == 127){
//	plusSkillArr.add(((D2ItemProperty)set.get(y)));
//	}
//	charStatArray[((D2ItemProperty)set.get(y)).getiProp()] = charStatArray[((D2ItemProperty)set.get(y)).getiProp()] + ((D2ItemProperty)set.get(y)).getRealValue();
//	}else{
//	if(((D2ItemProperty)set.get(y)).getiProp() == 1337){
//	//ALL RESISTANCES
//	charStatArray[39] = charStatArray[39] + ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[41] = charStatArray[41] + ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[43] = charStatArray[43] + ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[45] = charStatArray[45] + ((D2ItemProperty)set.get(y)).getRealValue();

//	}else if(((D2ItemProperty)set.get(y)).getiProp() == 1338){
//	//ALL STATS
//	charStatArray[0] = charStatArray[0] + ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[1] = charStatArray[1] + ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[2] = charStatArray[2] + ((D2ItemProperty)set.get(y)).getRealValue();
//	charStatArray[3] = charStatArray[3] + ((D2ItemProperty)set.get(y)).getRealValue();
//	}
//	} 
//	}
//	}

//	ArrayList bla = pItem.getAllProps();
//	for(int y = 0;y<bla.size();y=y+1){

//	if(!pItem.isTypeArmor()){
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 31){   				
//	charStatArray[338] = charStatArray[338] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 16){
//	charStatArray[339] = charStatArray[339] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	}

//	if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 188 || ((D2ItemProperty)bla.get(y)).getiProp() == 97||((D2ItemProperty)bla.get(y)).getiProp() == 126||((D2ItemProperty)bla.get(y)).getiProp() == 107||((D2ItemProperty)bla.get(y)).getiProp() == 83||((D2ItemProperty)bla.get(y)).getiProp() == 127){
//	plusSkillArr.remove(((D2ItemProperty)bla.get(y)));
//	}
//	charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = charStatArray[((D2ItemProperty)bla.get(y)).getiProp()] - ((D2ItemProperty)bla.get(y)).getRealValue();

//	}else{
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
//	//ALL RESISTANCES
//	charStatArray[39] = charStatArray[39] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[41] = charStatArray[41] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[43] = charStatArray[43] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[45] = charStatArray[45] - ((D2ItemProperty)bla.get(y)).getRealValue();

//	}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
//	//ALL STATS
//	charStatArray[0] = charStatArray[0] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[1] = charStatArray[1] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[2] = charStatArray[2] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	charStatArray[3] = charStatArray[3] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	}     
//	}
//	}


//	iCharStats[0] = iInitStats[0] +  (charStatArray[220]*(int)iCharLevel) + charStatArray[0];
//	iCharStats[2] = iInitStats[2] +  (charStatArray[221]*(int)iCharLevel)+ charStatArray[2];
//	iCharStats[1] = iInitStats[1] +  (charStatArray[222]*(int)iCharLevel)+ charStatArray[1];
//	iCharStats[3] = iInitStats[3] +  (charStatArray[223]*(int)iCharLevel)+ charStatArray[3];

//	int[] manaHpStam = setHPManaStam();
//	iCharStats[7] = (((int)Math.floor(((double)(manaHpStam[0] + charStatArray[7])/(double)100) * charStatArray[76]))+(manaHpStam[0] + charStatArray[7])+ (int)Math.floor((charStatArray[216] * 0.125)*(int)iCharLevel))+(hpCounter*20);;
////	OFF ARMOR DEF
//	iCharStats[9] = ((int)Math.floor(((double)(manaHpStam[1] + charStatArray[9])/(double)100) * charStatArray[77]))+(manaHpStam[1] + charStatArray[9])+(int)Math.floor((charStatArray[217]* 0.125)*(int)iCharLevel);
//	iCharStats[11] = (manaHpStam[2] + charStatArray[11])+(charStatArray[242]*(int)iCharLevel);
//	iCharStats[16] = (int)(Math.floor((((double)((int)(Math.floor((double)iCharStats[2]/(double)4))) + charStatArray[338])/(double)100)*charStatArray[339])) + (((int)(Math.floor((double)iCharStats[2]/(double)4))) + charStatArray[338]);

//	iCharStats[18] = iInitStats[18] + charStatArray[39];
//	iCharStats[19] = iInitStats[19] + charStatArray[41];
//	iCharStats[20] = iInitStats[20] + charStatArray[43];
//	iCharStats[21] = iInitStats[21] + charStatArray[45];
//	iCharStats[22] = charStatArray[80]+ (int)Math.round((charStatArray[240] * 0.125) * (iCharLevel - 1));
//	iCharStats[23] = charStatArray[96];
//	iCharStats[24] = charStatArray[105];
//	iCharStats[25] = charStatArray[93];
//	iCharStats[26] = charStatArray[99];
//	iCharStats[27] = charStatArray[79];
//	iCharStats[28] = charStatArray[127];
//	iCharStats[29] = charStatArray[20] + Integer.parseInt(D2TxtFile.CHARSTATS.searchColumns("class", getCharClass()).get("BlockFactor"));
//	iCharStats[29] = (int) Math.floor((iCharStats[29] * (getCharDex() - 15))/(iCharLevel * 2));
//	//16 is DEF
//	//17 AR
//	//18 FR
//	///19 CR
//	//20 LR
//	//21 PR

//	dealWithSkills();
//	}

//	public void updateMercStats(String string, D2Item pItem){

////	statArray = new int[338];

//	if(string.equals("D")){
//	ArrayList bla = pItem.getAllProps();
//	for(int y = 0;y<bla.size();y=y+1){
//	if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
//	mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	}else{
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
//	//ALL RESISTANCES
//	mercStatArray[39] = mercStatArray[39] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[41] = mercStatArray[41] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[43] = mercStatArray[43] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[45] = mercStatArray[45] + ((D2ItemProperty)bla.get(y)).getRealValue();

//	}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
//	//ALL STATS
//	mercStatArray[0] = mercStatArray[0] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[2] = mercStatArray[2] + ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	}        	
//	}

//	}else{
//	ArrayList bla = pItem.getAllProps();
//	for(int y = 0;y<bla.size();y=y+1){
//	if(((D2ItemProperty)bla.get(y)).getiProp() < 340){
//	mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] = mercStatArray[((D2ItemProperty)bla.get(y)).getiProp()] - ((D2ItemProperty)bla.get(y)).getRealValue();

//	}else{
//	if(((D2ItemProperty)bla.get(y)).getiProp() == 1337){
//	//ALL RESISTANCES
//	mercStatArray[39] = mercStatArray[39] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[41] = mercStatArray[41] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[43] = mercStatArray[43] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[45] = mercStatArray[45] - ((D2ItemProperty)bla.get(y)).getRealValue();

//	}else if(((D2ItemProperty)bla.get(y)).getiProp() == 1338){
//	//ALL STATS
//	mercStatArray[0] = mercStatArray[0] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	mercStatArray[2] = mercStatArray[2] - ((D2ItemProperty)bla.get(y)).getRealValue();
//	}
//	}     
//	}
//	}


//	iMercStr = iMercInitStr +  (mercStatArray[220]*iMercLevel) + mercStatArray[0];
//	iMercDex = iMercInitDex +  (mercStatArray[221]*iMercLevel)+ mercStatArray[2];
//	iMercHP = ((int)Math.floor(((double)(iMercInitHP + mercStatArray[7])/(double)100) * mercStatArray[76]))+(iMercInitHP + mercStatArray[7])+(mercStatArray[216]*iMercLevel);
//	iMercDef = (int)Math.floor(((double)(iMercInitDef + (calcMercArmor()) + (mercStatArray[214] * iMercLevel))/(double)100)*(mercStatArray[215] * iMercLevel)) +  iMercInitDef + (calcMercArmor()) + (mercStatArray[214] * iMercLevel)  ;
//	iMercAR = (int)Math.floor(((double)(iMercInitAR + (mercStatArray[19]) + (mercStatArray[224] * iMercLevel))/(double)100)* (mercStatArray[225] * iMercLevel)) +  iMercInitAR + (mercStatArray[19]) + (mercStatArray[224] * iMercLevel)  ;
//	iMercFireRes = iMercInitFireRes + mercStatArray[39];
//	iMercLightRes = iMercInitLightRes + mercStatArray[41];
//	iMercColdRes = iMercInitColdRes + mercStatArray[43];
//	iMercPoisRes = iMercInitPoisRes + mercStatArray[45];

//	}

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

//		if(getCharClass().equals("Barbarian")){
//		out[0] = (4*(iCharStats[3]-25))+(2*(int)(iCharLevel-1)) + 55;
//		out[2] = (1*(iCharStats[3]-25))+(1*(int)(iCharLevel-1)) + 92;
//		out[1] = (1*(iCharStats[1]-10))+(1*(int)(iCharLevel-1)) + 10;
//		}else if(getCharClass().equals("Paladin")){
//		out[0] = (3*(iCharStats[3]-25))+(2*(int)(iCharLevel-1)) + 55;
//		out[2] = (1*(iCharStats[3]-25))+(1*(int)(iCharLevel-1)) + 89;
//		out[1] = ((int)(Math.floor(1.5*(iCharStats[1]-15))))+((int)(Math.floor(1.5*(int)(iCharLevel-1)))) + 15;
//		}else if(getCharClass().equals("Assasin")){
//		out[0] = (3*(iCharStats[3]-20))+(2*(int)(iCharLevel-1)) + 50;
//		out[2] = ((int)Math.floor(1.5*(iCharStats[3]-20)))+(1*(int)(iCharLevel-1)) + 95;
//		out[1] = ((int)Math.floor(1.5*(iCharStats[1]-25)))+((int)Math.floor(1.5*(int)(iCharLevel-1))) + 25;
//		}else if(getCharClass().equals("Amazon")){
//		out[0] = (3*(iCharStats[3]-20))+(2*(int)(iCharLevel-1)) + 50;
//		out[2] = (1*(iCharStats[3]-20))+(1*(int)(iCharLevel-1)) + 84;
//		out[1] = ((int)Math.floor(1.5*(iCharStats[1]-15)))+((int)Math.floor(1.5*(int)(iCharLevel-1))) + 15;
//		}else if(getCharClass().equals("Druid")){
//		out[0] = (2*(iCharStats[3]-25))+((int)(Math.floor(1.5*(int)(iCharLevel-1)))) + 55;
//		out[2] = (1*(iCharStats[3]-25))+(1*(int)(iCharLevel-1)) + 84;
//		out[1] = (2*(iCharStats[1]-20))+(2*(int)(iCharLevel-1)) + 20;
//		}else if(getCharClass().equals("Necromancer")){
//		out[0] = (2*(iCharStats[3]-15))+((int)Math.floor(1.5*(int)(iCharLevel-1))) + 45;
//		out[2] = (1*(iCharStats[3]-15))+(1*(int)(iCharLevel-1)) + 79;
//		out[1] = (2*(iCharStats[1]-25))+(2*(int)(iCharLevel-1)) + 25;
//		}else if(getCharClass().equals("Sorceress")){
//		out[0] = (2*(iCharStats[3]-10))+(1*(int)(iCharLevel-1)) + 40;
//		out[2] = (1*(iCharStats[3]-10))+(1*(int)(iCharLevel-1)) + 74;
//		out[1] = (2*(iCharStats[1]-35))+(2*(int)(iCharLevel-1)) + 35;
//		}

		return out;

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
		return cMercInfo != null;
	}

	// clear all the grids
	// grids keep track of which spots that items
	// can be place are occupied
	public void clearGrid(){

		for (int i = 0; i < iEquipped.length; i++)
		{
			iEquipped[i] = false;
		}
		for (int i = 0; i < iMerc.length; i++)
		{
			iMerc[i] = false;
		}
		for (int i = 0; i < iCorpse.length; i++)
		{
			iCorpse[i] = false;
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

	public boolean markCorpseGrid(D2Item i)
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

			}
			// in a socket, not handled yet
			else if (location == 6)
			{

			}
			// not on the belt
			else
			{
				int body_position = (int) i.get_body_position();
				if (iCorpse[body_position] == true)
				{
					return false;
				}
				else
				{
					iCorpse[body_position] = true;
				}
			}
			break;
		}
		return true;
	}

	public ArrayList getBeltPotions()
	{
		ArrayList lList = new ArrayList();

		for (int i=0;i<4;i++) {
			for (int j=1;j<4;j++) {
				int y = getCharItemIndex(2, i, j);
				if (y != -1) {
					lList.add((D2Item)iCharItems.get(y));
				}
			}
		}
		return lList;
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
		pItem.setCharLvl((int)iCharLevel);
		setModified(true);
	}

	public void addCorpseItem(D2Item pItem)
	{
		iCorpseItems .add(pItem);
		pItem.setCharLvl((int)iCharLevel);
		setModified(true);
	}

	// insert an item into the vector
	public void addMercItem(D2Item pItem)
	{
		iMercItems.add(pItem);
		pItem.setCharLvl((int)iCharLevel);
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

	public D2Item getCorpseItem(int i)
	{
		return (D2Item) iCorpseItems.get(i);
	}

	public int getMercItemNr()
	{
		return iMercItems.size();
	}

	public int getCorpseItemNr()
	{
		return iCorpseItems.size();
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

	public boolean checkCorpsePanel(int panel, int x, int y, D2Item pItem)
	{
		if (panel >= 10)
		{
			if (pItem == null)
			{
				return iCorpse[panel - 10];
			}
			if (iCorpse[panel - 10])
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

	public int getCorpseItemIndex(int panel, int x, int y)
	{
		if (panel == BODY_BELT_CONTENT)
		{
			for (int i = 0; i < iCorpseItems.size(); i++)
			{
				D2Item temp_item = (D2Item) iCorpseItems.get(i);
				if (temp_item.get_location() == panel)
				{
					if (temp_item.get_col() == 4 * y + x)
						return i;
				}
			}
		}
		else if (panel >= 10)
		{
			for (int i = 0; i < iCorpseItems.size(); i++)
			{
				D2Item temp_item = (D2Item) iCorpseItems.get(i);
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
			for (int i = 0; i < iCorpseItems.size(); i++)
			{
				D2Item temp_item = (D2Item) iCorpseItems.get(i);
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

		int lCharItemCountPos = lPos - 2;
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
		// get all the bytes
		iReader.set_byte_pos(0);
		byte[] data = iReader.get_bytes(iReader.get_length());

		byte[] oldchecksum = { data[12], data[13], data[14], data[15] };
		// clear the current checksum
		byte[] checksum = { 0, 0, 0, 0 }; // byte checksum
		iReader.setBytes(12, checksum);
		byte[] length = new byte[4];
		length[3] = (byte) ((0xff000000 & data.length) >>> 24);
		length[2] = (byte) ((0x00ff0000 & data.length) >>> 16);
		length[1] = (byte) ((0x0000ff00 & data.length) >>> 8);
		length[0] = (byte) (0x000000ff & data.length);
		iReader.setBytes(8, length);
		iReader.set_byte_pos(0);
		long lCheckSum = calculateCheckSum();
		checksum[3] = (byte) ((0xff000000 & lCheckSum) >>> 24);
		checksum[2] = (byte) ((0x00ff0000 & lCheckSum) >>> 16);
		checksum[1] = (byte) ((0x0000ff00 & lCheckSum) >>> 8);
		checksum[0] = (byte) (0x000000ff & lCheckSum);
		iReader.setBytes(12, checksum);
		iReader.save();
		setModified(false);
	}

	public int getGold()
	{
		return (int)iReadStats[14];
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
		return (int)iReadStats[15];
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

	public ArrayList getInitSkillListA(){
		return iCharInitSkillsA;
	}

	public ArrayList getInitSkillListB(){
		return iCharInitSkillsB;
	}

	public ArrayList getInitSkillListC(){
		return iCharInitSkillsC;
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


	public String fullDumpStr()
	{

		String out = "";
		String[] skillStrArr = new String[3];
		skillStrArr[0] ="\n";
		skillStrArr[1] ="\n";
		skillStrArr[2] ="\n";
		Iterator[] it = {iCharInitSkillsA.iterator(),iCharInitSkillsB.iterator(),iCharInitSkillsC.iterator(),iCharSkillsA.iterator(),iCharSkillsB.iterator(),iCharSkillsC.iterator()};

		ArrayList skillArr = D2TxtFile.SKILLS.searchColumnsMultipleHits("charclass", cClass);
		for(int x = 0;x<skillArr.size();x=x+1){

			int page = Integer.parseInt((D2TxtFile.SKILL_DESC.getRow(Integer.parseInt(((D2TxtFileItemProperties)skillArr.get(x)).get("Id")))).get("SkillPage"));

			switch(page){

			case 1:
				skillStrArr[0] = skillStrArr[0] + D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",((D2TxtFileItemProperties)skillArr.get(x)).get("skilldesc")).get("str name")) + ": " +  it[0].next() + "/" + it[3].next() + "\n";
//				System.out.println(((D2TxtFileItemProperties)skillArr.get(x)).get("skill"));
				break;

			case 2:
				skillStrArr[1] = skillStrArr[1] + D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",((D2TxtFileItemProperties)skillArr.get(x)).get("skilldesc")).get("str name")) + " : " +  it[1].next()+ "/" + it[4].next() + "\n";
//				System.out.println(((D2TxtFileItemProperties)skillArr.get(x)).get("skill"));
				break;

			case 3:
				skillStrArr[2] = skillStrArr[2] + D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",((D2TxtFileItemProperties)skillArr.get(x)).get("skilldesc")).get("str name"))  + " : " +  it[2].next() + "/" + it[5].next()+ "\n";
//				System.out.println(iCharSkillsC.get(x));
				break;

			}

		}

		out = out + skillStrArr[0] + skillStrArr[1]+skillStrArr[2] + "\n";

		if ( iCharItems != null )
		{
			for ( int i = 0 ; i < iCharItems.size() ; i++ )
			{
				D2Item lItem = (D2Item) iCharItems.get(i);
				out = out + lItem.toString(1);
				out = out + ("\n");
				out = out + ("\n");
			}
		}


		out = out + ("Mercenary:"+"\n");
		out = out + ("\n");

		if ( iMercItems != null )
		{

			for ( int i = 0 ; i < iMercItems.size() ; i++ )
			{
				D2Item lItem = (D2Item) iMercItems.get(i);
				out = out + lItem.toString(1);
				out = out + ("\n");
				out = out + ("\n");
			}
		}

		return out;
	}

	public String getMercName() {
		return iMercName;
	}

	public String getMercType() {
		return iMercType;
	}

	public long getMercExp() {
		return iMercExp;
	}

	public String getMercRace() {
		return iMercRace;
	}

	public int getMercLevel() {
		return iMercLevel;
	}

	public boolean getMercDead() {
		return iMercDead;
	}

	public int getMercStr() {
		return iMercStr;
	}

	public int getMercDex() {
		return iMercDex;
	}

	public int getMercHP() {
		return iMercHP;
	}

	public long getMercDef() {
		return iMercDef;
	}

	public int getMercAR() {
		return iMercAR;
	}

	public int getMercFireRes() {
		return iMercFireRes;
	}

	public int getMercColdRes() {
		return iMercColdRes;
	}

	public int getMercLightRes() {
		return iMercLightRes;
	}

	public int getMercPoisRes() {
		return iMercPoisRes;
	}

	public int getMercInitStr() {
		return iMercInitStr;
	}

	public int getMercInitDex() {
		return iMercInitDex;
	}

	public int getMercInitHP() {
		return iMercInitHP;
	}

	public long getMercInitDef() {
		return iMercInitDef;
	}

	public int getMercInitAR() {
		return iMercInitAR;
	}

	public int getMercInitFireRes() {
		return iMercInitFireRes;
	}

	public int getMercInitColdRes() {
		return iMercInitColdRes;
	}

	public int getMercInitLightRes() {
		return iMercInitLightRes;
	}

	public int getMercInitPoisRes() {
		return iMercInitPoisRes;
	}

	public String getCharClass() {
		return iCharClass;
	}

	public long getCharExp() {
		return iReadStats[13];
	}

	public boolean getCharDead() {
		return true;
	}

	public int getCharStr() {
		return cStats[0];
	}

	public int getCharDex() {
		return cStats[4];
	}
	public int getCharNrg() {
		return cStats[2];
	}

	public int getCharVit() {
		return cStats[6];
	}

	public int getCharRemStat() {
		return (int) iReadStats[4];
	}

	public int getCharRemSkill() {
		return (int) iReadStats[5];
	}

	public int getCharMana() {
		return cStats[10];
	}
	public int getCharStam() {
		return cStats[12];
	}

	public int getCharHP() {
		return cStats[8];
	}

	public long getCharDef() {
		return cDef ;
	}

	public int getCharAR() {
		return cStats[14];
	}

	public int getCharFireRes() {
		return cStats[18];
	}

	public int getCharColdRes() {
		return cStats[20];
	}

	public int getCharLightRes() {
		return cStats[19];
	}

	public int getCharPoisRes() {
		return cStats[21];
	}

	public int getCharInitStr() {
		return (int) iReadStats[0];
	}

	public int getCharInitDex() {
		return (int) iReadStats[2];
	}

	public int getCharInitNrg() {
		return (int) iReadStats[1];
	}

	public int getCharInitVit() {
		return (int) iReadStats[3];
	}

	public int getCharInitMana() {
		return (int) iReadStats[9];
	}

	public int getCharInitStam() {
		return (int) iReadStats[11];
	}

	public int getCharInitHP() {
		return (int) iReadStats[7];
	}

	public int getCharLevel() {
		return (int)iCharLevel;
	}

	public int getCharMF(){
		return cStats[22];
	}

	public int getCharGF(){
		return cStats[28];
	}

	public int getCharFHR(){
		return cStats[27];
	}

	public int getCharIAS(){
		return cStats[26];
	}

	public int getCharFRW(){
		return cStats[24];
	}
	public int getCharFCR(){
		return cStats[25];
	}

	public int getCharSkillRem(){
		return (int) iReadStats[5];
	}

	public int getCharCode() {
		return (int)lCharCode;
	}

	public String[][] getQuests(){
		return iQuests;
	}

	public String[][] getWaypoints(){
		return iWaypoints;
	}


	public int getPlusAllSkills(){
		return cStats[30];
	}


	public ArrayList getPlusSkills(){
		return plusSkillArr;
	}

	public D2Item getGolemItem() {
		return golemItem;
	}

	public void incrementSkills(char c, int val, int i, char ch) {


		switch(ch){
		case 'Y':
			switch(c){
			case('A'):

				iCharSkillsA.set(i, new Integer(((Integer)iCharSkillsA.get(i)).intValue() + val));

			break;
			case('B'):

				iCharSkillsB.set(i, new Integer(((Integer)iCharSkillsB.get(i)).intValue() + val));

			break;
			case('C'):

				iCharSkillsC.set(i, new Integer(((Integer)iCharSkillsC.get(i)).intValue() + val));

			break;
			}
			break;

		case 'N':
			switch(c){
			case('A'):
				if(!iCharSkillsA.get(i).equals(new Integer(0))){
					iCharSkillsA.set(i, new Integer(((Integer)iCharSkillsA.get(i)).intValue() + val));
				}
			break;
			case('B'):
				if(!iCharSkillsB.get(i).equals(new Integer(0))){
					iCharSkillsB.set(i, new Integer(((Integer)iCharSkillsB.get(i)).intValue() + val));
				}
			break;
			case('C'):
				if(!iCharSkillsC.get(i).equals(new Integer(0))){
					iCharSkillsC.set(i, new Integer(((Integer)iCharSkillsC.get(i)).intValue() + val));
				}
			break;
			}
			break;
		}




	}

	public void resetSkills(){
		iCharSkillsA.clear();
		iCharSkillsB.clear();
		iCharSkillsC.clear();

		iCharSkillsA.addAll(iCharInitSkillsA);
		iCharSkillsB.addAll(iCharInitSkillsB);
		iCharSkillsC.addAll(iCharInitSkillsC);
	}

	public void updateCharStats(String string, D2Item temp) {
		// TODO Auto-generated method stub

	}

	public void updateMercStats(String string, D2Item dropItem) {
		// TODO Auto-generated method stub

	}


}