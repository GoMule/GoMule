package DropCalc;

import java.util.ArrayList;
import java.util.HashMap;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class MonDiff {

	String monID;
	String monDiff;
	String monName;
	String classOfMon;
	String SUID;
	String minionBoss;
	ArrayList initTC = new ArrayList();
	ArrayList monAreas = new ArrayList();
	ArrayList levelArr = new ArrayList();
	ArrayList finalTCs = new ArrayList();
	ArrayList finalProbs = new ArrayList();

	public MonDiff(String monID, String monDiff, String classOfMon){
		this.monID = monID;
		this.monDiff = monDiff;
		this.classOfMon = classOfMon;
		if(classOfMon.equals("MIN")){
			
			if(D2TxtFile.SUPUNIQ.searchColumns("Name", monID) != null){
				this.minionBoss = monID;
				this.monID = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get("Class");	
			}else{
				this.minionBoss = monID;
			}
			
			this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.monID).get("NameStr"));
			this.monAreas = findLocsMonster(this.monID);
			this.levelArr = lookupMONSTERReturnLVL();
			this.initTC = getInitTC();
			
			
		}else if(!classOfMon.equals("SUPUNIQ")){
			this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("NameStr"));
			this.monAreas = findLocsMonster(monID);
			this.levelArr = lookupMONSTERReturnLVL();
			this.initTC = getInitTC();
		}else{
			this.SUID = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get("Class");
			this.monName =D2TblFile.getString(monID);
			this.monAreas = findLocsMonster(monID);
			this.levelArr = lookupMONSTERReturnLVL();
			this.initTC = getInitTC();
		}

	}
	
	private ArrayList getInitTC(){
		String initTC =  "";
		String header = "TreasureClass1";
		if(classOfMon.equals("CHAMP")){
			header = "TreasureClass2";
		}else if(classOfMon.equals("UNIQ")){
			header = "TreasureClass3";
		}else if(classOfMon.equals("SUPUNIQ")){
			header = "TC";
			ArrayList tcArr = new ArrayList();
			for(int x = 0;x<levelArr.size();x=x+1){
			
			if(this.getDiff().equals("NORMAL")){
			 initTC = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header);
			}else if (this.getDiff().equals("NIGHTMARE")){
				initTC = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header+"(N)");
				initTC = bumpTC(initTC, ((Integer)levelArr.get(x)).intValue());
			}else{
				initTC = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header+"(H)");
				initTC = bumpTC(initTC, ((Integer)levelArr.get(x)).intValue());
			}
			tcArr.add(initTC);
			}
			
			return tcArr;
		}
		ArrayList tcArr = new ArrayList();
		for(int x = 0;x<levelArr.size();x=x+1){
		
		if(this.getDiff().equals("NORMAL")){
		 initTC = D2TxtFile.MONSTATS.searchColumns("Id", monID).get(header);
		}else if (this.getDiff().equals("NIGHTMARE")){
			initTC = D2TxtFile.MONSTATS.searchColumns("Id", monID).get(header+"(N)");
			initTC = bumpTC(initTC, ((Integer)levelArr.get(x)).intValue());
		}else{
			initTC = D2TxtFile.MONSTATS.searchColumns("Id", monID).get(header+"(H)");
			initTC = bumpTC(initTC, ((Integer)levelArr.get(x)).intValue());
		}
		tcArr.add(initTC);
		}
		
		return tcArr;
	}
	
	
	private String bumpTC(String initTC, int lvl) {
		
		
		D2TxtFileItemProperties initTCRow = D2TxtFile.TCS.searchColumns("Treasure Class", initTC);
		if(initTCRow == null){
			return initTC;
		}
//		System.out.println(initTC);
		if(initTCRow.get("level").equals("") || Integer.parseInt(initTCRow.get("level")) > lvl){
			return initTC;
		}
			
		while(Integer.parseInt(initTCRow.get("level")) != 0){
//		System.out.println(initTCRow.getRowNum() + " " +initTCRow.get("level") + " " +initTCRow.get("Treasure Class"));
		if(Integer.parseInt(initTCRow.get("level"))<lvl){
			initTCRow = new D2TxtFileItemProperties(D2TxtFile.TCS, initTCRow.getRowNum()+1);
		}else{
			if(Integer.parseInt(initTCRow.get("level"))>lvl){
			return new D2TxtFileItemProperties(D2TxtFile.TCS, initTCRow.getRowNum()).get("Treasure Class");
			}else{
				return initTCRow.get("Treasure Class");
			}
		}
		if(initTCRow.get("level").equals("")){
			return initTCRow.get("Treasure Class");
		}
		}
//		System.out.println("ARGH");
		initTCRow = new D2TxtFileItemProperties(D2TxtFile.TCS, initTCRow.getRowNum()-1);
//		System.out.println("WIERD STUFF>>>> " + initTCRow.get("Treasure Class"));
		return initTCRow.get("Treasure Class");
	}
	
//private String bumpTC(String initTC, int lvl) {
//		
//		
//		D2TxtFileItemProperties initTCRow = D2TxtFile.TCS.searchColumns("Treasure Class", initTC);
////		System.out.println(initTC);
//		if(initTCRow.get("level").equals("") || Integer.parseInt(initTCRow.get("level")) > lvl){
//			return initTC;
//		}
//			
//		while(Integer.parseInt(initTCRow.get("level")) != 0){
////		System.out.println(initTCRow.getRowNum() + " " +initTCRow.get("level") + " " +initTCRow.get("Treasure Class"));
//		if(Integer.parseInt(initTCRow.get("level"))<lvl){
//			initTCRow = new D2TxtFileItemProperties(D2TxtFile.TCS, initTCRow.getRowNum()+1);
//		}else{
//			if(Integer.parseInt(initTCRow.get("level"))>lvl){
//			return new D2TxtFileItemProperties(D2TxtFile.TCS, initTCRow.getRowNum()-1).get("Treasure Class");
//			}else{
//				return initTCRow.get("Treasure Class");
//			}
//		}
//		if(initTCRow.get("level").equals("")){
//			return initTCRow.get("Treasure Class");
//		}
//		}
//		System.out.println("ARGH");
//		return "BLA";
//	}

	
	public ArrayList getRealInitTC(){
		return initTC;
	}

	public ArrayList lookupMONSTERReturnLVL(){
		ArrayList lvlArray = new ArrayList();
		
		if(classOfMon.equals("BOSS")){
			lvlArray= dealWithBoss("LEVEL");
			
			if(lvlArray.size() > 0){
				return lvlArray;
			}
			
		}
		
		
		for(int x = 0;x<monAreas.size();x=x+1){
		if(monDiff.equals("NORMAL")){
			if(classOfMon.equals("CHAMP")){
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level"))+2));
			}else if(classOfMon.equals("REG")){
				lvlArray.add(new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level")));
			}else if(classOfMon.equals("UNIQ") || classOfMon.equals("MIN")){
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level"))+3));
			}else if(classOfMon.equals("SUPUNIQ")){
				
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", SUID).get("Level"))+3));
			}
			else{
				lvlArray.add(new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level")));
			}
		}else if (monDiff.equals("NIGHTMARE")){
			if(classOfMon.equals("CHAMP")){
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl2Ex"))+2)) ;
			}else if(classOfMon.equals("REG")){
				lvlArray.add(new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl2Ex"))) ;
			}else if(classOfMon.equals("UNIQ") || classOfMon.equals("SUPUNIQ") || classOfMon.equals("MIN")){
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl2Ex"))+3)) ;
			}
			else{
				lvlArray.add(new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl2Ex"))) ;
				}
			
			
		}else{
			if(classOfMon.equals("CHAMP")){
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl3Ex"))+2));
}else if(classOfMon.equals("REG")){
				lvlArray.add(new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl3Ex")));
}else if(classOfMon.equals("UNIQ")||classOfMon.equals("SUPUNIQ") || classOfMon.equals("MIN")){
	lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl3Ex"))+3));
}else{
				lvlArray.add(new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl3Ex")));
}
			
					}
		}
		return lvlArray;
	}


	public String getDiff(){
		return this.monDiff;
	}
	
	public String getID(){
		return this.monID;
	}
	
	public String getType(){
		return this.classOfMon;
	}
	
	
	
	public String getName(){
		return this.monID + "(" +this.monDiff+")";
	}
	
	public String getRealName(){
		return this.monName;
	}
	
	public ArrayList getAreas(){
		return this.monAreas;
	}
	public ArrayList getLevels(){
		return this.levelArr;
	}
	
	public ArrayList getFinalTCs(){
		return this.finalTCs;
	}
	public ArrayList getFinalProbs(){
		return this.finalProbs;
	}
	
	public void setFinalTCs(ArrayList arr){
		this.finalTCs.add( arr);
	}
	public void setFinalProbs(ArrayList arr){
		this.finalProbs.add( arr);
	}
	
	private ArrayList findLocsMonster(String monStr){
		ArrayList locsOut = new ArrayList();
		
		if(classOfMon.equals("MIN")){
		if(D2TxtFile.SUPUNIQ.searchColumns("Name", minionBoss) != null){
			
			locsOut =  dealWithSU("AREA", 1);
			if(locsOut.size() > 0){
				return locsOut;
			}		
			
		}
		}else if(classOfMon.equals("BOSS")){
			locsOut =  dealWithBoss("AREA");
			if(locsOut.size() > 0){
				return locsOut;
			}
		}else if(classOfMon.equals("SUPUNIQ")){
			locsOut =  dealWithSU("AREA", 0);
			if(locsOut.size() > 0){
				return locsOut;
			}
		}
		
//		String monID = D2TxtFile.MONSTATS.searchColumns("NameStr", monStr).get("Id");
		ArrayList monSearch = new ArrayList();
		String selector = "mon1";
		
		if(monDiff.equals("NORMAL")){
			if(classOfMon.equals("CHAMP")){
				selector = "umon1";
			}
		}else{
			selector = "nmon1";
		}
		
		

		for(int x = 1;x<11;x=x+1){
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
	
	private ArrayList dealWithSU(String choice, int i) {


		ArrayList lvlArr = new ArrayList();
		ArrayList uniqIDs = new ArrayList();
		HashMap areaSUPair = new HashMap();

		
		

		if(choice.equals("AREA")){

			areaSUPair.put("Bishibosh", "Cold Plains");
			areaSUPair.put("Bonebreak","Crypt");
			areaSUPair.put("Coldcrow","Cave Level 1");
			areaSUPair.put("Rakanishu","Stony Field");
			areaSUPair.put("Treehead WoodFist","Dark Wood");
			areaSUPair.put("Griswold","Tristram");
			areaSUPair.put("The Countess","Tower Cellar Level 5");
			areaSUPair.put("Pitspawn Fouldog","Jail Level 2");
			areaSUPair.put("Flamespike the Crawler","Moo Moo Farm");
			areaSUPair.put("Boneash","Cathedral");
			areaSUPair.put("Radament","Sewers Level 3");
			areaSUPair.put("Bloodwitch the Wild","Halls of the Dead Level 3");
			areaSUPair.put("Fangskin","Claw Viper Temple Level 2");
			areaSUPair.put("Beetleburst","Far Oasis");
			areaSUPair.put("Leatherarm","Stony Tomb Level 2");
			areaSUPair.put("Coldworm the Burrower","Maggot Lair Level 3");
			areaSUPair.put("Fire Eye","Palace Cellar Level 3");
			areaSUPair.put("Dark Elder","Lost City");
			areaSUPair.put("The Summoner","Arcane Sanctuary");
			areaSUPair.put("Ancient Kaa the Soulless","Tal Rasha's Tomb");
			areaSUPair.put("The Smith","Barracks");
			areaSUPair.put("Web Mage the Burning","Spider Cavern");
			areaSUPair.put("Witch Doctor Endugu","Flayer Dungeon Level 3");
			areaSUPair.put("Stormtree","Lower Kurast");
			areaSUPair.put("Sarina the Battlemaid","Ruined Temple");
			areaSUPair.put("Icehawk Riftwing","Sewers Level 1");
			areaSUPair.put("Ismail Vilehand","Travincal");
			areaSUPair.put("Geleb Flamefinger","Travincal");
			areaSUPair.put("Bremm Sparkfist","Durance of Hate Level 3");
			areaSUPair.put("Toorc Icefist","Travincal");
			areaSUPair.put("Wyand Voidfinger","Durance of Hate Level 3");
			areaSUPair.put("Maffer Dragonhand","Durance of Hate Level 3");
			areaSUPair.put("Winged Death","Moo Moo Farm");
			areaSUPair.put("The Tormentor","Moo Moo Farm");
			areaSUPair.put("Taintbreeder","Moo Moo Farm");
			areaSUPair.put("Riftwraith the Cannibal","Moo Moo Farm");
			areaSUPair.put("Infector of Souls","Chaos Sanctum");
			areaSUPair.put("Lord De Seis","Chaos Sanctum");
			areaSUPair.put("Grand Vizier of Chaos","Chaos Sanctum");
			areaSUPair.put("The Cow King","Moo Moo Farm");
			areaSUPair.put("Corpsefire","Den of Evil");
			areaSUPair.put("The Feature Creep","River of Flame");
			areaSUPair.put("Siege Boss","Bloody Foothills");
			areaSUPair.put("Ancient Barbarian 1","Rocky Summit");
			areaSUPair.put("Ancient Barbarian 2","Rocky Summit");
			areaSUPair.put("Ancient Barbarian 3","Rocky Summit");
			areaSUPair.put("Axe Dweller","Moo Moo Farm");
			areaSUPair.put("Bonesaw Breaker","Crystalized Cavern Level 2");
			areaSUPair.put("Dac Farren","Bloody Foothills");
			areaSUPair.put("Megaflow Rectifier","Rigid Highlands");
			areaSUPair.put("Eyeback Unleashed","Rigid Highlands");
			areaSUPair.put("Threash Socket","Arreat Plateau");
			areaSUPair.put("Pindleskin","Nihlathaks Temple");
			areaSUPair.put("Snapchip Shatter","Glacial Caves Level 2");
			areaSUPair.put("Anodized Elite","Moo Moo Farm");
			areaSUPair.put("Vinvear Molech","Moo Moo Farm");
			areaSUPair.put("Sharp Tooth Sayer","Rigid Highlands");
			areaSUPair.put("Magma Torquer","Moo Moo Farm");
			areaSUPair.put("Blaze Ripper","Moo Moo Farm");
			areaSUPair.put("Frozenstein","Cellar of Pity");
			areaSUPair.put("Nihlathak","Halls of Vaught");
			areaSUPair.put("Baal Subject 1","Throne of Destruction");
			areaSUPair.put("Baal Subject 2","Throne of Destruction");
			areaSUPair.put("Baal Subject 3","Throne of Destruction");
			areaSUPair.put("Baal Subject 4","Throne of Destruction");
			areaSUPair.put("Baal Subject 5","Throne of Destruction");
				if(i==1){
					lvlArr.add(areaSUPair.get(this.minionBoss));
				}else{
			lvlArr.add(areaSUPair.get(this.monID));
				}
		}
		return lvlArr;
		
	}

	private ArrayList dealWithBoss(String choice) {
		   ArrayList lvlArr = new ArrayList();
		   
		   if(choice.equals("AREA")){
		   
		   if(monID.equals("andariel")){
			   lvlArr.add("Catacombs Level 4");
			   return lvlArr;
		   }else if(monID.equals("duriel")){
			   lvlArr.add("Duriel's Lair");
			   return lvlArr;
		   }else if(monID.equals("radament")){
			   lvlArr.add("Sewers Level 3");
			   return lvlArr;
		   }else if(monID.equals("mephisto")){
			   lvlArr.add("Durance of Hate Level 3");
			   return lvlArr;
		   }else if(monID.equals("diablo")){
			   lvlArr.add("Chaos Sanctuary");
			   return lvlArr;
		   }else if(monID.equals("summoner")){
			   lvlArr.add("Arcane Sanctuary");
			   return lvlArr;
		   }else if(monID.equals("izual")){
			   lvlArr.add("Plains of Despair");
			   return lvlArr;
		   }else if(monID.equals("bloodraven")){
			   lvlArr.add("Burial Grounds");
			   return lvlArr;
		   }else if(monID.equals("diabloclone")){
//			   lvlArr.add("");
			   return lvlArr;
		   }else if(monID.equals("griswold")){
			   lvlArr.add("Tristram");
			   return lvlArr;
		   }else if(monID.equals("nihlathakboss")){
			   lvlArr.add("Halls of Vaught");
			   return lvlArr;
		   }else if(monID.equals("baalcrab")){
			   lvlArr.add("The Worldstone Chamber");
			   return lvlArr;
		   }
		   }else{
			   
			   String selector = "Level";
			   if(monDiff.equals("HELL")){
				selector = selector + "(H)";   
			   }else if(monDiff.equals("NIGHTMARE")){
				   selector = selector + "(N)";   
			   }
			   
			   lvlArr.add(new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get(selector)));
			   return lvlArr;
			   
			  
		   }
		   
		   return lvlArr;
	}

	public void deleteDuplicated(ArrayList list, ArrayList probList)
	    {
		   for(int x = 0;x<list.size();x=x+1){
			   while(list.lastIndexOf(list.get(x)) != x){
//				   try{
				   probList.set(x, new Double(((Double)probList.get(list.lastIndexOf(list.get(x)))).doubleValue() + ((Double)probList.get(x)).doubleValue()));
//				   }catch(Exception e){
//					   System.out.println("OBJECTION!");
//					   System.out.println(x);
//					   System.out.println(probList.get(x));
//				   }
				   probList.remove(list.lastIndexOf(list.get(x)));
				   list.remove(list.lastIndexOf(list.get(x)));

				  
				   
			   }
			   
			   if(((String)list.get(x)).indexOf("Equip") != -1 || ((String)list.get(x)).indexOf("Act") != -1 ||((String)list.get(x)).indexOf("gld") != -1 ){
				   System.out.println();
				   probList.remove(x);
				   list.remove(x);
				   x=x-1;
			   }
			   
		   }
		   
		   
	
	    }

	public void setFinals(ArrayList allTCS) {
		
		
		
		ArrayList temp1 = new ArrayList();
		ArrayList temp2 = new ArrayList();
		for(int x = 0;x<allTCS.size();x=x+1){
			
		temp1.addAll(((ProbTCRow)allTCS.get(x)).getTC());
		temp2.addAll(((ProbTCRow)allTCS.get(x)).getProb());
		
		}
		
		finalTCs.add(temp1);
		finalProbs.add(temp2);
		deleteDuplicated((ArrayList)finalTCs.get(finalTCs.size() -1), (ArrayList)finalProbs.get(finalProbs.size() -1));
	}

	public void setInitTC(int t, String string) {
		initTC.set(t, string);		
	}

	public void resetFinalArrays() {
		this.finalProbs.clear();
		this.finalTCs.clear();
		this.initTC = getInitTC();
		
	}

	public String getBoss() {
		// TODO Auto-generated method stub
		return this.minionBoss;
	}
	
}
