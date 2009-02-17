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

package gomule.item;

import gomule.gui.*;
import gomule.util.*;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import randall.d2files.*;
import randall.flavie.*;

//an item class
//manages one item
//keeps the a copy of the bytes representing
//an item and a bitreader to manipulate them
//also stores most data from the item in

//this class is NOT designed to edit items
//any methods that allow the item's bytes
//to be written only exist to facillitate
//moving items. writing other item fields
//is not supported by this class
public class D2Item implements Comparable, D2ItemInterface {

	private D2PropCollection iProps;

	private ArrayList iSocketedItems;

	// general item data
	private int flags;

	private short version;

	private short location;

	private short body_position;

	private short row;

	private short col;

	private short panel;

	private String item_type;

	// additional data for complex items
	private long iSocketNrFilled = 0;

	private long iSocketNrTotal = 0;

	private long fingerprint;

	private short ilvl;

	private short quality;

	private short gfx_num;

	private D2TxtFileItemProperties automod_info;

	private short low_quality;

	private short hi_quality;

	private short magic_prefix;

	private short magic_suffix;

	private short set_id;

	private short rare_name_1;

	private short rare_name_2;

	private short[] rare_prefixes;

	private short[] rare_suffixes;

	private short unique_id;

	private int runeword_id;

	private String personalization;

	// useful item data to keep on hand
	// that's in txt files rather than
	// in the character file
	private short width;

	private short height;

	private String image_file;

	private String name;

	private D2TxtFileItemProperties iItemType;

	private String iType;

	private String iType2;

	private boolean iEthereal;

	private boolean iSocketed;

	private boolean iThrow;

	private boolean iMagical;

	private boolean iRare;

	private boolean iCrafted;

	private boolean iSet;

	private boolean iUnique;

	private boolean iRuneWord;

	private boolean iSmallCharm;

	private boolean iLargeCharm;

	private boolean iGrandCharm;

	private boolean iJewel;

	private boolean iEquipped = false;

	private boolean iGem;

	private boolean iStackable = false;

	private ArrayList iGemProps;

	private boolean iRune;

	private boolean iTypeMisc;

	private boolean iIdentified;

	private boolean iTypeWeapon;

	private boolean iTypeArmor;

	protected String iItemName;

	protected String iBaseItemName;

	protected String iItemNameNoPersonalising;

	private long iCurDur;

	private long iMaxDur;

	private long iDef;

	private long cBlock;

	private long iBlock;

	private long iInitDef;

	private long iMinDmg;

	private long iMaxDmg;

	// BARBARIANS
	private long i2MinDmg;

	private long i2MaxDmg;

	private long iinitMinDmg;

	private long iinitMaxDmg;

	// BARBARIANS
	private long iinit2MinDmg;

	private long iinit2MaxDmg;

	// 0 FOR BOTH 1 FOR 1H 2 FOR 2H
	private int iWhichHand;

	// private int iLvl;
	private String iFP;

	private boolean hasGUID;

	private String iGUID;

	private boolean iBody = false;

	private String iBodyLoc1 = null;

	private String iBodyLoc2 = null;

	private boolean iBelt = false;

	private D2BitReader iItem;

	private String iFileName;

	private boolean iIsChar;

	private int iCharLvl;

	private int iReqLvl = -1;

	private int iReqStr = -1;

	private int iReqDex = -1;

	private String iSetName;

	private int setSize;

	private String iItemQuality = "none";

	public D2Item(String pFileName, D2BitReader pFile, int pPos, long pCharLvl)
	throws Exception {
		iFileName = pFileName;
		iIsChar = iFileName.endsWith(".d2s");
		iCharLvl = (int)pCharLvl;
		try {
			pFile.set_byte_pos(pPos);
			read_item(pFile, pPos);
			int lCurrentReadLength = pFile.get_pos() - pPos * 8;
			int lNextJMPos = pFile.findNextFlag("JM", pFile.get_byte_pos());
			int lLengthToNextJM = lNextJMPos - pPos;

			if (lLengthToNextJM < 0) {
				int lNextKFPos = pFile.findNextFlag("kf", pFile.get_byte_pos());
				int lNextJFPos = pFile.findNextFlag("jf", pFile.get_byte_pos());
				if (lNextJFPos >= 0) {

					lLengthToNextJM = lNextJFPos - pPos;

				} else if (lNextKFPos >= 0) {
					lLengthToNextJM = lNextKFPos - pPos;
				}

				else {
					// last item (for stash only)
					lLengthToNextJM = pFile.get_length() - pPos;
				}
			} else if ((lNextJMPos > pFile.findNextFlag("kf", pFile
					.get_byte_pos()))
					&& (pPos < pFile.findNextFlag("kf", pFile.get_byte_pos()))) {
				lLengthToNextJM = pFile
				.findNextFlag("kf", pFile.get_byte_pos())
				- pPos;
			} else if ((lNextJMPos > pFile.findNextFlag("jf", pFile
					.get_byte_pos()))
					&& (pPos < pFile.findNextFlag("jf", pFile.get_byte_pos()))) {

				lLengthToNextJM = pFile
				.findNextFlag("jf", pFile.get_byte_pos())
				- pPos;

			}

			int lDiff = ((lLengthToNextJM * 8) - lCurrentReadLength);
			if (lDiff > 7) {
				throw new D2ItemException(
						"Item not read complete, missing bits: " + lDiff
						+ getExStr());
			}

			pFile.set_byte_pos(pPos);
			iItem = new D2BitReader(pFile.get_bytes(lLengthToNextJM));
			pFile.set_byte_pos(pPos + lLengthToNextJM);
		} catch (D2ItemException pEx) {
			throw pEx;
		} catch (Exception pEx) {
			pEx.printStackTrace();
			throw new D2ItemException("Error: " + pEx.getMessage() + getExStr());
		}
	}

	public String getFileName() {
		return iFileName;
	}

	public boolean isCharacter() {
		return iIsChar;
	}

	// bit checker
	// if the specified bit of flags is set,
	// return true
	private boolean check_flag(int bit) {
		if (((flags >>> (32 - bit)) & 1) == 1)
			return true;
		else
			return false;
	}

	// read basic information from the bytes
	// common to all items, then split based on
	// whether the item is an ear
	private void read_item(D2BitReader pFile, int pos) throws Exception {
		pFile.skipBytes(2);
		flags = (int) pFile.unflip(pFile.read(32), 32); // 4 bytes

		iSocketed = check_flag(12);
		iEthereal = check_flag(23);
		iRuneWord = check_flag(27);
		iIdentified = check_flag(5);
		version = (short) pFile.read(8);

		pFile.skipBits(2);
		location = (short) pFile.read(3);

		body_position = (short) pFile.read(4);
		col = (short) pFile.read(4);
		row = (short) pFile.read(4);
		panel = (short) pFile.read(3);

		// flag 17 is an ear
		if (!check_flag(17)) {

			readExtend(pFile);
		} else {
			read_ear(pFile);
		}
		if (personalization == null) {
			iItemNameNoPersonalising = iItemName;
		}

		if (isTypeWeapon()) {

			if (iEthereal) {
				applyEthDmg();
			}

			//Blunt does 150 damage to undead
			if (iType.equals("club") || iType.equals("scep")|| iType.equals("mace") || iType.equals("hamm")){
				iProps.add(new D2Prop(122, new int[]{150}, 0));
			}

//			ArrayList lvlSkills = new ArrayList();

//			for (int x = 0; x < iProperties.size(); x = x + 1) {
//			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 107
//			|| ((D2ItemProperty) iProperties.get(x)).getiProp() == 97) {
//			lvlSkills.add(iProperties.get(x));
//			}
//			}
//			if (lvlSkills.size() > 0) {
//			modifyLvl(lvlSkills);
//			}


		} else if (isTypeArmor()) {

			if(iType.equals("ashd")||iType.equals("shie")||iType.equals("head")){
//				applyBlock();	
			}

//			ArrayList lvlSkills = new ArrayList();
//			for (int x = 0; x < iProperties.size(); x = x + 1) {
//			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 107
//			|| ((D2ItemProperty) iProperties.get(x)).getiProp() == 97) {
//			lvlSkills.add(iProperties.get(x));
//			}
//			}
//			if (lvlSkills.size() > 0) {
//			modifyLvl(lvlSkills);
//			}

		}
		
		if(iProps != null && location != 6){
			iProps.tidy();
		}
	}

//	private void applyReqLPlus() {

//	for (int x = 0; x < iProperties.size(); x = x + 1) {
//	if((((D2ItemProperty) iProperties.get(x)).getiProp())==92){
//	iReqLvl=iReqLvl+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}

//	}

//	}

	// read ear related data from the bytes
	private void read_ear(D2BitReader pFile) {

		int eClass = (int) pFile.read(3);
		int eLevel = (int) (pFile.read(7));

		StringBuffer lCharName = new StringBuffer();
		for (int i = 0; i < 18; i++)
			// while(1==1)
		{
			long lChar = pFile.read(7);
			if (lChar != 0) {
				lCharName.append((char) lChar);
			} else {
				break;
			}
		}
		iItemType = D2TxtFile.search("ear");
		height = Short.parseShort(iItemType.get("invheight"));
		width = Short.parseShort(iItemType.get("invwidth"));
		image_file = iItemType.get("invfile");
		name = iItemType.get("name");
		iBaseItemName = iItemName = lCharName.toString() + "'s Ear";

		iProps = new D2PropCollection();
		iProps.add(new D2Prop(185,new int[]{eClass, eLevel}, 0,true,39));

		// for (int i = 0; i < 18; i++) {
		// pFile.read(7); // name
		// }
	}

	// read non ear data from the bytes,
	// setting class variables for easier access
	private void readExtend(D2BitReader pFile) throws Exception {
		// 9,5 bytes already read (common data)
		item_type = "";
		// skip spaces or hashing won't work
		for (int i = 0; i < 4; i++) {
			char c = (char) pFile.read(8); // 4 bytes
			if (c != 32) {
				item_type += c;
			}
		}

		iItemType = D2TxtFile.search(item_type);
		height = Short.parseShort(iItemType.get("invheight"));
		width = Short.parseShort(iItemType.get("invwidth"));
		image_file = iItemType.get("invfile");
		name = iItemType.get("name");

		String lD2TxtFileName = iItemType.getFileName();
		if (lD2TxtFileName != null) {
			iTypeMisc = ("Misc".equals(lD2TxtFileName));
			iTypeWeapon = ("weapons".equals(lD2TxtFileName));
			iTypeArmor = ("armor".equals(lD2TxtFileName));
		}

		iType = iItemType.get("type");
		iType2 = iItemType.get("type2");


		//Shields - block chance.
		if(iType.equals("ashd")||iType.equals("shie")||iType.equals("head")){
			cBlock = Long.parseLong(iItemType.get("block"));
		}

		// Requerements
		if (iTypeMisc) {
			iReqLvl = getReq(iItemType.get("levelreq"));
		} else if (iTypeArmor) {
			iReqLvl = getReq(iItemType.get("levelreq"));
			iReqStr = getReq(iItemType.get("reqstr"));

			D2TxtFileItemProperties qualSearch = D2TxtFile.ARMOR.searchColumns(
					"normcode", item_type);
			iItemQuality = "normal";
			if (qualSearch == null) {
				qualSearch = D2TxtFile.ARMOR.searchColumns("ubercode",
						item_type);
				iItemQuality = "exceptional";
				if (qualSearch == null) {
					qualSearch = D2TxtFile.ARMOR.searchColumns("ultracode",
							item_type);
					iItemQuality = "elite";
				}
			}

		} else if (iTypeWeapon) {
			iReqLvl = getReq(iItemType.get("levelreq"));
			iReqStr = getReq(iItemType.get("reqstr"));
			iReqDex = getReq(iItemType.get("reqdex"));

			D2TxtFileItemProperties qualSearch = D2TxtFile.WEAPONS
			.searchColumns("normcode", item_type);
			iItemQuality = "normal";
			if (qualSearch == null) {
				qualSearch = D2TxtFile.WEAPONS.searchColumns("ubercode",
						item_type);
				iItemQuality = "exceptional";
				if (qualSearch == null) {
					qualSearch = D2TxtFile.WEAPONS.searchColumns("ultracode",
							item_type);
					iItemQuality = "elite";
				}
			}
		}

		String lItemName = D2TblFile.getString(item_type);
		if (lItemName != null) {
			iItemName = lItemName;
			iBaseItemName = iItemName;
		}

		// flag 22 is a simple item (extend1)
		if (!check_flag(22)) {
			readExtend1(pFile);
		}

		// gold (?)
		if ("gold".equals(item_type)) {
			if (pFile.read(1) == 0) {
				pFile.read(12);
			} else {
				pFile.read(32);
			}
		}

		long lHasRand = pFile.read(1);

		if (lHasRand == 1) { // GUID ???
			if (iType.startsWith("rune") || iType.startsWith("gem")
					|| iType.startsWith("amu") || iType.startsWith("rin")
					|| !isTypeMisc()) {
				hasGUID = true;

				iGUID = "0x" + Integer.toHexString((int) pFile.read(32))
				+ " 0x" + Integer.toHexString((int) pFile.read(32))
				+ " 0x" + Integer.toHexString((int) pFile.read(32));
			} else {
				pFile.read(3);
			}
		}

		// flag 22 is a simple item (extend2)
		if (!check_flag(22)) {
			readExtend2(pFile);
		}

		if (iType != null && iType2 != null && iType.startsWith("gem")) {
			if (iType2.equals("gem0") || iType2.equals("gem1")
					|| iType2.equals("gem2") || iType2.equals("gem3")
					|| iType2.equals("gem4")) {
				iProps = new D2PropCollection();
				readPropertiesGems(pFile);
				iGem = true;
			}
		}

		if (iType != null && iType2 != null && iType.startsWith("rune")) {
			iProps = new D2PropCollection();
			readPropertiesGems(pFile);
			iRune = true;
		}

		D2TxtFileItemProperties lItemType = D2TxtFile.ITEM_TYPES.searchColumns(
				"Code", iType);

		if (lItemType == null) {
			lItemType = D2TxtFile.ITEM_TYPES.searchColumns("Equiv1", iType);
			if (lItemType == null) {
				lItemType = D2TxtFile.ITEM_TYPES.searchColumns("Equiv2", iType);
			}
		}

		if ("1".equals(lItemType.get("Body"))) {
			iBody = true;
			iBodyLoc1 = lItemType.get("BodyLoc1");
			iBodyLoc2 = lItemType.get("BodyLoc2");
		}
		if ("1".equals(lItemType.get("Beltable"))) {
			iBelt = true;

			readPropertiesPots(pFile);
		}

		int lLastItem = pFile.get_byte_pos();

		if (iSocketNrFilled > 0) {
			iGemProps = new ArrayList();
			iSocketedItems = new ArrayList();

			for (int i = 0; i < iSocketNrFilled; i++) {
				int lStartNewItem = pFile.findNextFlag("JM", lLastItem);
				D2Item lSocket = new D2Item(iFileName, pFile, lStartNewItem,
						iCharLvl);
				lLastItem = lStartNewItem + lSocket.getItemLength();
				iSocketedItems.add(lSocket);


				if (lSocket.isJewel()) {
					iProps.addAll(lSocket.getPropCollection());
				} else {
					if (isTypeWeapon()) {
						iProps.addAll(lSocket.getPropCollection(),7);
					} else if (isTypeArmor()) {
						if (iType.equals("tors") || iType.equals("helm")
								|| iType.equals("phlm") || iType.equals("pelt")
								|| iType.equals("cloa") || iType.equals("circ")) {
							iProps.addAll(lSocket.getPropCollection(),8);
						} else {
							iProps.addAll(lSocket.getPropCollection(),9);
						}			
					}
					if (lSocket.iReqLvl > iReqLvl) {
						iReqLvl = lSocket.iReqLvl;
					}
				}
			}
		}

		if (iRuneWord) {
			ArrayList lList = new ArrayList();
			for (int i = 0; i < iSocketedItems.size(); i++) {
				lList.add(((D2Item) iSocketedItems.get(i)).getRuneCode());
			}

			D2TxtFileItemProperties lRuneWord = D2TxtFile.RUNES
			.searchRuneWord(lList);
			runeword_id = lRuneWord.getRowNum();
			if (lRuneWord != null) {
				iItemName = D2TblFile.getString(lRuneWord.get("Name"));
			}
		}

		if (iSocketNrFilled > 0 && isNormal()) {
			iItemName = "Gemmed " + iItemName;
		}

		if (iItemName != null) {
			iItemName = iItemName.trim();

		}

		if (iBaseItemName != null) {
			iBaseItemName = iBaseItemName.trim();

		}

		if (iEthereal) {
			if (iReqStr != -1) {
				iReqStr -= 10;
			}
			if (iReqDex != -1) {
				iReqDex -= 10;
			}
		}
	}

	private D2PropCollection getPropCollection() {
		return iProps;
	}

	public String getItemQuality() {
		return iItemQuality;
	}

	private int getReq(String pReq) {
		if (pReq != null) {
			String lReq = pReq.trim();
			if (!lReq.equals("") && !lReq.equals("0")) {
				try {
					return Integer.parseInt(lReq);
				} catch (Exception pEx) {
					// do nothing, no req
				}
			}
		}
		return -1;
	}

	private void readExtend1(D2BitReader pFile) throws Exception {
		// extended item
		iSocketNrFilled = (short) pFile.read(3);
		fingerprint = pFile.read(32);
		iFP = "0x" + Integer.toHexString((int) fingerprint);
		ilvl = (short) pFile.read(7);
		quality = (short) pFile.read(4);
		iProps = new D2PropCollection();
		// check variable graphic flag
		gfx_num = -1;
		if (pFile.read(1) == 1) {
			gfx_num = (short) pFile.read(3);
			if (iItemType.get("namestr").compareTo("cm1") == 0) {
				iSmallCharm = true;
				image_file = "invch" + ((gfx_num) * 3 + 1);
			} else if (iItemType.get("namestr").compareTo("cm2") == 0) {
				iLargeCharm = true;
				image_file = "invch" + ((gfx_num) * 3 + 2);
			} else if (iItemType.get("namestr").compareTo("cm3") == 0) {
				iGrandCharm = true;
				image_file = "invch" + ((gfx_num) * 3 + 3);
			} else if (iItemType.get("namestr").compareTo("jew") == 0) {
				iJewel = true;
				image_file = "invjw" + (gfx_num + 1);
			} else {
				image_file += (gfx_num + 1);
			}
		}
		// check class info flag
		if (pFile.read(1) == 1) {
			automod_info = D2TxtFile.AUTOMAGIC.getRow((int)pFile.read(11) -1);


		}

		// path determined by item quality
		switch (quality) {
		case 1: // low quality item
		{
			low_quality = (short) pFile.read(3);

			switch(low_quality){

			case 0:
			{
				iItemName = "Crude " + iItemName;
				break;
			}

			case 1:
			{
				iItemName = "Cracked " + iItemName;
				break;
			}

			case 2:
			{
				iItemName = "Damaged " + iItemName;
				break;
			}

			case 3:
			{
				iItemName = "Low Quality " + iItemName;
				break;
			}

			}


			break;
		}
		case 3: // high quality item
		{
			iItemName = "Superior " + iItemName;
			iBaseItemName = iItemName;
			hi_quality = (short) pFile.read(3);
			break;
		}
		case 4: // magic item
		{
			iMagical = true;
			magic_prefix = (short) pFile.read(11);
			magic_suffix = (short) pFile.read(11);

			if (magic_suffix == 0) {
				magic_suffix = 10000;
			}

			D2TxtFileItemProperties lPrefix = D2TxtFile.PREFIX
			.getRow(magic_prefix);
			String lPreName = lPrefix.get("Name");
			if (lPreName != null && !lPreName.equals("")) {
				iItemName = D2TblFile.getString(lPreName) + " " + iItemName;
				int lPreReq = getReq(lPrefix.get("levelreq"));
				if (lPreReq > iReqLvl) {
					iReqLvl = lPreReq;
				}
			}

			D2TxtFileItemProperties lSuffix = D2TxtFile.SUFFIX
			.getRow(magic_suffix);
			String lSufName = lSuffix.get("Name");
			if (lSufName != null && !lSufName.equals("")) {
				iItemName = iItemName + " " + D2TblFile.getString(lSufName);
				int lSufReq = getReq(lSuffix.get("levelreq"));
				if (lSufReq > iReqLvl) {
					iReqLvl = lSufReq;
				}
			}
			applyAutomodLvl();
			break;
		}
		case 5: // set item
		{
			iSet = true;
			set_id = (short) pFile.read(12);
			if (gfx_num == -1) {
				String s = (String) iItemType.get("setinvfile");
				if (s.compareTo("") != 0)
					image_file = s;
			}

			D2TxtFileItemProperties lSet = D2TxtFile.SETITEMS.getRow(set_id);
			iItemName = D2TblFile.getString(lSet.get("index"));
			iSetName = lSet.get("set");

			setSize = (D2TxtFile.SETITEMS.searchColumnsMultipleHits("set",
					iSetName)).size();

			int lSetReq = getReq(lSet.get("lvl req"));
			if (lSetReq != -1) {
				iReqLvl = lSetReq;
			}
			
			applyAutomodLvl();
			break;
		}
		case 7:
		{
			iUnique = true;
			unique_id = (short) pFile.read(12);
			String s = iItemType.get("uniqueinvfile");
			if (s.compareTo("") != 0) {
				image_file = s;
			}

			D2TxtFileItemProperties lUnique = D2TxtFile.UNIQUES
			.getRow(unique_id);
			String lNewName = D2TblFile.getString(lUnique.get("index"));
			if (lNewName != null) {
				iItemName = lNewName;
			}

			int lUniqueReq = getReq(lUnique.get("lvl req"));
			if (lUniqueReq != -1) {
				iReqLvl = lUniqueReq;
			}
			applyAutomodLvl();
			break;
		}
		case 6: // rare item
		{
			iRare = true;
			iItemName = "Rare " + iItemName;
		}
		case 8: // also a rare item, do the same (one's probably crafted)
		{
				iCrafted = true;
				iItemName = "Crafted " + iItemName;
		}
		applyAutomodLvl();
		rare_name_1 = (short) pFile.read(8);
		rare_name_2 = (short) pFile.read(8);
		D2TxtFileItemProperties lRareName1 = D2TxtFile.RAREPREFIX
		.getRow(rare_name_1 - 156);
		D2TxtFileItemProperties lRareName2 = D2TxtFile.RARESUFFIX
		.getRow(rare_name_2 - 1);
		iItemName = D2TblFile.getString(lRareName1.get("name")) + " "
		+ D2TblFile.getString(lRareName2.get("name"));

		rare_prefixes = new short[3];
		rare_suffixes = new short[3];
		short pre_count = 0;
		short suf_count = 0;
		for (int i = 0; i < 3; i++) {
			if (pFile.read(1) == 1) {
				rare_prefixes[pre_count] = (short) pFile.read(11);
				D2TxtFileItemProperties lPrefix = D2TxtFile.PREFIX
				.getRow(rare_prefixes[pre_count]);
				pre_count++;
				String lPreName = lPrefix.get("Name");
				if (lPreName != null && !lPreName.equals("")) {
					int lPreReq = getReq(lPrefix.get("levelreq"));
					if (lPreReq > iReqLvl) {
						iReqLvl = lPreReq;
					}
				}

			}
			if (pFile.read(1) == 1) {
				rare_suffixes[suf_count] = (short) pFile.read(11);
				D2TxtFileItemProperties lSuffix = D2TxtFile.SUFFIX
				.getRow(rare_suffixes[suf_count]);
				suf_count++;
				String lSufName = lSuffix.get("Name");
				if (lSufName != null && !lSufName.equals("")) {
					int lSufReq = getReq(lSuffix.get("levelreq"));
					if (lSufReq > iReqLvl) {
						iReqLvl = lSufReq;
					}
				}
			}
		}

		if(isCrafted()){
			iReqLvl = 	iReqLvl + 10+(3* (suf_count + pre_count));
		}
		break;

		case 2: {
			readTypes(pFile);
			break;
		}
		}

		// rune word
		if (check_flag(27)) {
			pFile.skipBits(12);
			pFile.skipBits(4);
		}
		iItemNameNoPersonalising = iItemName;
		// personalized
		if (check_flag(25)) {
			personalization = "";
			boolean lNotEnded = true;
			for (int i = 0; i < 15 && lNotEnded; i++) {
				char c = (char) pFile.read(7);
				if (c == 0) {
					lNotEnded = false;
				} else {
					personalization += c;
				}
			}
			if(lNotEnded == true){
				pFile.read(7);
			}
		}
	}

	private void applyAutomodLvl() {
		// modifies the level if the automod is higher
		if(automod_info == null){
			return;
		}
		if(Integer.parseInt(automod_info.get("levelreq")) > iReqLvl){
			iReqLvl = Integer.parseInt(automod_info.get("levelreq"));
		}

	}

	// MBR: unknown, but should be according to file format
	private void readTypes(D2BitReader pFile) {
		// charms ??
		if (isCharm()) {
			long lCharm1 = pFile.read(1);
			long lCharm2 = pFile.read(11);
			// System.err.println("Charm (?): " + lCharm1 );
			// System.err.println("Charm (?): " + lCharm2 );
		}

		// books / scrolls ??
		if ("tbk".equals(item_type) || "ibk".equals(item_type)) {
			long lTomb = pFile.read(5);
			// System.err.println("Tome ID: " + lTomb );
		}

		if ("tsc".equals(item_type) || "isc".equals(item_type)) {
			long lTomb = pFile.read(5);
			// System.err.println("Tome ID: " + lTomb );
		}

		// body ??
		if ("body".equals(item_type)) {
			long lMonster = pFile.read(10);
			// System.err.println("Monster ID: " + lMonster );
		}
	}

	private void readExtend2(D2BitReader pFile) throws Exception {
		if (isTypeArmor()) {
			iDef = (pFile.read(11) - 10); // -10 ???
			iInitDef = iDef;
			iMaxDur = pFile.read(8);

			if (iMaxDur != 0) {
				iCurDur = pFile.read(9);
			}

		} else if (isTypeWeapon()) {
			if (iType.equals("tkni") || iType.equals("taxe")
					|| iType.equals("jave") || iType.equals("ajav")) {
				iThrow = true;
			}
			iMaxDur = pFile.read(8);

			if (iMaxDur != 0) {
				iCurDur = pFile.read(9);
			}

			if ((D2TxtFile.WEAPONS.searchColumns("code", item_type)).get(
			"1or2handed").equals("")
			&& !iThrow) {

				if ((D2TxtFile.WEAPONS.searchColumns("code", item_type)).get(
				"2handed").equals("1")) {
					iWhichHand = 2;
					iinitMinDmg = iMinDmg = Long.parseLong((D2TxtFile.WEAPONS
							.searchColumns("code", item_type))
							.get("2handmindam"));
					iinitMaxDmg = iMaxDmg = Long.parseLong((D2TxtFile.WEAPONS
							.searchColumns("code", item_type))
							.get("2handmaxdam"));
				} else {
					iWhichHand = 1;
					iinitMinDmg = iMinDmg = Long.parseLong((D2TxtFile.WEAPONS
							.searchColumns("code", item_type)).get("mindam"));
					iinitMaxDmg = iMaxDmg = Long.parseLong((D2TxtFile.WEAPONS
							.searchColumns("code", item_type)).get("maxdam"));
				}

			} else {
				iWhichHand = 0;
				if (iThrow) {
					iinit2MinDmg = i2MinDmg = Long
					.parseLong((D2TxtFile.WEAPONS.searchColumns("code",
							item_type)).get("minmisdam"));
					iinit2MaxDmg = i2MaxDmg = Long
					.parseLong((D2TxtFile.WEAPONS.searchColumns("code",
							item_type)).get("maxmisdam"));
				} else {
					iinit2MinDmg = i2MinDmg = Long.parseLong((D2TxtFile.WEAPONS
							.searchColumns("code", item_type))
							.get("2handmindam"));
					iinit2MaxDmg = i2MaxDmg = Long.parseLong((D2TxtFile.WEAPONS
							.searchColumns("code", item_type))
							.get("2handmaxdam"));
				}
				iinitMinDmg = iMinDmg = Long.parseLong((D2TxtFile.WEAPONS
						.searchColumns("code", item_type)).get("mindam"));
				iinitMaxDmg = iMaxDmg = Long.parseLong((D2TxtFile.WEAPONS
						.searchColumns("code", item_type)).get("maxdam"));
			}

			if ("1".equals(iItemType.get("stackable"))) {
				iStackable = true;
				iCurDur = pFile.read(9);
			}
		} else if (isTypeMisc()) {
			if ("1".equals(iItemType.get("stackable"))) {
				iStackable = true;
				iCurDur = pFile.read(9);
			}

		}

		if (iSocketed) {
			iGemProps = new ArrayList();
			iSocketNrTotal = pFile.read(4);
		}

		int[] lSet = new int[5];

		if (quality == 5) {
			for(int x = 0;x<5;x++){
				lSet[x] = (int)pFile.read(1);
			}
		}

		readProperties(pFile, 0);


		if (quality == 5) {
			for(int x = 0;x<5;x++){
				if(lSet[x] == 1){
					readProperties(pFile, x + 2);
				}
			}
		}
		if (iRuneWord) {
			readProperties(pFile, 0);
		}
	}


	private String getExStr() {
		return " (" + iItemName + ", " + iFP + ")";
	}

	private void readPropertiesPots(D2BitReader pfile) {

		String[] statsToRead = { "stat1", "stat2" };

		for (int x = 0; x < statsToRead.length; x = x + 1) {
			String txtCheck = (D2TxtFile.MISC.searchColumns("code", item_type))
			.get(statsToRead[x]);

			if (!txtCheck.equals("")) {

				int lProp = Integer.parseInt((D2TxtFile.ITEM_STAT_COST
						.searchColumns("Stat", txtCheck)).get("ID"));

				D2ItemProperty lProperty = new D2ItemProperty(lProp, iCharLvl,
						iItemName);

//				iProperties.add(lProperty);

				D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST
				.getRow(lProperty.getPropNrs()[0]);

				lProperty.set(lProp, lItemStatCost, 0, Long
						.parseLong(((D2TxtFile.MISC.searchColumns("code",
								item_type)).get(statsToRead[x].replaceFirst(
										"stat", "calc")))));

			}

		}

	}

	private void readPropertiesGems(D2BitReader pFile) {

		String[][] gemHeaders = {
				{ "weaponMod1", "weaponMod2", "weaponMod3" },
				{ "helmMod1", "helmMod2", "helmMod3" },
				{ "shieldMod1", "shieldMod2", "shieldMod3" } };

		for(int x = 0;x<gemHeaders.length;x++){

			for(int y = 0;y<gemHeaders[x].length;y++){

				if(D2TxtFile.GEMS.searchColumns("code", item_type).get(gemHeaders[x][y] + "Code").equals("")){
					continue;
				}
				iProps.addAll(D2TxtFile.propToStat(D2TxtFile.GEMS.searchColumns("code", item_type).get(gemHeaders[x][y] + "Code"), D2TxtFile.GEMS.searchColumns("code", item_type).get(gemHeaders[x][y] + "Min"), D2TxtFile.GEMS.searchColumns("code", item_type).get(gemHeaders[x][y] + "Max"), D2TxtFile.GEMS.searchColumns("code", item_type).get(gemHeaders[x][y] + "Param"), (x+7)));
			}
		}
	}

	private void readProperties(D2BitReader pFile, int qFlag) {

		int rootProp = (int) pFile.read(9);

		while(rootProp != 511){

			iProps.readProp(pFile, rootProp, qFlag);

			if (rootProp == 17)
			{
				iProps.readProp(pFile,18, qFlag);
			}
			else if (rootProp == 48)
			{
				iProps.readProp(pFile,49, qFlag);
			}
			else if (rootProp == 50)
			{
				iProps.readProp(pFile,51, qFlag);
			}
			else if (rootProp == 52)
			{
				iProps.readProp(pFile,53, qFlag);
			}
			else if (rootProp == 54)
			{
				iProps.readProp(pFile,55, qFlag);
				iProps.readProp(pFile,56, qFlag);
			}
			else if (rootProp == 57)
			{
				iProps.readProp(pFile,58, qFlag);
				iProps.readProp(pFile,59, qFlag);
			}
			rootProp = (int)pFile.read(9);
		}


	}
	
	private void applyEthDmg() {
		iinitMinDmg = iMinDmg = (long) Math
		.floor((((double) iMinDmg / (double) 100) * (double) 50)
				+ iMinDmg);
		iinitMaxDmg = iMaxDmg = (long) Math
		.floor((((double) iMaxDmg / (double) 100) * (double) 50)
				+ iMaxDmg);

		if (iWhichHand == 0) {
			iinit2MinDmg = i2MinDmg = (long) Math
			.floor((((double) i2MinDmg / (double) 100) * (double) 50)
					+ i2MinDmg);
			iinit2MaxDmg = i2MaxDmg = (long) Math
			.floor((((double) i2MaxDmg / (double) 100) * (double) 50)
					+ i2MaxDmg);
		}
	}

	private void modifyLvl(ArrayList skillArr) {
		for (int x = 0; x < skillArr.size(); x = x + 1) {

			D2TxtFileItemProperties lSkill = D2TxtFile.SKILL_DESC
			.getRow((int) ((D2ItemProperty) skillArr.get(x))
					.getRealValue());

			if (iReqLvl < Integer.parseInt(D2TxtFile.SKILLS.searchColumns(
					"skilldesc", lSkill.get("skilldesc")).get("reqlevel"))) {

				iReqLvl = (Integer.parseInt(D2TxtFile.SKILLS.searchColumns(
						"skilldesc", lSkill.get("skilldesc")).get("reqlevel")));
			}
		}
	}

//	private void applyEDef() {

//	iDef = 0;
//	int ENDef = 0;
//	int Def = 0;
//	int Dur = 0;
//	int PlusDur = 0;

//	if (isSet()) {

//	for (int x = 0; x < iSetProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 16) {
//	ENDef = ENDef
//	+ ((D2ItemProperty) iSetProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 31) {
//	Def = Def
//	+ ((D2ItemProperty) iSetProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 75) {
//	Dur = Dur
//	+ ((D2ItemProperty) iSetProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 73) {
//	PlusDur = PlusDur
//	+ ((D2ItemProperty) iSetProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 214) {
//	Def = Def
//	+ (int) Math.floor((((D2ItemProperty) iSetProps
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	}

//	}

//	if (isSocketed()) {

//	if (isRuneWord()) {
//	for (int x = 0; x < iRuneWordProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 16) {

//	ENDef = ENDef
//	+ ((D2ItemProperty) iRuneWordProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 31) {
//	Def = Def
//	+ ((D2ItemProperty) iRuneWordProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 75) {
//	Dur = Dur
//	+ ((D2ItemProperty) iRuneWordProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 73) {
//	PlusDur = PlusDur
//	+ ((D2ItemProperty) iRuneWordProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 214) {
//	Def = Def
//	+ (int) Math
//	.floor((((D2ItemProperty) iRuneWordProps
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	}
//	}

//	for (int x = 0; x < iGemProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 16) {
//	ENDef = ENDef
//	+ ((D2ItemProperty) iGemProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 31) {
//	Def = Def
//	+ ((D2ItemProperty) iGemProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 75) {
//	Dur = Dur
//	+ ((D2ItemProperty) iGemProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 73) {
//	PlusDur = PlusDur
//	+ ((D2ItemProperty) iGemProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 214) {
//	Def = Def
//	+ (int) Math.floor((((D2ItemProperty) iGemProps
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	}

//	}
//	for (int x = 0; x < iProperties.size(); x = x + 1) {
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 16) {
//	ENDef = ENDef
//	+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 31) {
//	Def = Def
//	+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 75) {
//	Dur = Dur
//	+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 73) {
//	PlusDur = PlusDur
//	+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 214) {
//	Def = Def
//	+ (int) Math.floor((((D2ItemProperty) iProperties
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	}
//	iDef = (long) Math.floor((((double) iInitDef / (double) 100) * ENDef)
//	+ (iInitDef + Def));
//	iMaxDur = (long) Math.floor((((double) iMaxDur / (double) 100) * Dur)
//	+ (iMaxDur + PlusDur));

//	}

//	private void applyBlock() {	

//	int block = 0;

//	if (isSet()) {

//	for (int x = 0; x < iSetProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 20) {
//	block = block + ((D2ItemProperty) iSetProps.get(x)).getRealValue();	
//	}
//	}

//	}

//	if (isSocketed()) {

//	if (isRuneWord()) {
//	for (int x = 0; x < iRuneWordProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 20) {
//	block = block + ((D2ItemProperty) iRuneWordProps.get(x)).getRealValue();	
//	}
//	}
//	}

//	for (int x = 0; x < iGemProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 20) {
//	block = block + ((D2ItemProperty) iGemProps.get(x)).getRealValue();	
//	}
//	}

//	}
//	for (int x = 0; x < iProperties.size(); x = x + 1) {
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 20) {
//	block = block + ((D2ItemProperty) iProperties.get(x)).getRealValue();	
//	}
//	}

//	iBlock = cBlock + block;

//	}

//	private void applyEDmg() {

//	int ENDam = 0;
//	int ENMaxDam = 0;
//	int MinDam = 0;
//	int MaxDam = 0;

//	if (isSocketed()) {

//	if (isRuneWord()) {
//	for (int x = 0; x < iRuneWordProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 17) {
//	ENDam = ENDam
//	+ ((D2ItemProperty) iRuneWordProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 219) {
//	ENMaxDam = ENMaxDam
//	+ (int) Math
//	.floor((((D2ItemProperty) iRuneWordProps
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 218) {
//	MaxDam = MaxDam
//	+ (int) Math
//	.floor((((D2ItemProperty) iRuneWordProps
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 21) {
//	MinDam = MinDam
//	+ ((D2ItemProperty) iRuneWordProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iRuneWordProps.get(x)).getiProp() == 22) {
//	MaxDam = MaxDam
//	+ ((D2ItemProperty) iRuneWordProps.get(x))
//	.getRealValue();
//	}
//	}
//	}

//	for (int x = 0; x < iGemProps.size(); x = x + 1) {
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 17) {
//	ENDam = ENDam
//	+ ((D2ItemProperty) iGemProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 21) {
//	MinDam = MinDam
//	+ ((D2ItemProperty) iGemProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 22) {
//	MaxDam = MaxDam
//	+ ((D2ItemProperty) iGemProps.get(x))
//	.getRealValue();
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 219) {
//	ENMaxDam = ENMaxDam
//	+ (int) Math.floor((((D2ItemProperty) iGemProps
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	if (((D2ItemProperty) iGemProps.get(x)).getiProp() == 218) {
//	MaxDam = MaxDam
//	+ (int) Math.floor((((D2ItemProperty) iGemProps
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	}

//	}
//	for (int x = 0; x < iProperties.size(); x = x + 1) {
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 17) {
//	ENDam = ENDam
//	+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 21) {
//	MinDam = MinDam
//	+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 22) {
//	MaxDam = MaxDam
//	+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 219) {
//	ENMaxDam = ENMaxDam
//	+ (int) Math.floor((((D2ItemProperty) iProperties
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 218) {
//	MaxDam = MaxDam
//	+ (int) Math.floor((((D2ItemProperty) iProperties
//	.get(x)).getRealValue() * 0.125)
//	* iCharLvl);
//	}
//	}
//	iMinDmg = (long) Math.floor((((double) iMinDmg / (double) 100) * ENDam)
//	+ (iMinDmg + MinDam));
//	iMaxDmg = (long) Math
//	.floor((((double) iMaxDmg / (double) 100) * (ENDam + ENMaxDam))
//	+ (iMaxDmg + MaxDam));

//	if (iWhichHand == 0) {
//	i2MinDmg = (long) Math
//	.floor((((double) i2MinDmg / (double) 100) * ENDam)
//	+ (i2MinDmg + MinDam));
//	i2MaxDmg = (long) Math
//	.floor((((double) i2MaxDmg / (double) 100) * (ENDam + ENMaxDam))
//	+ (i2MaxDmg + MaxDam));
//	}

//	}

	// public ArrayList getAllProps(){
	// if(null != propsList){
	// int[] propsListOut = new int[propsList.size()];
	//		
	// for(int x = 0;x<propsList.size();x=x+1){
	// propsListOut[x] = ((D2ItemProperty)propsList.get(x)).getiProp();
	// System.out.println(propsListOut[x]);
	// }
	//		
	// return propsListOut;
	// }else{
	// return new int[0];
	// }
	// }

	private void modifyReqs(int value) {
//		System.out.println(iItemName);
		if (getReqDex() != -1) {
			iReqDex = iReqDex + ((int)(iReqDex*((double) value / (double) 100)));
		}

		if (getReqStr() != -1) {
			iReqStr = iReqStr+ ((int) (iReqStr	* ((double) value / (double) 100)));
		}
	}

	public boolean isBodyLArm() {
		return isBodyLocation("larm");
	}

	public boolean isBodyRRin() {
		return isBodyLocation("rrin");
	}

	public boolean isBodyLRin() {
		return isBodyLocation("lrin");
	}

	public boolean isWeaponType(D2WeaponTypes pType) {
		if (iTypeWeapon) {
			if (pType.isType(iType)) {
				return true;
			}
		}
		return false;
	}

	public boolean isBodyLocation(D2BodyLocations pLocation) {
		if (iBody) {
			if (pLocation.getLocation().equals(iBodyLoc1)) {
				return true;
			}
			if (pLocation.getLocation().equals(iBodyLoc2)) {
				return true;
			}
		}
		return false;
	}

	private boolean isBodyLocation(String pLocation) {
		if (iBody) {
			if (pLocation.equals(iBodyLoc1)) {
				return true;
			}
			if (pLocation.equals(iBodyLoc2)) {
				return true;
			}
		}
		return false;
	}

	public boolean isBelt() {
		return iBelt;
	}

	public boolean isCharm() {
		return (iSmallCharm || iLargeCharm || iGrandCharm);
	}

	public boolean isCharmSmall() {
		return iSmallCharm;
	}

	public boolean isCharmLarge() {
		return iLargeCharm;
	}

	public boolean isCharmGrand() {
		return iGrandCharm;
	}

	public boolean isJewel() {
		return iJewel;
	}

	// accessor for the row
	public short get_row() {
		return row;
	}

	// accessor for the column
	public short get_col() {
		return col;
	}

	// setter for the row
	// necessary for moving items
	public void set_row(short r) {
		iItem.set_byte_pos(7);
		iItem.skipBits(13);
		iItem.write((long) r, 4);
		row = r;
	}

	// setter for the column
	// necessary for moving items
	public void set_col(short c) {
		iItem.set_byte_pos(7);
		iItem.skipBits(9);
		iItem.write((long) c, 4);
		col = c;
	}

	public void set_location(short l) {
		iItem.set_byte_pos(7);
		iItem.skipBits(2);
		iItem.write((long) l, 3);
		location = l;
	}

	public void set_body_position(short bp) {
		iItem.set_byte_pos(7);
		iItem.skipBits(5);
		iItem.write((long) bp, 4);
		body_position = bp;
	}

	public void set_panel(short p) {
		iItem.set_byte_pos(7);
		iItem.skipBits(17);
		iItem.write((long) p, 3);
		panel = p;
	}

	public short get_location() {
		return location;
	}

	public short get_body_position() {
		return body_position;
	}

	public short get_panel() {
		return panel;
	}

	public short get_width() {
		return width;
	}

	public short get_height() {
		return height;
	}

	public String get_image() {
		return image_file;
	}

	public String get_name() {
		return name;
	}

	public String get_version() {

		if (version == 0) {
			return "Legacy (pre 1.08)";
		}

		if (version == 1) {
			return "Classic";
		}

		if (version == 100) {
			return "Expansion";
		}

		if (version == 101) {
			return "Expansion 1.10+";
		}

		return "UNKNOWN";
	}

	public long getSocketNrFilled() {
		return iSocketNrFilled;
	}

	public long getSocketNrTotal() {
		return iSocketNrTotal;
	}

	public byte[] get_bytes() {
		return iItem.getFileContent();
	}

	public int getItemLength() {
		return iItem.get_length();
	}

	public String getItemName() {
		return iItemName;
	}

	public String getName() {
		return iItemNameNoPersonalising;
	}

	public String getFingerprint() {
		return iFP;
	}

	public String getILvl() {
		return Short.toString(ilvl);
	}

	public String toString(int disSepProp) {
		ArrayList lDump = getFullItemDump(1, disSepProp);
		StringBuffer lReturn = new StringBuffer("");
		for (int i = 0; i < lDump.size(); i++) {
			if (i > 0 && !lDump.get(i).equals("")) {
				lReturn.append("\n");
			}
			lReturn.append((String) lDump.get(i));
		}
		return lReturn.toString();
	}

	public void toWriter(PrintWriter pWriter) {
		ArrayList lDump = getFullItemDump(1, 0);
		for (int i = 0; i < lDump.size(); i++) {
			pWriter.println((String) lDump.get(i));
		}
		pWriter.println();
	}

	public String toStringHtml(int stash, int disSepProp) {
		ArrayList lDump = getFullItemDump(stash, disSepProp);
		StringBuffer lReturn = new StringBuffer("<HTML>");
		for (int i = 0; i < lDump.size(); i++) {
			if (i > 0) {
				lReturn.append("<BR>");
			}
			lReturn.append((String) lDump.get(i));
		}
		return lReturn.toString();
	}

	private ArrayList getProperties(String pProps, ArrayList pProperties) {
		ArrayList lReturn = new ArrayList();

		if (pProperties != null) {
			if (pProps != null) {
				lReturn.add(pProps);
			}
			for (int i = 0; i < pProperties.size(); i++) {
				D2ItemProperty lValue = (D2ItemProperty) pProperties.get(i);
				if (!lValue.hasNoValue()) {
					String lText = lValue.getValue();
					if (lText != null) {
						if(((D2ItemProperty) pProperties.get(i)).getItemName().equals("Um Rune")){
							if(this.iItemName.equals("Um Rune")){
								//System.out.println(this.iItemName);
							}
						}


						lReturn.add(lText);
					}
				}
			}
		}

		return lReturn;
	}

	public int getReqLvl() {
		return iReqLvl;
	}

	public int getReqStr() {
		return iReqStr;
	}

	public int getReqDex() {
		return iReqDex;
	}

	public ArrayList getFullItemDump(int stash, int disSepProp) {
		ArrayList lReturn = new ArrayList();

		String base = Integer.toHexString(Color.white.getRGB());
		base = base.substring(2, base.length());

		String rgb = Integer.toHexString(getItemColor().getRGB());
		rgb = rgb.substring(2, rgb.length());
		// System.out.println(rgb);
		if (stash == 1) {

			if (personalization == null) {
				lReturn.add(iItemName);
			}else{
				lReturn.add(personalization + "'s " + iItemName);
			}


			if (!iBaseItemName.equals(iItemName)) {
				lReturn.add(iBaseItemName);
			}
		} else {
			if (disSepProp == 1) {

				if (personalization == null) {
					lReturn.add("<font face=\"Dialog\" size=\"3\" color=\"#" + base
							+ "\">" + "<font color=\"#" + rgb + "\">" + iItemName
							+ "</font>");
				}else{

					lReturn.add("<font face=\"Dialog\" size=\"3\" color=\"#" + base
							+ "\">" + "<font color=\"#" + rgb + "\">" + personalization + "'s " + iItemName
							+ "</font>");

				}


			} else {

				if (personalization == null) {
					lReturn.add("<font color=\"#" + base + "\">"
							+ "<font color=\"#" + rgb + "\">" + iItemName
							+ "</font>");
				}else{
					lReturn.add("<font color=\"#" + base + "\">"
							+ "<font color=\"#" + rgb + "\">" + personalization + "'s " + iItemName
							+ "</font>");
				}

			}
			if (!iBaseItemName.equals(iItemName)) {
				if (!isRuneWord()) {
					lReturn.add("<font color=\"#" + rgb + "\">" + iBaseItemName
							+ "</font>");
				} else {
					rgb = Integer.toHexString(Color.gray.getRGB());
					rgb = rgb.substring(2, rgb.length());
					lReturn.add("<font color=\"#" + rgb + "\">" + iBaseItemName
							+ "</font>");

				}
			}
		}
		if (isTypeWeapon()) {
			if (iWhichHand == 0) {
				if (iThrow) {
					lReturn.add("Throw Damage: " + i2MinDmg + " - " + i2MaxDmg);
					lReturn
					.add("One Hand Damage: " + iMinDmg + " - "
							+ iMaxDmg);

				} else {
					lReturn
					.add("One Hand Damage: " + iMinDmg + " - "
							+ iMaxDmg);
					lReturn.add("Two Hand Damage: " + i2MinDmg + " - "
							+ i2MaxDmg);
				}
			} else {
				if (iWhichHand == 1) {
					lReturn
					.add("One Hand Damage: " + iMinDmg + " - "
							+ iMaxDmg);
				} else {
					lReturn
					.add("Two Hand Damage: " + iMinDmg + " - "
							+ iMaxDmg);
				}
			}
		} else if (isTypeArmor()) {
			lReturn.add("Defense: " + iDef);
		}

		if(isTypeArmor()){
			if(iType.equals("ashd")||iType.equals("shie")||iType.equals("head")){
				lReturn.add("Chance to Block: " + iBlock);
			}
		}

		if (isTypeWeapon() || isTypeArmor()) {
			if (isStackable()) {
				lReturn.add("Quantity: " + iCurDur);
			} else {
				if (iMaxDur == 0) {
					lReturn.add("Indestructible");
				} else {
					lReturn.add("Durability: " + iCurDur + " of " + iMaxDur);
				}
			}
		}

		if (iReqLvl > 0) {
			lReturn.add("Required Level: " + iReqLvl);
		}
		if (iReqStr > 0) {
			lReturn.add("Required Strength: " + iReqStr);
		}
		if (iReqDex > 0) {
			lReturn.add("Required Dexterity: " + iReqDex);
		}

		if (iFP != null) {
			lReturn.add("Fingerprint: " + iFP);
		}
		if (hasGUID) {
			lReturn.add("GUID: " + iGUID);
		}

		if (ilvl != 0) {
			lReturn.add("Item Level: " + ilvl);
		}

		lReturn.add("Version: " + get_version());
		if (!iIdentified) {
			lReturn.add("Unidentified");
		}
		lReturn.add("Properties: ");
		lReturn.addAll(iProps.generateDisplay(0, iCharLvl));
//		lReturn.addAll(getProperties("Properties: ", iProperties));
		if (isGem() || isRune()) {

			lReturn.add("Weapons: ");
			lReturn.addAll(iProps.generateDisplay(7, iCharLvl));
			lReturn.add("Armor: ");
			lReturn.addAll(iProps.generateDisplay(8, iCharLvl));
			lReturn.add("Shields: ");
			lReturn.addAll(iProps.generateDisplay(9, iCharLvl));

		}


		//Set Items
		if(quality == 5){

			for(int x = 2;x<7;x++){
				ArrayList outArr = iProps.generateDisplay(x, iCharLvl);
				if(outArr.size() > 0){
					lReturn.add("Set (" + x + " items): ");
					lReturn.addAll(outArr);
				}
			}
		}

		if (iEthereal) {
			lReturn.add("Ethereal");
		}
		if (iSocketNrTotal > 0) {
			lReturn.add(iSocketNrTotal + " Sockets (" + iSocketNrFilled + " used)");
			if (iSocketedItems != null) {
				for (int i = 0; i < iSocketedItems.size(); i++) {
					D2Item lSocket = ((D2Item) iSocketedItems.get(i));
					lReturn.add("Socketed: " + lSocket.getItemName());
				}
			}
		}

		if (disSepProp == 1) {

			if (isSocketed()) {
				lReturn.add("");
				if (stash == 1) {
					if (iSocketedItems != null) {
						for (int x = 0; x < iSocketedItems.size(); x = x + 1) {
							if (((D2Item) iSocketedItems.get(x)) != null) {
								lReturn.add(((D2Item) iSocketedItems.get(x))
										.toString(0)
										+ "\n");
							}
						}
					}
				} else {
					if (iSocketedItems != null) {
						for (int x = 0; x < iSocketedItems.size(); x = x + 1) {

							lReturn.add(((D2Item) iSocketedItems.get(x))
									.toStringHtml(stash, 0));
						}
					}
				}

			}

		}

		if (stash == 0) {
			lReturn.add("</font>");
		}

		return lReturn;
	}

	public Color getItemColor() {
		if (isUnique()) {
			// return Color.yellow.darker().darker();
			return new Color(255, 222, 173);
		}
		if (isSet()) {
			return Color.green.darker();
		}
		if (isRare()) {
			return Color.yellow.brighter();
		}
		if (isMagical()) {
			return new Color(72, 118, 255);
		}
		if (isRune()) {
			return Color.red;
		}
		if (isCrafted()) {
			return Color.orange;
		}
		if (isRuneWord()) {
			return new Color(255, 222, 173);
		}
		if (isEthereal() || isSocketed()) {
			return Color.gray;
		}
		return Color.white;
	}

	public boolean isUnique() {
		return iUnique;
	}

	public boolean isSet() {
		return iSet;
	}

	public boolean isRuneWord() {
		return iRuneWord;
	}

	public boolean isCrafted() {
		return iCrafted;
	}

	public boolean isRare() {
		return iRare;
	}

	public boolean isMagical() {
		return iMagical;
	}

	public boolean isNormal() {
		return !(iMagical || iRare || iCrafted || iRuneWord || isRune() || iSet || iUnique);
	}

	public boolean isSocketFiller() {
		return isRune() || isJewel() || isGem();
	}

	public boolean isGem() {
		return iGem;
	}

	public boolean isRune() {
		return getRuneCode() != null;
	}

	public String getRuneCode() {
		if (iItemType != null) {
			if ("rune".equals(iItemType.get("type"))) {
				return iItemType.get("code");
			}
		}
		return null;
	}

	public boolean isEthereal() {
		return iEthereal;
	}

	public boolean isSocketed() {
		return iSocketed;
	}

	public boolean isStackable() {
		return iStackable;
	}

	public boolean isTypeMisc() {
		return iTypeMisc;
	}

	public boolean isTypeArmor() {
		return iTypeArmor;
	}

	public boolean isTypeWeapon() {
		return iTypeWeapon;
	}

	public void setCharLvl(int pCharLvl) {
//		iCharLvl = pCharLvl;
////		setCharLvl(iProperties, pCharLvl);
//		setCharLvl(iSet1, pCharLvl);
//		setCharLvl(iSet2, pCharLvl);
//		setCharLvl(iSet3, pCharLvl);
//		setCharLvl(iSet4, pCharLvl);
//		setCharLvl(iSet5, pCharLvl);
	}

	private void setCharLvl(ArrayList pProperties, long pCharLvl) {
		if (pProperties != null) {
			for (int i = 0; i < pProperties.size(); i++) {
				((D2ItemProperty) pProperties.get(i)).setCharLvl(pCharLvl);
			}
		}
	}

	public boolean isCursorItem() {
		if (location != 0 && location != 2) {
			if (body_position == 0) {
				// System.err.println("location: " + location );
				return true;
			}
		}
		return false;
	}

	public int compareTo(Object pObject) {
		if (pObject instanceof D2Item) {
			String lItemName = ((D2Item) pObject).iItemName;
			if (iItemName == lItemName) {
				// also both "null"
				return 0;
			}
			if (iItemName == null) {
				return -1;
			}
			if (lItemName == null) {
				return 1;
			}
			return iItemName.compareTo(lItemName);
		}
		return -1;
	}

	public ArrayList getAllProps() {

//		ArrayList out = new ArrayList(iProperties);
//		if (iRuneWord) {
//		out.addAll(iRuneWordProps);
//		}
//		if (iSocketed) {
//		out.addAll(iGemProps);
//		}
//		if (iSet) {
//		out.addAll(iSetProps);
//		}
//		return out;
		return null;
	}

	public int getiDef() {
		return (int) iDef;
	}

	public boolean isEquipped() {

		if (get_location() == 1) {
			return true;
		} else if (get_panel() == 1 && isCharm()) {
			return true;
		} else {
			return false;
		}
	}

	public void setSetProps(int numItems) {
//		iSetProps.clear();
		/*
		 * if(numItems == 0){ iSetProps.clear(); }else if(numItems == 1){
		 * iSetProps.clear();
		 * 
		 * 
		 * }else
		 */

//		if (numItems == 2) {
//			iSetProps.addAll(iSet1);
//		} else if (numItems == 3) {
//			iSetProps.addAll(iSet1);
//			iSetProps.addAll(iSet2);
//		} else if (numItems == 4) {
//			iSetProps.addAll(iSet1);
//			iSetProps.addAll(iSet2);
//			iSetProps.addAll(iSet3);
//		} else if (numItems == 5) {
//			iSetProps.addAll(iSet1);
//			iSetProps.addAll(iSet2);
//			iSetProps.addAll(iSet3);
//			iSetProps.addAll(iSet4);
//		}
		// for(int x =0;x<iSetProps.size();x=x+1){
		// System.out.println(numItems + " VAL:
		// "+((D2ItemProperty)iSetProps.get(x)).getValue());
		// }

		if (isTypeArmor()) {
//			applyEDef();

		}
	}

	public int getSetID() {
		return (int) set_id;
	}

	public int getSetSize() {
		return setSize;
	}

	public String getSetName() {
		return iSetName;
	}

	public boolean statModding() {

		if (iJewel || iGem || iRune) {
			return false;
		} else {

			return true;
		}
	}

	public ArrayList getPerfectStringUS() {
		D2ItemProperty[] outProp;
		ArrayList outArrL = new ArrayList();
		D2TxtFileItemProperties pRow;
		if (this.isUnique()) {
			pRow = D2TxtFile.UNIQUES.getRow(unique_id);
		} else if (this.isSet()) {
			pRow = D2TxtFile.SETITEMS.getRow(set_id);
		} else {
			pRow = D2TxtFile.RUNES.getRow(runeword_id);
		}

		int counter = 1;
		int max = 13;
		if (this.isSet()) {
			max = 10;
		} else if (this.isRuneWord()) {
			max = 8;
		}
		String prop = "prop";

		if (isRuneWord()) {
			prop = "T1Code";
		}

		while (counter < max) {
			outProp = new D2ItemProperty[2];
			if (!pRow.get(prop + counter).equals("")) {

				if (pRow.get(prop + counter).indexOf("*") != -1) {
					break;
				}

//				System.out.println(pRow.get("prop" + counter) + " -- " +
//				pRow.get("min" + counter) + "-" + pRow.get("max" + counter));

				/*
				 * int lProp = Integer.parseInt((D2TxtFile.ITEM_STAT_COST
				 * .searchColumns("Stat", ((D2TxtFile.PROPS
				 * .searchColumns("code", (D2TxtFile.GEMS .searchColumns("code",
				 * item_type)) .get(interestingSubProp[x][y])))
				 * .get("stat1")))).get("ID"));
				 */

				int lProp = Integer.parseInt(D2TxtFile.ITEM_STAT_COST
						.searchColumns(
								"Stat",
								(D2TxtFile.PROPS.searchColumns("code", pRow
										.get(prop + counter)).get("stat1")))
										.get("ID"));
				if (pRow.get(prop + counter).equals("res-all")) {
					lProp = 1337;
				}



				if (lProp == 54 || lProp == 55 || lProp == 56) {
					D2ItemProperty lProperty = new D2ItemProperty(101010,
							iCharLvl, iItemName);
					lProperty.set(101010, null, 0, 0);
					outProp[0] = lProperty;
					outProp[1] = lProperty;
					outArrL.add(outProp);
					if(pRow.get("prop" + counter).equals("dmg-cold")){
						counter = counter + 1;
					}else{
						counter = counter + 3;
					}
					continue;
				}

				if (lProp == 48 || lProp == 49) {
					D2ItemProperty lProperty = new D2ItemProperty(101010,
							iCharLvl, iItemName);
					lProperty.set(101010, null, 0, 1);
					outProp[0] = lProperty;
					outProp[1] = lProperty;
					outArrL.add(outProp);
					if(pRow.get("prop" + counter).equals("dmg-fire")){
						counter = counter + 1;
					}else{
						counter = counter + 2;
					}
					continue;
				}

				if (lProp == 50 || lProp == 51) {
					D2ItemProperty lProperty = new D2ItemProperty(101010,
							iCharLvl, iItemName);
					lProperty.set(101010, null, 0, 2);
					outProp[0] = lProperty;
					outProp[1] = lProperty;
					outArrL.add(outProp);
					if(pRow.get("prop" + counter).equals("dmg-ltng")){
						counter = counter + 1;
					}else{
						counter = counter + 2;
					}
					continue;
				}

				if (lProp == 52 || lProp == 53) {
					D2ItemProperty lProperty = new D2ItemProperty(101010,
							iCharLvl, iItemName);
					lProperty.set(101010, null, 0, 3);
					outProp[0] = lProperty;
					outProp[1] = lProperty;
					outArrL.add(outProp);
					if(pRow.get("prop" + counter).equals("dmg-mag")){
						counter = counter + 1;
					}else{
						counter = counter + 2;
					}
					continue;
				}

				if (lProp == 57 || lProp == 58 || lProp == 59) {
					D2ItemProperty lProperty = new D2ItemProperty(101010,
							iCharLvl, iItemName);
					lProperty.set(101010, null, 0, 4);
					outProp[0] = lProperty;
					outProp[1] = lProperty;
					outArrL.add(outProp);
					if(pRow.get("prop" + counter).equals("dmg-pois")){
						counter = counter + 1;
					}else{
						counter = counter + 3;
					}
					continue;
				}

				int lPropTemp = lProp;
				D2ItemProperty lProperty = new D2ItemProperty(lPropTemp,
						iCharLvl, iItemName);

				D2TxtFileItemProperties lItemStatCost = D2TxtFile.ITEM_STAT_COST
				.getRow(lProperty.getPropNrs()[0]);

				String pMin = pRow.get("min" + counter);
				String pMax = pRow.get("max" + counter);
				String pParam = pRow.get("par" + counter);

				if (isRuneWord()) {
					pMin = pRow.get("T1Min" + counter);
					pMax = pRow.get("T1Max" + counter);
					pParam = pRow.get("T1Param" + counter);
				}


				if (pRow.get(prop + counter).equals("dmg-norm")) {

					lProperty = new D2ItemProperty(21,
							iCharLvl, iItemName);

					lItemStatCost = D2TxtFile.ITEM_STAT_COST
					.getRow(lProperty.getPropNrs()[0]);

					lProperty.set(lPropTemp, lItemStatCost, 0, Long
							.parseLong(pMin));

					outProp[0] = lProperty;
					outProp[1] = lProperty;
					outArrL.add(outProp);

					outProp = new D2ItemProperty[2];

					lProperty = new D2ItemProperty(22,
							iCharLvl, iItemName);

					lItemStatCost = D2TxtFile.ITEM_STAT_COST
					.getRow(lProperty.getPropNrs()[0]);

					lProperty.set(lPropTemp, lItemStatCost, 0, Long
							.parseLong(pMax));

					outProp[0] = lProperty;
					outProp[1] = lProperty;
					outArrL.add(outProp);
					counter ++;
					continue;



				}

				if (lPropTemp == 201 || lPropTemp == 197 || lPropTemp == 199
						|| lPropTemp == 195 || lPropTemp == 198
						|| lPropTemp == 196) {

					if (!pMax.equals("")) {
						lProperty.set(lPropTemp, lItemStatCost, 0, Long
								.parseLong(pMax));
					}

					if (!pParam.equals("")) {
						// if(lPropTemp==198){

						try {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(D2TxtFile.SKILLS.searchColumns(
											"skill", pParam).get("Id")));
						} catch (NullPointerException e) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pParam));
						}
						// }else{
						// lProperty.set(lPropTemp, lItemStatCost, 0,
						// Long.parseLong(pParam));
						// }
					}

					if (!pMin.equals("")) {
						lProperty.set(lPropTemp, lItemStatCost, 0, Long
								.parseLong(pMin));
					}
				} else {

					if (!pMin.equals("")) {
						if (lPropTemp == 204) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMax));
						} else if (lPropTemp == 107) {
							try {
								lProperty.set(lPropTemp, lItemStatCost, 0, Long
										.parseLong(D2TxtFile.SKILLS
												.searchColumns("skill", pParam)
												.get("Id")));
							} catch (NullPointerException e) {
								lProperty.set(lPropTemp, lItemStatCost, 0, Long
										.parseLong(pParam));
							}


						} else {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMin));
						}
					}

					if (!pMax.equals("")) {
						if (lPropTemp == 204) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMin));
						} else if (lPropTemp == 107) {

						} else {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMax));
						}
					}

					if (!pParam.equals("")) {

						if (lPropTemp == 204 ||lPropTemp == 97) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(D2TxtFile.SKILLS.searchColumns(
											"skill", pParam).get("Id")));

//							System.out.println(pParam);
//							System.out.println(Long
//							.parseLong(D2TxtFile.SKILLS.searchColumns(
//							"skill", pParam).get("Id")));

						} else if (lPropTemp == 107) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMin));
						} else {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pParam));
						}
					}
				}
				outProp[0] = lProperty;
				lPropTemp = lProp;

				lProperty = new D2ItemProperty(lPropTemp, iCharLvl, iItemName);

				lItemStatCost = D2TxtFile.ITEM_STAT_COST.getRow(lProperty
						.getPropNrs()[0]);

				pMin = pRow.get("min" + counter);
				pMax = pRow.get("max" + counter);
				pParam = pRow.get("par" + counter);

				if (isRuneWord()) {
					pMin = pRow.get("T1Min" + counter);
					pMax = pRow.get("T1Max" + counter);
					pParam = pRow.get("T1Param" + counter);
				}

				if (lPropTemp == 201 || lPropTemp == 197 || lPropTemp == 199
						|| lPropTemp == 195 || lPropTemp == 198
						|| lPropTemp == 196) {

					if (!pMax.equals("")) {
						lProperty.set(lPropTemp, lItemStatCost, 0, Long
								.parseLong(pMax));
					}

					if (!pParam.equals("")) {
						// if(lPropTemp==198){
						try {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(D2TxtFile.SKILLS.searchColumns(
											"skill", pParam).get("Id")));
						} catch (NullPointerException e) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pParam));
						}
						// }else{
						// lProperty.set(lPropTemp, lItemStatCost, 0,
						// Long.parseLong(pParam));
						// }
					}

					if (!pMin.equals("")) {
						lProperty.set(lPropTemp, lItemStatCost, 0, Long
								.parseLong(pMin));
					}
				} else {
					if (!pMax.equals("")) {
						if (lPropTemp == 107) {

						} else {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMax));
						}
					}

					if (!pMin.equals("")) {
						if (lPropTemp == 107) {
							try {
								lProperty.set(lPropTemp, lItemStatCost, 0, Long
										.parseLong(D2TxtFile.SKILLS
												.searchColumns("skill", pParam)
												.get("Id")));
							} catch (NullPointerException e) {
								lProperty.set(lPropTemp, lItemStatCost, 0, Long
										.parseLong(pParam));
							}
						} else {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMin));
						}
					}

					if (!pParam.equals("")) {
						if (lPropTemp == 204||lPropTemp == 97) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(D2TxtFile.SKILLS.searchColumns(
											"skill", pParam).get("Id")));
						} else if (lPropTemp == 107) {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pMax));
						} else {
							lProperty.set(lPropTemp, lItemStatCost, 0, Long
									.parseLong(pParam));
						}
					}
				}

				// System.out.println(lProperty.getValue());
				outProp[1] = lProperty;
				outArrL.add(outProp);

			}
			counter++;
		}

		// System.out.println(applyPerfDef(tempProp));
		return outArrL;
	}

	public String[] getPerfectDef(ArrayList outArrL) {
		ArrayList tempProp = new ArrayList();
		String[] out = new String[2];
		for (int x = 0; x < outArrL.size(); x++) {
			tempProp.add(((D2ItemProperty[]) outArrL.get(x))[0]);
		}
		out[1] = Long.toString(applyPerfDef(tempProp));
		tempProp = new ArrayList();
		for (int x = 0; x < outArrL.size(); x++) {
			tempProp.add(((D2ItemProperty[]) outArrL.get(x))[1]);
		}
		out[0] = Long.toString(applyPerfDef(tempProp));

		return out;
	}

	public String[] getPerfectDmg(ArrayList outArrL) {
		ArrayList tempProp = new ArrayList();
		String[] out = new String[2];
		for (int x = 0; x < outArrL.size(); x++) {
			tempProp.add(((D2ItemProperty[]) outArrL.get(x))[0]);
		}
		tempProp = applyPerfEDmg(tempProp);
		String outStr = "One Hand Damage: ";
		for (int x = 0; x < tempProp.size(); x = x + 1) {
			if (x == 0) {
				outStr = outStr + tempProp.get(x) + " - ";
			} else if (x == 1) {
				outStr = outStr + tempProp.get(x) + "\n";
			} else if (x == 2) {
				if (iThrow) {
					outStr = outStr + "Throw Damage: " + tempProp.get(x)
					+ " - ";
				} else {
					outStr = outStr + "Two Hand Damage: " + tempProp.get(x)
					+ " - ";
				}
			} else if (x == 3) {
				outStr = outStr + tempProp.get(x) + "\n";
			}
		}
		out[0] = outStr;
		tempProp = new ArrayList();
		for (int x = 0; x < outArrL.size(); x++) {
			tempProp.add(((D2ItemProperty[]) outArrL.get(x))[1]);
		}
		tempProp = applyPerfEDmg(tempProp);

		outStr = "One Hand Damage: ";
		for (int x = 0; x < tempProp.size(); x = x + 1) {
			if (x == 0) {
				outStr = outStr + tempProp.get(x) + " - ";
			} else if (x == 1) {
				outStr = outStr + tempProp.get(x) + "\n";
			} else if (x == 2) {
				if (iThrow) {
					outStr = outStr + "Throw Damage: " + tempProp.get(x)
					+ " - ";
				} else {
					outStr = outStr + "Two Hand Damage: " + tempProp.get(x)
					+ " - ";
				}
			} else if (x == 3) {
				outStr = outStr + tempProp.get(x) + "\n";
			}
		}
		out[1] = outStr;

		return out;
	}

	public ArrayList getPerfectString() {

		if (isUnique() || isSet()) {
			return sortStats(getPerfectStringUS());
		}else if(isRuneWord()){

			ArrayList perfArr = getPerfectStringUS();
			for(int x = 0;x<iGemProps.size();x=x+1){
				//D2ItemProperty[] tempP = {(D2ItemProperty)iGemProps.get(x),(D2ItemProperty)iGemProps.get(x)};
				//perfArr.add(tempP);
			}


			return sortStats(perfArr);
		}

		return null;
	}

	private ArrayList sortStats(ArrayList perfectStringUS) {

		int[] sortArr = new int[perfectStringUS.size()];
		ArrayList outSorted = new ArrayList();
		for (int x = 0; x < perfectStringUS.size(); x = x + 1) {
			sortArr[x] = (((D2ItemProperty[]) perfectStringUS.get(x))[0])
			.getiProp();

		}

		Arrays.sort(sortArr);

		for (int x = 0; x < sortArr.length; x = x + 1) {
			D2ItemProperty[] obj = findObjProp(sortArr[x], perfectStringUS);
			outSorted.add(x, obj);
			perfectStringUS.remove(obj);
		}

		return outSorted;
	}

	private D2ItemProperty[] findObjProp(int i, ArrayList perfectStringUS) {

		for (int x = 0; x < perfectStringUS.size(); x = x + 1) {
			if ((((D2ItemProperty[]) perfectStringUS.get(x))[0]).getiProp() == i) {
				return (D2ItemProperty[]) perfectStringUS.get(x);
			}

		}

		return null;
	}

	private ArrayList applyPerfEDmg(ArrayList iProperties) {

		int ENDam = 0;
		int ENMaxDam = 0;
		int MinDam = 0;
		int MaxDam = 0;
		ArrayList out = new ArrayList();

		for (int x = 0; x < iProperties.size(); x = x + 1) {
			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 17) {
				ENDam = ENDam
				+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
			}
			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 21) {
				MinDam = MinDam
				+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
			}
			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 22) {
				MaxDam = MaxDam
				+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
			}
			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 219) {
				ENMaxDam = ENMaxDam
				+ (int) Math.floor((((D2ItemProperty) iProperties
						.get(x)).getRealValue() * 0.125)
						* iCharLvl);
			}
			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 218) {
				MaxDam = MaxDam
				+ (int) Math.floor((((D2ItemProperty) iProperties
						.get(x)).getRealValue() * 0.125)
						* iCharLvl);
			}
		}

		out.add(String.valueOf((long) Math
				.floor((((double) iinitMinDmg / (double) 100) * ENDam)
						+ (iinitMinDmg + MinDam))));
		out
		.add(String
				.valueOf((long) Math
						.floor((((double) iinitMaxDmg / (double) 100) * (ENDam + ENMaxDam))
								+ (iinitMaxDmg + MaxDam))));

		if (iWhichHand == 0) {
			out.add(String.valueOf((long) Math
					.floor((((double) iinit2MinDmg / (double) 100) * ENDam)
							+ (iinit2MinDmg + MinDam))));
			out
			.add(String
					.valueOf((long) Math
							.floor((((double) iinit2MaxDmg / (double) 100) * (ENDam + ENMaxDam))
									+ (iinit2MaxDmg + MaxDam))));
		}

		return out;

	}

	private long applyPerfDef(ArrayList iProperties) {

//		int ENDef = 0;
//		int Def = 0;
//
//		if (isSet()) {
//
//			for (int x = 0; x < iSetProps.size(); x = x + 1) {
//				if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 16) {
//					ENDef = ENDef
//					+ ((D2ItemProperty) iSetProps.get(x))
//					.getRealValue();
//				}
//				if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 31) {
//					Def = Def
//					+ ((D2ItemProperty) iSetProps.get(x))
//					.getRealValue();
//				}
//				if (((D2ItemProperty) iSetProps.get(x)).getiProp() == 214) {
//					Def = Def
//					+ (int) Math.floor((((D2ItemProperty) iSetProps
//							.get(x)).getRealValue() * 0.125)
//							* iCharLvl);
//				}
//			}
//
//		}
//
//		for (int x = 0; x < iProperties.size(); x = x + 1) {
//			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 16) {
//				ENDef = ENDef
//				+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//			}
//			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 31) {
//				Def = Def
//				+ ((D2ItemProperty) iProperties.get(x)).getRealValue();
//			}
//			if (((D2ItemProperty) iProperties.get(x)).getiProp() == 214) {
//				Def = Def
//				+ (int) Math.floor((((D2ItemProperty) iProperties
//						.get(x)).getRealValue() * 0.125)
//						* iCharLvl);
//			}
//		}
//		return (int) Math.floor((((double) iInitDef / (double) 100) * ENDef)
//				+ (iInitDef + Def));

		return 0;
	}

	public String getPreSuf() {

		String retStr = "";
		for (int x = 0; x < rare_prefixes.length; x++) {

			if (rare_prefixes[x] > 1) {

				retStr = retStr
				+ D2TblFile.getString(D2TxtFile.PREFIX.getRow(
						rare_prefixes[x]).get("Name")) + " ";
			}
		}

		retStr = retStr + iBaseItemName + " ";

		for (int x = 0; x < rare_suffixes.length; x++) {

			if (rare_suffixes[x] > 1) {

				retStr = retStr
				+ D2TblFile.getString(D2TxtFile.SUFFIX.getRow(
						rare_suffixes[x]).get("Name")) + " ";
			}
		}

		return retStr;
	}

	public boolean conforms(String prop, int pVal, boolean min) {

//		System.out.println();
		ArrayList dumpStr = getFullItemDump(1, 0);

		for(int x = 0;x<dumpStr.size();x=x+1){
			if(((String)dumpStr.get(x)).toLowerCase().contains(prop.toLowerCase())){
//				System.out.println(dumpStr.get(x));

				if(pVal == -1337){
					return true;
				}

				Pattern pat = Pattern.compile("\\d+");
				Matcher mat = pat.matcher((String)dumpStr.get(x));

				while(mat.find()){



					if(min == true){
//						System.out.println(mat.group());
						if(Integer.parseInt(mat.group()) >= pVal){

							return true;
						}
					}else{
						if(Integer.parseInt(mat.group()) <= pVal){

							return true;
						}
					}
				}

			}
		}

		return false;
	}

	public int getBlock() {
		// TODO Auto-generated method stub
		return (int)this.cBlock;
	}

	public boolean isABelt() {
		if ( iType.equals("belt") ) {
			System.out.println(iItemName);
			return true;
		}
		else {
			return false;
		}
	}
}
