package gomule.item;

import com.google.common.io.BaseEncoding;
import gomule.util.D2BitReader;
import org.junit.jupiter.api.Test;
import randall.d2files.D2TxtFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class D2ItemTest {

    public static final byte[] HEALTH_POT = decode("10 04 A0 08 15 00 00 4F B4 00");
    public static final byte[] SMALL_CHARM = decode("10 00 80 00 05 24 44 D8 4F D8 8E 84 0E 0B 50 B0 0C 00 68 F1 EC 3F");

    @Test
    public void viridianSmallCharm() throws Exception {
        String expected = "Viridian Small Charm\n" +
                "Small Charm\n" +
                "Required Level: 10\n" +
                "Fingerprint: 0x61d091db\n" +
                "Item Level: 1\n" +
                "Version: Resurrected\n" +
                "Poison Resist +7%\n";
        runItemDumpComparison(expected, loadD2Item(SMALL_CHARM), SMALL_CHARM.length);
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
        byte[] bytes = decode("10 00 80 00 8D 08 E0 59 18 8E 08 FD D1 AE 37 20 02 00 25 01 4A 02 4A 03 4A 27 C4 A5 10 B7 42 5C 0B 71 7F F4 1F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
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
        byte[] bytes = decode("10 40 80 00 4D 26 80 1B 0D 16 96 C0 7B E3 A2 08 D0 40 62 81 73 40 CA 02 6A 0E 20 27 E8 81 E1 C9 51 30 FD 2F E0 F5 5F C0 EB BF 80 D7 7F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
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
        byte[] bytes = decode("10 00 80 00 8D 2A C0 AC 1B 0A 1C 41 CE 6F 46 6D 57 49 54 45 DC FC 48 B9 34 0B 0B 48 10 00 21 C6 09 6B 3C 06 50 F6 BA A0 78 09 00 E8 3F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
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
        byte[] bytes = {16, 8, -128, 0, 5, 72, 84, -4, -66, 19, 33, 117, 39, 22, 86, 48, -126, -111, 7, -28, 63, 16, 0, -96, 0, 53, 0, -32, 124, 92, 0, 16, 0, -96, 0, 53, 4, -32, 124, 92, 0};
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void ear() throws Exception {
        String expected = "Tas's Ear\n" +
                "Version: Resurrected\n" +
                "Level 72 Necromancer\n";
        byte[] bytes = decode("10 00 A1 00 05 54 44 48 6A 78 0E 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void khalimsEye() throws Exception {
        String expected = "Khalim's Eye\n" +
                "Version: Resurrected\n" +
                "Difficulty: Hell\n";
        byte[] bytes = decode("10 00 A0 00 05 88 64 73 40 25");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void ear2() throws Exception {
        String expected = "Jeremy's Ear\n" +
                "Version: Resurrected\n" +
                "Level 59 Barbarian\n";
        byte[] bytes = decode("10 00 A1 00 05 88 84 3B 65 59 5E 6E E7 01 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void rejuvPot() throws Exception {
        String expected = "Rejuvenation Potion\n" +
                "Version: Resurrected\n" +
                "Replenishes Mana 35%\n" +
                "Replenishes Health 35%\n";
        byte[] bytes = decode("10 00 A0 00 15 08 E0 EC 28 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void rejuvPot_afterRoW() throws Exception {
        String expected = "Rejuvenation Potion\n" +
                "Version: Resurrected\n" +
                "Replenishes Mana 35%\n" +
                "Replenishes Health 35%\n";
        byte[] bytes = decode("10 00 A0 00 15 08 E0 EC 28 01 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void rejuvPot_single_inMaterialStash() throws Exception {
        String expected = "Rejuvenation Potion\n" +
                "Version: Resurrected\n" +
                "Replenishes Mana 35%\n" +
                "Replenishes Health 35%\n";
        byte[] bytes = decode("10 00 A0 00 25 00 F4 EC 28 03 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void healthPot() throws Exception {
        String expected = "Super Healing Potion\n" +
                "Version: Resurrected\n" +
                "Replenish Life +320\n";
        runItemDumpComparison(expected, loadD2Item(HEALTH_POT), HEALTH_POT.length);
    }

    @Test
    public void moveItem() throws Exception {
        String expected = "Super Healing Potion\n" +
                "Version: Resurrected\n" +
                "Replenish Life +320\n";
        byte[] bytes = {16, 4, -96, 8, 21, 0, 0, 79, -76, 0};
        D2Item d2Item = loadD2Item(bytes);
        d2Item.set_location((short) 0);
        d2Item.set_col((short) 4);
        d2Item.set_row((short) 4);
        d2Item.set_panel((short) 1);
        D2Item afterMove = loadD2Item(d2Item.get_bytes());
        runItemDumpComparison(expected, afterMove);
        assertEquals((short) 0, afterMove.get_location());
        assertEquals((short) 4, afterMove.get_col());
        assertEquals((short) 4, afterMove.get_row());
        assertEquals((short) 1, afterMove.get_panel());
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
        runItemDumpComparison(expected, loadD2Item(decode("10 08 80 0C CB 2E 00 D0 AC 4D F8 AF 1B 3D E1 04 2A 00 00 00 00 00 00 00 C0 95 AF D7 ED F6 46 92 82 11 11 F5 3F C2 B7 6F 4A D4 2E BC 30 92 94 30 95 9A 30 9B 92 3F FA 0F 10 00 A0 08 33 00 E0 7C 3E 05 00 00 00 00 00 00 00 60 0D 1D 90 22 AF F0 33 00 10 00 A0 08 33 04 E0 7C 23 05 00 00 00 00 00 00 00 78 FC 81 37 BB BD 44 38 00 10 00 A0 08 33 08 E0 30 DB 02 00 00 00 00 00 00 00 F4 67 20 04 46 50 CE 17 00 10 00 A0 08 33 0C E0 30 5F 05 00 00 00 00 00 00 00 90 42 A4 ED F6 91 48 2B 00 10 00 A0 08 33 10 E0 30 3E 00")));
    }

    @Test
    public void titans() throws Exception {
        String expected = "BigBoobsBigBow's Titan's Revenge\n" +
                "Matriarchal Javelin\n" +
                "Throw Damage: 169 - 324\n" +
                "One Hand Damage: 149 - 274\n" +
                "Quantity: 156\n" +
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
        byte[] bytes = decode("10 40 C0 01 0D 11 E0 59 39 A0 1C 11 B1 5E 8F 8C 10 4A 3B 13 7A 7B 13 9B 13 4A 3B 13 7A BB 03 60 50 20 27 00 34 02 68 11 62 C5 AA 90 59 90 F1 28 4C 81 B8 1C C0 C8 3E 65 A0 64 BC 04 00 D4 CF F3 C7 F3 1F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
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
        runItemDumpComparison(expected, loadD2Item(decode("10 00 80 00 05 5C 44 D8 6D C0 D2 B7 92 AC 52 80 8D A9 B0 30 C0 30 00 35 E0 25 00 90 FF 00")));
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
                "+20 Cold Absorb\n" +
                "Cannot Be Frozen\n";
        runItemDumpComparison(expected, loadD2Item(decode("10 00 80 00 8B 19 E0 FC D8 B0 57 AF 2E A5 BD 33 11 08 A0 25 40 9A F0 8B 8D 87 16 64 94 28 99 FE 07")));
    }

    @Test
    public void standardOfHeroes() throws Exception {
        String expected = "Standard of Heroes\n" +
                "Required Level: 90\n" +
                "Fingerprint: 0xe2dad357\n" +
                "Item Level: 99\n" +
                "Version: Resurrected\n";
        byte[] bytes = decode("10 00 80 00 05 00 84 CC 18 AE A6 B5 C5 C7 C7 FF F3 1F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void coh() throws Exception {
        String expected = "Chains of Honor\n" + "Archon Plate\n"
                + "DolUmBerIst\n"
                + "Defense: 882\n"
                + "Durability: 15 of 60\n"
                + "Required Level: 63\n"
                + "Required Strength: 103\n"
                + "Fingerprint: 0xed9dd144\n"
                + "Item Level: 85\n"
                + "Version: Resurrected\n"
                + "+2 to All Skills\n"
                + "+200% Damage to Demons\n"
                + "+100% Damage to Undead\n"
                + "8% Life stolen per hit\n"
                + "+70% Enhanced Defense\n"
                + "+20 to Strength\n"
                + "Replenish Life +7\n"
                + "All Resistances +65\n"
                + "Damage Reduced by 8%\n"
                + "25% Better Chance of Getting Magic Items\n"
                + "4 Sockets (4 used)\n"
                + "Socketed: Dol Rune\n"
                + "Socketed: Um Rune\n"
                + "Socketed: Ber Rune\n"
                + "Socketed: Ist Rune\n"
                + "\n"
                + "Dol Rune\n"
                + "Required Level: 31\n"
                + "Version: Resurrected\n"
                + "Weapons: Hit Causes Monster to Flee +25%\n"
                + "Armor: Replenish Life +7\n"
                + "Shields: Replenish Life +7\n"
                + "\n"
                + "Um Rune\n"
                + "Required Level: 47\n"
                + "Version: Resurrected\n"
                + "Weapons: 25% Chance of Open Wounds\n"
                + "Armor: Cold Resist +15%\n"
                + "Lightning Resist +15%\n"
                + "Fire Resist +15%\n"
                + "Poison Resist +15%\n"
                + "Shields: Cold Resist +22%\n"
                + "Lightning Resist +22%\n"
                + "Fire Resist +22%\n"
                + "Poison Resist +22%\n"
                + "\n"
                + "Ber Rune\n"
                + "Required Level: 63\n"
                + "Version: Resurrected\n"
                + "Weapons: 20% Chance of Crushing Blow\n"
                + "Armor: Damage Reduced by 8%\n"
                + "Shields: Damage Reduced by 8%\n"
                + "\n"
                + "Ist Rune\n"
                + "Required Level: 51\n"
                + "Version: Resurrected\n"
                + "Weapons: 30% Better Chance of Getting Magic Items\n"
                + "Armor: 25% Better Chance of Getting Magic Items\n"
                + "Shields: 25% Better Chance of Getting Magic Items\n";
        runItemDumpComparison(expected, loadD2Item(decode("10 48 80 0C CD 0C 00 9A 19 89 A2 3B DB AB 02 0A 94 08 F1 3C 40 FF 01 D0 40 30 72 42 5F 0A 7D 2B F4 B5 D0 C7 03 91 87 9B 1E 3C 7F F4 1F 10 00 A0 08 35 00 E0 7C BE 02 10 00 A0 08 33 04 E0 30 4C 00 10 00 A0 08 35 08 E0 6C BF 03 10 00 A0 08 35 0C E0 30 5F 01")));
    }

    @Test
    public void runeword_2_6() throws Exception {
        String expected = "Bulwark\n" +
                "Mask\n" +
                "ShaelIoSol\n" +
                "Defense: 38\n" +
                "Durability: 17 of 20\n" +
                "Required Level: 35\n" +
                "Required Strength: 23\n" +
                "Fingerprint: 0x4635278f\n" +
                "Item Level: 85\n" +
                "Version: Resurrected\n" +
                "+20% Faster Hit Recovery\n" +
                "4% Life stolen per hit\n" +
                "+76% Enhanced Defense\n" +
                "+10 to Vitality\n" +
                "Increase Maximum Life 5%\n" +
                "Replenish Life +30\n" +
                "Damage Reduced by 11%\n" +
                "Damage Reduced by 7\n" +
                "3 Sockets (3 used)\n" +
                "Socketed: Shael Rune\n" +
                "Socketed: Io Rune\n" +
                "Socketed: Sol Rune\n" +
                "\n" +
                "Shael Rune\n" +
                "Required Level: 29\n" +
                "Version: Resurrected\n" +
                "Weapons: +20% Increased Attack Speed\n" +
                "Armor: +20% Faster Hit Recovery\n" +
                "Shields: +20% Faster Block Rate\n" +
                "\n" +
                "Io Rune\n" +
                "Required Level: 35\n" +
                "Version: Resurrected\n" +
                "Weapons: +10 to Vitality\n" +
                "Armor: +10 to Vitality\n" +
                "Shields: +10 to Vitality\n" +
                "\n" +
                "Sol Rune\n" +
                "Required Level: 27\n" +
                "Version: Resurrected\n" +
                "Weapons: Armor: Damage Reduced by 7\n" +
                "Shields: Damage Reduced by 7\n";
        byte[] bytes = decode("10 08 80 04 05 84 D5 92 D4 1E 4F 6A 8C AA 82 51 1B 10 50 44 30 FF 21 30 21 31 8D 07 41 09 CF E4 F9 0F 10 00 A0 00 35 00 E0 7C B6 01 10 00 A0 00 35 04 E0 7C F6 01 10 00 A0 00 35 08 E0 7C 98 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void new_charm() throws Exception {
        String expected = "Flame Rift\n" + "Grand Charm\n"
                + "Required Level: 75\n"
                + "Fingerprint: 0x69f99b80\n"
                + "Item Level: 99\n"
                + "Version: Resurrected\n"
                + "Monster Fire Immunity is Sundered\n"
                + "Fire Resist -86%\n";
        byte[] bytes = decode("10 00 80 00 05 00 54 D8 6D 00 DC CC 4F 1B 5F 91 0C 4E C8 E9 C5 D2 7F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void key() throws Exception {
        String expected = "Key\n" +
                "Fingerprint: 0x60c6dbc3\n" +
                "Item Level: 68\n" +
                "Version: Resurrected\n";
        byte[] bytes = decode("10 00 80 00 05 C0 44 1A 50 61 78 DB 18 8C 28 C8 E0 3F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void ring_with_trailing_zero() throws Exception {
        String expected = "Ocher Ring of Fortune\n" +
                "Ring\n" +
                "Required Level: 31\n" +
                "Fingerprint: 0x7b5167fc\n" +
                "Item Level: 85\n" +
                "Version: Resurrected\n" +
                "Lightning Resist +17%\n" +
                "19% Better Chance of Getting Magic Items\n";
        byte[] bytes = decode("10 00 80 00 05 18 F4 FC D8 C0 7F 16 B5 57 A5 60 18 90 90 22 1B 94 BB FF 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void jade_figurine() throws Exception {
        String expected = "A Jade Figurine\n" +
                "Version: Resurrected\n" +
                "Difficulty: Normal\n";
        byte[] bytes = decode("10 00 A0 00 04 58 04 DD F6 2B 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void bugged_golden_bird() throws Exception {
        String expected = "The Golden Bird\n" +
                "Version: Resurrected\n" +
                "Difficulty: Hell\n";
        byte[] bytes = decode("10 00 A0 00 05 60 64 6D BF 12");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void golden_bird() throws Exception {
        String expected = "The Golden Bird\n" +
                "Version: Resurrected\n" +
                "Difficulty: Normal\n";
        byte[] bytes = decode("10 20 A0 00 05 58 64 6D BF 02");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void cairn_stones_partial_read() throws Exception {
        byte[] bytes = decode("10 00 A0 00 05 C8 54 A5 31 00 00");
        D2Item d2Item = loadD2Item(bytes);
        String expected = "Key to the Cairn Stones\n" +
                "Version: Resurrected\n" +
                "Difficulty: Normal\n";
        runItemDumpComparison(expected, d2Item, bytes.length);
    }

    @Test
    public void blueRowTome() throws Exception {
        String expected = "Dark Tome of Regeneration\n" +
                "Dark Tome\n" +
                "Defense: 48\n" +
                "Durability: 11 of 20\n" +
                "Required Level: 30\n" +
                "Required Strength: 41\n" +
                "Fingerprint: 0xc412e035\n" +
                "Item Level: 70\n" +
                "Version: Resurrected\n" +
                "Unidentified\n" +
                "Adds 6 - 10 Magic Damage\n" +
                "+2 to Psychic Ward (Warlock Only)\n" +
                "Replenish Life +3\n";
        byte[] bytes = decode("00 00 80 00 05 A0 04 3C 1F 6A C0 25 88 8D A4 09 00 E0 10 3A A0 58 80 86 81 02 25 E1 9A C1 FA 0F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void buggedToken() throws Exception {
        String expected = "Token of Absolution\n" +
                "Version: Resurrected\n";
        byte[] bytes = decode("10 00 A0 00 04 D4 D4 FC 5F 02 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void realToken() throws Exception {
        String expected = "Token of Absolution\n" +
                "Fingerprint: 0xefae8ff0\n" +
                "Item Level: 1\n" +
                "Version: Resurrected\n";
        byte[] bytes = decode("10 00 80 00 05 40 D4 FC 5F 80 7F 74 7D 0F 08 FC 07");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void coif() throws Exception {
        String expected = "Coif of Glory\n" +
                "Helm\n" +
                "Defense: 26\n" +
                "Durability: 23 of 24\n" +
                "Required Level: 14\n" +
                "Required Strength: 26\n" +
                "Fingerprint: 0x4e64f6e5\n" +
                "Item Level: 95\n" +
                "Version: Resurrected\n" +
                "Unidentified\n" +
                "Hit Blinds Target +1\n" +
                "+10 Defense\n" +
                "+100 Defense vs. Missile\n" +
                "Lightning Resist +15%\n" +
                "Attacker Takes Lightning Damage of 7\n";
        byte[] bytes = decode("00 00 80 00 05 98 04 5F 1B CA ED C9 9C BE 47 12 D0 00 C6 05 1F 28 00 82 4C 8A 6B 71 02 80 CE 7F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void unidGloves() throws Exception {
        String expected = "Dracul's Grasp\n" +
                "Vampirebone Gloves\n" +
                "Defense: 144\n" +
                "Durability: 9 of 14\n" +
                "Required Level: 76\n" +
                "Required Strength: 50\n" +
                "Fingerprint: 0xea3909f3\n" +
                "Item Level: 99\n" +
                "Version: Resurrected\n" +
                "Unidentified\n" +
                "5% Chance to cast level 10 Life Tap on striking\n" +
                "9% Life stolen per hit\n" +
                "25% Chance of Open Wounds\n" +
                "+119% Enhanced Defense\n" +
                "+13 to Strength\n" +
                "+6 Life after each Kill\n";
        byte[] bytes = decode("00 00 80 10 05 80 04 EE 56 98 4F C8 51 1F 1F 6C 81 09 0E 09 00 68 81 70 87 47 C2 8A E1 50 C6 98 22 45 E1 BF FD 82 87 A2 70 00 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void potionOfLife() throws Exception {
        String expected = "Potion of Life\n" +
                "Fingerprint: 0x11e40d89\n" +
                "Item Level: 25\n" +
                "Version: Resurrected\n";
        byte[] bytes = decode("10 00 80 00 05 08 84 A3 36 42 62 03 79 44 46 E0 3F");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    @Test
    public void unidGloves2() throws Exception {
        String expected = "Horazon's Hold\n" +
                "Demonhide Gloves\n" +
                "Defense: 33\n" +
                "Durability: 7 of 12\n" +
                "Required Level: 48\n" +
                "Required Strength: 20\n" +
                "Fingerprint: 0x15ae438f\n" +
                "Item Level: 96\n" +
                "Version: Resurrected\n" +
                "Unidentified\n" +
                "+143 to Attack Rating\n" +
                "Adds 140 - 270 Fire Damage\n" +
                "10% Chance of Crushing Blow\n" +
                "+10 to Dexterity\n" +
                "+33 to Life\n" +
                "Set (2 items): +30% Increased Attack Speed\n" +
                "Set (3 items): 7% Mana stolen per hit\n" +
                "Set (4 items): 7% Life stolen per hit\n\n";
        byte[] bytes = decode("00 00 80 10 05 00 84 DF 15 1E 87 5C 2B C0 45 22 58 01 C3 01 47 80 EA 40 90 09 8F C0 60 74 88 48 F1 BF 8B EC BF 8F C3 7F 1E 87 FF E1 0B F1 8F C2 01 00");
        runItemDumpComparison(expected, loadD2Item(bytes), bytes.length);
    }

    public static byte[] decode(String s) {
        return BaseEncoding.base16().decode(s.replaceAll(" ", ""));
    }

    public static String encodeWithSpaces(byte[] bytes) {
        return BaseEncoding.base16().withSeparator(" ", 2).encode(bytes);
    }

    private void runItemDumpComparison(String expected, D2Item d2Item) {
        assertEquals(expected, D2ItemRenderer.itemDump(d2Item, true).replaceAll("\r", ""));
    }

    private void runItemDumpComparison(String expected, D2Item d2Item, int endOfItemsInBytesOnRead) {
        runItemDumpComparison(expected, d2Item);
        assertEquals(endOfItemsInBytesOnRead, d2Item.getEndOfItemInBytes());
    }

    private D2Item loadD2Item(byte[] bytes) throws Exception {
        D2TxtFile.constructTxtFiles("./d2111");
        return new D2Item("my-test-file", new D2BitReader(bytes), 10);
    }
}