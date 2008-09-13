package gomule.dropCalc;

import gomule.dropCalc.items.Item;
import gomule.dropCalc.items.SetItem;
import gomule.dropCalc.items.UniqItem;
import gomule.dropCalc.items.WhiteItem;
import gomule.dropCalc.monsters.Monster;
import gomule.dropCalc.monsters.MonsterTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class DCNew {
	
	CalcWriter CW = new CalcWriter("tempTCs.txt");
	
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

	public void writeMonstersTC(int TC){


		/**
		 * 0:Reg
		 * 1:Min
		 * 2:Champ
		 * 3:Unique
		 * 4:Superunique
		 * 5:Boss
		 */
		CW.writeData(TC+",");
		for(int x = 0;x< mainRegMonArray.size();x=x+1){
			((Monster)mainRegMonArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
			ArrayList mTuples = ((Monster)mainRegMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
//					System.out.println(D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," +((Monster)mainRegMonArray.get(x)).monName);
//					System.out.print("0-" + x+"-"+y +",");
					CW.writeData("0-" + x+"-"+y +",");
				}
			}
		}

		for(int x = 0;x< mainMinMonArray.size();x=x+1){
			((Monster)mainMinMonArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
			ArrayList mTuples = ((Monster)mainMinMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
//					System.out.print("1-" + x+"-"+y +",");
					CW.writeData("1-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
				}
			}
		}
		
		for(int x = 0;x< mainChampMonArray.size();x=x+1){
			((Monster)mainChampMonArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
			ArrayList mTuples = ((Monster)mainChampMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
//					System.out.print("2-" + x+"-"+y +",");
					CW.writeData("2-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
				}
			}
		}
		
		for(int x = 0;x< mainUniqArray.size();x=x+1){
			((Monster)mainUniqArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
			ArrayList mTuples = ((Monster)mainUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
//					System.out.print("3-" + x+"-"+y +",");
					CW.writeData("3-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
				}
			}
		}
		
		for(int x = 0;x< mainSupUniqArray.size();x=x+1){
			if(((Monster)mainSupUniqArray.get(x)).getMonName().equals("Fangskin") || ((Monster)mainSupUniqArray.get(x)).getSUID().startsWith("ancientbarb")){
				continue;
			}
			((Monster)mainSupUniqArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
			ArrayList mTuples = ((Monster)mainSupUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
//					System.out.print("4-" + x+"-"+y +",");
					CW.writeData("4-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
				}
			}
		}
		
		for(int x = 0;x< mainBossArray.size();x=x+1){
			((Monster)mainBossArray.get(x)).lookupBASETCReturnATOMICTCS(1, 1);
			ArrayList mTuples = ((Monster)mainBossArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("armo" + TC) || ((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey("weap" + TC)){
//					System.out.print("5-" + x+"-"+y +",");
					CW.writeData("5-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
				}
			}
		}

		CW.writeData("\n");
	}
	
	public HashMap findMonstersTC(String key, double d, int monSelection, int nPlayers, int nGroup){

		//SHOULD BE HASHMAP?
		HashMap monsterTCList = new HashMap();
		/**
		 * 0:Reg
		 * 1:Min
		 * 2:Champ
		 * 3:Unique
		 * 4:Superunique
		 * 5:Boss
		 */
		
		switch (monSelection){
		
		case 0:
		
		for(int x = 0;x< mainRegMonArray.size();x=x+1){
			((Monster)mainRegMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainRegMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){
				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.println(D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," +((Monster)mainRegMonArray.get(x)).monName + ","+ ((Monster)mainRegMonArray.get(x)).monDiff+ "," +((Monster)mainRegMonArray.get(x)).monName + ","+ ((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d);
//					System.out.print("0-" + x+"-"+y +",");
//					CW.writeData("0-" + x+"-"+y +",");
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d));
				}
			}
		}
		
//		if(monsterTCList.isEmpty()){
//		return null;
//		}
		break;
		case 1:

		for(int x = 0;x< mainMinMonArray.size();x=x+1){
			((Monster)mainMinMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainMinMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("1-" + x+"-"+y +",");
//					CW.writeData("1-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d));
				}
			}
		}
		break;
		case 2:
		for(int x = 0;x< mainChampMonArray.size();x=x+1){
			((Monster)mainChampMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainChampMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("2-" + x+"-"+y +",");
//					CW.writeData("2-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d));
				}
			}
		}
		break;
		case 3:
		for(int x = 0;x< mainUniqArray.size();x=x+1){
			((Monster)mainUniqArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("3-" + x+"-"+y +",");
//					CW.writeData("3-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d));
				}
			}
		}
		break;
		case 4:
		for(int x = 0;x< mainSupUniqArray.size();x=x+1){
			if(((Monster)mainSupUniqArray.get(x)).getMonName().equals("Fangskin") || ((Monster)mainSupUniqArray.get(x)).getSUID().startsWith("ancientbarb")){
				continue;
			}
			((Monster)mainSupUniqArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainSupUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("4-" + x+"-"+y +",");
//					CW.writeData("4-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d));
				}
			}
		}
		break;
		case 5:
		for(int x = 0;x< mainBossArray.size();x=x+1){
			((Monster)mainBossArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainBossArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("5-" + x+"-"+y +",");
//					CW.writeData("5-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d));
				}
			}
		}
		break;
		}
		

		return monsterTCList;
	}
	
	public HashMap findMonstersTC(String key, double d, int monSelection, UniqItem item, int MF, int nPlayers, int nGroup){

		//SHOULD BE HASHMAP?
		HashMap monsterTCList = new HashMap();
		/**
		 * 0:Reg
		 * 1:Min
		 * 2:Champ
		 * 3:Unique
		 * 4:Superunique
		 * 5:Boss
		 */
		
		switch (monSelection){
		
		case 0:
		
		for(int x = 0;x< mainRegMonArray.size();x=x+1){
			((Monster)mainRegMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainRegMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){
				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.println(D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," +((Monster)mainRegMonArray.get(x)).monName + ","+ ((Monster)mainRegMonArray.get(x)).monDiff+ "," +((Monster)mainRegMonArray.get(x)).monName + ","+ ((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d);
//					System.out.print("0-" + x+"-"+y +",");
//					CW.writeData("0-" + x+"-"+y +",");
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
				}
			}
		}
		
//		if(monsterTCList.isEmpty()){
//		return null;
//		}
		break;
		case 1:

		for(int x = 0;x< mainMinMonArray.size();x=x+1){
			((Monster)mainMinMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainMinMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("1-" + x+"-"+y +",");
//					CW.writeData("1-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 2:
		for(int x = 0;x< mainChampMonArray.size();x=x+1){
			((Monster)mainChampMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainChampMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("2-" + x+"-"+y +",");
//					CW.writeData("2-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 3:
		for(int x = 0;x< mainUniqArray.size();x=x+1){
			((Monster)mainUniqArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("3-" + x+"-"+y +",");
//					CW.writeData("3-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 4:
		for(int x = 0;x< mainSupUniqArray.size();x=x+1){
			if(((Monster)mainSupUniqArray.get(x)).getMonName().equals("Fangskin") || ((Monster)mainSupUniqArray.get(x)).getSUID().startsWith("ancientbarb")){
				continue;
			}
			((Monster)mainSupUniqArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainSupUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("4-" + x+"-"+y +",");
//					CW.writeData("4-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 5:
		for(int x = 0;x< mainBossArray.size();x=x+1){
			((Monster)mainBossArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainBossArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("5-" + x+"-"+y +",");
//					CW.writeData("5-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		}
		

		return monsterTCList;
	}
	
	public HashMap findMonstersTC(String key, double d, int monSelection, SetItem item, int MF, int nPlayers, int nGroup){

		//SHOULD BE HASHMAP?
		HashMap monsterTCList = new HashMap();
		/**
		 * 0:Reg
		 * 1:Min
		 * 2:Champ
		 * 3:Unique
		 * 4:Superunique
		 * 5:Boss
		 */
		
		switch (monSelection){
		
		case 0:
		
		for(int x = 0;x< mainRegMonArray.size();x=x+1){
			if(item.getRealName().toLowerCase().contains("cow")){
				if(!(((Monster)mainRegMonArray.get(x)).getMonID()).equals("hellbovine")){
					continue;
				}
			}
			((Monster)mainRegMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainRegMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){
				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.println(D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," +((Monster)mainRegMonArray.get(x)).monName + ","+ ((Monster)mainRegMonArray.get(x)).monDiff+ "," +((Monster)mainRegMonArray.get(x)).monName + ","+ ((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d);
//					System.out.print("0-" + x+"-"+y +",");
//					CW.writeData("0-" + x+"-"+y +",");
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * (1 - getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))) * getQualitySet(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
				}
			}
		}
		
//		if(monsterTCList.isEmpty()){
//		return null;
//		}
		break;
		case 1:

		for(int x = 0;x< mainMinMonArray.size();x=x+1){
			if(item.getRealName().toLowerCase().contains("cow")){
				
				if(!(((Monster)mainMinMonArray.get(x)).getMonID()).equals("hellbovine")){
					continue;
				}
			}
			((Monster)mainMinMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainMinMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("1-" + x+"-"+y +",");
//					CW.writeData("1-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y))) * getQualitySet(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 2:
		for(int x = 0;x< mainChampMonArray.size();x=x+1){
			if(item.getRealName().toLowerCase().contains("cow")){
				if(!(((Monster)mainChampMonArray.get(x)).getMonID()).equals("hellbovine")){
					continue;
				}
			}
			((Monster)mainChampMonArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainChampMonArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("2-" + x+"-"+y +",");
//					CW.writeData("2-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y))) * getQualitySet(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 3:
		for(int x = 0;x< mainUniqArray.size();x=x+1){
			if(item.getRealName().toLowerCase().contains("cow")){
				if(!(((Monster)mainUniqArray.get(x)).getMonID()).equals("hellbovine")){
					continue;
				}
			}
			((Monster)mainUniqArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("3-" + x+"-"+y +",");
//					CW.writeData("3-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y))) * getQualitySet(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 4:
		for(int x = 0;x< mainSupUniqArray.size();x=x+1){
			if(item.getRealName().toLowerCase().contains("cow")){
				if(!(((Monster)mainSupUniqArray.get(x)).getMonID()).equals("The Cow King")){
					continue;
				}
			}
			if(((Monster)mainSupUniqArray.get(x)).getMonName().equals("Fangskin") || ((Monster)mainSupUniqArray.get(x)).getSUID().startsWith("ancientbarb")){
				continue;
			}
			((Monster)mainSupUniqArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainSupUniqArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("4-" + x+"-"+y +",");
//					CW.writeData("4-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y))) * getQualitySet(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		case 5:
		for(int x = 0;x< mainBossArray.size();x=x+1){
			if(item.getRealName().toLowerCase().contains("cow")){
				if(!(((Monster)mainBossArray.get(x)).getMonID()).equals("hellbovine")){
					continue;
				}
			}
			((Monster)mainBossArray.get(x)).lookupBASETCReturnATOMICTCS(nPlayers, nGroup);
			ArrayList mTuples = ((Monster)mainBossArray.get(x)).getmTuples();

			for(int y = 0;y<mTuples.size();y=y+1){

				if(((MonsterTuple)mTuples.get(y)).getFinalTCs().containsKey(key)){
//					System.out.print("5-" + x+"-"+y +",");
//					CW.writeData("5-" + x+"-"+y +",");
//					System.out.println(((Monster)mainMinMonArray.get(x)).monName+ "," +((Monster)mainMinMonArray.get(x)).getRealBossName() + ","+ D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",((MonsterTuple)mTuples.get(y)).AreaName).get("LevelName"))+ "," + ((Monster)mainMinMonArray.get(x)).monDiff);
//					monsterTCList.add(mTuples.get(y));
					monsterTCList.put(mTuples.get(y), new Double(((Double)((MonsterTuple)mTuples.get(y)).getFinalTCs().get(key)).doubleValue() * d * getQualityUniq(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y))) * getQualitySet(item, ((MonsterTuple)mTuples.get(y)).getLevel(), MF,((MonsterTuple)mTuples.get(y)))));
					}
			}
		}
		break;
		}
		

		return monsterTCList;
	}

	private void populateItemArrays() {

//		for(int x = 0; x < D2TxtFile.UNIQUES.getRowSize();x=x+1){
////		uniqItemArray.add(D2TxtFile.UNIQUES.getRow(x).get("index"));
//		uniqItemArray.add(new Item(D2TxtFile.UNIQUES.getRow(x)));
//		}

		for(int x = 0;x<D2TxtFile.WEAPONS.getRowSize();x=x+1){
			if(D2TxtFile.WEAPONS.getRow(x).get("spawnable").equals("1")){
				regItemArray.add(new WhiteItem(D2TxtFile.WEAPONS.getRow(x)));
			}
		}

		for(int x = 0;x<D2TxtFile.ARMOR.getRowSize();x=x+1){
			if(D2TxtFile.ARMOR.getRow(x).get("spawnable").equals("1")){
				regItemArray.add(new WhiteItem(D2TxtFile.ARMOR.getRow(x)));
			}
		}

		for(int x = 0;x<D2TxtFile.UNIQUES.getRowSize();x=x+1){
			if(!D2TxtFile.UNIQUES.getRow(x).get("lvl").equals("0") && D2TxtFile.UNIQUES.getRow(x).get("enabled").equals("1") && !D2TxtFile.UNIQUES.getRow(x).get("code").startsWith("cm")&&!D2TxtFile.UNIQUES.getRow(x).get("code").equals("rin")&&!D2TxtFile.UNIQUES.getRow(x).get("code").equals("jew")&& !D2TxtFile.UNIQUES.getRow(x).get("code").equals("amu")){
				uniqItemArray.add(new UniqItem(D2TxtFile.UNIQUES.getRow(x)));
			}
		}

		for(int x = 0;x<D2TxtFile.SETITEMS.getRowSize();x=x+1){
			if(D2TxtFile.SETITEMS.getRow(x).get("set")!=null && !D2TxtFile.SETITEMS.getRow(x).get("item").equals("rin")&& !D2TxtFile.SETITEMS.getRow(x).get("item").equals("amu")){
				setItemArray.add(new SetItem(D2TxtFile.SETITEMS.getRow(x)));
			}
		}
		
		sortItemArrays();

	}

	private void sortItemArrays() {
		

		Collections.sort(regItemArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Item lItem1 = (Item) pObj1;
				Item lItem2 = (Item) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(magItemArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Item lItem1 = (Item) pObj1;
				Item lItem2 = (Item) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(rareItemArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Item lItem1 = (Item) pObj1;
				Item lItem2 = (Item) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(setItemArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Item lItem1 = (Item) pObj1;
				Item lItem2 = (Item) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(uniqItemArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Item lItem1 = (Item) pObj1;
				Item lItem2 = (Item) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		
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
				if(!D2TxtFile.MONSTATS.getRow(x).get("TreasureClass4").equals("")){
					mainBossArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "BOSSQ"));
					mainBossArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "BOSSQ"));
					mainBossArray.add(new Monster(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "BOSSQ"));
					
				}
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
		sortArrays();
	}

	private void sortArrays() {
		
		Collections.sort(mainRegMonArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Monster lItem1 = (Monster) pObj1;
				Monster lItem2 = (Monster) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(mainMinMonArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Monster lItem1 = (Monster) pObj1;
				Monster lItem2 = (Monster) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(mainChampMonArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Monster lItem1 = (Monster) pObj1;
				Monster lItem2 = (Monster) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(mainUniqArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Monster lItem1 = (Monster) pObj1;
				Monster lItem2 = (Monster) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(mainSupUniqArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Monster lItem1 = (Monster) pObj1;
				Monster lItem2 = (Monster) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
		Collections.sort(mainBossArray, new Comparator()
		{
			public int compare(Object pObj1, Object pObj2)
			{
				Monster lItem1 = (Monster) pObj1;
				Monster lItem2 = (Monster) pObj2;
				
				return lItem1.getRealName().compareTo(lItem2.getRealName());
				
				
			}
		});
		
	}

	public double getQualityUniq(Item item, int ilvl, int MF, MonsterTuple mon) {

//		All items have rarity 3 except class-specific items have rarity 1
//		, assassin claws have rarity 2, and wands, staves and sceptres (rods) have rarity 1.
		

//		switch()
		D2TxtFileItemProperties ratioRow = null;
		if(item.isClassSpec()){
		 ratioRow = D2TxtFile.ITEMRATIO.getRow(4);
		}else{
			 ratioRow = D2TxtFile.ITEMRATIO.getRow(2);
		}
		
		double dChance = (Integer.parseInt(ratioRow.get("Unique")) - (((double)(ilvl - item.getqLvl()))/((double)Integer.parseInt(ratioRow.get("UniqueDivisor"))))) * 128;
		double eMF = ((double)(MF * 250))/((double)(MF + 250));
		
		dChance = ((double)(dChance * 100))/((double)(100 + eMF));
		
		if(dChance < Double.parseDouble(ratioRow.get("UniqueMin"))){
			dChance = Double.parseDouble(ratioRow.get("UniqueMin"));
		}
		
		dChance = dChance - (dChance * (((double)mon.getUqual())/((double)1024)));
//		System.out.println(((double)128)/dChance);
		return ((double)128)/dChance;
//		(ilvl - item.getqLvl())/
		
//		1) Find proper line in itemratio.txt.
//		2) Chance = (BaseChance - ((ilvl-qlvl)/Divisor)) * 128
//		3) if (we check for unique, set or rare quality) EffectiveMF=MF*Factor/(MF+Factor)
//		else EffectiveMF=MF
//		4) Chance= Chance* 100/(100+ EffectiveMF).
//		5) if Chance< MinChance chance == minchance
//		6) FinalChance=Chance-(Chance*QualityFactor/1024)
//		7) If (RND[ FinalChance ])<128 return Success
//		else return Fail

	}
	
	public double getQualitySet(Item item, int ilvl, int MF, MonsterTuple mon) {

//		All items have rarity 3 except class-specific items have rarity 1
//		, assassin claws have rarity 2, and wands, staves and sceptres (rods) have rarity 1.
		

//		switch()
		D2TxtFileItemProperties ratioRow = null;
		if(item.isClassSpec()){
		 ratioRow = D2TxtFile.ITEMRATIO.getRow(4);
		}else{
			 ratioRow = D2TxtFile.ITEMRATIO.getRow(2);
		}
		
		double dChance = (Integer.parseInt(ratioRow.get("Set")) - (((double)(ilvl - item.getqLvl()))/((double)Integer.parseInt(ratioRow.get("SetDivisor"))))) * 128;
		double eMF = ((double)(MF * 500))/((double)(MF + 500));
		
		dChance = ((double)(dChance * 100))/((double)(100 + eMF));
		
		if(dChance < Double.parseDouble(ratioRow.get("SetMin"))){
			dChance = Double.parseDouble(ratioRow.get("SetMin"));
		}
		
		dChance = dChance - (dChance * (((double)mon.getSqual())/((double)1024)));
		return ((double)128)/dChance;


	}

	public ArrayList getRegItemArray() {
		return regItemArray;
	}

	public ArrayList getSetItemArray() {
		return setItemArray;
	}

	public ArrayList getUniqItemArray() {
		return uniqItemArray;
	}

	public ArrayList getMainRegMonArray() {
		return mainRegMonArray;
	}

	public ArrayList getMainMinMonArray() {
		return mainMinMonArray;
	}

	public ArrayList getMainChampMonArray() {
		return mainChampMonArray;
	}

	public ArrayList getMainUniqArray() {
		return mainUniqArray;
	}

	public ArrayList getMainSupUniqArray() {
		return mainSupUniqArray;
	}

	public ArrayList getMainBossArray() {
		return mainBossArray;
	}
}

