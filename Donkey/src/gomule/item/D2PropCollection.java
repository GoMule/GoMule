package gomule.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class D2PropCollection {



	public ArrayList pArr = new ArrayList();
	private HashMap modMap;


	//Stage 1: Remove useless(MIGHT NOT BE!!!) mods STASH+CHAR
	//Stage 2: Combine props, like Cold min, cold max, cold res STASH+CHAR
	//Stage 3: Group mods such as resistance etc STASH+CHAR
	//Stage 4: Modify base values for DEF, DMG etc. STASH+CHAR
	//Stage 5: Populate modMap to display what properties are being modified CHAR ONLY?  


//BASE VALS
	
//	REQUREMENTS
//	applyReqLPlus (92)

//	UNDEAD DAMAGE ON BLUNT
//	if (iType.equals("club") || iType.equals("scep")|| iType.equals("mace") || iType.equals("hamm")) ADD 92, 150

//	+SKILLS CAN CHANGE LVL REQS
//	for (int x = 0; x < iProperties.size(); x = x + 1) {
//	if (((D2ItemProperty) iProperties.get(x)).getiProp() == 107
//	|| ((D2ItemProperty) iProperties.get(x)).getiProp() == 97) {
//	lvlSkills.add(iProperties.get(x));
//	}
//	}
//	if (lvlSkills.size() > 0) {
//	modifyLvl(lvlSkills);
//	}

//	EARS NEED CLASS AND LVL ON D2ITEM

//	APPLY DEF!
//	16 = EN DEF %
//	31 = DEF
//	214 = DEF PER LEVEL

//	APPLY INC MAX DUR!
//	75 = DUR %
//	73 = PLUS DUR

//	APPLY BLOCK!
//	20 = BLOCK
	
//	APPLY DAMAGE!
//	17 = EN DAMAGE %
//	219 = MaxDMG % per LEVEL
//	218 = MaxDMG PER LEVEL
//	21 = Min DAMAGE
//	22 = MAX DAMAGE


//--------------------------------
//COMBINE PROPS - EG if you have a rune embedded in an item, need to combine VALS
//Props such as +skills can appear twice, prop nums: 107,97,188,201,198,204 should be ignored
//COMBINE MAXRES, RES, STATS
	
	
	
	
	//POTS??
	




	private void cleanProps(){

		for(int x = 0;x<pArr.size();x++){

			//159 = Min throw damage
			//160 = Max throw damage
			//23 = 2h Min damage
			//24 = 2h Max damage
			if(((D2Prop)pArr.get(x)).getPNum() == 160 || ((D2Prop)pArr.get(x)).getPNum() == 159 || ((D2Prop)pArr.get(x)).getPNum() == 23 || ((D2Prop)pArr.get(x)).getPNum() == 24){
				pArr.remove(x);
				x--;
			}

		}

	}


	private void generateMods() {




		modMap = new HashMap();




	}

	public void add(D2Prop prop) {

		pArr.add(prop);

	}


	public ArrayList generateDisplay() {

		ArrayList arrOut = new ArrayList();

		if(modMap == null){
			generateMods();
		}


		for(int x = 0;x<pArr.size();x++){
			arrOut.add(((D2Prop)pArr.get(x)).generateDisplay());		
		}

		cleanProps();

		return arrOut;
	}





}
