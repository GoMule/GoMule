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
//		if(monID.equals("))
		this.monID = monID;
		this.monDiff = monDiff;
		this.classOfMon = classOfMon;

		if(classOfMon.equals("MIN")||classOfMon.equals("MINSEC")){

			if(D2TxtFile.SUPUNIQ.searchColumns("Name", monID) != null){
				
				if(D2TxtFile.MONSTATS.searchColumns("Id",D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get("Class")).get("boss").equals("1")){
					this.minionBoss = monID;
					setUpBossMinionTuples(D2TxtFile.MONSTATS.searchColumns("Id",D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get("Class")).get("minion1"));
					this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.monID).get("NameStr"));
				}else{
				this.minionBoss = monID;
				this.monID = D2TxtFile.SUPUNIQ.searchColumns("Name", monID).get("Class");	
				this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.monID).get("NameStr"));
				setUpTuples();
				}
			}else{
				if(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("minion1").equals( "")){
				this.minionBoss = monID;
				this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.monID).get("NameStr"));
				setUpTuples();
				}else{
					if(classOfMon.equals("MINSEC")){
						this.minionBoss = monID;
						this.classOfMon = "MIN";
						
						setUpMinionTuples(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("minion2"));
						this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.monID).get("NameStr"));
					}else{
				this.minionBoss = monID;
				
				setUpMinionTuples(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("minion1"));
				this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.monID).get("NameStr"));
					}
				}

			}




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

	private void setUpBossMinionTuples(String newID) {
		mTuples = new ArrayList();
		this.monID = this.monID.toLowerCase();
		this.classOfMon = "BOSS";
		HashMap areas = findLocsMonster();
		
		enterMonLevel(areas);
		this.classOfMon = "MIN";
		this.monID = newID;
		ArrayList initTCs = getInitTC(areas);

		mTuples = createTuples(areas, initTCs);

		
	}
	
	private void setUpMinionTuples(String newID) {
		mTuples = new ArrayList();

		HashMap areas = findLocsMonster();
		this.monID = newID;
		enterMonLevel(areas);
		ArrayList initTCs = getInitTC(areas);

		mTuples = createTuples(areas, initTCs);

		
	}

	private void setUpTuples() {
		mTuples = new ArrayList();

		HashMap areas = findLocsMonster();

		enterMonLevel(areas);
//		if(monID.equals("unraveler4") && classOfMon.equals("REG")){
//			System.out.println(areas);
//		}
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
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl2Ex"))+2)) ;
				}else if(classOfMon.equals("REG")){
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl2Ex"))) ;
				}else if(classOfMon.equals("UNIQ") || classOfMon.equals("SUPUNIQ") || classOfMon.equals("MIN")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl2Ex"))+3)) ;
				}
				else{
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl2Ex"))) ;
				}

			}else{

				if(classOfMon.equals("CHAMP")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl3Ex"))+2));
				}else if(classOfMon.equals("REG")){
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl3Ex")));
				}else if(classOfMon.equals("UNIQ")||classOfMon.equals("SUPUNIQ") || classOfMon.equals("MIN")){
					monLvlAreas.put(area, new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl3Ex"))+3));
				}else{
					monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl3Ex")));
				}

//				monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("Name", area).get("MonLvl3Ex")));

			}

		}
	}


	private void dealWithBoss(String choice, HashMap monLvlAreas) {

		if(choice.equals("AREA")){

			if(monID.equals("andariel")){
				monLvlAreas.put("Act 1 - Catacombs 4", new Integer(0));
				return;
			}else if(monID.equals("duriel")){
				monLvlAreas.put("Act 2 - Duriel's Lair", new Integer(0));
				return;
			}else if(monID.equals("radament")){
				monLvlAreas.put("Act 2 - Sewer 1 C", new Integer(0));
				return;
			}else if(monID.equals("mephisto")){
				monLvlAreas.put("Act 3 - Mephisto 3", new Integer(0));
				return;
			}else if(monID.equals("diablo")){
				monLvlAreas.put("Act 4 - Diablo 1", new Integer(0));
				return;
			}else if(monID.equals("summoner")){
				monLvlAreas.put("Act 2 - Arcane", new Integer(0));
				return;
			}else if(monID.equals("izual")){
				monLvlAreas.put("Act 4 - Mesa 2", new Integer(0));
				return;
			}else if(monID.equals("bloodraven")){
				monLvlAreas.put("Act 1 - Graveyard", new Integer(0));
				return;
			}else if(monID.equals("diabloclone")){
//				monLvlAreas.put("", new Integer(0));
				return;
			}else if(monID.equals("griswold")){
				monLvlAreas.put("Act 1 - Tristram", new Integer(0));
				return;
			}else if(monID.equals("nihlathakboss")){
				monLvlAreas.put("Act 5 - Temple Boss", new Integer(0));
				return;
			}else if(monID.equals("baalcrab")){
				monLvlAreas.put("Act 5 - World Stone", new Integer(0));
				return;
			}
		}else{

			String selector = "Level";
			if(monDiff.equals("HELL")){
				selector = selector + "(H)";   
			}else if(monDiff.equals("NIGHTMARE")){
				selector = selector + "(N)";   
			}
//			WHY DID I DO THIS? THIS IS WHY THE DEFILERS DON"T WORK.
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


		areaSUPair.put("Bishibosh", "Act 1 - Wilderness 2");
		areaSUPair.put("Bonebreak","Act 1 - Crypt 1 A");
		areaSUPair.put("Coldcrow","Act 1 - Cave 2");
		areaSUPair.put("Rakanishu","Act 1 - Wilderness 3");
		areaSUPair.put("Treehead WoodFist","Act 1 - Wilderness 4");
		areaSUPair.put("Griswold","Act 1 - Tristram");
		areaSUPair.put("The Countess","Act 1 - Crypt 3 E");
		areaSUPair.put("Pitspawn Fouldog","Act 1 - Jail 2");
		areaSUPair.put("Flamespike the Crawler","Act 1 - Moo Moo Farm");
		areaSUPair.put("Boneash","Act 1 - Cathedral");
		areaSUPair.put("Radament","Act 2 - Sewer 1 C");
		areaSUPair.put("Bloodwitch the Wild","Act 2 - Tomb 2 Treasure");
		areaSUPair.put("Fangskin","Act 2 - Tomb 3 Treasure");
		areaSUPair.put("Beetleburst","Act 2 - Desert 3");
		areaSUPair.put("Leatherarm","Act 2 - Tomb 1 Treasure");
		areaSUPair.put("Coldworm the Burrower","Act 2 - Lair 1 Treasure");
		areaSUPair.put("Fire Eye","Act 2 - Basement 3");
		areaSUPair.put("Dark Elder","Act 2 - Desert 4");
		areaSUPair.put("The Summoner","Act 2 - Arcane");
		areaSUPair.put("Ancient Kaa the Soulless","Act 2 - Tomb Tal");
		areaSUPair.put("The Smith","Act 1 - Barracks");
		areaSUPair.put("Web Mage the Burning","Act 3 - Spider 2");
		areaSUPair.put("Witch Doctor Endugu","Act 3 - Dungeon 2 Treasure");
		areaSUPair.put("Stormtree","Act 3 - Kurast 1");
		areaSUPair.put("Sarina the Battlemaid","Act 3 - Temple 1");
		areaSUPair.put("Icehawk Riftwing","Act 3 - Sewer 1");
		areaSUPair.put("Ismail Vilehand","Act 3 - Travincal");
		areaSUPair.put("Geleb Flamefinger","Act 3 - Travincal");
		areaSUPair.put("Bremm Sparkfist","Act 3 - Mephisto 3");
		areaSUPair.put("Toorc Icefist","Act 3 - Travincal");
		areaSUPair.put("Wyand Voidfinger","Act 3 - Mephisto 3");
		areaSUPair.put("Maffer Dragonhand","Act 3 - Mephisto 3");
		areaSUPair.put("Winged Death","Act 1 - Moo Moo Farm");
		areaSUPair.put("The Tormentor","Act 1 - Moo Moo Farm");
		areaSUPair.put("Taintbreeder","Act 1 - Moo Moo Farm");
		areaSUPair.put("Riftwraith the Cannibal","Act 1 - Moo Moo Farm");
		areaSUPair.put("Infector of Souls","Act 4 - Diablo 1");
		areaSUPair.put("Lord De Seis","Act 4 - Diablo 1");
		areaSUPair.put("Grand Vizier of Chaos","Act 4 - Diablo 1");
		areaSUPair.put("The Cow King","Act 1 - Moo Moo Farm");
		areaSUPair.put("Corpsefire","Act 1 - Cave 1");
		areaSUPair.put("The Feature Creep","Act 4 - Lava 1");
		areaSUPair.put("Siege Boss","Act 5 - Siege 1");
		areaSUPair.put("Ancient Barbarian 1","Act 5 - Mountain Top");
		areaSUPair.put("Ancient Barbarian 2","Act 5 - Mountain Top");
		areaSUPair.put("Ancient Barbarian 3","Act 5 - Mountain Top");
		areaSUPair.put("Axe Dweller","Act 1 - Moo Moo Farm");
		areaSUPair.put("Bonesaw Breaker","Act 5 - Ice Cave 2");
		areaSUPair.put("Dac Farren","Act 5 - Siege 1");
		areaSUPair.put("Megaflow Rectifier","Act 5 - Barricade 1");
		areaSUPair.put("Eyeback Unleashed","Act 5 - Barricade 1");
		areaSUPair.put("Threash Socket","Act 5 - Barricade 2");
		areaSUPair.put("Pindleskin","Act 5 - Temple Entrance");
		areaSUPair.put("Snapchip Shatter","Act 5 - Ice Cave 3A");
		areaSUPair.put("Anodized Elite","Act 1 - Moo Moo Farm");
		areaSUPair.put("Vinvear Molech","Act 1 - Moo Moo Farm");
		areaSUPair.put("Sharp Tooth Sayer","Act 5 - Barricade 1");
		areaSUPair.put("Magma Torquer","Act 1 - Moo Moo Farm");
		areaSUPair.put("Blaze Ripper","Act 1 - Moo Moo Farm");
		areaSUPair.put("Frozenstein","Act 5 - Ice Cave 1A");
		areaSUPair.put("Nihlathak","Act 5 - Temple Boss");
		areaSUPair.put("Baal Subject 1","Act 5 - Throne Room");
		areaSUPair.put("Baal Subject 2","Act 5 - Throne Room");
		areaSUPair.put("Baal Subject 3","Act 5 - Throne Room");
		areaSUPair.put("Baal Subject 4","Act 5 - Throne Room");
		areaSUPair.put("Baal Subject 5","Act 5 - Throne Room");

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
					monLvlAreas.put(((D2TxtFileItemProperties)monSearch.get(y)).get("Name"), new Integer(0));
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

	public HashMap getFinal() {

		HashMap finalList = new HashMap();
		for(int x = 0;x<mTuples.size();x=x+1){

			finalList.putAll(((MonsterTuple)mTuples.get(x)).getFinalTCs());


		}

		return finalList;


	}

	public String getBoss() {
		// TODO Auto-generated method stub
		return minionBoss;
	}
	
	public String getRealBossName(){
		if(D2TxtFile.SUPUNIQ.searchColumns("Name", minionBoss) == null){
			return D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.minionBoss).get("NameStr"));
		}
		return D2TblFile.getString( getBoss());
	}
}



