package gomule.item;

import gomule.util.D2BitReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class D2PropCollection {



	public ArrayList pArr = new ArrayList();
	private HashMap modMap;



	/**
	 * COMPLETED
	 * 	//Stage 1: Remove useless(MIGHT NOT BE!!!) mods STASH+CHAR
	//Stage 2: Combine props, like Cold min, cold max, cold res STASH+CHAR
	 * 
//	COMBINE PROPS - EG if you have a rune embedded in an item, need to combine VALS
//	Props such as +skills can appear twice, prop nums: 107,97,188,201,198,204 should be ignored
	 * 
	 */

	/**
	 * INCOMPLETE
	 */


	//Stage 3: Group mods such as resistance etc STASH+CHAR
	//Stage 4: Modify base values for DEF, DMG etc. STASH+CHAR
	//Stage 5: Populate modMap to display what properties are being modified CHAR ONLY?  


//	BASE VALS

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


//	--------------------------------
//	COMBINE MAXRES, RES, STATS




	//POTS??



	/**
	 * Clean up some of those useless properties
	 *
	 */
	private void cleanProps(){

		for(int x = 0;x<pArr.size();x++){

			//159 = Min throw damage
			//160 = Max throw damage
			//23 = 2h Min damage
			//24 = 2h Max damage
			//140 = Extra Blood
			if(((D2Prop)pArr.get(x)).getPNum() == 160 || ((D2Prop)pArr.get(x)).getPNum() == 159 || ((D2Prop)pArr.get(x)).getPNum() == 23 || ((D2Prop)pArr.get(x)).getPNum() == 24 || ((D2Prop)pArr.get(x)).getPNum() == 140){
				pArr.remove(x);
				x--;
			}
		}
	}


	/**
	 * If 2 properties are present, combine them.
	 *
	 */
	private void combineProps(){

		for(int x = 0 ;x < pArr.size();x++){

			for(int y = 0;y<pArr.size();y++){

				if(pArr.get(x) == pArr.get(y))continue;

				if((((D2Prop)pArr.get(x)).getPNum() == ((D2Prop)pArr.get(y)).getPNum()) && (((D2Prop)pArr.get(x)).getQFlag() == ((D2Prop)pArr.get(y)).getQFlag())&& (((D2Prop)pArr.get(x)).getQFlag() == 0)){
					if(((D2Prop)pArr.get(x)).getPNum() == 107 || ((D2Prop)pArr.get(x)).getPNum() == 97 || ((D2Prop)pArr.get(x)).getPNum() == 188 || ((D2Prop)pArr.get(x)).getPNum() == 201 || ((D2Prop)pArr.get(x)).getPNum() == 198 || ((D2Prop)pArr.get(x)).getPNum() == 204 )continue;



					((D2Prop)pArr.get(x)).addPVals(((D2Prop)pArr.get(y)).getPVals());
					pArr.remove(y);
					y--;
				}
			}
		}
	}




	/**
	 * DeDupe properties including:
	 * Problem: L2 and L3 are guaranteed to be collected together. EG if we have 
	 * an emerald in a weapon with poison damage already present, it will still have
	 * (pMin, pMax, pLength) as a triple of consecutive values.
	 * 
	 * This is not the case for dedupe level 4. If we have an item with FireR, ColdR and 
	 * PoisR which is then socketed with an item with LightR, we will see a spread of 
	 * resistences. Therefore dedupe L4 needs to be different, comparing all possible 
	 * value, not just considering the next X values. 
	 * (DEDUPE LEVEL 2)
	 * Enhanced Damage
	 * Damage
	 * Fire Damage
	 * Lightning Damage
	 * Magic Damage
	 * 
	 * (DEDUPE LEVEL 3)
	 * Cold Damage
	 * Poison Damage
	 *
	 *(DEDUP LEVEL 4)
	 * Stats	
	 * Res
	 * Max Res
	 */

	private void deDupeL4(){

		/**
		 * 183 and 184 are used as this ensures non null values for the search on the txt files.
		 */
		//Fire 39
		//Light 41
		//Cold 43
		//Poison 45
		ArrayList resMap = new ArrayList();

		//Str 0
		//Ener 1 
		//Dex 2 
		//Vit 3 
		ArrayList statMap = new ArrayList();

		for(int x = 0;x<pArr.size();x++){

			if(D2TxtFile.ITEM_STAT_COST.searchColumns("ID",Integer.toString(((D2Prop)pArr.get(x)).getPNum())).get("dgrp").equals(""))continue;
			if(((D2Prop)pArr.get(x)).getPNum() == 0 ||((D2Prop)pArr.get(x)).getPNum() == 1 ||((D2Prop)pArr.get(x)).getPNum() == 2 ||((D2Prop)pArr.get(x)).getPNum() == 3)statMap.add(pArr.get(x));
			if(((D2Prop)pArr.get(x)).getPNum() == 39 ||((D2Prop)pArr.get(x)).getPNum() == 41 ||((D2Prop)pArr.get(x)).getPNum() == 43 ||((D2Prop)pArr.get(x)).getPNum() == 45)resMap.add(pArr.get(x));

		}

		if(resMap.size() == 4){
			int vMin = 1000;

			for(int y = 0;y<resMap.size();y++){

				if(((D2Prop)resMap.get(y)).getPVals()[0] < vMin){
					vMin =((D2Prop)resMap.get(y)).getPVals()[0];
				}
			}
			pArr.add(new D2Prop(183, new int[]{vMin},((D2Prop)resMap.get(0)).getQFlag() ,true,37));
			threshDelete(resMap, vMin);
		}

		if(statMap.size() == 4){

			int vMin = 1000;

			for(int y = 0;y<statMap.size();y++){

				if(((D2Prop)statMap.get(y)).getPVals()[0] < vMin){
					vMin =((D2Prop)statMap.get(y)).getPVals()[0];
				}
			}
			pArr.add(new D2Prop(184, new int[]{vMin},((D2Prop)statMap.get(0)).getQFlag() ,true,38));
			threshDelete(statMap, vMin);

		}




	}



	private void threshDelete(ArrayList valMap, int vMin) {

		for(int x = 0;x<valMap.size();x++){
			if(((D2Prop)valMap.get(x)).getPVals()[0] == vMin){
				pArr.remove(valMap.get(x));
			}else{
				((D2Prop)valMap.get(x)).setPVals(new int[]{((D2Prop)valMap.get(x)).getPVals()[0] - vMin});
			}
		}


	}


	private void deDupeProps(){

		//DeDupe L2 and L3

		for(int x = 0 ;x < pArr.size();x++){


			if(x+1<pArr.size()){
				//Enhanced Damage %
				if(((D2Prop)pArr.get(x)).getPNum() == 17 && ((D2Prop)pArr.get(x+1)).getPNum() == 18){

					((D2Prop)pArr.get(x)).modifyVals(30, ((D2Prop)pArr.get(x)).getPVals());

					pArr.remove(x+1);

				}

				//Damage
				if(((D2Prop)pArr.get(x)).getPNum() == 21 && ((D2Prop)pArr.get(x+1)).getPNum() == 22){

					((D2Prop)pArr.get(x)).modifyVals(31, new int[]{((D2Prop)pArr.get(x)).getPVals()[0],((D2Prop)pArr.get(x+1)).getPVals()[0]});

					pArr.remove(x+1);

				}

				//Fire Damage
				if(((D2Prop)pArr.get(x)).getPNum() == 48 && ((D2Prop)pArr.get(x+1)).getPNum() == 49){

					((D2Prop)pArr.get(x)).modifyVals(32, new int[]{((D2Prop)pArr.get(x)).getPVals()[0],((D2Prop)pArr.get(x+1)).getPVals()[0]});

					pArr.remove(x+1);

				}

				//Lightning Damage
				if(((D2Prop)pArr.get(x)).getPNum() == 50 && ((D2Prop)pArr.get(x+1)).getPNum() == 51){

					((D2Prop)pArr.get(x)).modifyVals(33, new int[]{((D2Prop)pArr.get(x)).getPVals()[0],((D2Prop)pArr.get(x+1)).getPVals()[0]});

					pArr.remove(x+1);

				}

				//Magic Damage
				if(((D2Prop)pArr.get(x)).getPNum() == 52 && ((D2Prop)pArr.get(x+1)).getPNum() == 53){

					((D2Prop)pArr.get(x)).modifyVals(34, new int[]{((D2Prop)pArr.get(x)).getPVals()[0],((D2Prop)pArr.get(x+1)).getPVals()[0]});

					pArr.remove(x+1);

				}

				if(x+2<pArr.size()){
					//Cold Damage
					if(((D2Prop)pArr.get(x)).getPNum() == 54 && ((D2Prop)pArr.get(x+1)).getPNum() == 55 && ((D2Prop)pArr.get(x+2)).getPNum() == 56){

						((D2Prop)pArr.get(x)).modifyVals(35, new int[]{((D2Prop)pArr.get(x)).getPVals()[0],((D2Prop)pArr.get(x+1)).getPVals()[0],((D2Prop)pArr.get(x+2)).getPVals()[0]});

						pArr.remove(x+2);
						pArr.remove(x+1);

					}

					//Poison Damage
					if(((D2Prop)pArr.get(x)).getPNum() == 57 && ((D2Prop)pArr.get(x+1)).getPNum() == 58 && ((D2Prop)pArr.get(x+2)).getPNum() == 59){

						if(((D2Prop)pArr.get(x+2)).getPVals()[2] != 0){
							((D2Prop)pArr.get(x)).modifyVals(36, new int[]{((D2Prop)pArr.get(x)).getPVals()[0],((D2Prop)pArr.get(x+1)).getPVals()[0],((D2Prop)pArr.get(x+2)).getPVals()[0], ((D2Prop)pArr.get(x+2)).getPVals()[2]});

						}else{
							((D2Prop)pArr.get(x)).modifyVals(36, new int[]{((D2Prop)pArr.get(x)).getPVals()[0],((D2Prop)pArr.get(x+1)).getPVals()[0],((D2Prop)pArr.get(x+2)).getPVals()[0]});
						}

						pArr.remove(x+2);
						pArr.remove(x+1);

					}
				}
			}
		}
	}


	private void generateMods() {

		modMap = new HashMap();

	}

	public void add(D2Prop prop) {

		pArr.add(prop);

	}

	public void tidy(){
		cleanProps();
		combineProps();
		deDupeProps();
		deDupeL4();
	}


	public ArrayList generateDisplay(int qFlag, int cLvl) {

		ArrayList arrOut = new ArrayList();

		for(int x = 0;x<pArr.size();x++){
			String val = ((D2Prop)pArr.get(x)).generateDisplay(qFlag, cLvl);
			if(val != null && !val.equals("")){
				arrOut.add(val);		
			}
		}



		return arrOut;
	}


	public void readProp(D2BitReader pFile, int rootProp, int qFlag) {

		D2TxtFileItemProperties pRow = D2TxtFile.ITEM_STAT_COST.getRow(rootProp);
//		System.out.println(rootProp + " , " + getName());
		int readLength = Integer.parseInt(pRow.get("Save Bits"));
		int saveAdd = 0;
		if(!pRow.get("Save Add").equals("")){
			saveAdd = Integer.parseInt(pRow.get("Save Add"));
		}
		if (rootProp == 201 || rootProp == 197 || rootProp == 199
				|| rootProp == 195 || rootProp == 198 || rootProp == 196) {
			add(new D2Prop(rootProp, new int[] {(int)pFile.read(6)-saveAdd,(int)pFile.read(10)-saveAdd,(int)pFile.read(readLength) - saveAdd}, qFlag));
		} else if (rootProp == 204) {
			add(new D2Prop(rootProp, new int[] {(int)pFile.read(6)-saveAdd,(int)pFile.read(10)-saveAdd,(int)pFile.read(8)-saveAdd,(int)pFile.read(8)-saveAdd}, qFlag));
		} else if(!pRow.get("Save Param Bits").equals("")){
			add(new D2Prop(rootProp,new int[] {(int)pFile.read(Integer.parseInt(pRow.get("Save Param Bits"))) - saveAdd,(int)pFile.read(readLength) - saveAdd}, qFlag));
		} else {
			add(new D2Prop(rootProp,new int[] {(int)pFile.read(readLength) - saveAdd}, qFlag));
		}

	}



	public void addAll(ArrayList pList) {

		pArr.addAll(pList);

	}


	public void addAll(D2PropCollection propCollection, int qFlag) {

		pArr.addAll(propCollection.getPartialList(qFlag));	
	}

	public void addAll(D2PropCollection propCollection) {
		pArr.addAll(propCollection.getFullList());	
	}



	private ArrayList getPartialList(int qFlag) {

		ArrayList partialList = new ArrayList();
//		NEED TO ADD AS A NEW WITH STANDARD Q FLAG
		for(int x = 0;x<pArr.size();x++){
			if(((D2Prop)pArr.get(x)).getQFlag() == qFlag){
				//D2Prop constructor (d2Prop) sets QFlag to be 0
				partialList.add(new D2Prop((D2Prop)pArr.get(x)));
			}
		}

		return partialList;

	}

	private ArrayList getFullList() {
		return this.pArr;
	}




}
