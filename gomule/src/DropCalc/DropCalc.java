package DropCalc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class DropCalc {
	ArrayList monID = new ArrayList();
	ArrayList normLvl = new ArrayList();
	ArrayList normTC1 = new ArrayList();

	ArrayList mainRegMonArray = new ArrayList();
	ArrayList mainMinMonArray = new ArrayList();
	ArrayList mainChampMonArray = new ArrayList();
	ArrayList mainUniqArray = new ArrayList();
	ArrayList mainSupUniqArray = new ArrayList();
	ArrayList mainBossArray = new ArrayList();
	
	public static void main(String[] args){
		


	
		
		
//		D2TxtFile.readAllFiles("d2111");
		new DropCalc();
//		
		
	}
	


	
	public DropCalc(){
		D2TxtFile.readAllFiles("d2111");
		D2TblFile.readAllFiles("d2111");
		System.out.println(D2TblFile.getString("Echo Chamber"));
		populateArrays();
			
		System.out.println("By jove!");
		
	}

	private ArrayList findLocsMonster(String monStr){
		ArrayList locsOut = new ArrayList();
//		String monID = D2TxtFile.MONSTATS.searchColumns("NameStr", monStr).get("Id");
		ArrayList monSearch = new ArrayList();
		String selector = "mon1";
		for(int x = 0;x<11;x=x+1){
		monSearch.clear();
		monSearch = D2TxtFile.LEVELS.searchColumnsMultipleHits(selector, monStr);
		if(monSearch.size() > 0){
			for(int y = 0;y<monSearch.size();y=y+1){
				//new MonLvl(((D2TxtFileItemProperties)monSearch.get(y)).get("LevelName"), ;
			locsOut.add(((D2TxtFileItemProperties)monSearch.get(y)).get("LevelName"));
			}
		}
		selector = selector.substring(0, selector.length() - 1) + (new Integer(x+1)); 
//		System.out.println(monID);
		
		}
		
		return locsOut;
	}
	
	private ArrayList lookupHELLTCReturnATOMICTCS(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayList lookupNMTCReturnATOMICTCS(String string) {
		// TODO Auto-generated method stub
		return null;
	}


	public void populateArrays(){
		
//		ArrayList mainRegMonArray = new ArrayList();
//		ArrayList mainMinMonArray = new ArrayList();
//		ArrayList mainChampMonArray = new ArrayList();
//		ArrayList mainUniqArray = new ArrayList();
//		ArrayList mainSupUniqArray = new ArrayList();
//		ArrayList mainBossArray = new ArrayList();
		
		ArrayList SuBossCross = new ArrayList();

		SuBossCross.add("Radament");
		SuBossCross.add("The Summoner");
		SuBossCross.add("Griswold");
		SuBossCross.add("Nihlathak");
		
		for(int x = 0; x < D2TxtFile.MONSTATS.getRowSize();x=x+1){

			if(D2TxtFile.MONSTATS.getRow(x).get("Id").equals("quillrat6")){
				System.out.println();
			}
			
			if(D2TxtFile.MONSTATS.getRow(x).get("enabled").equals("1")&& D2TxtFile.MONSTATS.getRow(x).get("killable").equals("1") && !D2TxtFile.MONSTATS.getRow(x).get("TreasureClass1").equals("")){
				mainRegMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "REG"));
				if(((MonDiff)mainRegMonArray.get(mainRegMonArray.size()-1)).getAreas().size() < 1){
//					System.out.println(((MonDiff)mainRegMonArray.get(mainRegMonArray.size()-1)).getName());
					mainRegMonArray.remove(mainRegMonArray.size()-1);
					
				}
				mainRegMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "REG"));
				if(((MonDiff)mainRegMonArray.get(mainRegMonArray.size()-1)).getAreas().size() < 1){
//					System.out.println(((MonDiff)mainRegMonArray.get(mainRegMonArray.size()-1)).getName());
					mainRegMonArray.remove(mainRegMonArray.size()-1);
				}
				mainRegMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "REG"));
				if(((MonDiff)mainRegMonArray.get(mainRegMonArray.size()-1)).getAreas().size() < 1){
//					System.out.println(((MonDiff)mainRegMonArray.get(mainRegMonArray.size()-1)).getName());
					mainRegMonArray.remove(mainRegMonArray.size()-1);
				}
				

				
				mainChampMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "CHAMP"));
				if(((MonDiff)mainChampMonArray.get(mainChampMonArray.size()-1)).getAreas().size() < 1){
//					System.out.println(((MonDiff)mainChampMonArray.get(mainChampMonArray.size()-1)).getName());
					mainChampMonArray.remove(mainChampMonArray.size()-1);
				}
				mainChampMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "CHAMP"));
				if(((MonDiff)mainChampMonArray.get(mainChampMonArray.size()-1)).getAreas().size() < 1){
//					System.out.println(((MonDiff)mainChampMonArray.get(mainChampMonArray.size()-1)).getName());
					mainChampMonArray.remove(mainChampMonArray.size()-1);
				}
				mainChampMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "CHAMP"));
				if(((MonDiff)mainChampMonArray.get(mainChampMonArray.size()-1)).getAreas().size() < 1){
//					System.out.println(((MonDiff)mainChampMonArray.get(mainChampMonArray.size()-1)).getName());
					mainChampMonArray.remove(mainChampMonArray.size()-1);
				}
				
				
				if(!D2TxtFile.MONSTATS.getRow(x).get("MaxGrp").equals("0")){
					mainMinMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "MIN"));
					
					if(((MonDiff)mainMinMonArray.get(mainMinMonArray.size()-1)).getAreas().size() < 1){
						mainMinMonArray.remove(mainMinMonArray.size()-1);
					}
					
					mainMinMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "MIN"));
					
					if(((MonDiff)mainMinMonArray.get(mainMinMonArray.size()-1)).getAreas().size() < 1){
						mainMinMonArray.remove(mainMinMonArray.size()-1);
					}
					
					mainMinMonArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "MIN"));
					
					if(((MonDiff)mainMinMonArray.get(mainMinMonArray.size()-1)).getAreas().size() < 1){
						mainMinMonArray.remove(mainMinMonArray.size()-1);
					}
				}
				
				
				mainUniqArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "UNIQ"));
				if(((MonDiff)mainUniqArray.get(mainUniqArray.size()-1)).getAreas().size() < 1){
					mainUniqArray.remove(mainUniqArray.size()-1);
				}
				mainUniqArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "UNIQ"));
				if(((MonDiff)mainUniqArray.get(mainUniqArray.size()-1)).getAreas().size() < 1){
					mainUniqArray.remove(mainUniqArray.size()-1);
				}
				mainUniqArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "UNIQ"));
				if(((MonDiff)mainUniqArray.get(mainUniqArray.size()-1)).getAreas().size() < 1){
					mainUniqArray.remove(mainUniqArray.size()-1);
				}
				
				
				
				if(D2TxtFile.MONSTATS.getRow(x).get("boss").equals("1")){
				mainBossArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NORMAL", "BOSS"));
				mainBossArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "NIGHTMARE", "BOSS"));
				mainBossArray.add(new MonDiff(D2TxtFile.MONSTATS.getRow(x).get("Id"), "HELL", "BOSS"));
				}

			}			
		}
		
		
		
		for(int x = 0 ;x<D2TxtFile.SUPUNIQ.getRowSize();x=x+1){
			
			if(!D2TxtFile.SUPUNIQ.getRow(x).get("Class").equals("") && !SuBossCross.contains(D2TxtFile.SUPUNIQ.getRow(x).get("Name"))){
				mainSupUniqArray.add(new MonDiff(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NORMAL", "SUPUNIQ"));
				mainSupUniqArray.add(new MonDiff(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NIGHTMARE", "SUPUNIQ"));
				mainSupUniqArray.add(new MonDiff(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "HELL", "SUPUNIQ"));
				
				if(!D2TxtFile.SUPUNIQ.getRow(x).get("MaxGrp").equals("0")){
					if(D2TxtFile.SUPUNIQ.getRow(x).get("Name") != null){
					mainMinMonArray.add(new MonDiff(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NORMAL", "MIN"));
					mainMinMonArray.add(new MonDiff(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "NIGHTMARE", "MIN"));
					mainMinMonArray.add(new MonDiff(D2TxtFile.SUPUNIQ.getRow(x).get("Name"), "HELL", "MIN"));
					
					
					}
				}
				
			}
		}
		
		
		System.out.println("BLEH?");
		}
	
	
	
//	public void lookupMonsterTC(String monsID){
//		
//		String initTC = D2TxtFile.MONSTATS.getRow(monID.indexOf(monsID)).get("TreasureClass1");
//		
//
//	}
	
//	public ArrayList lookupTCReturnSUBTCS(String tcQuery){
//		
//		D2TxtFileItemProperties initTCRow = D2TxtFile.TCS.searchColumns("Treasure Class", tcQuery);
//		String selector = "Item1";
//		String probSelector = "Prob1";
//		ArrayList thisSubTC = new ArrayList();
//		ArrayList thisSubTCProb = new ArrayList();
//		ArrayList thatSubTC = new ArrayList();
//		
//		if(initTCRow != null){
//			for(int x = 1;x<11;x=x+1){
//				
//					if(!initTCRow.get(selector).equals("")){
//						thisSubTC.add(initTCRow.get(selector));
//						thisSubTCProb.add(initTCRow.get(probSelector));
//						
////						if(((String)thisSubTC.get(x -1)).indexOf("Act")!=-1){
////							System.out.println("HM " + lookupTCReturnSUBTCS((String)thisSubTC.get(x-1)));
////						}
//					}
//				selector = selector.substring(0, selector.length() - 1) + (new Integer(x+1)); 
//				probSelector = probSelector.substring(0, probSelector.length() - 1) + (new Integer(x+1)); 
//				}
//			}
//		
//		for(int x = 0;x<thisSubTC.size();x=x+1){
//			if(((String)thisSubTC.get(x)).indexOf("Act")!=-1){
//			thisSubTC.addAll(lookupTCReturnSUBTCS((String)thisSubTC.get(x)));
//			thisSubTC.remove(x);
//			x = x-1;
//			}
//		}
//		
////		thisSubTC.addAll(thatSubTC);
//		
//		return thisSubTC;
//		
//	}
	
//	public ArrayList lookupTCReturnSUBATOMICTCS(String tcQuery){
//		
//		D2TxtFileItemProperties initTCRow = D2TxtFile.TCS.searchColumns("Treasure Class", tcQuery);
//		String selector = "Item1";
//		String probSelector = "Prob1";
//		ArrayList thisSubTC = new ArrayList();
//		ArrayList thisSubTCProb = new ArrayList();
//		ProbTCRow TCROW;
//		
//		if(initTCRow != null){
//			for(int x = 1;x<11;x=x+1){
//				
//					if(initTCRow.get(selector).indexOf("Equip")!=-1 || initTCRow.get(selector).indexOf("weap")!=-1||initTCRow.get(selector).indexOf("armo")!=-1){
//						thisSubTC.add(initTCRow.get(selector));
//						thisSubTCProb.add(initTCRow.get(probSelector));
//						
////						if(((String)thisSubTC.get(x -1)).indexOf("Act")!=-1){
////							System.out.println("HM " + lookupTCReturnSUBTCS((String)thisSubTC.get(x-1)));
////						}
//					}
//				selector = selector.substring(0, selector.length() - 1) + (new Integer(x+1)); 
//				probSelector = probSelector.substring(0, probSelector.length() - 1) + (new Integer(x+1)); 
//				}
//			}
//		
//		
//		TCROW = (new ProbTCRow(thisSubTC, thisSubTCProb));
//		System.out.println(tcQuery+" , "+TCROW.getTC());
//		for(int x = 0;x<thisSubTC.size();x=x+1){
//			if(((String)thisSubTC.get(x)).indexOf("Act")!=-1){
//			thisSubTC.addAll(lookupTCReturnSUBATOMICTCS((String)thisSubTC.get(x)));
////			TCROW.add(new ProbTCRow(thisSubTC, thisSubTCProb));
//			
//			thisSubTC.remove(x);
//			x = x-1;
//
//			}
//		}
//		
////		thisSubTC.addAll(thatSubTC);
//		
//		return thisSubTC;
//		
//	}
	
	
	public ProbTCRow lookupTCReturnSUBATOMICTCROW(String tcQuery){
		
		D2TxtFileItemProperties initTCRow = D2TxtFile.TCS.searchColumns("Treasure Class", tcQuery);
		String selector = "Item1";
		String probSelector = "Prob1";
		ArrayList thisSubTC = new ArrayList();
		ArrayList thisSubTCProb = new ArrayList();
		
		if(initTCRow != null){
			for(int x = 1;x<11;x=x+1){
				
//					if(initTCRow.get(selector).indexOf("Equip")!=-1 || initTCRow.get(selector).indexOf("weap")!=-1||initTCRow.get(selector).indexOf("armo")!=-1){
				if(!initTCRow.get(selector).equals("")){
						thisSubTC.add(initTCRow.get(selector));
						thisSubTCProb.add(new Double(Double.parseDouble(initTCRow.get(probSelector))));
						
//						if(((String)thisSubTC.get(x -1)).indexOf("Act")!=-1){
//							System.out.println("HM " + lookupTCReturnSUBTCS((String)thisSubTC.get(x-1)));
//						}
				}
//					}
				selector = selector.substring(0, selector.length() - 1) + (new Integer(x+1)); 
				probSelector = probSelector.substring(0, probSelector.length() - 1) + (new Integer(x+1)); 
				}
			}
		
		
			
		return new ProbTCRow(thisSubTC, thisSubTCProb)	;
		
	}
	
	
	public ArrayList removeDuplicates(ArrayList list){
		Set set = new HashSet();
		set.addAll(list);
		 
//		 avoid overhead :D
		if(set.size() < list.size()) {
		    list.clear();
		    list.addAll(set);
		}
		
		return list;
	}
	
	public int lookupAREAReturnLVL(String LevelName, String Difficulty){
		if(Difficulty.equals("NORMAL")){
		return Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", LevelName).get("MonLvl1Ex"));
		}else if (Difficulty.equals("NIGHTMARE")){
			return Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", LevelName).get("MonLvl2Ex"));
		}else{
			return Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", LevelName).get("MonLvl3Ex"));
		}
	}
	
	public ArrayList lookupMONTCReturnATOMICTCS(MonDiff monster){
//		String initTC =  "";
//		if(monster.getDiff().equals("NORMAL")){
//		 initTC = D2TxtFile.MONSTATS.searchColumns("Id", monster.getID()).get("TreasureClass1");
//		}else if (monster.getDiff().equals("NIGHTMARE")){
//			initTC = D2TxtFile.MONSTATS.searchColumns("Id", monster.getID()).get("TreasureClass1(N)");
//			initTC = bumpTC(initTC, monster);
//		}else{
//			initTC = D2TxtFile.MONSTATS.searchColumns("Id", monster.getID()).get("TreasureClass1(H)");
//			initTC = bumpTC(initTC, monster);
//		}
//		
//		return lookupBASETCReturnATOMICTCS();
		return monster.initTC;
	}
	
	private String bumpTC(String initTC, MonDiff monster) {
		
		
		return null;
		
	}

	public void lookupBASETCReturnATOMICTCS(MonDiff selectedMon, int nPlayers){
	
		nPlayers = 1;
		selectedMon.resetFinalArrays();
		for(int t = 0;t<selectedMon.getAreas().size();t=t+1){
		double counter = 1;
		ArrayList allTCS = new ArrayList();
		ArrayList atomicTCSWep = new ArrayList();
		ArrayList atomicTCSArm = new ArrayList();
		
		
		
//		System.out.println(tcQuery+" , "+TCROW.getTC());
		allTCS.add(lookupTCReturnSUBATOMICTCROW((String)selectedMon.getRealInitTC().get(t)));
		System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
		if(selectedMon.getType().equals("CHAMP") || selectedMon.getType().equals("UNIQ")|| selectedMon.getType().equals("SUPUNIQ") || selectedMon.getID().equals("griswold")){
		allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0))));
		System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
		}else if(selectedMon.getID().equals("duriel")){

			allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(1))));
			System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
			selectedMon.setInitTC(t, ((String)((ProbTCRow)allTCS.get(0)).getTC().get(1)));
			allTCS.remove(0);
			
			
		
		}
		
		if(selectedMon.getType().equals("CHAMP") || selectedMon.getType().equals("REG") ||selectedMon.getType().equals("BOSS") || selectedMon.getType().equals("SUPUNIQ") || selectedMon.getType().equals("MIN")){
			if(!selectedMon.getID().equals("griswold") && !selectedMon.getType().equals("SUPUNIQ")){
			allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(1))));
			System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
			}
			else{
				if(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0)).contains("gld")){
					allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(1))));
				}else{
				allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0))));
				}
				System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
			}
			if(selectedMon.getID().equals("duriel")){
//				allTCS.remove(0);
//				allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(1))));
//				System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
			}
			
			
		
		
		}else{
			allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0))));
			System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
		}
		
//		System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1));
		if(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip") !=-1){
		
		do{
			allTCS.add(lookupTCReturnSUBATOMICTCROW((String) ((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)));
			System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
//			System.out.println(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip"));
		}while(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip") !=-1);
		
		}
//		for(int x = 0;x<thisSubTC.size();x=x+1){
//			if(((String)thisSubTC.get(x)).indexOf("Act")!=-1){
//			thisSubTC.addAll(lookupTCReturnSUBATOMICTCS((String)thisSubTC.get(x)));
////			TCROW.add(new ProbTCRow(thisSubTC, thisSubTCProb));
//			
//			thisSubTC.remove(x);
//			x = x-1;
//0
//			}
//		}
		
//		thisSubTC.addAll(thatSubTC);
		
		if(nPlayers != 1){
			
		}
		int f = 0;
		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",(String)selectedMon.getRealInitTC().get(t)).get("Picks"));

		
		if(picks < 0){
			
			
				
				
			
			((ProbTCRow)allTCS.get(1)).setTotProb(((ProbTCRow)allTCS.get(1)).getTotProb()) ;
			constructTCPairs((ProbTCRow)allTCS.get(1));
			if(selectedMon.getType().equals("CHAMP") || selectedMon.getType().equals("REG") ){
			counter =  (((Double)((ProbTCRow)allTCS.get(1)).getProb().get(1)).doubleValue());
			}else{
				counter =  (((Double)((ProbTCRow)allTCS.get(1)).getProb().get(0)).doubleValue());
			}
			
			f=2;
		}else{
			System.out.println(Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class", (String)selectedMon.getRealInitTC().get(t)).get("NoDrop")));
			((ProbTCRow)allTCS.get(0)).setTotProb(((ProbTCRow)allTCS.get(0)).getTotProb() + Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class", (String)selectedMon.getRealInitTC().get(t)).get("NoDrop"))) ;
			constructTCPairs((ProbTCRow)allTCS.get(0));
			counter =  (((Double)((ProbTCRow)allTCS.get(0)).getProb().get(1)).doubleValue());
			f=1;
		}

		/**
		 * MODIFY FOR NODROP
		 */

		
		
		for(int x = f; x< allTCS.size();x=x+1){
			if(!((String)((ProbTCRow)allTCS.get(x)).getTC().get(0)).equals("gld")){
				constructTCPairs((ProbTCRow)allTCS.get(x));
				if(x == 4){
					System.out.println();
				}
				multiplyOut((ProbTCRow)allTCS.get(x), counter);
				if(((String)((ProbTCRow)allTCS.get(x)).getTC().get(((ProbTCRow)allTCS.get(x)).getTC().size() -1 )).indexOf("Equip") != -1){
					counter =  ((((Double)((ProbTCRow)allTCS.get(x)).getProb().get(((ProbTCRow)allTCS.get(x)).getProb().size() -1 ))).doubleValue());
				}
				
				
			}else{

				
			}
			
			
			
			}
		

//		

		
//			if(((String)allTCS.get(x)).indexOf("armo") != -1){
//				atomicTCSArm.add(allTCS.get(x));
//			}
//		}
		
//		System.out.println(atomicTCSWep.size()+atomicTCSArm.size());
//		atomicTCSWep=removeDuplicates(atomicTCSWep);
//		atomicTCSArm=removeDuplicates(atomicTCSArm);
//		System.out.println(atomicTCSWep.size()+atomicTCSArm.size());
//		atomicTCSWep.addAll(atomicTCSArm);
		
		selectedMon.setFinals(allTCS);
		System.out.println(selectedMon.getFinalProbs().get(0));
		/**
		 * APPLY PICKS...
		 * 
		 */
		
		
		
		if(picks > 1){
			if(picks > 6){
				picks = picks -1;
			}
				for(int y = 0;y< ((ArrayList)selectedMon.getFinalProbs().get(t)).size();y=y+1){
				((ArrayList)selectedMon.getFinalProbs().get(t)).set(y,new Double(1-(Math.pow((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()), picks))));
				}
//				System.out.println(((ArrayList)selectedMon.getFinalProbs().get(t)).get(y));
//				System.out.println((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()));
//				System.out.println(1-(Math.pow((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()), picks -1)));

		}
		
		if(picks == -4 && selectedMon.getType().equals("SUPUNIQ")){
			int pow = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",(String)selectedMon.getRealInitTC().get(t)).get("Prob1"));
			for(int y = 0;y< ((ArrayList)selectedMon.getFinalProbs().get(t)).size();y=y+1){
				((ArrayList)selectedMon.getFinalProbs().get(t)).set(y,new Double(1-(Math.pow((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()), pow ))));
				}
		}
		
		}
		/**
		 * 
		 */
		
//		return allTCS;
		
	}

	private void multiplyOut(ProbTCRow row, double counter) {
		for(int x = 0; x<row.getProb().size();x=x+1){
		  row.setProb(((Double)row.getProb().get(x)).doubleValue() *  counter , x);
		}
		
	}

	public ProbTCRow constructTCPairs(ProbTCRow row) {
		
//		int totalProb = sum(row.getProb());		
		
		ArrayList probs = new ArrayList();
		for(int x = 0;x<row.getTC().size();x=x+1){
			 
			for(int y = x;y<row.getTC().size();y=y+1){
			if(row.getTC().get(y).equals(row.getTC().get(x)) && y!=x){
				row.getTC().remove(y);
				row.getProb().set(x, new Double((((Double)row.getProb().get(x)).doubleValue())+ (((Double)row.getProb().get(y)).doubleValue())));
				row.getProb().remove(y);
			}
			}
			row.getProb().set(x, new Double((((Double)row.getProb().get(x)).doubleValue()) / row.getTotProb()));
		}
		
		System.out.println("By jove!");
		return(row);
	}

}

