/*******************************************************************************
 * 
 * Copyright 2007 Randall & Silospen
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
package randall.d2files;


import gomule.gui.D2FileManager;
import gomule.item.D2Prop;

import java.io.*;
import java.util.*;

/**
 * @author Marco
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class D2TxtFile
{
	private static String    sMod;

	public static D2TxtFile  MISC;
	public static D2TxtFile  ARMOR;
	public static D2TxtFile  WEAPONS;
	public static D2TxtFile  UNIQUES;
	public static D2TxtFile  SETITEMS;
	public static D2TxtFile  PREFIX;
	public static D2TxtFile  SUFFIX;
	public static D2TxtFile  RAREPREFIX;
	public static D2TxtFile  RARESUFFIX;
	public static D2TxtFile  RUNES;
	public static D2TxtFile  ITEM_TYPES;
	public static D2TxtFile  ITEM_STAT_COST;
	public static D2TxtFile  SKILL_DESC;
	public static D2TxtFile  SKILLS;
	public static D2TxtFile  GEMS;
	public static Properties GOMULE_PROPS;
	public static D2TxtFile PROPS;
	public static D2TxtFile HIRE;
	public static D2TxtFile FULLSET;
	public static D2TxtFile CHARSTATS;
	public static D2TxtFile AUTOMAGIC;

	/**
	 * DROP CALC
	 */
	 public static D2TxtFile MONSTATS;
	public static D2TxtFile TCS;
	public static D2TxtFile LEVELS;
	public static D2TxtFile SUPUNIQ;
	public static D2TxtFile ITEMRATIO;

	//    public static D2TxtFile PROPERTIES;

	private String           iFileName;
	private ArrayList        iHeader;
	private ArrayList        iData;


	public static void readAllFiles(String pMod)
	{


		sMod = pMod;
		MISC = new D2TxtFile("Misc");
		ARMOR = new D2TxtFile("armor");
		WEAPONS = new D2TxtFile("weapons");
		UNIQUES = new D2TxtFile("UniqueItems");
		SETITEMS = new D2TxtFile("SetItems");
		PREFIX = new D2TxtFile("MagicPrefix");
		SUFFIX = new D2TxtFile("MagicSuffix");
		RAREPREFIX = new D2TxtFile("RarePrefix");
		RARESUFFIX = new D2TxtFile("RareSuffix");
		RUNES = new D2TxtFile("Runes");
		ITEM_TYPES = new D2TxtFile("ItemTypes");
		ITEM_STAT_COST = new D2TxtFile("ItemStatCost");
		SKILL_DESC = new D2TxtFile("SkillDesc");
		SKILLS = new D2TxtFile("Skills");
		GEMS = new D2TxtFile("Gems");
		PROPS = new D2TxtFile("Properties");
		MONSTATS = new D2TxtFile("MonStats");
		TCS = new D2TxtFile("TreasureClassEx");
		LEVELS = new D2TxtFile("Levels");
		SUPUNIQ = new D2TxtFile("SuperUniques");
		HIRE = new D2TxtFile("Hireling");
		FULLSET = new D2TxtFile("Sets");
		CHARSTATS = new D2TxtFile("CharStats");
		AUTOMAGIC = new D2TxtFile("automagic");
		ITEMRATIO = new D2TxtFile("ItemRatio");

		GOMULE_PROPS = new Properties();
		try
		{
			FileInputStream lFileIn = new FileInputStream(sMod + File.separator + "GoMuleProps.properties");
			GOMULE_PROPS.load(lFileIn);
			lFileIn.close();
		}
		catch (Exception pEx)
		{
			D2FileManager.displayErrorDialog(pEx);
		}
	}

	public String getFileName()
	{
		return iFileName;
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
			return "Assassin";
		}
		return "<none>";
	}

	public int getRowSize()
	{
		return iData.size();
	}


	public static ArrayList propToStat(String pCode, String pMin, String pMax, String pParam, int qFlag){

		ArrayList outArr = new ArrayList();
		for(int x = 1;x<8;x++){

			if(D2TxtFile.PROPS.searchColumns("code", pCode).get("stat" + x).equals("")){
				break;
			}

			int[] pVals = {0,0,0};


			if(!pMin.equals("")){
				try{
					pVals[0] = Integer.parseInt(pMin);
				}catch(NumberFormatException e){
					return outArr;
				}
			};

			if(!pMax.equals("")){
				try{
					pVals[1] = Integer.parseInt(pMax);
				}catch(NumberFormatException e){
					return outArr;
				}
			};

			if(!pParam.equals("")){
				try{
					pVals[2] = Integer.parseInt(pParam);
				}catch(NumberFormatException e){
					return outArr;
				}
			};

//			if(pVals[0] != pVals[1]){

			if(D2TxtFile.PROPS.searchColumns("code", pCode).get("stat" + x).contains("max")){
				pVals[0] = pVals[1];
			}else if(D2TxtFile.PROPS.searchColumns("code", pCode).get("stat" + x).contains("length")){
				if(pVals[2] != 0){
					pVals[0] = pVals[2];
				}
			}
			pVals[2] = 0;
//			}
			System.out.println(Integer.parseInt(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",D2TxtFile.PROPS.searchColumns("code", pCode).get("stat" + x)).get("ID")));
			outArr.add(new D2Prop(Integer.parseInt(D2TxtFile.ITEM_STAT_COST.searchColumns("Stat",D2TxtFile.PROPS.searchColumns("code", pCode).get("stat" + x)).get("ID")), pVals, qFlag));

		}
		return outArr;

	}

	public static D2TxtFileItemProperties search(String pCode)
	{
		//        System.err.println("Test1: " + MISC.searchAllData(pCode) );
		D2TxtFileItemProperties lFound = MISC.searchColumns("code", pCode);
		if (lFound == null)
		{
			lFound = ARMOR.searchColumns("code", pCode);
		}
		if (lFound == null)
		{
			lFound = WEAPONS.searchColumns("code", pCode);
		}
		//        if ( lFound != null )
		//        {
		//            System.err.println("Test1: " + lFound.getName() );
		//            System.err.println("Test2: " + lFound.getTblName() );
		//        }
		//        else
		//        {
		//            System.err.println("Test: Not found" );
		//        }
		return lFound;
	}

	private D2TxtFile(String pFileName)
	{
		iFileName = pFileName;
		try
		{
			String lSeparator = new Character((char) 9).toString();
			// read single txt file
			FileReader lFileIn = new FileReader(sMod + File.separator + iFileName + ".txt");

			BufferedReader lIn = new BufferedReader(lFileIn);

			String lFirstLine = lIn.readLine();

			//            System.err.println("Test: " + lFirstLine );

			iHeader = split(lFirstLine, lSeparator);

			iData = new ArrayList();
			String lLine = lIn.readLine();

			boolean lSkipExpansion = "UniqueItems".equals(iFileName) || "SetItems".equals(iFileName);

			while (lLine != null)
			{
				ArrayList lSplit = split(lLine, lSeparator);

				if (lSkipExpansion && lSplit.get(0).equals("Expansion"))
				{
					// skip
//					System.err.println("Skip: " + lLine);
				}
				else
				{
					iData.add(lSplit);
				}
				lLine = lIn.readLine();
			}

			lFileIn.close();
			lIn.close();
		}
		catch (Exception pEx)
		{
			D2FileManager.displayErrorDialog(pEx);
		}
	}

	private ArrayList split(String pText, String pSplit)
	{
		ArrayList lList = new ArrayList();

		int lCurrent = 0;
		int lIndex = pText.indexOf(pSplit, lCurrent);

		while (lIndex != -1)
		{
			lList.add(pText.substring(lCurrent, lIndex));
			lCurrent = lIndex + 1;
			lIndex = pText.indexOf(pSplit, lCurrent);
		}

		return lList;
	}

	protected String getValue(int pRowNr, String pCol)
	{
		int lColNr = iHeader.indexOf(pCol);

		if (lColNr != -1 && pRowNr < iData.size())
		{
			return (String) ((ArrayList) iData.get(pRowNr)).get(lColNr);
		}

		return null;
	}

	public D2TxtFileItemProperties getRow(int pRowNr)
	{
		return new D2TxtFileItemProperties(this, pRowNr);
	}

	public D2TxtFileItemProperties searchColumns(String pCol, String pText)
	{
		int lColNr = iHeader.indexOf(pCol);

		if (lColNr != -1)
		{
			for (int i = 0; i < iData.size(); i++)
			{
				if(!((ArrayList)iData.get(i)).isEmpty()){
					if (((ArrayList) iData.get(i)).get(lColNr).equals(pText))
					{
						return new D2TxtFileItemProperties(this, i);
					}
				}
			}
		}

		return null;
	}

	public ArrayList searchColumnsMultipleHits(String pCol, String pText)
	{
		ArrayList hits = new ArrayList();
		int lColNr = iHeader.indexOf(pCol);

		if (lColNr != -1)
		{
			for (int i = 0; i < iData.size(); i++)
			{
				if(!((ArrayList)iData.get(i)).isEmpty()){
					if (((ArrayList) iData.get(i)).get(lColNr).equals(pText))
					{
						hits.add(new D2TxtFileItemProperties(this, i));
					}
				}
			}
		}

		return hits;
	}

	public D2TxtFileItemProperties searchRuneWord(ArrayList pList)
	{
		int lRuneNr[] = new int[] { iHeader.indexOf("Rune1"), iHeader.indexOf("Rune2"), iHeader.indexOf("Rune3"), iHeader.indexOf("Rune4"), iHeader.indexOf("Rune5"), iHeader.indexOf("Rune6") };
		for (int i = 0; i < iData.size(); i++)
		{
			ArrayList lRW = new ArrayList();
			for (int j = 0; j < lRuneNr.length; j++)
			{
				String lFile = (String) ((ArrayList) iData.get(i)).get(lRuneNr[j]);

				if (lFile != null && !lFile.equals(""))
				{
					lRW.add(lFile);
				}
				else
				{
					break;
				}
			}

			if (pList.size() == lRW.size())
			{
				boolean lIsRuneWord = true;
				for (int j = 0; j < pList.size() && lIsRuneWord; j++)
				{
					if (!((String) lRW.get(j)).equals((String) pList.get(j)))
					{
						lIsRuneWord = false;
					}
				}
				if (lIsRuneWord)
				{
					return new D2TxtFileItemProperties(this, i);
				}
			}
		}
		return null;
	}

	private int searchAllData(String pText)
	{
		for (int i = 0; i < iData.size(); i++)
		{
			if (((ArrayList) iData.get(i)).contains(pText))
			{
				int lHeader = ((ArrayList) iData.get(i)).indexOf(pText);
				System.err.println("Found at: " + lHeader + " - " + iHeader.get(lHeader));
				return i;
			}
		}

		return -1;
	}

	public static Boolean getGoMuleProperty(String pKey)
	{
		String lProperty = GOMULE_PROPS.getProperty(pKey);
		if ("true".equals(lProperty))
		{
			return Boolean.TRUE;
		}
		if ("false".equals(lProperty))
		{
			return Boolean.FALSE;
		}

		return null;
	}


}