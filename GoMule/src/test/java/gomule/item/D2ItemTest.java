package gomule.item;

import gomule.util.D2BitReader;
import org.junit.Test;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;

import static org.junit.Assert.assertEquals;

public class D2ItemTest {

    @Test
    public void viridianSmallCharm() throws Exception {
        String expected = "Viridian Small Charm\n" +
                "Small Charm\n" +
                "Required Level: 10\n" +
                "Fingerprint: 0x61d091db\n" +
                "Item Level: 1\n" +
                "Version: Resurrected\n" +
                "Poison Resist +7%\n";
        runTest(expected, new byte[]{16, 0, -128, 0, 5, 36, 68, -40, 79, -40, -114, -124, 14, 11, 80, -80, 12, 0, -76, -56, -7, 15});
    }

    @Test
    public void maras() throws Exception {
        String expected = "Mara's Kaleidoscope\n" +
                "Amulet\n" +
                "Required Level: 67\n" +
                "Fingerprint: 0x68fe8447\n" +
                "Item Level: 87\n" +
                "Version: Resurrected\n" +
                "+2 to All Skills\n" +
                "All Stats +5\n" +
                "All Resistances +26\n";
        byte[] bytes = {16, 0, -128, 0, -115, 8, -32, 89, 24, -114, 8, -3, -47, -82, 55, 32, 2, -128, -110, 0, 37, 1, -91, 1, -91, 19, 76, 41, -104, 86, 48, -75, 96, -6, -93, -1};
        runTest(expected, bytes);
    }

    @Test
    public void aldursBoots() throws Exception {
        String expected = "Aldur's Advance\n" +
                "Battle Boots\n" +
                "Defense: 42\n" +
                "Durability: 11 of 18\n" +
                "Required Level: 45\n" +
                "Required Strength: 95\n" +
                "Fingerprint: 0x7bc09616\n" +
                "Item Level: 99\n" +
                "Version: Resurrected\n" +
                "Indestructible\n" +
                "+40% Faster Run/Walk\n" +
                "+50 to Life\n" +
                "+180 Maximum Stamina\n" +
                "Heal Stamina Plus 32%\n" +
                "Fire Resist +44%\n" +
                "10% Damage Taken Goes To Mana\n" +
                "Set (2 items): +15 to Dexterity\n" +
                "Set (3 items): +15 to Dexterity\n" +
                "Set (4 items): +15 to Dexterity\n" +
                "\n";
        byte[] bytes = {16, 64, -128, 0, 77, 38, -128, 27, 13, 22, -106, -64, 123, -29, -94, 8, -48, 64, 98, -63, 57, 32, 101, 1, 53, 7, -112, 19, 94, 96, 120, 114, 20, 76, -1, 11, 120, -3, 23, -16, -6, 47, -32, -11, 31};
        runTest(expected, bytes);
    }

    @Test
    public void rareGloves() throws Exception {
        String expected = "Loath Clutches\n" +
                "Light Gauntlets\n" +
                "Defense: 17\n" +
                "Durability: 4 of 18\n" +
                "Required Level: 35\n" +
                "Required Strength: 45\n" +
                "Fingerprint: 0xe7208e05\n" +
                "Item Level: 55\n" +
                "Version: Resurrected\n" +
                "+2 to Javelin and Spear Skills (Amazon Only)\n" +
                "+20% Increased Attack Speed\n" +
                "3% Life stolen per hit\n" +
                "+49% Enhanced Defense\n" +
                "Fire Resist +14%\n" +
                "23% Better Chance of Getting Magic Items\n";
        byte[] bytes = {16, 0, -128, 0, -115, 42, -64, -84, 27, 10, 28, 65, -50, 111, 70, 109, 87, 73, 84, 69, -36, -4, 72, -71, 52, 11, 11, 72, 16, -128, 16, -29, 4, 16, -113, 1, -108, -67, 46, 40, 94, 2, 0, -6, 15};
        runTest(expected, bytes);
    }

    @Test
    public void socketedHelm() throws Exception {
        String expected = "Gemmed Circlet\n" +
                "Circlet\n" +
                "Defense: 25\n" +
                "Durability: 15 of 35\n" +
                "Required Level: 41\n" +
                "Fingerprint: 0x589dd484\n" +
                "Item Level: 88\n" +
                "Version: Resurrected\n" +
                "+20 to Strength\n" +
                "2 Sockets (2 used)\n" +
                "Socketed: Fal Rune\n" +
                "Socketed: Fal Rune\n" +
                "\n" +
                "Fal Rune\n" +
                "Required Level: 41\n" +
                "Version: Resurrected\n" +
                "Weapons: +10 to Strength\n" +
                "Armor: +10 to Strength\n" +
                "Shields: +10 to Strength\n" +
                "\n" +
                "Fal Rune\n" +
                "Required Level: 41\n" +
                "Version: Resurrected\n" +
                "Weapons: +10 to Strength\n" +
                "Armor: +10 to Strength\n" +
                "Shields: +10 to Strength\n";
        byte[] bytes = {16, 8, -128, 0, 5, 72, 84, -4, -66, 19, 33, 117, 39, 22, 86, 48, -126, -111, 7, -14, 31, 16, 0, -96, 0, 53, 0, -32, 124, 92, 0, 16, 0, -96, 0, 53, 4, -32, 124, 92, 0};
        runTest(expected, bytes);
    }

    @Test
    public void healthPot() throws Exception {
        String expected = "Super Healing Potion\n" +
                "Version: Resurrected\n" +
                "Replenish Life +320\n";
        byte[] bytes = {16, 4, -96, 8, 21, 0, 0, 79, -76, 0};
        runTest(expected, bytes);
    }

    @Test
    public void cta() throws Exception {
        String expected = "Call to Arms\n" +
                "War Scepter\n" +
                "AmnRalMalIstOhm\n" +
                "One Hand Damage: 37 - 63\n" +
                "Durability: 68 of 70\n" +
                "Required Level: 57\n" +
                "Required Strength: 55\n" +
                "Fingerprint: 0x1baff84d\n" +
                "GUID: 0x0 0x0 0xb75ebe57 0xa491bdb\n" +
                "Item Level: 61\n" +
                "Version: Resurrected\n" +
                "+2 to All Skills\n" +
                "+40% Increased Attack Speed\n" +
                "273% Enhanced Damage\n" +
                "+150% Damage to Undead\n" +
                "Adds 5 - 30 Fire Damage\n" +
                "7% Life stolen per hit\n" +
                "Prevent Monster Heal\n" +
                "+9 to Battle Command\n" +
                "+13 to Battle Orders\n" +
                "+10 to Battle Cry\n" +
                "Replenish Life +12\n" +
                "30% Better Chance of Getting Magic Items\n" +
                "5 Sockets (5 used)\n" +
                "Socketed: Amn Rune\n" +
                "Socketed: Ral Rune\n" +
                "Socketed: Mal Rune\n" +
                "Socketed: Ist Rune\n" +
                "Socketed: Ohm Rune\n" +
                "\n" +
                "Amn Rune\n" +
                "Required Level: 25\n" +
                "GUID: 0x0 0x0 0x5203a1ac 0x67e15e4\n" +
                "Version: Resurrected\n" +
                "Weapons: 7% Life stolen per hit\n" +
                "Armor: Attacker Takes Damage of 14\n" +
                "Shields: Attacker Takes Damage of 14\n" +
                "\n" +
                "Ral Rune\n" +
                "Required Level: 19\n" +
                "GUID: 0x0 0x0 0x66f03f8f 0x70897b7\n" +
                "Version: Resurrected\n" +
                "Weapons: Adds 5 - 30 Fire Damage\n" +
                "Armor: Fire Resist +30%\n" +
                "Shields: Fire Resist +35%\n" +
                "\n" +
                "Mal Rune\n" +
                "Required Level: 49\n" +
                "GUID: 0x0 0x0 0x810819fd 0x5f39411\n" +
                "Version: Resurrected\n" +
                "Weapons: Prevent Monster Heal\n" +
                "Armor: Magic Damage Reduced by 7\n" +
                "Shields: Magic Damage Reduced by 7\n" +
                "\n" +
                "Ist Rune\n" +
                "Required Level: 51\n" +
                "GUID: 0x0 0x0 0xddb48852 0x569123e\n" +
                "Version: Resurrected\n" +
                "Weapons: 30% Better Chance of Getting Magic Items\n" +
                "Armor: 25% Better Chance of Getting Magic Items\n" +
                "Shields: 25% Better Chance of Getting Magic Items\n" +
                "\n" +
                "Ohm Rune\n" +
                "Required Level: 57\n" +
                "Version: Resurrected\n" +
                "Weapons: +50% Enhanced Damage\n" +
                "Armor: +5% to Maximum Cold Resist\n" +
                "Shields: +5% to Maximum Cold Resist\n";
        byte[] bytes = {16, 8, -128, 12, -53, 46, 0, -48, -84, 77, -8, -81, 27, 61, -31, 4, 42, 0, 0, 0, 0, 0, 0, 0, -64, -107, -81, -41, -19, -10, 70, -110, -126, 17, -111, -6, 31, -31, -37, 55, 37, 106, 23, 94, 24, 73, 74, -104, 74, 77, -104, 77, -55, 31, -3, 7, 16, 0, -96, 8, 51, 0, -32, 124, 62, 5, 0, 0, 0, 0, 0, 0, 0, 96, 13, 29, -112, 34, -81, -16, 51, 0, 16, 0, -96, 8, 51, 4, -32, 124, 35, 5, 0, 0, 0, 0, 0, 0, 0, 120, -4, -127, 55, -69, -67, 68, 56, 0, 16, 0, -96, 8, 51, 8, -32, 48, -37, 2, 0, 0, 0, 0, 0, 0, 0, -12, 103, 32, 4, 70, 80, -50, 23, 0, 16, 0, -96, 8, 51, 12, -32, 48, 95, 5, 0, 0, 0, 0, 0, 0, 0, -112, 66, -92, -19, -10, -111, 72, 43, 0, 16, 0, -96, 8, 51, 16, -32, 48, 62};
        runTest(expected, bytes);
    }

    @Test
    public void titans() throws Exception {
        String expected = "BigBoobsBigBow's Titan's Revenge\n" +
                "Matriarchal Javelin\n" +
                "Throw Damage: 169 - 324\n" +
                "One Hand Damage: 149 - 274\n" +
                "Quantity: 140\n" +
                "Required Level: 55\n" +
                "Required Strength: 97\n" +
                "Required Dexterity: 141\n" +
                "Fingerprint: 0xac444728\n" +
                "Item Level: 87\n" +
                "Version: Resurrected\n" +
                "+2 to Javelin and Spear Skills (Amazon Only)\n" +
                "+2 to Amazon Skill Levels\n" +
                "+30% Faster Run/Walk\n" +
                "177% Enhanced Damage\n" +
                "Adds 25 - 50 Damage\n" +
                "5% Life stolen per hit\n" +
                "+20 to Strength\n" +
                "+20 to Dexterity\n" +
                "Increased Stack Size\n" +
                "Replenishes quantity\n" +
                "Required Level +7\n" +
                "Ethereal\n";
        byte[] bytes = {16, 64, -64, 1, 13, 17, -32, 89, 57, -96, 28, 17, -79, 94, -113, -116, 16, -90, -49, -62, -9, 91, 60, 23, -90, -49, -62, -9, 29, -64, -96, 0, 35, 0, 52, 2, 104, 17, 98, -59, -86, -112, 89, -112, -15, 40, 76, -127, -72, 28, -64, -56, 62, 101, -96, 100, -68, 4, 0, -44, -49, -13, -57, -13, 31};
        runTest(expected, bytes);
    }

    @Test
    public void charm() throws Exception {
        String expected = "Harpoonist's Grand Charm of Maiming\n" +
                "Grand Charm\n" +
                "Required Level: 63\n" +
                "Fingerprint: 0x9256fa58\n" +
                "Item Level: 85\n" +
                "Version: Resurrected\n" +
                "+1 to Javelin and Spear Skills (Amazon Only)\n" +
                "+3 to Maximum Damage\n";
        byte[] bytes = {16, 0, -128, 0, 5, 92, 68, -40, 109, -64, -46, -73, -110, -84, 82, -128, -115, -87, 88, 24, 96, 24, -128, 26, -16, 18, 0, -56, 127};
        runTest(expected, bytes);
    }

    @Test
    public void ring() throws Exception {
        String expected = "Raven Frost\n" +
                "Ring\n" +
                "Required Level: 45\n" +
                "Fingerprint: 0x52eaf57b\n" +
                "Item Level: 90\n" +
                "Version: Resurrected\n" +
                "+191 to Attack Rating\n" +
                "Adds 15 - 45 Cold Damage Over 4 Secs (100 Frames)\n" +
                "+20 to Dexterity\n" +
                "+40 to Mana\n" +
                "Cold Absorb 20%\n" +
                "Cannot Be Frozen\n";
        byte[] bytes = {16, 0, -128, 0, -117, 25, -32, -4, -40, -80, 87, -81, 46, -91, -67, 51, 17, 4, -48, 18, 32, 77, -8, -59, -58, 67, 11, 50, 74, -108, 76, -1, 3};
        runTest(expected, bytes);
    }

    private void runTest(String expected, byte[] bytes) throws Exception {
        D2TxtFile.constructTxtFiles("./d2111");
        D2TblFile.readAllFiles("./d2111");
        D2Item d2Item = new D2Item("my-test-file", new D2BitReader(bytes), 10);
        assertEquals(expected, d2Item.itemDump(true).replaceAll("\r", ""));
    }
}