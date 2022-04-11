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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * @author Marco
 */
public final class D2TxtFile {

    private static final String DELIMITER = "	";

    public static D2TxtFile MISC;
    public static D2TxtFile ARMOR;
    public static D2TxtFile WEAPONS;
    public static D2TxtFile UNIQUES;
    public static D2TxtFile SETITEMS;
    public static D2TxtFile PREFIX;
    public static D2TxtFile SUFFIX;
    public static D2TxtFile RAREPREFIX;
    public static D2TxtFile RARESUFFIX;
    public static D2TxtFile RUNES;
    public static D2TxtFile ITEM_TYPES;
    public static D2TxtFile ITEM_STAT_COST;
    public static D2TxtFile SKILL_DESC;
    public static D2TxtFile SKILLS;
    public static D2TxtFile GEMS;
    public static D2TxtFile PROPS;
    public static D2TxtFile HIRE;
    public static D2TxtFile FULLSET;
    public static D2TxtFile CHARSTATS;
    public static D2TxtFile AUTOMAGIC;
    /*
     * DROP CALC
     */
    public static D2TxtFile MONSTATS;
    public static D2TxtFile TCS;
    public static D2TxtFile LEVELS;
    public static D2TxtFile SUPUNIQ;
    public static D2TxtFile ITEMRATIO;

    private static String folder;
    private static boolean read = false;

    private final String fileName;
    private List<String> header;
    private List<List<String>> data;

    private D2TxtFile(String fileName) {
        this.fileName = fileName;
    }

    public static void constructTxtFiles(String folder) {
        if (read) return;
        D2TxtFile.folder = folder;
        MISC = new D2TxtFile("misc");
        ARMOR = new D2TxtFile("armor");
        WEAPONS = new D2TxtFile("weapons");
        UNIQUES = new D2TxtFile("uniqueitems");
        SETITEMS = new D2TxtFile("setitems");
        PREFIX = new D2TxtFile("magicprefix");
        SUFFIX = new D2TxtFile("magicsuffix");
        RAREPREFIX = new D2TxtFile("rareprefix");
        RARESUFFIX = new D2TxtFile("raresuffix");
        RUNES = new D2TxtFile("runes");
        ITEM_TYPES = new D2TxtFile("itemtypes");
        ITEM_STAT_COST = new D2TxtFile("itemstatcost");
        SKILL_DESC = new D2TxtFile("skilldesc");
        SKILLS = new D2TxtFile("skills");
        GEMS = new D2TxtFile("gems");
        PROPS = new D2TxtFile("properties");
        MONSTATS = new D2TxtFile("monstats");
        TCS = new D2TxtFile("treasureclassex");
        LEVELS = new D2TxtFile("levels");
        SUPUNIQ = new D2TxtFile("superuniques");
        HIRE = new D2TxtFile("hireling");
        FULLSET = new D2TxtFile("sets");
        CHARSTATS = new D2TxtFile("charstats");
        AUTOMAGIC = new D2TxtFile("automagic");
        ITEMRATIO = new D2TxtFile("itemRatio");
        read = true;
    }

    public static String getCharacterCode(int pChar) {
        switch (pChar) {
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

    public static List<D2Prop> propToStat(String pCode, String pMin, String pMax, String pParam, int qFlag) {
        List<D2Prop> outArr = new ArrayList<>();
        for (int x = 1; x < 8; x++) {
            String propsStatCode = D2TxtFile.PROPS.searchColumn("code", pCode).get("stat" + x);
            if (propsStatCode.equals("")) {
                if (pCode.equals("dmg%") && x == 1) {
                    propsStatCode = "item_maxdamage_percent";
                } else {
                    break;
                }
            }
            int[] pVals = {0, 0, 0};
            if (!pMin.equals("")) {
                try {
                    pVals[0] = Integer.parseInt(pMin);
                } catch (NumberFormatException e) {
                    return outArr;
                }
            }
            if (!pMax.equals("")) {
                try {
                    pVals[1] = Integer.parseInt(pMax);
                } catch (NumberFormatException e) {
                    return outArr;
                }
            }
            if (!pParam.equals("")) {
                try {
                    pVals[2] = Integer.parseInt(pParam);
                } catch (NumberFormatException e) {
                    return outArr;
                }
            }
            if (propsStatCode.indexOf("max") != -1) {
                pVals[0] = pVals[1];
            } else if (propsStatCode.indexOf("length") != -1) {
                if (pVals[2] != 0) {
                    pVals[0] = pVals[2];
                }
            }
            pVals[2] = 0;
            if (propsStatCode.equals("item_addclassskills")) {
                pVals[0] = Integer.parseInt(D2TxtFile.PROPS.searchColumn("code", pCode).get("val1"));
            }
            outArr.add(new D2Prop(Integer.parseInt(D2TxtFile.ITEM_STAT_COST.searchColumn("Stat", propsStatCode).get("*ID")), pVals, qFlag));
        }
        return outArr;
    }

    public static D2TxtFileItemProperties search(String pCode) {
        D2TxtFileItemProperties lFound = MISC.searchColumn("code", pCode);
        if (lFound == null) {
            lFound = ARMOR.searchColumn("code", pCode);
        }
        if (lFound == null) {
            lFound = WEAPONS.searchColumn("code", pCode);
        }
        return lFound;
    }

    public String getFileName() {
        return fileName;
    }

    public int getRowSize() {
        init();
        return data.size();
    }

    private void init() {
        if (data != null) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(folder + File.separator + fileName + ".txt"))) {
            header = asList(reader.readLine().split(DELIMITER));
            data = reader.lines()
                    .map(line -> asList(line.split(DELIMITER)))
                    .filter(this::meaningfulRow)
                    .collect(toList());
        } catch (Exception ex) {
            D2FileManager.displayErrorDialog(ex);
        }
    }

    private boolean meaningfulRow(List<String> values) {
        return !asList("UniqueItems", "SetItems").contains(fileName)
                || !values.isEmpty() && !values.get(0).equals("Expansion");
    }

    String getValue(String columnName, int rowNumber) {
        int columnNumber = findColumnNumber(columnName);
        if (columnNumber != -1 && rowNumber < data.size()) {
            List<String> row = data.get(rowNumber);
            if (columnNumber < row.size()) {
                return row.get(columnNumber);
            }
        }
        return "";
    }

    private int findColumnNumber(String columnName) {
        init();
        return header.indexOf(columnName);
    }

    public D2TxtFileItemProperties getRow(int pRowNr) {
        return new D2TxtFileItemProperties(this, pRowNr);
    }

    public D2TxtFileItemProperties searchColumn(String columnName, String columnValue) {
        int columnNumber = findColumnNumber(columnName);
        if (columnNumber != -1) {
            for (int rowNumber = 0; rowNumber < data.size(); rowNumber++) {
                List<String> row = data.get(rowNumber);
                if (columnNumber < row.size() && row.get(columnNumber).equals(columnValue)) {
                    return new D2TxtFileItemProperties(this, rowNumber);
                }
            }
        }
        return null;
    }

    public List<D2TxtFileItemProperties> searchColumnsMultipleHits(String pCol, String pText) {
        List<D2TxtFileItemProperties> hits = new ArrayList<>();
        int lColNr = findColumnNumber(pCol);
        if (lColNr != -1) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).size() - 1 >= lColNr) {
                    if (data.get(i).get(lColNr).equals(pText)) {
                        hits.add(new D2TxtFileItemProperties(this, i));
                    }
                }
            }
        }
        return hits;
    }

    public D2TxtFileItemProperties searchRuneWord(List<String> pList) {
        int[] lRuneNr = new int[]{findColumnNumber("Rune1"), findColumnNumber("Rune2"), findColumnNumber("Rune3"), findColumnNumber("Rune4"), findColumnNumber("Rune5"), findColumnNumber("Rune6")};
        for (int i = 0; i < data.size(); i++) {
            List<String> lRW = new ArrayList<>();
            for (int j = 0; j < lRuneNr.length; j++) {
                String lFile = data.get(i).get(lRuneNr[j]);
                if (lFile != null && !lFile.equals("")) {
                    lRW.add(lFile);
                } else {
                    break;
                }
            }
            if (pList.size() == lRW.size()) {
                boolean lIsRuneWord = true;
                for (int j = 0; j < pList.size() && lIsRuneWord; j++) {
                    if (!lRW.get(j).equals(pList.get(j))) {
                        lIsRuneWord = false;
                    }
                }
                if (lIsRuneWord) {
                    return new D2TxtFileItemProperties(this, i);
                }
            }
        }
        return null;
    }
}