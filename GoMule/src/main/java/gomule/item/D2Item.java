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

import gomule.D2Files;
import gomule.util.D2BitReader;
import gomule.util.D2ItemException;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;
import randall.flavie.D2ItemInterface;

import java.awt.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    protected String iItemName;
    protected String iBaseItemName;
    private D2PropCollection iProps = new D2PropCollection();
    private ArrayList<D2Item> iSocketedItems;
    private int flags;
    private short version;
    private short location;
    private short body_position;
    private short row;
    private short col;
    private short panel;
    private String item_type;
    private short iSocketNrFilled = 0;
    private short iSocketNrTotal = 0;
    private long fingerprint;
    private short ilvl;
    private short quality;
    private short gfx_num;
    private D2TxtFileItemProperties automod_info;
    private short[] rare_prefixes;
    private short[] rare_suffixes;
    private String personalization;
    private short width;
    private short height;
    private String image_file;
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
    private boolean iGem;
    private boolean iStackable = false;
    private boolean iRune;
    private boolean iTypeMisc;
    private boolean iIdentified;
    private boolean iTypeWeapon;
    private boolean iTypeArmor;
    private short iCurDur;
    private boolean questItem;

    private short iMaxDur;

    private short iDef;

    private short cBlock;

    private short iBlock;

    private short iInitDef;

    private short[] i1Dmg;
    private short[] i2Dmg;

    // 0 FOR BOTH 1 FOR 1H 2 FOR 2H
    private int iWhichHand;

    // private int iLvl;
    private String iFP;

    private String iGUID;

    private boolean iBody = false;

    private String iBodyLoc1;

    private String iBodyLoc2;

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

    private short set_id;

    private final HuffmanLookupTable huffmanLookupTable = HuffmanLookupTable.withStandardDictionary();

    public D2Item(String pFileName, D2BitReader pFile, long pCharLvl)
            throws Exception {
        iFileName = pFileName;
        iIsChar = iFileName.endsWith(".d2s");
        iCharLvl = (int) pCharLvl;

        try {
            int startOfItemInBytes = pFile.get_byte_pos();
            read_item(pFile);
            int endOfItemInBytes = pFile.getNextByteBoundaryInBits() / 8;
            int lLengthToNextJM = endOfItemInBytes - startOfItemInBytes;
            pFile.set_byte_pos(startOfItemInBytes);
            iItem = new D2BitReader(pFile.get_bytes(lLengthToNextJM));
            pFile.set_byte_pos(startOfItemInBytes + lLengthToNextJM);
        } catch (D2ItemException pEx) {
            throw pEx;
        } catch (Exception pEx) {
            pEx.printStackTrace();
            throw new D2ItemException("Error: " + pEx.getMessage() + getExStr());
        }
    }

    // read basic information from the bytes
    // common to all items, then split based on
    // whether the item is an ear
    private void read_item(D2BitReader pFile) throws Exception {
        flags = (int) pFile.unflip(pFile.read(32), 32); // 4 bytes

        iSocketed = check_flag(12);
        iEthereal = check_flag(23);
        iRuneWord = check_flag(27);
        iIdentified = check_flag(5);
        version = 9999;

        pFile.skipBits(3);
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

        //Need to tidy up the properties before the item mods are calculated.
        iProps.deleteUselessProperties();


        if (isTypeArmor() || isTypeWeapon()) {
//			Blunt does 150 damage to undead
            if (iType.equals("club") || iType.equals("scep")
                    || iType.equals("mace") || iType.equals("hamm")) {
                iProps.add(new D2Prop(122, new int[]{150}, 0));
            }
            applyItemMods();
        }
    }

    // read ear related data from the bytes
    private void read_ear(D2BitReader pFile) {
        int eClass = (int) pFile.read(3);
        int eLevel = (int) (pFile.read(7));

        StringBuffer lCharName = new StringBuffer();
        for (int i = 0; i < 18; i++) {
            long lChar = pFile.read(7);
            if (lChar != 0) {
                lCharName.append((char) lChar);
            } else {
                pFile.set_pos(pFile.getNextByteBoundaryInBits() == pFile.get_pos() ? pFile.get_pos() + 8 : pFile.getNextByteBoundaryInBits());
                break;
            }
        }
        iItemType = D2TxtFile.search("ear");
        height = Short.parseShort(iItemType.get("invheight"));
        width = Short.parseShort(iItemType.get("invwidth"));
        image_file = iItemType.get("invfile");
        iBaseItemName = iItemName = lCharName.toString() + "'s Ear";

        iProps.add(new D2Prop(185, new int[]{eClass, eLevel}, 0, true, 39));
    }

    // read non ear data from the bytes,
    // setting class variables for easier access
    private void readExtend(D2BitReader pFile) throws Exception {
        // 9,5 bytes already read (common data)
        item_type = huffmanLookupTable.readHuffmanEncodedString(pFile);
        iItemType = D2TxtFile.search(item_type);
        height = Short.parseShort(iItemType.get("invheight"));
        width = Short.parseShort(iItemType.get("invwidth"));
        image_file = iItemType.get("invfile");

        String lD2TxtFileName = iItemType.getFileName();
        if (lD2TxtFileName != null) {
            iTypeMisc = ("misc".equals(lD2TxtFileName));
            iTypeWeapon = ("weapons".equals(lD2TxtFileName));
            iTypeArmor = ("armor".equals(lD2TxtFileName));
            questItem = !iItemType.get("quest").equals("");
        }

        iType = iItemType.get("type");
        iType2 = iItemType.get("type2");

        // Shields - block chance.
        if (isShield()) {
            cBlock = Short.parseShort(iItemType.get("block"));
        }

        // Requerements
        if (iTypeMisc) {
            iReqLvl = getReq(iItemType.get("levelreq"));
        } else if (iTypeArmor) {
            iReqLvl = getReq(iItemType.get("levelreq"));
            iReqStr = getReq(iItemType.get("reqstr"));

            D2TxtFileItemProperties qualSearch = D2TxtFile.ARMOR.searchColumn(
                    "normcode", item_type);
            iItemQuality = "normal";
            if (qualSearch == null) {
                qualSearch = D2TxtFile.ARMOR.searchColumn("ubercode",
                        item_type);
                iItemQuality = "exceptional";
                if (qualSearch == null) {
                    qualSearch = D2TxtFile.ARMOR.searchColumn("ultracode",
                            item_type);
                    iItemQuality = "elite";
                }
            }

        } else if (iTypeWeapon) {
            iReqLvl = getReq(iItemType.get("levelreq"));
            iReqStr = getReq(iItemType.get("reqstr"));
            iReqDex = getReq(iItemType.get("reqdex"));

            D2TxtFileItemProperties qualSearch = D2TxtFile.WEAPONS
                    .searchColumn("normcode", item_type);
            iItemQuality = "normal";
            if (qualSearch == null) {
                qualSearch = D2TxtFile.WEAPONS.searchColumn("ubercode",
                        item_type);
                iItemQuality = "exceptional";
                if (qualSearch == null) {
                    qualSearch = D2TxtFile.WEAPONS.searchColumn("ultracode",
                            item_type);
                    iItemQuality = "elite";
                }
            }
        }

        String lItemName = D2Files.getInstance().getTranslations().getTranslation(item_type);
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

        long lHasGUID = pFile.read(1);

        if (lHasGUID == 1) { // GUID ???
            if (iType.startsWith("rune") || iType.startsWith("gem")
                    || iType.startsWith("amu") || iType.startsWith("rin")
                    || isCharm() || !isTypeMisc()) {

                iGUID = "0x" + Integer.toHexString((int) pFile.read(32))
                        + " 0x" + Integer.toHexString((int) pFile.read(32))
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
                readPropertiesGems();
                iGem = true;
            }
        }

        if (iType != null && iType2 != null && iType.startsWith("rune")) {
            readPropertiesGems();
            iRune = true;
        }

        D2TxtFileItemProperties lItemType = D2TxtFile.ITEM_TYPES.searchColumn(
                "Code", iType);

        if (lItemType == null) {
            lItemType = D2TxtFile.ITEM_TYPES.searchColumn("Equiv1", iType);
            if (lItemType == null) {
                lItemType = D2TxtFile.ITEM_TYPES.searchColumn("Equiv2", iType);
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
            iSocketedItems = new ArrayList<>();
            pFile.set_pos(pFile.getNextByteBoundaryInBits());
            for (int i = 0; i < iSocketNrFilled; i++) {
                D2Item lSocket = new D2Item(iFileName, pFile, iCharLvl);
                iSocketedItems.add(lSocket);

                if (lSocket.isJewel()) {
                    iProps.addAll(lSocket.getPropCollection(), 1);
                } else if (isTypeWeapon()) {
                    iProps.addAll(lSocket.getPropCollection(), 7);
                } else if (isTypeArmor()) {
                    if (iType.equals("tors") || iType.equals("helm")
                            || iType.equals("phlm") || iType.equals("pelt")
                            || iType.equals("cloa") || iType.equals("circ")) {
                        iProps.addAll(lSocket.getPropCollection(), 8);
                    } else {
                        iProps.addAll(lSocket.getPropCollection(), 9);
                    }
                }
                if (lSocket.iReqLvl > iReqLvl) {
                    iReqLvl = lSocket.iReqLvl;
                }

            }
        }

        if (iRuneWord) {
            ArrayList lList = new ArrayList();
            for (int i = 0; i < iSocketedItems.size(); i++) {
                lList.add(iSocketedItems.get(i).getRuneCode());
            }

            D2TxtFileItemProperties lRuneWord = D2TxtFile.RUNES
                    .searchRuneWord(lList);
            if (lRuneWord != null) {
                iItemName = D2Files.getInstance().getTranslations().getTranslation(lRuneWord.get("Name"));
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
            automod_info = D2TxtFile.AUTOMAGIC.getRow((int) pFile.read(11) - 1);
        }

        // path determined by item quality
        switch (quality) {
            case 1: // low quality item
            {
                short low_quality = (short) pFile.read(3);

                switch (low_quality) {

                    case 0: {
                        iItemName = "Crude " + iItemName;
                        break;
                    }

                    case 1: {
                        iItemName = "Cracked " + iItemName;
                        break;
                    }

                    case 2: {
                        iItemName = "Damaged " + iItemName;
                        break;
                    }

                    case 3: {
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
                // 3bytes, don't know what they are.
                pFile.read(3);
                break;
            }
            case 4: // magic item
            {
                iMagical = true;
                short magic_prefix = (short) pFile.read(11);
                short magic_suffix = (short) pFile.read(11);

                if (magic_suffix == 0) {
                    magic_suffix = 10000;
                }

                D2TxtFileItemProperties lPrefix = D2TxtFile.PREFIX
                        .getRow(magic_prefix);
                String lPreName = lPrefix.get("Name");
                if (lPreName != null && !lPreName.equals("")) {
                    iItemName = D2Files.getInstance().getTranslations().getTranslation(lPreName) + " " + iItemName;
                    int lPreReq = getReq(lPrefix.get("levelreq"));
                    if (lPreReq > iReqLvl) {
                        iReqLvl = lPreReq;
                    }
                }

                D2TxtFileItemProperties lSuffix = D2TxtFile.SUFFIX
                        .getRow(magic_suffix);
                String lSufName = lSuffix.get("Name");
                if (lSufName != null && !lSufName.equals("")) {
                    iItemName = iItemName + " " + D2Files.getInstance().getTranslations().getTranslation(lSufName);
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

                D2TxtFileItemProperties lSet = D2TxtFile.SETITEMS.searchColumn("*ID", String.valueOf(set_id));
                String nameFromSetFile = lSet.get("index");
                String translatedName = D2Files.getInstance().getTranslations().getTranslation(nameFromSetFile);
                iItemName = translatedName == null ? nameFromSetFile : translatedName;
                iSetName = lSet.get("set");

                setSize = (D2TxtFile.SETITEMS.searchColumns("set",
                        iSetName)).size();

                int lSetReq = getReq(lSet.get("lvl req"));
                if (lSetReq != -1 && lSetReq > iReqLvl) {
                    iReqLvl = lSetReq;
                }

                applyAutomodLvl();
                addSetProperties(D2TxtFile.FULLSET.searchColumn("index", lSet.get("set")));
                break;
            }
            case 7: {
                iUnique = true;
                short unique_id = (short) pFile.read(12);
                String s = iItemType.get("uniqueinvfile");
                if (s.compareTo("") != 0) {
                    image_file = s;
                }

                D2TxtFileItemProperties lUnique = D2TxtFile.UNIQUES
                        .searchColumn("*ID", String.valueOf(unique_id));
                if (lUnique == null) break;
                String lNewName = D2Files.getInstance().getTranslations().getTranslation(lUnique.get("index"));
                if (lNewName != null) {
                    iItemName = lNewName;
                }

                if (s.equals("") && !lUnique.get("invfile").equals("")) image_file = lUnique.get("invfile");

                if (lUnique.get("code").equals(item_type)) {
                    int lUniqueReq = getReq(lUnique.get("lvl req"));
                    if (lUniqueReq != -1) {
                        iReqLvl = lUniqueReq;
                    }
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
                if (!iRare) {
                    iCrafted = true;
                    iItemName = "Crafted " + iItemName;
                }
            }

            applyAutomodLvl();
            short rare_name_1 = (short) pFile.read(8);
            short rare_name_2 = (short) pFile.read(8);
            D2TxtFileItemProperties lRareName1 = D2TxtFile.RAREPREFIX
                    .getRow(rare_name_1 - 156);
            D2TxtFileItemProperties lRareName2 = D2TxtFile.RARESUFFIX
                    .getRow(rare_name_2 - 1);
            iItemName = D2Files.getInstance().getTranslations().getTranslation(lRareName1.get("name")) + " "
                    + D2Files.getInstance().getTranslations().getTranslation(lRareName2.get("name"));

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

            if (isCrafted()) {
                iReqLvl = iReqLvl + 10 + (3 * (suf_count + pre_count));
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
        // personalized
        if (check_flag(25)) {
            personalization = "";
            boolean lNotEnded = true;
            for (int i = 0; i < 15 && lNotEnded; i++) {
                char c = (char) pFile.read(8);
                if (c == 0) {
                    lNotEnded = false;
                } else {
                    personalization += c;
                }
            }
            if (lNotEnded == true) {
                pFile.read(8);
            }
        }
    }

    private void addSetProperties(D2TxtFileItemProperties fullsetRow) {

        for (int x = 2; x < 6; x++) {
            if (fullsetRow.get("PCode" + x + "a").equals("")) continue;
            iProps.addAll(D2TxtFile.propToStat(fullsetRow.get("PCode" + x + "a"), fullsetRow.get("PMin" + x + "a"), fullsetRow.get("PMax" + x + "a"), fullsetRow.get("PParam" + x + "a"), (20 + x)));
        }
        for (int x = 1; x < 9; x++) {
            if (fullsetRow.get("FCode" + x).equals("")) continue;
            iProps.addAll(D2TxtFile.propToStat(fullsetRow.get("FCode" + x), fullsetRow.get("FMin" + x), fullsetRow.get("FMax" + x), fullsetRow.get("FParam" + x), 26));
        }
    }

    private void readExtend2(D2BitReader pFile) throws Exception {
        if (isTypeArmor()) {
            iDef = (short) (pFile.read(11) - 10); // -10 ???
            iInitDef = iDef;
            iMaxDur = (short) pFile.read(8);

            if (iMaxDur != 0) {
                iCurDur = (short) pFile.read(9);
            }

        } else if (isTypeWeapon()) {
            if (iType.equals("tkni") || iType.equals("taxe")
                    || iType.equals("jave") || iType.equals("ajav")) {
                iThrow = true;
            }
            iMaxDur = (short) pFile.read(8);

            if (iMaxDur != 0) {
                iCurDur = (short) pFile.read(9);
            }

            if ((D2TxtFile.WEAPONS.searchColumn("code", item_type)).get(
                    "1or2handed").equals("")
                    && !iThrow) {

                if ((D2TxtFile.WEAPONS.searchColumn("code", item_type)).get(
                        "2handed").equals("1")) {
                    iWhichHand = 2;
                    i1Dmg = new short[4];
                    i1Dmg[0] = i1Dmg[1] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumn("code", item_type))
                            .get("2handmindam"));
                    i1Dmg[2] = i1Dmg[3] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumn("code", item_type))
                            .get("2handmaxdam"));
                } else {
                    iWhichHand = 1;
                    i1Dmg = new short[4];
                    i1Dmg[0] = i1Dmg[1] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumn("code", item_type)).get("mindam"));
                    i1Dmg[2] = i1Dmg[3] = Short.parseShort((D2TxtFile.WEAPONS
                            .searchColumn("code", item_type)).get("maxdam"));
                }

            } else {
                iWhichHand = 0;
                if (iThrow) {
                    i2Dmg = new short[4];
                    i2Dmg[0] = i2Dmg[1] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumn(
                                    "code", item_type)).get("minmisdam"));
                    i2Dmg[2] = i2Dmg[3] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumn(
                                    "code", item_type)).get("maxmisdam"));
                } else {
                    i2Dmg = new short[4];
                    i2Dmg[0] = i2Dmg[1] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumn(
                                    "code", item_type)).get("2handmindam"));
                    i2Dmg[2] = i2Dmg[3] = Short
                            .parseShort((D2TxtFile.WEAPONS.searchColumn(
                                    "code", item_type)).get("2handmaxdam"));
                }
                i1Dmg = new short[4];
                i1Dmg[0] = i1Dmg[1] = Short.parseShort((D2TxtFile.WEAPONS
                        .searchColumn("code", item_type)).get("mindam"));
                i1Dmg[2] = i1Dmg[3] = Short.parseShort((D2TxtFile.WEAPONS
                        .searchColumn("code", item_type)).get("maxdam"));
            }

            if ("1".equals(iItemType.get("stackable"))) {
                iStackable = true;
                iCurDur = (short) pFile.read(9);
            }
        } else if (isTypeMisc()) {
            if ("1".equals(iItemType.get("stackable"))) {
                iStackable = true;
                iCurDur = (short) pFile.read(9);
            }

        }

        if (iSocketed) {
            iSocketNrTotal = (short) pFile.read(4);
        }

        int[] lSet = new int[5];

        if (quality == 5) {
            for (int x = 0; x < 5; x++) {
                lSet[x] = (int) pFile.read(1);
            }
        }

        if (iJewel) {
            readProperties(pFile, 1);
        } else {
            readProperties(pFile, 0);
        }
        if (quality == 5) {
            for (int x = 0; x < 5; x++) {
                if (lSet[x] == 1) {
                    readProperties(pFile, x + 2);
                }
            }
        }
        if (iRuneWord) {
            readProperties(pFile, 0);
        }
    }

    private void applyAutomodLvl() {
        // modifies the level if the automod is higher
        if (automod_info == null) {
            return;
        }
        if (Integer.parseInt(automod_info.get("levelreq")) > iReqLvl) {
            iReqLvl = Integer.parseInt(automod_info.get("levelreq"));
        }

    }

    // MBR: unknown, but should be according to file format
    private void readTypes(D2BitReader pFile) {
        // charms ??
        if (isCharm()) {
//			long lCharm1 = pFile.read(1);
            pFile.read(1);
//			long lCharm2 = pFile.read(11);
            pFile.read(11);
            // System.err.println("Charm (?): " + lCharm1 );
            // System.err.println("Charm (?): " + lCharm2 );
        }

        // books / scrolls ??
        if ("tbk".equals(item_type) || "ibk".equals(item_type)) {
//			long lTomb = pFile.read(5);
            pFile.read(5);
            // System.err.println("Tome ID: " + lTomb );
        }

        if ("tsc".equals(item_type) || "isc".equals(item_type)) {
//			long lTomb = pFile.read(5);
            pFile.read(5);
            // System.err.println("Tome ID: " + lTomb );
        }

        // body ??
        if ("body".equals(item_type)) {
//			long lMonster = pFile.read(10);
            pFile.read(10);
            // System.err.println("Monster ID: " + lMonster );
        }
    }

    private void readPropertiesPots(D2BitReader pfile) {

        String[] statsToRead = {"stat1", "stat2"};

        for (int x = 0; x < statsToRead.length; x = x + 1) {

            if ((D2TxtFile.MISC.searchColumn("code", item_type)).get(
                    statsToRead[x]).equals(""))
                continue;
            iProps.add(new D2Prop(Integer.parseInt((D2TxtFile.ITEM_STAT_COST.searchColumn("Stat", (D2TxtFile.MISC
                    .searchColumn("code", item_type))
                    .get(statsToRead[x]))).get("*ID")),
                    new int[]{Integer.parseInt(((D2TxtFile.MISC
                            .searchColumn("code", item_type))
                            .get(statsToRead[x].replaceFirst("stat",
                                    "calc"))))}, 0));
        }
    }

    private void readPropertiesGems() {
//		RUNES ARE GEMS TOO!!!!
        String[][] gemHeaders = {{"weaponMod1", "weaponMod2", "weaponMod3"},
                {"helmMod1", "helmMod2", "helmMod3"},
                {"shieldMod1", "shieldMod2", "shieldMod3"}};

        for (int x = 0; x < gemHeaders.length; x++) {

            for (int y = 0; y < gemHeaders[x].length; y++) {

                if (D2TxtFile.GEMS.searchColumn("code", item_type).get(
                        gemHeaders[x][y] + "Code").equals(""))
                    continue;
                iProps.addAll(D2TxtFile.propToStat(D2TxtFile.GEMS
                        .searchColumn("code", item_type).get(
                                gemHeaders[x][y] + "Code"), D2TxtFile.GEMS
                        .searchColumn("code", item_type).get(
                                gemHeaders[x][y] + "Min"), D2TxtFile.GEMS
                        .searchColumn("code", item_type).get(
                                gemHeaders[x][y] + "Max"), D2TxtFile.GEMS
                        .searchColumn("code", item_type).get(
                                gemHeaders[x][y] + "Param"), (x + 7)));
            }
        }
    }

    private void readProperties(D2BitReader pFile, int qFlag) {

        int rootProp = (int) pFile.read(9);

        while (rootProp != 511) {

            iProps.readProp(pFile, rootProp, qFlag);

            if (rootProp == 17) {
                iProps.readProp(pFile, 18, qFlag);
            } else if (rootProp == 48) {
                iProps.readProp(pFile, 49, qFlag);
            } else if (rootProp == 50) {
                iProps.readProp(pFile, 51, qFlag);
            } else if (rootProp == 52) {
                iProps.readProp(pFile, 53, qFlag);
            } else if (rootProp == 54) {
                iProps.readProp(pFile, 55, qFlag);
                iProps.readProp(pFile, 56, qFlag);
            } else if (rootProp == 57) {
                iProps.readProp(pFile, 58, qFlag);
                iProps.readProp(pFile, 59, qFlag);
            }
            rootProp = (int) pFile.read(9);
        }

    }

    private void applyItemMods() {

        int[] armourTriple = new int[]{0, 0, 0};
        int[] dmgTriple = new int[]{0, 0, 0, 0, 0};
        int[] durTriple = new int[]{0, 0};
        RequirementModifierAccumulator requirementModifierAccumulator = new RequirementModifierAccumulator();

        iProps.applyOp(iCharLvl);

        for (int x = 0; x < iProps.size(); x++) {
            if (((D2Prop) iProps.get(x)).getQFlag() != 0 && ((D2Prop) iProps.get(x)).getQFlag() != 12 && ((D2Prop) iProps.get(x)).getQFlag() != 13 && ((D2Prop) iProps.get(x)).getQFlag() != 14 && ((D2Prop) iProps.get(x)).getQFlag() != 15 && ((D2Prop) iProps.get(x)).getQFlag() != 16)
                continue;

            // +Dur
            if (((D2Prop) iProps.get(x)).getPNum() == 73) {
                durTriple[0] = durTriple[0]
                        + ((D2Prop) iProps.get(x)).getPVals()[0];
            }

            // Dur%
            if (((D2Prop) iProps.get(x)).getPNum() == 75) {
                durTriple[1] = durTriple[1]
                        + ((D2Prop) iProps.get(x)).getPVals()[0];
            }

            // +LvlReq
            if (((D2Prop) iProps.get(x)).getPNum() == 92) {
                requirementModifierAccumulator.accumulateLevelRequirement(((D2Prop) iProps.get(x)).getPVals()[0]);
            }

            // -Req
            if (((D2Prop) iProps.get(x)).getPNum() == 91) {
                requirementModifierAccumulator.accumulatePercentRequirements(((D2Prop) iProps.get(x)).getPVals()[0]);
            }

            // +Skills modify level
            if (((D2Prop) iProps.get(x)).getPNum() == 97
                    || ((D2Prop) iProps.get(x)).getPNum() == 107) {

                D2TxtFileItemProperties skillsRow = D2TxtFile.SKILLS.searchColumn(
                        "skilldesc",
                        D2TxtFile.SKILL_DESC.getRow(
                                ((D2Prop) iProps.get(x)).getPVals()[0]).get(
                                "skilldesc"));
                String reqlevel = skillsRow.get("reqlevel");
                try {
                    if (iReqLvl < Integer.parseInt(reqlevel)) {
                        iReqLvl = (Integer.parseInt(reqlevel));
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse level req number for " + skillsRow.get("skill"));
                }
            }

            if (isTypeArmor()) {

                // EDef
                if (((D2Prop) iProps.get(x)).getPNum() == 16) {
                    armourTriple[0] = armourTriple[0]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                }

                // +Def
                if (((D2Prop) iProps.get(x)).getPNum() == 31) {
                    armourTriple[1] = armourTriple[1]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                }

                // +Def/lvl
                if (((D2Prop) iProps.get(x)).getPNum() == 214) {
                    armourTriple[2] = armourTriple[2]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                }

                if (isShield()) {
                    if (((D2Prop) iProps.get(x)).getPNum() == 20) {
                        iBlock = (short) (cBlock + ((D2Prop) iProps.get(x))
                                .getPVals()[0]);
                    }
                }

            } else if (isTypeWeapon()) {

                // EDmg
                if (((D2Prop) iProps.get(x)).getPNum() == 17) {
                    dmgTriple[0] = dmgTriple[0]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                }

                // MinDMg
                if (((D2Prop) iProps.get(x)).getPNum() == 21) {
                    dmgTriple[1] = dmgTriple[1]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                    if (((D2Prop) iProps.get(x)).getFuncN() == 31) {
                        dmgTriple[2] = dmgTriple[2]
                                + ((D2Prop) iProps.get(x)).getPVals()[1];
                    }
                }

                // MaxDmg
                if (((D2Prop) iProps.get(x)).getPNum() == 22) {
                    dmgTriple[2] = dmgTriple[2]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                }

                // MaxDmg/Lvl
                if (((D2Prop) iProps.get(x)).getPNum() == 218) {
                    dmgTriple[3] = dmgTriple[3]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                }

                // MaxDmg%/lvl
                if (((D2Prop) iProps.get(x)).getPNum() == 219) {
                    dmgTriple[4] = dmgTriple[4]
                            + ((D2Prop) iProps.get(x)).getPVals()[0];
                }
            }
        }

        iReqLvl = iReqLvl + requirementModifierAccumulator.getLevelRequirement();
        double percentReqirementsModifier = requirementModifierAccumulator.getPercentRequirements() / (double) 100;
        iReqDex = iReqDex + ((int) (iReqDex * percentReqirementsModifier));
        iReqStr = iReqStr + ((int) (iReqStr * percentReqirementsModifier));

        if (isTypeWeapon()) {

            if (iEthereal) {
                i1Dmg[0] = i1Dmg[1] = (short) Math
                        .floor((((double) i1Dmg[1] / (double) 100) * (double) 50)
                                + i1Dmg[1]);
                i1Dmg[2] = i1Dmg[3] = (short) Math
                        .floor((((double) i1Dmg[3] / (double) 100) * (double) 50)
                                + i1Dmg[3]);

                if (iWhichHand == 0) {
                    i2Dmg[0] = i2Dmg[1] = (short) Math
                            .floor((((double) i2Dmg[1] / (double) 100) * (double) 50)
                                    + i2Dmg[1]);
                    i2Dmg[2] = i2Dmg[3] = (short) Math
                            .floor((((double) i2Dmg[3] / (double) 100) * (double) 50)
                                    + i2Dmg[3]);
                }
            }

            i1Dmg[1] = (short) Math
                    .floor((((double) i1Dmg[1] / (double) 100) * dmgTriple[0])
                            + (i1Dmg[1] + dmgTriple[1]));
            i1Dmg[3] = (short) Math
                    .floor((((double) i1Dmg[3] / (double) 100) * (dmgTriple[0] + dmgTriple[4]))
                            + (i1Dmg[3] + (dmgTriple[2] + dmgTriple[3])));

            if (iWhichHand == 0) {
                i2Dmg[1] = (short) Math.floor((((double) i2Dmg[1] / (double) 100) * dmgTriple[0])
                        + (i2Dmg[1] + dmgTriple[1]));
                i2Dmg[3] = (short) Math.floor((((double) i2Dmg[3] / (double) 100) * (dmgTriple[0] + dmgTriple[4]))
                        + (i2Dmg[3] + (dmgTriple[2] + dmgTriple[3])));
            }
            if (i1Dmg[1] > i1Dmg[3]) {
                i1Dmg[3] = (short) (i1Dmg[1] + 1);
            }


        } else if (isTypeArmor()) {
            iDef = (short) Math
                    .floor((((double) iInitDef / (double) 100) * armourTriple[0])
                            + (iInitDef + (armourTriple[1] + armourTriple[2])));
        }

        iMaxDur = (short) Math
                .floor((((double) iMaxDur / (double) 100) * durTriple[1])
                        + (iMaxDur + durTriple[0]));


    }

    private boolean check_flag(int bit) {
        if (((flags >>> (32 - bit)) & 1) == 1)
            return true;
        else
            return false;
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

    private String getExStr() {
        return " (" + iItemName + ", " + iFP + ")";
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

    public void toWriter(PrintWriter pw) {
        pw.println();
        pw.print(D2ItemRenderer.itemDump(this, true));
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

    // setter for the row
    // necessary for moving items
    public void set_row(short r) {
        iItem.set_byte_pos(4);
        iItem.skipBits(14);
        iItem.write((long) r, 4);
        row = r;
    }

    // accessor for the column
    public short get_col() {
        return col;
    }

    // setter for the column
    // necessary for moving items
    public void set_col(short c) {
        iItem.set_byte_pos(4);
        iItem.skipBits(10);
        iItem.write((long) c, 4);
        col = c;
    }

    public short get_location() {
        return location;
    }

    public void set_location(short l) {
        iItem.set_byte_pos(4);
        iItem.skipBits(3);
        iItem.write((long) l, 3);
        location = l;
    }

    public short get_body_position() {
        return body_position;
    }

    public void set_body_position(short bp) {
        iItem.set_byte_pos(4);
        iItem.skipBits(6);
        iItem.write((long) bp, 4);
        body_position = bp;
    }

    public short get_panel() {
        return panel;
    }

    public void set_panel(short p) {
        iItem.set_byte_pos(4);
        iItem.skipBits(18);
        iItem.write((long) p, 3);
        panel = p;
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

    public short getSetID() {
        return set_id;
    }

    public String get_version() {

        if (version == 0) {
            return "Legacy (pre 1.08)";
        }

        if (version == 1) {
            return "Classic";
        }
//2 is another version perhaps?
        if (version == 100) {
            return "Expansion";
        }

        if (version == 101) {
            return "Expansion 1.10+";
        }
//		System.out.println(version);

        if (version == 9999) {
            return "Resurrected";
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
        return iItemName;
    }

    public String getFingerprint() {
        return iFP;
    }

    public short getIlvl() {
        return ilvl;
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
            return Color.orange;
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

    public boolean isShield() {
        if (iType != null) {
            if (iType.equals("ashd") || iType.equals("shie")
                    || iType.equals("head")) {
                return true;
            }
        }
        return false;
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

    public int getiDef() {
        return (int) iDef;
    }

    public boolean isCharacterItem() {

        //Belt or equipped
        if (get_location() == 1 || get_location() == 2) {
            return true;
        } else if (get_location() == 0) {
            switch (get_panel()) {
                case 1:
                case 4:
                case 5:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }

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

    public boolean isEquipped(int wepSlot) {

        if (get_location() == 1) {

            if (!isTypeWeapon() && !isShield()) return true;
            if (wepSlot == 0) {
                if (get_body_position() == 4 || get_body_position() == 5) return true;
            } else if (wepSlot == 1) {
                if (get_body_position() == 11 || get_body_position() == 12) return true;
            }
            return false;
        } else if (get_panel() == 1 && isCharm()) {
            return true;
        } else {
            return false;
        }
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

    public void setCharLvl(int pCharLvl) {
        iCharLvl = pCharLvl;
    }

    public String getPreSuf() {

        String retStr = "";
        for (int x = 0; x < rare_prefixes.length; x++) {

            if (rare_prefixes[x] > 1) {

                retStr = retStr
                        + D2Files.getInstance().getTranslations().getTranslation(D2TxtFile.PREFIX.getRow(
                        rare_prefixes[x]).get("Name")) + " ";
            }
        }

        retStr = retStr + iBaseItemName + " ";

        for (int x = 0; x < rare_suffixes.length; x++) {

            if (rare_suffixes[x] > 1) {

                retStr = retStr
                        + D2Files.getInstance().getTranslations().getTranslation(D2TxtFile.SUFFIX.getRow(
                        rare_suffixes[x]).get("Name")) + " ";
            }
        }

        return retStr;
    }

    public boolean conforms(String prop, int pVal, boolean min) {
        String dumpStr = D2ItemRenderer.itemDump(this, true);
        if (dumpStr.toLowerCase().contains(prop.toLowerCase())) {
            if (pVal == -1337) {
                return true;
            }
            Pattern propertyLinePattern = Pattern.compile("(\\n.*" + prop.toLowerCase() + ".*\\n)", Pattern.UNIX_LINES);
            Matcher propertyPatternMatcher = propertyLinePattern.matcher("\n" + dumpStr.toLowerCase() + "\n");
            while (propertyPatternMatcher.find()) {
                Pattern pat = Pattern.compile("[^\\(?](\\d+)");
                Matcher mat = pat.matcher(propertyPatternMatcher.group());
                while (mat.find()) {
                    if (mat.groupCount() > 0) {
                        if (min == true) {
                            if (Integer.parseInt(mat.group(1)) >= pVal) {

                                return true;
                            }
                        } else {
                            if (Integer.parseInt(mat.group(1)) <= pVal) {

                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public int getBlock() {
        return (int) this.cBlock;
    }

    public boolean isABelt() {
        if (iType.equals("belt")) {
            return true;
        } else {
            return false;
        }
    }

    public D2PropCollection getPropCollection() {
        return iProps;
    }

    public String getItemQuality() {
        return iItemQuality;
    }

    public short getQuality() {
        return quality;
    }

    public String getFileName() {
        return iFileName;
    }

    public boolean isCharacter() {
        return iIsChar;
    }

    public void refreshItemMods() {
        if (isTypeArmor() || isTypeWeapon()) {
            applyItemMods();
        }
    }

    public String getBaseItemName() {
        return iBaseItemName;
    }

    public boolean isMoveable() {

        if (get_location() == 0 && get_panel() == 1 && (getName().toLowerCase().equals("horadric cube") || isCharm() || getName().toLowerCase().equals("key") || getName().toLowerCase().indexOf("tome of") != -1)) {
            //Inv
        } else if (get_location() == 2) {
            //Belt
        } else if (get_location() == 0 && get_panel() == 5 && getName().toLowerCase().equals("horadric cube")) {
            //Stash
        } else if (get_location() == 1) {
            //equipped
        } else {
            return true;
        }
        return false;
    }

    public boolean isQuestItem() {
        return questItem;
    }

    public String getPersonalization() {
        return personalization;
    }

    public ArrayList<D2Item> getiSocketedItems() {
        return iSocketedItems;
    }

    public int getiWhichHand() {
        return iWhichHand;
    }

    public boolean isiThrow() {
        return iThrow;
    }

    public short[] getI1Dmg() {
        return i1Dmg;
    }

    public short[] getI2Dmg() {
        return i2Dmg;
    }

    public short getiBlock() {
        return iBlock;
    }

    public short getiCurDur() {
        return iCurDur;
    }

    public short getiMaxDur() {
        return iMaxDur;
    }

    public String getiGUID() {
        return iGUID;
    }

    public boolean isiIdentified() {
        return iIdentified;
    }

    public int getiCharLvl() {
        return iCharLvl;
    }
}
