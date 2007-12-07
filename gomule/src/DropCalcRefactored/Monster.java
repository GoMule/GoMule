package DropCalcRefactored;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class Monster {


	String monID;
	String monDiff;
	String monName;
	String classOfMon;
	ArrayList mTuples;
	String SUID;
	String minionBoss;

	public Monster(String monID, String monDiff, String classOfMon){
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
			setUpTuples();


		}else if(!classOfMon.equals("SUPUNIQ")){
			this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("NameStr"));
			setUpTuples();
		}else{
			this.SUID = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get("Class");
			this.monName =D2TblFile.getString(monID);
			setUpTuples();
		}




//		findLocsMonster(this.monID);
//		enterMonLevel();
//		this.initTC = getInitTC();

	}

	private void setUpTuples() {
		mTuples = new ArrayList();

		HashMap areas = findLocsMonster();
		enterMonLevel(areas);
		ArrayList initTCs = getInitTC(areas);

		mTuples = createTuples(areas, initTCs);

	}

	private ArrayList createTuples(HashMap areas, ArrayList initTCs) {

		ArrayList tOut = new ArrayList();
		Iterator it = areas.keySet().iterator();
		int counter = 0;
		while(it.hasNext()){
			String area = (String) it.next();
			tOut.add(new MonsterTuple(area, (Integer)areas.get(area), (String)initTCs.get(counter)));

			counter = counter +1;
		}

		return tOut;
	}

	private ArrayList getInitTC(HashMap monLvlAreas){
		String initTC =  "";
		String header = "TreasureClass1";

		if(classOfMon.equals("CHAMP")){
			header = "TreasureClass2";
		}else if(classOfMon.equals("UNIQ")){
			header = "TreasureClass3";
		}else if(classOfMon.equals("SUPUNIQ")){

			header = "TC";
			ArrayList tcArr = new ArrayList();
			Iterator it = monLvlAreas.keySet().iterator();
			while(it.hasNext()){
				String area = (String)it.next();
				if(this.getDiff().equals("NORMAL")){
					initTC = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header);
				}else if (this.getDiff().equals("NIGHTMARE")){
					initTC = bumpTC(D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header+"(N)"), ((Integer)monLvlAreas.get(area)).intValue());
				}else{
					initTC = bumpTC(D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get(header+"(H)"), ((Integer)monLvlAreas.get(area)).intValue());
				}
				tcArr.add(initTC);
			}

			return tcArr;

		}

		ArrayList tcArr = new ArrayList();
		Iterator it = monLvlAreas.keySet().iterator();
		while(it.hasNext()){
			String area = (String)it.next();
			if(this.getDiff().equals("NORMAL")){
				initTC = D2TxtFile.MONSTATS.searchColumns("Id", monID).get(header);
			}else if (this.getDiff().equals("NIGHTMARE")){
				initTC = bumpTC(D2TxtFile.MONSTATS.searchColumns("Id", monID).get(header+"(N)"), ((Integer)monLvlAreas.get(area)).intValue());
			}else{
				initTC = bumpTC(D2TxtFile.MONSTATS.searchColumns("Id", monID).get(header+"(H)"), ((Integer)monLvlAreas.get(area)).intValue());
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
		if(initTCRow.get("level").equals("") || Integer.parseInt(initTCRow.get("level")) > lvl){
			return initTC;
		}

		while(Integer.parseInt(initTCRow.get("level")) != 0){
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
		initTCRow = new D2TxtFileItemProperties(D2TxtFile.TCS, initTCRow.getRowNum()-1);
		return initTCRow.get("Treasure Class");
	}

//	public ArrayList getRealInitTC(){
//	return initTC;
//	}



	public void enterMonLevel(HashMap monLvlAreas){

		if(classOfMon.equals("BOSS")){
			dealWithBoss("LEVEL", monLvlAreas);
			return;
		}
			
		
		Iterator it = monLvlAreas.keySet().iterator();
		while(it.hasNext()){

			String area = (String)it.next();

			if(monDiff.equals("NORMAL")){
				
				if(classOfMon.equals("CHAMP")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level"))+2));
				}else if(classOfMon.equals("REG")){
					monLvlAreas.put(area, new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level")));
				}else if(classOfMon.equals("UNIQ") || classOfMon.equals("MIN")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level"))+3));
				}else if(classOfMon.equals("SUPUNIQ")){

					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", SUID).get("Level"))+3));
				}
				else{
					monLvlAreas.put(area, new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level")));
				}
				
			}else if (monDiff.equals("NIGHTMARE")){
				
				if(classOfMon.equals("CHAMP")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl2Ex"))+2)) ;
				}else if(classOfMon.equals("REG")){
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl2Ex"))) ;
				}else if(classOfMon.equals("UNIQ") || classOfMon.equals("SUPUNIQ") || classOfMon.equals("MIN")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl2Ex"))+3)) ;
				}
				else{
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl2Ex"))) ;
				}
				
			}else{
				
				if(classOfMon.equals("CHAMP")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl3Ex"))+2));
				}else if(classOfMon.equals("REG")){
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl3Ex")));
				}else if(classOfMon.equals("UNIQ")||classOfMon.equals("SUPUNIQ") || classOfMon.equals("MIN")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl3Ex"))+3));
				}else{
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl3Ex")));
				}
				
				monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl3Ex")));

			}

		}
	}


	private void dealWithBoss(String choice, HashMap monLvlAreas) {

		if(choice.equals("AREA")){

			if(monID.equals("andariel")){
				monLvlAreas.put("Catacombs Level 4", new Integer(0));
				return;
			}else if(monID.equals("duriel")){
				monLvlAreas.put("Duriel's Lair", new Integer(0));
				return;
			}else if(monID.equals("radament")){
				monLvlAreas.put("Sewers Level 3", new Integer(0));
				return;
			}else if(monID.equals("mephisto")){
				monLvlAreas.put("Durance of Hate Level 3", new Integer(0));
				return;
			}else if(monID.equals("diablo")){
				monLvlAreas.put("Chaos Sanctuary", new Integer(0));
				return;
			}else if(monID.equals("summoner")){
				monLvlAreas.put("Arcane Sanctuary", new Integer(0));
				return;
			}else if(monID.equals("izual")){
				monLvlAreas.put("Plains of Despair", new Integer(0));
				return;
			}else if(monID.equals("bloodraven")){
				monLvlAreas.put("Burial Grounds", new Integer(0));
				return;
			}else if(monID.equals("diabloclone")){
//				monLvlAreas.put("", new Integer(0));
				return;
			}else if(monID.equals("griswold")){
				monLvlAreas.put("Tristram", new Integer(0));
				return;
			}else if(monID.equals("nihlathakboss")){
				monLvlAreas.put("Halls of Vaught", new Integer(0));
				return;
			}else if(monID.equals("baalcrab")){
				monLvlAreas.put("The Worldstone Chamber", new Integer(0));
				return;
			}
		}else{

			String selector = "Level";
			if(monDiff.equals("HELL")){
				selector = selector + "(H)";   
			}else if(monDiff.equals("NIGHTMARE")){
				selector = selector + "(N)";   
			}
			if(!monID.equals("diabloclone")&&!monID.equals("putriddefiler1")&&!monID.equals("putriddefiler2")&&!monID.equals("putriddefiler3")&&!monID.equals("putriddefiler4")&&!monID.equals("putriddefiler5")){
			monLvlAreas.put(monLvlAreas.keySet().toArray()[0],new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get(selector)));
			}
			return;


		}
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

	private void dealWithSU(int i, HashMap lvlArr) {


		HashMap areaSUPair = new HashMap();


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
				lvlArr.put(areaSUPair.get(this.minionBoss), new Integer(0));
			}else{
				lvlArr.put(areaSUPair.get(this.monID), new Integer(0));
			
		}
	}
	
	private HashMap findLocsMonster(){
		ArrayList monSearch = new ArrayList();
		HashMap monLvlAreas = new HashMap();
		
		
		if(classOfMon.equals("MIN")){
			if(D2TxtFile.SUPUNIQ.searchColumns("Name", minionBoss) != null){

				dealWithSU(1, monLvlAreas);
					return monLvlAreas;

			}
		}else if(classOfMon.equals("BOSS")){
			dealWithBoss("AREA", monLvlAreas);
				return monLvlAreas;
				
		}else if(classOfMon.equals("SUPUNIQ")){
			dealWithSU(0,monLvlAreas);
				return monLvlAreas;
		}

		
		
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
			monSearch = D2TxtFile.LEVELS.searchColumnsMultipleHits(selector, monID);
			if(monSearch.size() > 0){
				for(int y = 0;y<monSearch.size();y=y+1){
					monLvlAreas.put(((D2TxtFileItemProperties)monSearch.get(y)).get("LevelName"), new Integer(0));
				}
			}
			selector = selector.substring(0, selector.length() - 1) + (new Integer(x+1)); 		
		}

		return monLvlAreas;
	}
	
	
	public void lookupBASETCReturnATOMICTCS(int nPlayers, int nGroupSize) {



//		nPlayers = 1;
//		resetFinalArrays();
		for(int t = 0;t<mTuples.size();t=t+1){

			((MonsterTuple)mTuples.get(t)).lookupBASETCReturnATOMICTCS(this, nPlayers, nGroupSize);
			
		}

	}



	public void clearFinal() {

		for(int x = 0;x<mTuples.size();x=x+1){

			((MonsterTuple)mTuples.get(x)).getFinalTCs().clear();


		}


	}

	public String getBoss() {
		// TODO Auto-generated method stub
		return minionBoss;
	}
}



