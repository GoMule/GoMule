package gomule.d2s;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
public class D2CharacterTest {

    @Test
    public void complexChar() throws Exception {
        D2TxtFile.constructTxtFiles("./d2111");
        D2TblFile.readAllFiles("./d2111");
        D2Character d2Character = new D2Character(new File(Resources.getResource("charFiles/complexChar.d2s").toURI()).getAbsolutePath());
        assertEquals(expectedComplexChar, d2Character.fullDumpStr().replaceAll("\r", ""));
    }

    private String expectedComplexChar = "Name:       ThePerfectJava\n" +
            "Class:      Amazon\n" +
            "Experience: 3232620645\n" +
            "Level:      98\n" +
            "\n" +
            "            Naked/Gear\n" +
            "Strength:   61/173\n" +
            "Dexterity:  162/220\n" +
            "Vitality:   342/367\n" +
            "Energy:     15/20\n" +
            "HP:         1272/1354\n" +
            "Mana:       158/218\n" +
            "Stamina:    496/676\n" +
            "Defense:    40/1914\n" +
            "AR:         780/1261\n" +
            "\n" +
            "Fire:       179/139/79\n" +
            "Cold:       181/141/81\n" +
            "Lightning:  187/147/87\n" +
            "Poison:     177/137/77\n" +
            "\n" +
            "MF:         48       Block:      75\n" +
            "GF:         0       FR/W:       70\n" +
            "FHR:        24       IAS:        20\n" +
            "FCR:        25\n" +
            "\n" +
            "Magic Arrow: 0/0\n" +
            "Fire Arrow: 0/0\n" +
            "Cold Arrow: 0/0\n" +
            "Multiple Shot: 0/0\n" +
            "Exploding Arrow: 0/0\n" +
            "Ice Arrow: 0/0\n" +
            "Guided Arrow: 0/0\n" +
            "Strafe: 0/0\n" +
            "Immolation Arrow: 0/0\n" +
            "Freezing Arrow: 0/0\n" +
            "\n" +
            "Inner Sight: 0/0\n" +
            "Critical Strike: 1/9\n" +
            "Dodge: 1/9\n" +
            "Slow Missiles: 0/0\n" +
            "Avoid: 1/9\n" +
            "Penetrate: 1/9\n" +
            "Decoy: 0/0\n" +
            "Evade: 1/9\n" +
            "Valkyrie: 0/0\n" +
            "Pierce: 1/9\n" +
            "\n" +
            "Jab: 1/23\n" +
            "Power Strike: 20/42\n" +
            "Poison Javelin: 1/23\n" +
            "Impale: 0/0\n" +
            "Lightning Bolt: 20/42\n" +
            "Charged Strike: 20/42\n" +
            "Plague Javelin: 1/23\n" +
            "Fend: 0/0\n" +
            "Lightning Strike: 20/45\n" +
            "Lightning Fury: 20/45\n" +
            "\n" +
            "Viridian Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 10\n" +
            "Fingerprint: 0x61d091db\n" +
            "Item Level: 1\n" +
            "Version: Resurrected\n" +
            "Poison Resist +7%\n" +
            "\n" +
            "Emerald Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xe7a2403f\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "Poison Resist +10%\n" +
            "\n" +
            "Crimson Small Charm of Life\n" +
            "Small Charm\n" +
            "Required Level: 14\n" +
            "Fingerprint: 0xc1636060\n" +
            "Item Level: 22\n" +
            "Version: Resurrected\n" +
            "+9 to Life\n" +
            "Fire Resist +5%\n" +
            "\n" +
            "Titan's Revenge\n" +
            "Ceremonial Javelin\n" +
            "Throw Damage: 74 - 197\n" +
            "One Hand Damage: 74 - 145\n" +
            "Quantity: 140\n" +
            "Required Level: 42\n" +
            "Required Strength: 25\n" +
            "Required Dexterity: 109\n" +
            "Fingerprint: 0xdfa76564\n" +
            "Item Level: 87\n" +
            "Version: Resurrected\n" +
            "+2 to Javelin and Spear Skills (Amazon Only)\n" +
            "+2 to Amazon Skill Levels\n" +
            "+30% Faster Run/Walk\n" +
            "173% Enhanced Damage\n" +
            "Adds 25 - 50 Damage\n" +
            "9% Life stolen per hit\n" +
            "+20 to Strength\n" +
            "+20 to Dexterity\n" +
            "Increased Stack Size\n" +
            "Replenishes quantity\n" +
            "\n" +
            "Harpoonist's Grand Charm of Maiming\n" +
            "Grand Charm\n" +
            "Required Level: 63\n" +
            "Fingerprint: 0x9256fa58\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+3 to Maximum Damage\n" +
            "\n" +
            "Russet Grand Charm of Sustenance\n" +
            "Grand Charm\n" +
            "Required Level: 23\n" +
            "Fingerprint: 0xe4e9333d\n" +
            "Item Level: 69\n" +
            "Version: Resurrected\n" +
            "+24 to Life\n" +
            "Fire Resist +16%\n" +
            "\n" +
            "Viridian Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 10\n" +
            "Fingerprint: 0xe1dd3a1e\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "Poison Resist +6%\n" +
            "\n" +
            "Emerald Small Charm of the Glacier\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xba6b6fae\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "Adds 3 - 6 Cold Damage Over 1 Secs (25 Frames)\n" +
            "Poison Resist +11%\n" +
            "\n" +
            "Emerald Small Charm of Storms\n" +
            "Small Charm\n" +
            "Required Level: 37\n" +
            "Fingerprint: 0x4bd6750d\n" +
            "Item Level: 66\n" +
            "Version: Resurrected\n" +
            "Adds 1 - 20 Lightning Damage\n" +
            "Poison Resist +11%\n" +
            "\n" +
            "Emerald Small Charm of Dexterity\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0x2099a50e\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "+1 to Dexterity\n" +
            "Poison Resist +11%\n" +
            "\n" +
            "Crimson Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 1\n" +
            "Fingerprint: 0xfb404e24\n" +
            "Item Level: 73\n" +
            "Version: Resurrected\n" +
            "Fire Resist +3%\n" +
            "\n" +
            "Viridian Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 10\n" +
            "Fingerprint: 0x2378cd91\n" +
            "Item Level: 5\n" +
            "Version: Resurrected\n" +
            "Poison Resist +7%\n" +
            "\n" +
            "Viridian Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 10\n" +
            "Fingerprint: 0x9bce0b62\n" +
            "Item Level: 7\n" +
            "Version: Resurrected\n" +
            "Poison Resist +7%\n" +
            "\n" +
            "Emerald Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xaa284445\n" +
            "Item Level: 80\n" +
            "Version: Resurrected\n" +
            "Poison Resist +11%\n" +
            "\n" +
            "Crimson Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 1\n" +
            "Fingerprint: 0xbb6b812a\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "Fire Resist +5%\n" +
            "\n" +
            "Sapphire Small Charm of Dexterity\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0x85e8d8d3\n" +
            "Item Level: 86\n" +
            "Version: Resurrected\n" +
            "+1 to Dexterity\n" +
            "Cold Resist +11%\n" +
            "\n" +
            "Sapphire Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xada3471e\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "Cold Resist +10%\n" +
            "\n" +
            "Sapphire Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0x2c3323d1\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "Cold Resist +10%\n" +
            "\n" +
            "Harpoonist's Grand Charm of Craftmanship\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0x83b4c80d\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+1 to Maximum Damage\n" +
            "\n" +
            "Harpoonist's Grand Charm of Balance\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0xc5261c1b\n" +
            "Item Level: 99\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+12% Faster Hit Recovery\n" +
            "\n" +
            "Amber Small Charm of Dexterity\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0x4115290\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+2 to Dexterity\n" +
            "Lightning Resist +11%\n" +
            "\n" +
            "Amber Small Charm of Balance\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xa627a9a4\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+5% Faster Hit Recovery\n" +
            "Lightning Resist +11%\n" +
            "\n" +
            "Harpoonist's Grand Charm of Dexterity\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0xf15c64eb\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+4 to Dexterity\n" +
            "\n" +
            "Harpoonist's Grand Charm of Strength\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0x6aefa918\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+5 to Strength\n" +
            "\n" +
            "Amber Small Charm of Strength\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xe16d4cd6\n" +
            "Item Level: 80\n" +
            "Version: Resurrected\n" +
            "+1 to Strength\n" +
            "Lightning Resist +10%\n" +
            "\n" +
            "Harpoonist's Grand Charm of Strength\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0x40eb581d\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+5 to Strength\n" +
            "\n" +
            "Harpoonist's Grand Charm of Strength\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0x25db1c2a\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+4 to Strength\n" +
            "\n" +
            "Amber Small Charm of Strength\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xb790b4c1\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "+2 to Strength\n" +
            "Lightning Resist +10%\n" +
            "\n" +
            "Amber Small Charm of Flame\n" +
            "Small Charm\n" +
            "Required Level: 32\n" +
            "Fingerprint: 0xed06d3ce\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "Adds 1 - 2 Fire Damage\n" +
            "Lightning Resist +10%\n" +
            "\n" +
            "Small Charm of Good Luck\n" +
            "Small Charm\n" +
            "Required Level: 33\n" +
            "Fingerprint: 0xf835c91b\n" +
            "Item Level: 67\n" +
            "Version: Resurrected\n" +
            "7% Better Chance of Getting Magic Items\n" +
            "\n" +
            "Mara's Kaleidoscope\n" +
            "Amulet\n" +
            "Required Level: 67\n" +
            "Fingerprint: 0x68fe8447\n" +
            "Item Level: 87\n" +
            "Version: Resurrected\n" +
            "+2 to All Skills\n" +
            "All Stats +5\n" +
            "All Resistances +26\n" +
            "\n" +
            "Raven Frost\n" +
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
            "Cannot Be Frozen\n" +
            "\n" +
            "The Stone of Jordan\n" +
            "Ring\n" +
            "Required Level: 29\n" +
            "Fingerprint: 0x10ac91b9\n" +
            "Item Level: 99\n" +
            "Version: Resurrected\n" +
            "+1 to All Skills\n" +
            "Adds 1 - 12 Lightning Damage\n" +
            "+20 to Mana\n" +
            "Increase Maximum Mana 25%\n" +
            "\n" +
            "Thundergod's Vigor\n" +
            "War Belt\n" +
            "Defense: 159\n" +
            "Durability: 6 of 24\n" +
            "Required Level: 47\n" +
            "Required Strength: 110\n" +
            "Fingerprint: 0x652b277b\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "5% Chance to cast level 7 Fist of the Heavens when struck\n" +
            "Adds 1 - 50 Lightning Damage\n" +
            "+3 to Lightning Fury (Amazon Only)\n" +
            "+3 to Lightning Strike (Amazon Only)\n" +
            "+200% Enhanced Defense\n" +
            "+20 to Strength\n" +
            "+20 to Vitality\n" +
            "+10% to Maximum Lightning Resist\n" +
            "+20 Lightning Absorb\n" +
            "\n" +
            "Aldur's Advance\n" +
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
            "\n" +
            "\n" +
            "Loath Clutches\n" +
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
            "23% Better Chance of Getting Magic Items\n" +
            "\n" +
            "Coral Small Charm\n" +
            "Small Charm\n" +
            "Required Level: 20\n" +
            "Fingerprint: 0xf0273fb9\n" +
            "Item Level: 86\n" +
            "Version: Resurrected\n" +
            "Lightning Resist +8%\n" +
            "\n" +
            "Crimson Small Charm of Greed\n" +
            "Small Charm\n" +
            "Required Level: 15\n" +
            "Fingerprint: 0x46fdfa1e\n" +
            "Item Level: 22\n" +
            "Version: Resurrected\n" +
            "Fire Resist +5%\n" +
            "9% Extra Gold from Monsters\n" +
            "\n" +
            "Harpoonist's Grand Charm of Dexterity\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0x5c556f34\n" +
            "Item Level: 67\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+6 to Dexterity\n" +
            "\n" +
            "Ocher Small Charm of Frost\n" +
            "Small Charm\n" +
            "Required Level: 10\n" +
            "Fingerprint: 0x41fd42dd\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "Adds 1 - 2 Cold Damage Over 1 Secs (25 Frames)\n" +
            "Lightning Resist +7%\n" +
            "\n" +
            "Horadric Cube\n" +
            "Fingerprint: 0x180f812\n" +
            "Item Level: 13\n" +
            "Version: Resurrected\n" +
            "\n" +
            "Harpoonist's Grand Charm of Balance\n" +
            "Grand Charm\n" +
            "Required Level: 42\n" +
            "Fingerprint: 0x2232064a\n" +
            "Item Level: 59\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+12% Faster Hit Recovery\n" +
            "\n" +
            "Harpoonist's Grand Charm of Sustenance\n" +
            "Grand Charm\n" +
            "Required Level: 53\n" +
            "Fingerprint: 0x452acdf\n" +
            "Item Level: 68\n" +
            "Version: Resurrected\n" +
            "+1 to Javelin and Spear Skills (Amazon Only)\n" +
            "+32 to Life\n" +
            "\n" +
            "Toxic Small Charm of Inertia\n" +
            "Small Charm\n" +
            "Required Level: 55\n" +
            "Fingerprint: 0xe52700c3\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+3% Faster Run/Walk\n" +
            "Adds 100 Poison Damage Over 5 Secs (125 Frames)\n" +
            "\n" +
            "Chains of Honor\n" +
            "Archon Plate\n" +
            "DolUmBerIst\n" +
            "Defense: 882\n" +
            "Durability: 15 of 60\n" +
            "Required Level: 63\n" +
            "Required Strength: 103\n" +
            "Fingerprint: 0xed9dd144\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "+2 to All Skills\n" +
            "+200% Damage to Demons\n" +
            "+100% Damage to Undead\n" +
            "8% Life stolen per hit\n" +
            "+70% Enhanced Defense\n" +
            "+20 to Strength\n" +
            "Replenish Life +7\n" +
            "All Resistances +65\n" +
            "Damage Reduced by 8%\n" +
            "25% Better Chance of Getting Magic Items\n" +
            "4 Sockets (4 used)\n" +
            "Socketed: Dol Rune\n" +
            "Socketed: Um Rune\n" +
            "Socketed: Ber Rune\n" +
            "Socketed: Ist Rune\n" +
            "\n" +
            "Dol Rune\n" +
            "Required Level: 31\n" +
            "Version: Resurrected\n" +
            "Weapons: Hit Causes Monster to Flee 25%\n" +
            "Armor: Replenish Life +7\n" +
            "Shields: Replenish Life +7\n" +
            "\n" +
            "Um Rune\n" +
            "Required Level: 47\n" +
            "Version: Resurrected\n" +
            "Weapons: 25% Chance of Open Wounds\n" +
            "Armor: Cold Resist +15%\n" +
            "Lightning Resist +15%\n" +
            "Fire Resist +15%\n" +
            "Poison Resist +15%\n" +
            "Shields: Cold Resist +22%\n" +
            "Lightning Resist +22%\n" +
            "Fire Resist +22%\n" +
            "Poison Resist +22%\n" +
            "\n" +
            "Ber Rune\n" +
            "Required Level: 63\n" +
            "Version: Resurrected\n" +
            "Weapons: 20% Chance of Crushing Blow\n" +
            "Armor: Damage Reduced by 8%\n" +
            "Shields: Damage Reduced by 8%\n" +
            "\n" +
            "Ist Rune\n" +
            "Required Level: 51\n" +
            "Version: Resurrected\n" +
            "Weapons: 30% Better Chance of Getting Magic Items\n" +
            "Armor: 25% Better Chance of Getting Magic Items\n" +
            "Shields: 25% Better Chance of Getting Magic Items\n" +
            "\n" +
            "Widowmaker\n" +
            "Ward Bow\n" +
            "Two Hand Damage: 53 - 142\n" +
            "Durability: 45 of 48\n" +
            "Required Level: 65\n" +
            "Required Strength: 72\n" +
            "Required Dexterity: 146\n" +
            "Fingerprint: 0x68f39e6f\n" +
            "Item Level: 85\n" +
            "Version: Resurrected\n" +
            "Fires Magic Arrows\n" +
            "169% Enhanced Damage\n" +
            "Ignore Target's Defense\n" +
            "33% Deadly Strike\n" +
            "+5 to Guided Arrow\n" +
            "\n" +
            "Rejuvenation Potion\n" +
            "Version: Resurrected\n" +
            "Replenishes Mana 35%\n" +
            "Replenishes Health 35%\n" +
            "\n" +
            "Super Healing Potion\n" +
            "Version: Resurrected\n" +
            "Replenish Life +320\n" +
            "\n" +
            "Super Healing Potion\n" +
            "Version: Resurrected\n" +
            "Replenish Life +320\n" +
            "\n" +
            "Super Healing Potion\n" +
            "Version: Resurrected\n" +
            "Replenish Life +320\n" +
            "\n" +
            "Super Mana Potion\n" +
            "Version: Resurrected\n" +
            "Replenishes Mana 250%\n" +
            "\n" +
            "Call to Arms\n" +
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
            "Shields: +5% to Maximum Cold Resist\n" +
            "\n" +
            "Griffon's Eye\n" +
            "Diadem\n" +
            "Defense: 247\n" +
            "Durability: 19 of 20\n" +
            "Required Level: 76\n" +
            "Fingerprint: 0x5c55218a\n" +
            "Item Level: 86\n" +
            "Version: Resurrected\n" +
            "100% Chance to cast level 41 Nova when you Level-Up\n" +
            "+1 to All Skills\n" +
            "+25% Faster Cast Rate\n" +
            "Adds 1 - 74 Lightning Damage\n" +
            "-25% to Enemy Lightning Resistance\n" +
            "+20% to Lightning Skill Damage\n" +
            "+191 Defense\n" +
            "1 Sockets (1 used)\n" +
            "Socketed: Rainbow Facet\n" +
            "\n" +
            "Rainbow Facet\n" +
            "Jewel\n" +
            "Required Level: 49\n" +
            "Fingerprint: 0x79ae2700\n" +
            "Item Level: 99\n" +
            "Version: Resurrected\n" +
            "100% Chance to cast level 41 Nova when you Level-Up\n" +
            "Adds 1 - 74 Lightning Damage\n" +
            "-5% to Enemy Lightning Resistance\n" +
            "+5% to Lightning Skill Damage\n" +
            "\n" +
            "Gemmed Circlet\n" +
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
            "Shields: +10 to Strength\n" +
            "\n" +
            "BigBoobsBigBow's Titan's Revenge\n" +
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
            "Ethereal\n" +
            "\n" +
            "Stormshield\n" +
            "Monarch\n" +
            "Defense: 512\n" +
            "Chance to Block: 47\n" +
            "Indestructible\n" +
            "Required Level: 73\n" +
            "Required Strength: 156\n" +
            "Fingerprint: 0x3b767fa8\n" +
            "Item Level: 99\n" +
            "Version: Resurrected\n" +
            "100% Chance to cast level 47 Chain Lightning when you Die\n" +
            "Indestructible\n" +
            "+35% Faster Block Rate\n" +
            "25% Increased Chance of Blocking\n" +
            "Adds 1 - 74 Lightning Damage\n" +
            "-5% to Enemy Lightning Resistance\n" +
            "+5% to Lightning Skill Damage\n" +
            "+367 Defense (Based on Character Level)\n" +
            "+30 to Strength\n" +
            "Cold Resist +60%\n" +
            "Lightning Resist +25%\n" +
            "Damage Reduced by 35%\n" +
            "Attacker Takes Lightning Damage of 10\n" +
            "1 Sockets (1 used)\n" +
            "Socketed: Rainbow Facet\n" +
            "\n" +
            "Rainbow Facet\n" +
            "Jewel\n" +
            "Required Level: 49\n" +
            "Fingerprint: 0xf74a9abf\n" +
            "Item Level: 87\n" +
            "Version: Resurrected\n" +
            "100% Chance to cast level 47 Chain Lightning when you Die\n" +
            "Adds 1 - 74 Lightning Damage\n" +
            "-5% to Enemy Lightning Resistance\n" +
            "+5% to Lightning Skill Damage\n" +
            "\n" +
            "Mercenary:\n" +
            "\n" +
            "Name:       Razan\n" +
            "Race:       Desert Mercenary\n" +
            "Type:       Def-Nightmare\n" +
            "Experience: 107329840\n" +
            "Level:      96\n" +
            "Dead?:      false\n" +
            "\n" +
            "            Naked/Gear\n" +
            "Strength:   205/205\n" +
            "Dexterity:  166/166\n" +
            "HP:         2207/2207\n" +
            "Defense:    1614/2175\n" +
            "AR:         2042/2045\n" +
            "\n" +
            "Fire:       230/190/130\n" +
            "Cold:       260/220/160\n" +
            "Lightning:  230/190/130\n" +
            "Poison:     230/190/130\n" +
            "\n" +
            "Treachery\n" +
            "Wire Fleece\n" +
            "ShaelThulLem\n" +
            "Defense: 455\n" +
            "Durability: 29 of 32\n" +
            "Required Level: 53\n" +
            "Required Strength: 111\n" +
            "Fingerprint: 0xd979d7a7\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "5% Chance to cast level 15 Fade when struck\n" +
            "25% Chance to cast level 15 Venom on striking\n" +
            "+2 to Assassin Skill Levels\n" +
            "+45% Increased Attack Speed\n" +
            "+20% Faster Hit Recovery\n" +
            "Cold Resist +30%\n" +
            "50% Extra Gold from Monsters\n" +
            "3 Sockets (3 used)\n" +
            "Socketed: Shael Rune\n" +
            "Socketed: Thul Rune\n" +
            "Socketed: Lem Rune\n" +
            "\n" +
            "Shael Rune\n" +
            "Required Level: 29\n" +
            "Version: Resurrected\n" +
            "Weapons: +20% Increased Attack Speed\n" +
            "Armor: +20% Faster Hit Recovery\n" +
            "Shields: +20% Faster Block Rate\n" +
            "\n" +
            "Thul Rune\n" +
            "Required Level: 23\n" +
            "Version: Resurrected\n" +
            "Weapons: Adds 3 - 14 Cold Damage Over 3 Secs (75 Frames)\n" +
            "Armor: Cold Resist +30%\n" +
            "Shields: Cold Resist +35%\n" +
            "\n" +
            "Lem Rune\n" +
            "Required Level: 43\n" +
            "Version: Resurrected\n" +
            "Weapons: 75% Extra Gold from Monsters\n" +
            "Armor: 50% Extra Gold from Monsters\n" +
            "Shields: 50% Extra Gold from Monsters\n" +
            "\n" +
            "\n" +
            "Kira's Guardian\n" +
            "Tiara\n" +
            "Defense: 106\n" +
            "Durability: 17 of 25\n" +
            "Required Level: 77\n" +
            "Fingerprint: 0x843d90eb\n" +
            "Item Level: 99\n" +
            "Version: Resurrected\n" +
            "+20% Faster Hit Recovery\n" +
            "+64 Defense\n" +
            "All Resistances +70\n" +
            "Cannot Be Frozen\n" +
            "\n" +
            "\n" +
            "Infinity\n" +
            "Superior Great Poleaxe\n" +
            "BerMalBerIst\n" +
            "Two Hand Damage: 296 - 817\n" +
            "Durability: 28 of 28\n" +
            "Required Level: 63\n" +
            "Required Strength: 169\n" +
            "Required Dexterity: 89\n" +
            "Fingerprint: 0xc1a9ec4\n" +
            "Item Level: 88\n" +
            "Version: Resurrected\n" +
            "50% Chance to cast level 20 Chain Lightning when you Kill an Enemy\n" +
            "Level 12 Conviction Aura When Equipped\n" +
            "+35% Faster Run/Walk\n" +
            "330% Enhanced Damage\n" +
            "+3 to Attack Rating\n" +
            "-46% to Enemy Lightning Resistance\n" +
            "40% Chance of Crushing Blow\n" +
            "Prevent Monster Heal\n" +
            "+49 to Vitality (Based on Character Level)\n" +
            "30% Better Chance of Getting Magic Items\n" +
            "Level 21 Cyclone Armor (30/30 Charges)\n" +
            "Ethereal\n" +
            "4 Sockets (4 used)\n" +
            "Socketed: Ber Rune\n" +
            "Socketed: Mal Rune\n" +
            "Socketed: Ber Rune\n" +
            "Socketed: Ist Rune\n" +
            "\n" +
            "Ber Rune\n" +
            "Required Level: 63\n" +
            "Version: Resurrected\n" +
            "Weapons: 20% Chance of Crushing Blow\n" +
            "Armor: Damage Reduced by 8%\n" +
            "Shields: Damage Reduced by 8%\n" +
            "\n" +
            "Mal Rune\n" +
            "Required Level: 49\n" +
            "Version: Resurrected\n" +
            "Weapons: Prevent Monster Heal\n" +
            "Armor: Magic Damage Reduced by 7\n" +
            "Shields: Magic Damage Reduced by 7\n" +
            "\n" +
            "Ber Rune\n" +
            "Required Level: 63\n" +
            "Version: Resurrected\n" +
            "Weapons: 20% Chance of Crushing Blow\n" +
            "Armor: Damage Reduced by 8%\n" +
            "Shields: Damage Reduced by 8%\n" +
            "\n" +
            "Ist Rune\n" +
            "Required Level: 51\n" +
            "Version: Resurrected\n" +
            "Weapons: 30% Better Chance of Getting Magic Items\n" +
            "Armor: 25% Better Chance of Getting Magic Items\n" +
            "Shields: 25% Better Chance of Getting Magic Items\n" +
            "\n" +
            "\n";

}