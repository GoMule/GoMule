package DropCalc;

import java.util.ArrayList;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class MonDiff {

	String monID;
	String monDiff;
	String monName;
	String classOfMon;
	ArrayList initTC = new ArrayList();
	ArrayList monAreas = new ArrayList();
	ArrayList levelArr = new ArrayList();
	ArrayList finalTCs = new ArrayList();
	ArrayList finalProbs = new ArrayList();

	public MonDiff(String monID, String monDiff, String classOfMon){
		this.monID = monID;
		this.monDiff = monDiff;
		this.classOfMon = classOfMon;
		if(!classOfMon.equals("SUPUNIQ")){
			this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("NameStr"));
			this.monAreas = findLocsMonster(monID);
			this.levelArr = lookupMONSTERReturnLVL();
			this.initTC = getInitTC();
		}else{
			this.monName =D2TblFile.getString(D2TxtFile.SUPUNIQ.searchColumns("Class", monID).get("Name"));
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
		}else if(classOfMon.equals("UNIQ") || classOfMon.equals("SUPUNIQ")){
			header = "TreasureClass3";
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
			}else if(classOfMon.equals("UNIQ")){
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level"))+3));
			}else{
				lvlArray.add(new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level")));
			}
		}else if (monDiff.equals("NIGHTMARE")){
			if(classOfMon.equals("CHAMP")){
				lvlArray.add(new Integer(Integer.parseInt(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl2Ex"))+2)) ;
			}else if(classOfMon.equals("REG")){
				lvlArray.add(new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", (String)monAreas.get(x)).get("MonLvl2Ex"))) ;
			}else if(classOfMon.equals("UNIQ")){
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
}else if(classOfMon.equals("UNIQ")){
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
		
		if(classOfMon.equals("BOSS")){
			locsOut =  dealWithBoss("AREA");
			if(locsOut.size() > 0){
				return locsOut;
			}
		}else if(classOfMon.equals("SUPUNIQ")){
			locsOut =  dealWithSU("AREA");
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
	
	private ArrayList dealWithSU(String choice) {


		ArrayList lvlArr = new ArrayList();
		ArrayList uniqIDs = new ArrayList();
		
		uniqIDs.add("fallenshaman1");
		uniqIDs.add("skeleton1");
		uniqIDs.add("cr_archer1");
		uniqIDs.add("fallen2");
		uniqIDs.add("brute2");
		uniqIDs.add("corruptrogue3");
		uniqIDs.add("bighead2");
		uniqIDs.add("quillrat4");
		uniqIDs.add("skmage_pois3");
		uniqIDs.add("pantherwoman1");
		uniqIDs.add("clawviper3");
		uniqIDs.add("scarab2");
		uniqIDs.add("mummy2");
		uniqIDs.add("maggotqueen1");
		uniqIDs.add("sandraider3");
		uniqIDs.add("zombie5");
		uniqIDs.add("unraveler3");
		uniqIDs.add("smith");
		uniqIDs.add("arach4");
		uniqIDs.add("fetishshaman4");
		uniqIDs.add("thornhulk3");
		uniqIDs.add("corruptrogue5");
		uniqIDs.add("batdemon3");
		uniqIDs.add("councilmember1");
		uniqIDs.add("councilmember2");
		uniqIDs.add("councilmember3");
		uniqIDs.add("councilmember1");
		uniqIDs.add("councilmember2");
		uniqIDs.add("councilmember3");
		uniqIDs.add("megademon3");
		uniqIDs.add("willowisp3");
		uniqIDs.add("vilemother2");
		uniqIDs.add("regurgitator2");
		uniqIDs.add("megademon3");
		uniqIDs.add("doomknight3");
		uniqIDs.add("fingermage3");
		uniqIDs.add("hellbovine");
		uniqIDs.add("zombie1");
		uniqIDs.add("hephasto");
		uniqIDs.add("overseer1");
		uniqIDs.add("ancientbarb1");
		uniqIDs.add("ancientbarb2");
		uniqIDs.add("ancientbarb3");
		uniqIDs.add("bloodlord3");
		uniqIDs.add("reanimatedhorde2");
		uniqIDs.add("imp3");
		uniqIDs.add("minion1");
		uniqIDs.add("deathmauler1");
		uniqIDs.add("siegebeast3");
		uniqIDs.add("reanimatedhorde5");
		uniqIDs.add("frozenhorror1");
		uniqIDs.add("succubus4");
		uniqIDs.add("succubuswitch2");
		uniqIDs.add("overseer3");
		uniqIDs.add("imp5");
		uniqIDs.add("deathmauler5");
		uniqIDs.add("snowyeti4");
		uniqIDs.add("fallenshaman5");
		uniqIDs.add("unraveler5");
		uniqIDs.add("baalhighpriest");
		uniqIDs.add("venomlord");
		uniqIDs.add("baalminion1");
		
		

		if(choice.equals("AREA")){

				
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
	
}
