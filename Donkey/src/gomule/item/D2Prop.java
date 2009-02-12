package gomule.item;

import java.util.HashMap;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class D2Prop {

	private int[] pVals;
	private int pNum;
	private int funcN;





	public D2Prop(int pNum, int[] pVals){

		this.pNum = pNum;
		this.pVals = pVals;
	}


	public String getPString(){


		return null;
	}



	public int[] getPropVals() {

		return pVals;
	}

	public int getPNum(){

		return pNum;
	}




	public String generateDisplay() {


		//FUNCTION 0 means that you should use the txt files to find the print function to use. Otherwise, it should be a case of looking for custom funcs
		if(funcN == 0){

			if( D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descfunc") != null && ! D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descfunc").equals("")){
				funcN = Integer.parseInt( D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descfunc"));
			}
		}

		String oString = D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstrpos"));
		int dispLoc = 1;
//		System.out.println(oString + " " + pNum + " " + pVals[0]);

		try{
			dispLoc= Integer.parseInt(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descval"));
		}catch(NumberFormatException e){
			//leave dispLoc as  = 1
		}


		switch(funcN){

		case(1):
			if(dispLoc == 1){
				if(oString.contains("%d")){
					return oString.replace("%d", Integer.toString(pVals[0]));	
				}else{
					if(pVals[0] > -1){
						return "+" + pVals[0]+ " " + oString;
					}else{
						return pVals[0]+ " " + oString;
					}
				}
			}else if(dispLoc == 2){
				return oString + " +" + pVals[0];
			}else{
				return oString;
			}
		case(2):
			if(dispLoc == 1){
				return pVals[0] + "% " + oString;
			}else if(dispLoc == 2){
				return oString + " " + pVals[0] + "%" ;
			}else{
				return oString;
			}

		case(3):
			if(dispLoc == 1){
				return  pVals[0] + " " + oString;

			}else if(dispLoc == 2){
				return   oString  + " " + pVals[0];
			}else{
				return oString;
			}

		case(4):
			if(dispLoc == 1){
				return "+" + pVals[0]+"% " + oString;

			}else if(dispLoc == 2){
				return oString + " +" + pVals[0]+"%" ;
			}else{
				return oString;
			}

		case(5):
			if(dispLoc == 1){
				return ((pVals[0]*100)/128) +"% " + oString;

			}else if(dispLoc == 2){
				return oString + " " + ((pVals[0]*100)/128) +"%" ;
			}else{
				return oString;
			}

		case(6):
			if(dispLoc == 1){
				return "+" + pVals[0]+" " + oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2"));

			}else if(dispLoc == 2){
				return oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2")) + " +" + pVals[0];
			}else{
				return oString;
			}

		case(7):
			if(dispLoc == 1){
				return pVals[0]+"% " + oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2"));

			}else if(dispLoc == 2){
				return oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2")) + pVals[0]+"%";
			}else{
				return oString;
			}

		case(8):
			if(dispLoc == 1){
				return "+" + pVals[0]+"% " + oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2"));

			}else if(dispLoc == 2){
				return  oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2")) + " +" + pVals[0]+"%";
			}else{
				return oString;
			}

		case(9):
			if(dispLoc == 1){
				return pVals[0]+" " + oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2"));

			}else if(dispLoc == 2){
				return oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2")) + " " + pVals[0];
			}else{
				return oString;
			}

		case(10):
			if(dispLoc == 1){
				return (pVals[0]*100)/128 +"% " + oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2"));

			}else if(dispLoc == 2){
				return oString + " " + D2TblFile.getString(D2TxtFile.ITEM_STAT_COST.getRow(pNum).get("descstr2")) +  (pVals[0]*100)/128 +"%";
			}else{
				return oString;
			}

		case(11):

			return "Repairs 1 Durability in " + (100/pVals[0]) + " Seconds";

		case(12):
			if(dispLoc == 1){
				return "+" + pVals[0]+" " + oString;

			}else if(dispLoc == 2){
				return oString + " +" + pVals[0];
			}else{
				return oString;
			}

		case(13):

			return "+" + pVals[1] + " to " + D2TxtFile.getCharacterCode(pVals[0]) + " Skill Levels";

		case(14):

			return "+" + pVals[1] + " to " + getSkillTree(pVals[0]);

		case(15):

			oString = oString.replaceFirst("%d%", Integer.toString(pVals[0]));
		oString = oString.replace("%d", Integer.toString(pVals[2]));
		return oString.replace("%s", D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",D2TxtFile.SKILLS.getRow(pVals[1]).get("skilldesc")).get("str name")));

		case(16):

			oString = oString.replace("%d", Integer.toString(pVals[1]));
		return oString.replace("%s", D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",D2TxtFile.SKILLS.getRow(pVals[0]).get("skilldesc")).get("str name")));


		case(17):

			return "By time!? Oh shi....";


		case(18):
			return "By time!? Oh shi....";

		case(20):
			if(dispLoc == 1){
				return (pVals[0] * -1) + "% " + oString;
			}else if(dispLoc == 2){
				return  oString + " " +  (pVals[0] * -1) + "%";
			}else{
				return oString;
			}


		case(21):
			if(dispLoc == 1){
				return (pVals[0] * -1) + " " + oString;
			}else if(dispLoc == 2){
				return  oString + " " +  (pVals[0] * -1);
			}else{
				return oString;
			}

		case(23):

			return pVals[1] + "% " + oString + " " +  D2TblFile.getString(D2TxtFile.MONSTATS.getRow(pVals[0]).get("NameStr"));				

		case(24):

			oString = oString.replaceFirst("%d", Integer.toString(pVals[2]));
		oString = oString.replace("%d", Integer.toString(pVals[3]));
		return "Level " + pVals[0] + " " + D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",D2TxtFile.SKILLS.getRow(pVals[1]).get("skilldesc")).get("str name")) + " " + oString;

		case(27):
			return "+" + pVals[1] + " to " + D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",D2TxtFile.SKILLS.getRow(pVals[0]).get("skilldesc")).get("str name")) + " " + D2TblFile.getString((D2TxtFile.SKILLS.getRow(D2TxtFile.SKILL_DESC.getRow(pVals[0]).getRowNum()).get("charclass").charAt(0) + "").toUpperCase() + D2TxtFile.SKILLS.getRow(D2TxtFile.SKILL_DESC.getRow(pVals[0]).getRowNum()).get("charclass").substring(1) + "Only");

		case(28):

			return "+" + pVals[1] + " to " + D2TblFile.getString(D2TxtFile.SKILL_DESC.searchColumns("skilldesc",D2TxtFile.SKILLS.getRow(pVals[0]).get("skilldesc")).get("str name"));

		}
//		System.out.println();
		return "Unrecognized property: " + this.pNum;
	}




	public String getSkillTree(int lSkillNr){

		switch (lSkillNr)
		{
		case 0:
			return "Bow and Crossbow Skills (Amazon Only)";

		case 1:
			return "Passive and Magic Skills (Amazon Only)";

		case 2:
			return "Javelin and Spear Skills (Amazon Only)";

		case 8:
			return "Fire Skills (Sorceress Only)";

		case 9:
			return "Lightning Skills (Sorceress Only)";

		case 10:
			return "Cold Skills (Sorceress Only)";

		case 16:
			return "Curses (Necromancer only)";

		case 17:
			return "Poison and Bone Skills (Necromancer Only)";

		case 18:
			return "Summoning Skills (Necromancer Only)";

		case 24:
			return "Combat Skills (Paladin Only)";

		case 25:
			return "Offensive Aura Skills (Paladin Only)";

		case 26:
			return "Defensive Aura Skills (Paladin Only)";

		case 32:
			return "Combat Skills (Barbarian Only)";

		case 33:
			return "Masteries Skills (Barbarian Only)";

		case 34:
			return "Warcry Skills (Barbarian Only)";

		case 40:
			return "Summoning Skills (Druid Only)";

		case 41:
			return "Shape-Shifting Skills (Druid Only)";

		case 42:
			return "Elemental Skills (Druid Only)";

		case 48:
			return "Trap Skills (Assassin Only)";

		case 49:
			return "Shadow Discipline Skills (Assassin Only)";

		case 50:
			return "Martial Art Skills (Assassin Only)";

		}
		return "Unknown Tree (P 188)";

	}
}
