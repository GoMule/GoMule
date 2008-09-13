package gomule.dropCalc.items;

import gomule.dropCalc.DCNew;
import gomule.dropCalc.monsters.MonsterTuple;

import java.util.HashMap;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFileItemProperties;

public class WhiteItem extends Item {

	public WhiteItem(D2TxtFileItemProperties ItemRow) {
		super(ItemRow);
		ItemCode = ItemRow.get("code");
		getType();
		ItemRarity = getRarity(ItemType);
		getTC();
		this.ItemName = ItemRow.get("name");
		this.ItemQLvl = Integer.parseInt(ItemRow.get("level"));

	}


	public String getRealName() {

		return D2TblFile.getString(ItemCode);

	}

	public void getProbDrop(MonsterTuple MT){
		switch(ItemClass){
		case 0:

			System.out.println(MT.getFinalTCs().get("weap" + this.ItemTC));
			
			break;

		case 1:

			System.out.println(MT.getFinalTCs().get("armo" + this.ItemTC));
			
			break;
		}
		

	}
	
	public HashMap getFinalProbSum(DCNew DC, int monSelection, int MF, int nPlayers, int nGroup){
		
		switch(ItemClass){
		case 0:

			mDrops = DC.findMonstersTC("weap" + this.ItemTC, (double)ItemRarity/(double)this.getTCProbSum(), monSelection,nPlayers,nGroup);
			
			break;

		case 1:

			mDrops = DC.findMonstersTC("armo" + this.ItemTC,(double)ItemRarity/(double)this.getTCProbSum(), monSelection,nPlayers,nGroup);
			
			break;
		}
		
		return mDrops;
		
	}
	
	public HashMap getTuplesDrop(){
		return mDrops;
	}


	public String getItemTC() {
		switch(ItemClass){
		case 0:

			return "weap" + this.ItemTC;

		case 1:

			return "armo" + this.ItemTC;

		}
		return null;
	}
	


}
