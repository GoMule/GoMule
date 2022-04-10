package gomule.item;

import java.awt.*;
import java.util.ArrayList;

public class D2ItemRenderer {

    public static String itemDumpHtml(D2Item d2Item, boolean extended) {
        return generatePropString(d2Item, extended).toString();
    }

    public static String itemDump(D2Item d2Item, boolean extended) {
        return htmlStrip(generatePropString(d2Item, extended));
    }

    private static String htmlStrip(StringBuffer htmlString) {
        String dumpStr = htmlString.toString().replaceAll("<br>&#10;", System.getProperty("line.separator"));
        dumpStr = dumpStr.replaceAll("&#32;", "");
        return dumpStr.replaceAll("<[^>]*>", "");
    }

    private static StringBuffer generatePropString(D2Item d2Item, boolean extended) {
        StringBuffer propString = new StringBuffer("<html>");
        propString.append(generatePropStringNoHtmlTags(d2Item, extended));
        return propString.append("</html>");
    }

    private static StringBuffer generatePropStringNoHtmlTags(D2Item d2Item, boolean extended) {
        d2Item.getPropCollection().tidy();
        StringBuffer dispStr = new StringBuffer("<center>");
        String base = (Integer.toHexString(Color.white.getRGB())).substring(2, Integer.toHexString(Color.white.getRGB()).length());
        String rgb = (Integer.toHexString(d2Item.getItemColor().getRGB())).substring(2, Integer.toHexString(d2Item.getItemColor().getRGB()).length());
        String iItemName = d2Item.getItemName();
        if (d2Item.getPersonalization() == null) {
            dispStr.append("<font color=\"#" + base + "\">" + "<font color=\"#" + rgb + "\">" + iItemName + "</font>" + "<br>&#10;");
        } else {
            dispStr.append("<font color=\"#" + base + "\">" + "<font color=\"#" + rgb + "\">" + d2Item.getPersonalization() + "'s " + iItemName + "</font>" + "<br>&#10;");
        }
        String iBaseItemName = d2Item.getBaseItemName();
        ArrayList iSocketedItems = d2Item.getiSocketedItems();
        if (!iBaseItemName.equals(iItemName))
            dispStr.append("<font color=\"#" + rgb + "\">" + iBaseItemName + "</font>" + "<br>&#10;");
        if (d2Item.isRuneWord()) {
            dispStr.append("<font color=\"#" + rgb + "\">");
            for (int x = 0; x < iSocketedItems.size(); x++) {
                dispStr.append((((D2Item) iSocketedItems.get(x)).getName().substring(0, ((D2Item) iSocketedItems.get(x)).getName().length() - 5)));
            }
            dispStr.append("</font><br>&#10;");
        }
        if (d2Item.isTypeWeapon() || d2Item.isTypeArmor()) {
            if (d2Item.isTypeWeapon()) {
                int iWhichHand = d2Item.getiWhichHand();
                short[] i1Dmg = d2Item.getI1Dmg();
                short[] i2Dmg = d2Item.getI2Dmg();
                if (iWhichHand == 0) {
                    if (d2Item.isiThrow()) {
                        dispStr.append("Throw Damage: " + i2Dmg[1] + " - " + i2Dmg[3] + "<br>&#10;");
                        dispStr.append("One Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                    } else {
                        dispStr.append("One Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                        dispStr.append("Two Hand Damage: " + i2Dmg[1] + " - " + i2Dmg[3] + "<br>&#10;");
                    }
                } else if (iWhichHand == 1) {
                    dispStr.append("One Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                } else {
                    dispStr.append("Two Hand Damage: " + i1Dmg[1] + " - " + i1Dmg[3] + "<br>&#10;");
                }
            } else if (d2Item.isTypeArmor()) {
                dispStr.append("Defense: " + d2Item.getiDef() + "<br>&#10;");
                if (d2Item.isShield()) {
                    dispStr.append("Chance to Block: " + d2Item.getiBlock() + "<br>&#10;");
                }
            }
            if (d2Item.isStackable()) {
                dispStr.append("Quantity: " + d2Item.getiCurDur() + "<br>&#10;");
            } else {
                if (d2Item.getiMaxDur() == 0) {
                    dispStr.append("Indestructible" + "<br>&#10;");
                } else {
                    dispStr.append("Durability: " + d2Item.getiCurDur() + " of " + d2Item.getiMaxDur() + "<br>&#10;");
                }
            }
        }
        if (d2Item.getReqLvl() > 0) dispStr.append("Required Level: " + d2Item.getReqLvl() + "<br>&#10;");
        if (d2Item.getReqStr() > 0) dispStr.append("Required Strength: " + d2Item.getReqStr() + "<br>&#10;");
        if (d2Item.getReqDex() > 0) dispStr.append("Required Dexterity: " + d2Item.getReqDex() + "<br>&#10;");
        if (d2Item.getFingerprint() != null) dispStr.append("Fingerprint: " + d2Item.getFingerprint() + "<br>&#10;");
        if (d2Item.getiGUID() != null) dispStr.append("GUID: " + d2Item.getiGUID() + "<br>&#10;");
        if (d2Item.getIlvl() != 0) dispStr.append("Item Level: " + d2Item.getIlvl() + "<br>&#10;");
        dispStr.append("Version: " + d2Item.get_version() + "<br>&#10;");
        if (!d2Item.isiIdentified()) dispStr.append("Unidentified" + "<br>&#10;");

        dispStr.append(getItemPropertyString(d2Item));

        if (extended) {
            if (d2Item.isSocketed()) {

                if (iSocketedItems != null) {
                    dispStr.append("<br>&#10;");
                    for (int x = 0; x < iSocketedItems.size(); x = x + 1) {
                        if (iSocketedItems.get(x) != null) {
                            dispStr.append(D2ItemRenderer.generatePropStringNoHtmlTags((D2Item) iSocketedItems.get(x), false));
                            if (x != iSocketedItems.size() - 1) {
                                dispStr.append("<br>&#10;");
                            }
                        }
                    }
                }
            }
        }

        return dispStr.append("</center>");
    }

    private static StringBuffer getItemPropertyString(D2Item d2Item) {
        StringBuffer dispStr = new StringBuffer("");
        D2PropCollection iProps = d2Item.getPropCollection();
        int iCharLvl = d2Item.getiCharLvl();
        if (d2Item.isJewel()) {
            dispStr.append(iProps.generateDisplay(1, iCharLvl));
        } else {
            dispStr.append(iProps.generateDisplay(0, iCharLvl));
        }
        if (d2Item.isGem() || d2Item.isRune()) {
            dispStr.append("Weapons: ");
            dispStr.append(iProps.generateDisplay(7, iCharLvl));
            dispStr.append("Armor: ");
            dispStr.append(iProps.generateDisplay(8, iCharLvl));
            dispStr.append("Shields: ");
            dispStr.append(iProps.generateDisplay(9, iCharLvl));
        }
        if (d2Item.getQuality() == 5) {
            for (int x = 12; x < 17; x++) {
                StringBuffer setBuf = iProps.generateDisplay(x, iCharLvl);
                if (setBuf.length() > 29) {
                    dispStr.append("<font color=\"red\">Set (" + (x - 10) + " items): ");
                    dispStr.append(setBuf);
                    dispStr.append("</font>");
                }
            }

            for (int x = 2; x < 7; x++) {
                StringBuffer setBuf = iProps.generateDisplay(x, iCharLvl);
                if (setBuf.length() > 29) {
                    dispStr.append("Set (" + x + " items): ");
                    dispStr.append(setBuf);
                }
            }
        }
        if (d2Item.isEthereal()) {
            dispStr.append("<font color=\"#4850b8\">Ethereal</font><br>&#10;");
        }
        if (d2Item.getSocketNrTotal() > 0) {
            dispStr.append(d2Item.getSocketNrTotal() + " Sockets (" + d2Item.getSocketNrFilled() + " used)<br>&#10;");
            if (d2Item.getiSocketedItems() != null) {
                for (int i = 0; i < d2Item.getiSocketedItems().size(); i++) {
                    dispStr.append("Socketed: " + d2Item.getiSocketedItems().get(i).getItemName() + "<br>&#10;");
                }
            }
        }
        if (d2Item.getQuality() == 5) {
            dispStr.append("<br>&#10;");
            for (int x = 32; x < 36; x++) {
                StringBuffer setBuf = iProps.generateDisplay(x, iCharLvl);
                if (setBuf.length() > 29) {
                    dispStr.append("<font color=\"red\">(" + (x - 30) + " items): ");
                    dispStr.append(setBuf);
                    dispStr.append("</font>");
                }
            }
            StringBuffer setBuf = iProps.generateDisplay(36, iCharLvl);
            if (setBuf.length() > 33) {
                dispStr.append(setBuf);
            }
        }
        return dispStr;
    }
}
