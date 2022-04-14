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
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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

    public static List<D2Prop> propToStat(
            String propertyCode, String minStr, String maxStr, String paramStr, int qFlag) {
        try {
            int min = minStr.equals("") ? 0 : Integer.parseInt(minStr);
            int max = maxStr.equals("") ? 0 : Integer.parseInt(maxStr);
            int param = paramStr.equals("") ? 0 : Integer.parseInt(paramStr);
            return propToStat(propertyCode, min, max, param, qFlag);
        } catch (NumberFormatException e) {
            return emptyList();
        }
    }

    private static List<D2Prop> propToStat(String propertyCode, int minStr, int maxStr, int paramStr, int qFlag) {
        D2TxtFileItemProperties propertiesRow = D2TxtFile.PROPS.searchColumnUnsafe("code", propertyCode);
        List<D2Prop> d2Props = new ArrayList<>();
        for (int index = 1; index < 8; index++) {
            String statValue = calcStatValue(propertyCode, propertiesRow, index);
            if (statValue == null) {
                break;
            }
            int id = searchIdStat(statValue);
            int min = calcMin(minStr, maxStr, paramStr, statValue, propertiesRow);
            d2Props.add(new D2Prop(id, new int[]{min, maxStr, 0}, qFlag));
        }
        return d2Props;
    }

    private D2TxtFileItemProperties searchColumnUnsafe(String columnName, String propertyCode) {
        D2TxtFileItemProperties propertiesRow = searchColumn(columnName, propertyCode);
        if (propertiesRow == null) {
            throw new IllegalArgumentException("'" + columnName + "' column value not found: " + propertyCode);
        }
        return propertiesRow;
    }

    private static String calcStatValue(String propertyCode, D2TxtFileItemProperties propertiesRow, int index) {
        String statValue = propertiesRow.get("stat" + index);
        if (!statValue.equals("")) {
            return statValue;
        }
        if (propertyCode.equals("dmg%") && index == 1) {
            return "item_maxdamage_percent";
        }
        return null;
    }

    private static int searchIdStat(String statValue) {
        D2TxtFileItemProperties stat = D2TxtFile.ITEM_STAT_COST.searchColumnUnsafe("Stat", statValue);
        return Integer.parseInt(stat.get("*ID"));
    }

    private static int calcMin(int min, int max, int param, String statValue, D2TxtFileItemProperties propertiesRow) {
        if (statValue.contains("max")) {
            return max;
        } else if (statValue.contains("length") && param != 0) {
            return param;
        }
        if (statValue.equals("item_addclassskills")) {
            return Integer.parseInt(propertiesRow.get("val1"));
        }
        return min;
    }

    public static D2TxtFileItemProperties search(String columnValue) {
        return Stream.of(MISC, ARMOR, WEAPONS)
                .map(d2TxtFile -> d2TxtFile.searchColumn("code", columnValue))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
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
        List<D2TxtFileItemProperties> result = searchColumns(columnName, columnValue, true);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<D2TxtFileItemProperties> searchColumns(String columnName, String columnValue) {
        return searchColumns(columnName, columnValue, false);
    }

    private List<D2TxtFileItemProperties> searchColumns(String columnName, String columnValue, boolean single) {
        int columnNumber = findColumnNumber(columnName);
        if (columnNumber == -1) {
            return emptyList();
        }
        List<D2TxtFileItemProperties> hits = new ArrayList<>();
        for (int rowNumber = 0; rowNumber < data.size(); rowNumber++) {
            List<String> row = data.get(rowNumber);
            if (columnNumber < row.size() && row.get(columnNumber).equals(columnValue)) {
                hits.add(new D2TxtFileItemProperties(this, rowNumber));
                if (single) break;
            }
        }
        return hits;
    }

    public D2TxtFileItemProperties searchRuneWord(List<String> runesEmbedded) {
        List<Integer> runeColumnNumbers = asList(
                findColumnNumber("Rune1"),
                findColumnNumber("Rune2"),
                findColumnNumber("Rune3"),
                findColumnNumber("Rune4"),
                findColumnNumber("Rune5"),
                findColumnNumber("Rune6")
        );
        for (int rowNumber = 0; rowNumber < data.size(); rowNumber++) {
            List<String> row = data.get(rowNumber);
            if (runesEmbedded.equals(getRuneWord(row, runeColumnNumbers))) {
                return new D2TxtFileItemProperties(this, rowNumber);
            }
        }
        return null;
    }

    private List<String> getRuneWord(List<String> row, List<Integer> runeColumnNumbers) {
        return runeColumnNumbers.stream()
                .map(row::get)
                .filter(rune -> rune != null && !rune.equals(""))
                .collect(toList());
    }
}