package gomule.dropCalc.monsters;

import java.util.ArrayList;
import java.util.HashMap;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFileItemProperties;

public class Boss extends Monster {


	boolean isQuest = false;

	public Boss(D2TxtFileItemProperties monRow, String monDiff, int monClass, int flag) {
		super( monRow,  monDiff, monClass,  flag);
		if(flag==1){
			isQuest = true;
		}
		this.monName =D2TblFile.getString(monRow.get("NameStr"));
		mTuples = new ArrayList();
		HashMap areas = findLocsBossMonster();
		findLevelsBossMonster(areas);
		String header = "TreasureClass1";
		if(isQuest){
			header = "TreasureClass4";
		}
		ArrayList initTCs = getInitTC(areas, header);
		mTuples = createTuples(areas, initTCs);

	}
	

	public String getRealName(){
		if(isQuest){
		return this.monName  + " (Q) - " +  this.monID;
		}else{
			return this.monName  + " - " +  this.monID;
		}
	}


	public boolean getQuest() {
		// TODO Auto-generated method stub
		return isQuest;
	}
	



}
