package gomule.dropCalc.items;

import gomule.dropCalc.DCNew;

import java.util.HashMap;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFileItemProperties;

public class MiscItem extends Item {

	public MiscItem(D2TxtFileItemProperties ItemRow) {

		super(ItemRow);

		ItemCode = ItemRow.get("code");
		getType();
		ItemRarity = getRarity(ItemType);
		getTC();
		this.ItemName = ItemRow.get("name");
		this.ItemQLvl = this.BaseQLvl;
		this.iClassSpec = setClassSpec();
		iNUS = 3;



	}
	
	public HashMap getFinalProbSum(DCNew DC, int monSelection, int MF, int nPlayers, int nGroup){




				mDrops = DC.findMonstersTCTrueMisc(monSelection, this, MF, nPlayers, nGroup);


		return mDrops;

	}
	
	public String getRealName() {

		return D2TblFile.getString(ItemCode);

	}

}
