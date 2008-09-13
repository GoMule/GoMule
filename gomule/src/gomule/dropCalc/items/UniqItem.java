package gomule.dropCalc.items;

import gomule.dropCalc.DCNew;

import java.util.HashMap;

import randall.d2files.D2TxtFileItemProperties;

public class UniqItem extends Item {

	public UniqItem(D2TxtFileItemProperties ItemRow) {

		super(ItemRow);
		ItemCode = ItemRow.get("code");
		getType();
		ItemRarity = getRarity(ItemType);
		getTC();
		this.ItemName = ItemRow.get("index");
		this.ItemQLvl = Integer.parseInt(ItemRow.get("lvl"));
		this.iClassSpec = setClassSpec();
	}
	
	public HashMap getFinalProbSum(DCNew DC, int monSelection, int MF, int nPlayers, int nGroup){
		
		switch(ItemClass){
		case 0:

			mDrops = DC.findMonstersTC("weap" + this.ItemTC, (double)ItemRarity/(double)this.getTCProbSum(), monSelection, this, MF,  nPlayers,  nGroup);
			
			break;

		case 1:

			mDrops = DC.findMonstersTC("armo" + this.ItemTC,(double)ItemRarity/(double)this.getTCProbSum(), monSelection, this, MF, nPlayers, nGroup);
			
			break;
		}
		
		return mDrops;
		
	}

}
