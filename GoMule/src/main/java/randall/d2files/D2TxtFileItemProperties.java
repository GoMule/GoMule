/*******************************************************************************
 *
 * Copyright 2007 Randall
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

/**
 * @author Marco
 */
public class D2TxtFileItemProperties {

    private final D2TxtFile txtFile;
    private final int rowNumber;

    public D2TxtFileItemProperties(D2TxtFile txtFile, int rowNumber) {
        this.txtFile = txtFile; // txtFile.getRowNr()
        this.rowNumber = rowNumber;
    }

    public String get(String pKey) {
//        int x = 1;
//        if (pKey.equals("str name")) {
//            while (x == 1) {
//                int test = 221;
//                if (D2Files.getInstance().getTranslations().getTranslation(txtFile.getValue(test, "str name")) != null) {
//                    System.out.println(D2Files.getInstance().getTranslations().getTranslation(txtFile.getValue(test, "str name")));
//                    System.out.println((txtFile.getFileName()));
//                    System.out.println("bl");
//                }
//            }
//        }
        return txtFile.getValue(rowNumber, pKey);
    }

    public String getFileName() {
        return txtFile.getFileName();
    }

    public String getName() {
//        if (txtFile == D2TxtFile.MISC) {
//            String lType = txtFile.getValue(rowNumber, "type");
//            if (lType != null && !"".equals(lType.trim())) {
//                D2TxtFileItemProperties lItemType = D2TxtFile.ITEMTYPES.searchColumns("Code", lType);
//                if (lItemType != null) {
//                    return lItemType.get("ItemType");
//                }
//            }
//        }
        return txtFile.getValue(rowNumber, "name");
    }

    public String getTblName() {
        return txtFile.getValue(rowNumber, "transtbl");
    }

    public int getRowNum() {
        return rowNumber;
    }
}