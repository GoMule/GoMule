package DropCalcRefactored;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class CopyOfMonster {

	
	String monID;
	String monDiff;
	String monName;
	String classOfMon;
	ArrayList mTuples;
	String SUID;
	String minionBoss;
	
	public CopyOfMonster(String monID, String monDiff, String classOfMon){
		this.monID = monID;
		this.monDiff = monDiff;
		this.classOfMon = classOfMon;
		this.monName =D2TblFile.getString(D2TxtFile.MONSTATS.searchColumns("Id", this.monID).get("NameStr"));
		setUpTuples();
		
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
//		return initTC;
//	}

	public void enterMonLevel(HashMap monLvlAreas){

		Iterator it = monLvlAreas.keySet().iterator();


		while(it.hasNext()){

			String area = (String)it.next();

			if(monDiff.equals("NORMAL")){

				monLvlAreas.put(area, new Integer(D2TxtFile.MONSTATS.searchColumns("Id", monID).get("Level")));

			}else if (monDiff.equals("NIGHTMARE")){
				monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl2Ex"))) ;


			}else{
				monLvlAreas.put(area, new Integer(D2TxtFile.LEVELS.searchColumns("LevelName", area).get("MonLvl3Ex")));


			}

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

//	public HashMap getAreasLvl(){
//		return this.monLvlAreas;
//	}

//	public HashMap getFinalTCs(){
//		return this.finalTCs;
//	}

//	public void setFinalTCs(HashMap arr){
//		this.finalTCs.putAll( arr);
//	}

	private HashMap findLocsMonster(){
		ArrayList monSearch = new ArrayList();
		HashMap monLvlAreas = new HashMap();
		String selector = "mon1";

		if(monDiff.equals("NORMAL")){

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


	public void deleteDuplicated(ArrayList list, ArrayList probList)
	{
		for(int x = 0;x<list.size();x=x+1){
			while(list.lastIndexOf(list.get(x)) != x){

				probList.set(x, new Double(((Double)probList.get(list.lastIndexOf(list.get(x)))).doubleValue() + ((Double)probList.get(x)).doubleValue()));

				probList.remove(list.lastIndexOf(list.get(x)));
				list.remove(list.lastIndexOf(list.get(x)));
			}

			if(((String)list.get(x)).indexOf("Equip") != -1 || ((String)list.get(x)).indexOf("Act") != -1 ||((String)list.get(x)).indexOf("gld") != -1 ){
				probList.remove(x);
				list.remove(x);
				x=x-1;
			}

		}



	}

	public void setFinals(ArrayList allTCS) {



	}

//	public void setInitTC(int t, String string) {
//		initTC.set(t, string);		
//	}
//
//	public void resetFinalArrays() {
//		this.finalTCs.clear();
//		this.initTC = getInitTC();
//
//	}

	public void lookupBASETCReturnATOMICTCS(int nPlayers) {

		
		
		nPlayers = 1;
//		resetFinalArrays();
		for(int t = 0;t<mTuples.size();t=t+1){
			double counter = 1;
			ArrayList allTCS = new ArrayList();
			allTCS.add(lookupTCReturnSUBATOMICTCROW(
					((MonsterTuple)mTuples.get(t)).getInitTC()));


					if(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0)).contains("gld")){
						allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(1))));
					}else{
						allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0))));
					}






			if(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip") !=-1){

				do{
					allTCS.add(lookupTCReturnSUBATOMICTCROW((String) ((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)));
				}while(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip") !=-1);

			}


			if(nPlayers != 1){

			}
			int f = 0;
			int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",((MonsterTuple)mTuples.get(t)).getInitTC()).get("Picks"));


			if(picks < 0){





				((ProbTCRow)allTCS.get(1)).setTotProb(((ProbTCRow)allTCS.get(1)).getTotProb()) ;
				constructTCPairs((ProbTCRow)allTCS.get(1));
				counter =  (((Double)((ProbTCRow)allTCS.get(1)).getProb().get(1)).doubleValue());

				f=2;
			}else{
				((ProbTCRow)allTCS.get(0)).setTotProb(((ProbTCRow)allTCS.get(0)).getTotProb() + Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class", ((MonsterTuple)mTuples.get(t)).getInitTC()).get("NoDrop"))) ;
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
					}
					multiplyOut((ProbTCRow)allTCS.get(x), counter);
					if(((String)((ProbTCRow)allTCS.get(x)).getTC().get(((ProbTCRow)allTCS.get(x)).getTC().size() -1 )).indexOf("Equip") != -1){
						counter =  ((((Double)((ProbTCRow)allTCS.get(x)).getProb().get(((ProbTCRow)allTCS.get(x)).getProb().size() -1 ))).doubleValue());
					}


				}else{


				}



			}


//			for(int x = f; x< allTCS.size();x=x+1){
//				System.out.println(((ProbTCRow)allTCS.get(x)).getProb());
//			}


//			if(((String)allTCS.get(x)).indexOf("armo") != -1){
//			atomicTCSArm.add(allTCS.get(x));
//			}
//			}

//			System.out.println(atomicTCSWep.size()+atomicTCSArm.size());
//			atomicTCSWep=removeDuplicates(atomicTCSWep);
//			atomicTCSArm=removeDuplicates(atomicTCSArm);
//			System.out.println(atomicTCSWep.size()+atomicTCSArm.size());
//			atomicTCSWep.addAll(atomicTCSArm);
			((MonsterTuple)mTuples.get(t)).setFinalTCs(allTCS);
		
//			this.printFinals();
			/**
			 * APPLY PICKS...
			 * 
			 */



			if(picks > 1){
				if(picks > 6){
					picks = picks -1;
				}
				Iterator pickIt = ((MonsterTuple)mTuples.get(t)).getFinalTCs().keySet().iterator();
				while(pickIt.hasNext()){
					String pickItStr = (String) pickIt.next();
					((MonsterTuple)mTuples.get(t)).getFinalTCs().put(pickItStr,new Double(1-(Math.pow((1- ((Double)((MonsterTuple)mTuples.get(t)).getFinalTCs().get(pickItStr)).doubleValue()), picks))));
					
				}
//				}
//				System.out.println(((ArrayList)selectedMon.getFinalProbs().get(t)).get(y));
//				System.out.println((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()));
//				System.out.println(1-(Math.pow((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()), picks -1)));

			}
		}

	}
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
		
//		System.out.println("By jove!");
		return(row);
	}
	

	public void clearFinal() {
		
		for(int x = 0;x<mTuples.size();x=x+1){

			((MonsterTuple)mTuples.get(x)).getFinalTCs().clear();
			
		
	}


}
}



