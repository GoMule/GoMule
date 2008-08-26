package DropCalcRefactored;

import java.util.ArrayList;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;

public class DCNew {
	ArrayList monID = new ArrayList();
	ArrayList normLvl = new ArrayList();
	ArrayList normTC1 = new ArrayList();

	ArrayList mainRegMonArray = new ArrayList();
	ArrayList mainMinMonArray = new ArrayList();
	ArrayList mainChampMonArray = new ArrayList();
	ArrayList mainUniqArray = new ArrayList();
	ArrayList mainSupUniqArray = new ArrayList();
	ArrayList mainBossArray = new ArrayList();

	ArrayList regItemArray = new ArrayList();
	ArrayList magItemArray = new ArrayList();
	ArrayList rareItemArray = new ArrayList();
	ArrayList setItemArray = new ArrayList();
	ArrayList uniqItemArray = new ArrayList();

	public static void main(String[] args){

		new DCNew();

	}

	public DCNew(){
		D2TxtFile.readAllFiles("d2111");
		D2TblFile.readAllFiles("d2111");
		populateArrays();
		populateItemArrays();
//		System.out.println(D2TblFile.getString("Hell2"));

	}

	public void findMonstersTC(int TC){


//		for(int x = 0;x< mainRegMonArray.size();x=x+1){
//		((Monster)mainRegMonArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
//		ArrayList mTuples = ((Monster)mainRegMonArray.get(x)).mTuples;

//		for(int y = 0;y<mTuples.size();y=y+1){

//		if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
////		System.out.println(D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," +((Monster)mainRegMonArray.get(x)).monName);
//		}
//		}
//		}

		for(int x = 0;x< mainMinMonArray.size();x=x+1){
			((Monster)mainMinMonArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
			ArrayList mTuples = ((Monster)mainMinMonArray.get(x)).mTuples;

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
				}
			}
		}


	}

	private void populateItemArrays() {

		for(int x = 0; x < D2TxtFile.UNIQUES.getRowSize();x=x+1){
			uniqItemArray.add(D2TxtFile.UNIQUES.getRow(x).get("index"));
		}

	}

	public void populateArrays(){

		ArrayList SuBossCross = new ArrayList();
		ArrayList uniqueCheck = new ArrayList();

		SuBossCross.add("Radament");
		SuBossCross.add("The Summoner");
		SuBossCross.add("Griswold");
		SuBossCross.add("Nihlathak");

		for(int x = 0; x < D2TxtFile.MONSTATS.getRowSize();x=x+1){



			if(uniqueCheck.contains(D2TxtFile.MONSTATS.getRow(x).get("Id"))){
				continue;
			}
			uniqueCheck.add(D2TxtFile.MONSTATS.getRow(x).get("Id"));

			if(!D2TxtFile.MONSTATS.getRow(x).get("boss").equals("1") && D2TxtFile.MONSTATS.getRow(x).get("enabled").equals("1")&& D2TxtFile.MONSTATS.getRow(x).get("killable").equals("1") && !D2TxtFile.MONSTATS.getRow(x).get("TreasureClass1").equals("")&& !D2TxtFile.MONSTATS.getRow(x).get("TreasureClass1(N)").equals("")){
				mainRegMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "REG"));
				mainRegMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "REG"));
				mainRegMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "REG"));

				mainChampMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "CHAMP"));
				mainChampMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "CHAMP"));
				mainChampMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "CHAMP"));


				if(!D2TxtFile.MONSTATS.getRow(x).get("MaxGrp").equals("0")){
					mainMinMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "MIN"));					
					mainMinMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "MIN"));					
					mainMinMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "MIN"));

					if(!D2TxtFile.MONSTATS.getRow(x).get("minion2").equals("")){
						mainMinMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "MINSEC"));					
						mainMinMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "MINSEC"));					
						mainMinMonArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "MINSEC"));
					}

				}


				mainUniqArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "UNIQ"));
				mainUniqArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "UNIQ"));
				mainUniqArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "UNIQ"));			



			} else if(D2TxtFile.MONSTATS.getRow(x).get("boss").equals("1") && D2TxtFile.MONSTATS.getRow(x).get("enabled").equals("1")&& D2TxtFile.MONSTATS.getRow(x).get("killable").equals("1") && !D2TxtFile.MONSTATS.getRow(x).get("TreasureClass1").equals("")&& !D2TxtFile.MONSTATS.getRow(x).get("TreasureClass1(N)").equals("")){
				mainBossArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "BOSS"));
				mainBossArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "BOSS"));
				mainBossArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "BOSS"));
			}

		}



		for(int x = 0 ;x<D2TxtFile.SUPUNIQ.getRowSize();x=x+1){

			if(!D2TxtFile.SUPUNIQ.getRow(x).get("Class").equals("") && !SuBossCross.contains(D2TxtFile.SUPUNIQ.getRow(x).get("Name"))){
				mainSupUniqArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NORMAL", "SUPUNIQ"));
				mainSupUniqArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NIGHTMARE", "SUPUNIQ"));
				mainSupUniqArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "HELL", "SUPUNIQ"));
				if(!D2TxtFile.SUPUNIQ.getRow(x).get("MaxGrp").equals("0")){

					if(D2TxtFile.SUPUNIQ.getRow(x).get("Name") != null){
						mainMinMonArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NORMAL", "MIN"));
						mainMinMonArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NIGHTMARE", "MIN"));
						mainMinMonArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "HELL", "MIN"));

					}
				}
			}else if(!D2TxtFile.SUPUNIQ.getRow(x).get("Class").equals("") && SuBossCross.contains(D2TxtFile.SUPUNIQ.getRow(x).get("Name"))){
				if(!D2TxtFile.SUPUNIQ.getRow(x).get("MaxGrp").equals("0")){

					if(D2TxtFile.SUPUNIQ.getRow(x).get("Name") != null){
						mainMinMonArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NORMAL", "MIN"));
						mainMinMonArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NIGHTMARE", "MIN"));
						mainMinMonArray.add(new Monster(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "HELL", "MIN"));

					}
				}
			}
		}
		uniqueCheck.clear();
	}
}

