package gomule.dropCalc.monsters;

import gomule.dropCalc.DCNew;
import gomule.dropCalc.ProbTCRow;
import gomule.dropCalc.items.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import randall.d2files.D2TblFile;
import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class MonsterTuple {

	Monster mParent;
	String AreaName;
	String RealAreaName;
	int Level;
	String initTC;
	String calcd = "";
	int mUqual;
	int mSqual;

	//SO FOR EVERY INIT TC THERE IS A SETOF FINAL TCS MAPPED TO A SET OF FINAL TC PROBs
	HashMap finalTCs = new HashMap();
	HashMap finalMiscTCs = new HashMap();
	private int mPicks;
	private String calcdM = "";

	public MonsterTuple(String AreaName, Integer level, String initTC, Monster mParent){
		this.mParent = mParent;
		this.AreaName = AreaName;
		this.Level = level.intValue();
		this.initTC = initTC;
		this.RealAreaName = D2TblFile.getString(D2TxtFile.LEVELS.searchColumns("Name",AreaName).get("LevelName"));
		setQualities();
	}

	private void setQualities() {


		D2TxtFileItemProperties qRow = D2TxtFile.TCS.searchColumns("Treasure Class", initTC);

		if(qRow != null){

			if(qRow.get("Unique").equals("")){
				mUqual = 0;
			}

			if(qRow.get("Set").equals("")){
				mSqual = 0;
			}

		}else{
			mUqual = 0;
			mSqual = 0;
		}
	}

	public int getUqual() {
		return mUqual;
	}

	public String getInitTC() {
		return initTC;
	}

	public void setFinalTCs(ArrayList allTCS) {



		allTCS = deleteDuplicated(allTCS);

		for(int x = 0;x<allTCS.size();x=x+1){
			for(int y = 0;y<((ProbTCRow)allTCS.get(x)).getTC().size();y=y+1){
				if(finalTCs.containsKey(((ProbTCRow)allTCS.get(x)).getTC().get(y))){
					finalTCs.put(((ProbTCRow)allTCS.get(x)).getTC().get(y), new Double(((Double)finalTCs.get(((ProbTCRow)allTCS.get(x)).getTC().get(y))).doubleValue() + ((Double)((ProbTCRow)allTCS.get(x)).getProb().get(y)).doubleValue()));
				}else{
					finalTCs.put(((ProbTCRow)allTCS.get(x)).getTC().get(y), ((ProbTCRow)allTCS.get(x)).getProb().get(y));
				}
			}
		}		
	}
	
	public void setFinalTrueMiscTCs(ArrayList allTCS) {



		allTCS = deleteMiscDuplicated(allTCS);

		for(int x = 0;x<allTCS.size();x=x+1){
			for(int y = 0;y<((ProbTCRow)allTCS.get(x)).getTC().size();y=y+1){
				if(finalTCs.containsKey(((ProbTCRow)allTCS.get(x)).getTC().get(y))){
					finalTCs.put(((ProbTCRow)allTCS.get(x)).getTC().get(y), new Double(((Double)finalTCs.get(((ProbTCRow)allTCS.get(x)).getTC().get(y))).doubleValue() + ((Double)((ProbTCRow)allTCS.get(x)).getProb().get(y)).doubleValue()));
				}else{
					finalTCs.put(((ProbTCRow)allTCS.get(x)).getTC().get(y), ((ProbTCRow)allTCS.get(x)).getProb().get(y));
				}
			}
		}		
	}

	public void setFinalMiscTCs(ArrayList miscTCS) {

		for(int x = 0;x<miscTCS.size();x=x+1){
			for(int y = 0;y<((ProbTCRow)miscTCS.get(x)).getTC().size();y=y+1){
//				if(((ProbTCRow)miscTCS.get(x)).getTC().get(y).equals("rin") || ((ProbTCRow)miscTCS.get(x)).getTC().get(y).equals("amu")|| ((ProbTCRow)miscTCS.get(x)).getTC().get(y).equals("jew")){
					finalMiscTCs.put(((ProbTCRow)miscTCS.get(x)).getTC().get(y), ((ProbTCRow)miscTCS.get(x)).getProb().get(y));
//				}
			}
		}

	}
	public ArrayList deleteDuplicated(ArrayList allTCS)
	{

		for(int x = 0;x<allTCS.size();x=x+1){

			ProbTCRow dup = (ProbTCRow) allTCS.get(x);
			for(int y = 0;y<dup.getTC().size();y=y+1){
				if(((String)dup.getTC().get(y)).contains("Equip") || ((String)dup.getTC().get(y)).contains("Act")||((String)dup.getTC().get(y)).contains("gld")){
					dup.getTC().remove(dup.getTC().get(y));
					dup.getProb().remove(y);
					y=y-1;
				}
			}
		}

		return allTCS;

	}
	public ArrayList deleteMiscDuplicated(ArrayList allTCS)
	{

		for(int x = 0;x<allTCS.size();x=x+1){

			ProbTCRow dup = (ProbTCRow) allTCS.get(x);
			for(int y = 0;y<dup.getTC().size();y=y+1){
				if(((String)dup.getTC().get(y)).contains("Runes") || ((String)dup.getTC().get(y)).contains("Act")||((String)dup.getTC().get(y)).contains("gld")){
					dup.getTC().remove(dup.getTC().get(y));
					dup.getProb().remove(y);
					y=y-1;
				}
			}
		}

		return allTCS;

	}
	public HashMap getFinalTCs() {

		return finalTCs;
	}

	public void printFinalTCs() {

		System.out.println(finalTCs);

	}

	public ProbTCRow lookupTCReturnSUBATOMICTCROW(String tcQuery){



		D2TxtFileItemProperties initTCRow = D2TxtFile.TCS.searchColumns("Treasure Class", tcQuery);
		String selector = "Item1";
		String probSelector = "Prob1";
		ArrayList thisSubTC = new ArrayList();
		ArrayList thisSubTCProb = new ArrayList();

		if(initTCRow != null){
			for(int x = 1;x<11;x=x+1){

//				if(initTCRow.get(selector).indexOf("Equip")!=-1 || initTCRow.get(selector).indexOf("weap")!=-1||initTCRow.get(selector).indexOf("armo")!=-1){
				if(!initTCRow.get(selector).equals("")){
					thisSubTC.add(initTCRow.get(selector));
					thisSubTCProb.add(new Double(Double.parseDouble(initTCRow.get(probSelector))));

//					if(((String)thisSubTC.get(x -1)).indexOf("Act")!=-1){
//					System.out.println("HM " + lookupTCReturnSUBTCS((String)thisSubTC.get(x-1)));
//					}
				}
//				}
				selector = selector.substring(0, selector.length() - 1) + (new Integer(x+1)); 
				probSelector = probSelector.substring(0, probSelector.length() - 1) + (new Integer(x+1)); 
			}
			if(!initTCRow.get("Unique").equals("")){
				if(Integer.parseInt(initTCRow.get("Unique")) > mUqual){
				mUqual = Integer.parseInt(initTCRow.get("Unique"));
				}
			}

			if(!initTCRow.get("Set").equals("")){
				if(Integer.parseInt(initTCRow.get("Set")) > mSqual){
				mSqual = Integer.parseInt(initTCRow.get("Set"));
				}
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


	public ProbTCRow getLastRow(ArrayList arr){

		return ((ProbTCRow)arr.get(arr.size()-1));

	}
	
	public void lookupBASETCReturnTrueMiscTCS(int nPlayers, int nplayersParty, double input, Item item, DCNew DC, int MF) {


		if(calcdM.equals(nPlayers+","+nplayersParty+","+input)){
//			return;
		}
		

		mParent.clearFinalM(this);

		double miscCounter = 1;
		int keyRow = -1;
		ArrayList miscTCS = new ArrayList();

		if(mParent.getID().equals("duriel")){
			if(!getInitTC().contains("Base")){
				miscTCS.add(lookupTCReturnSUBATOMICTCROW(this.getInitTC()));
				setInitTC((String)getLastRow(miscTCS).getTC().get(1));
				miscTCS.clear();
			}
		}
		miscTCS.add(lookupTCReturnSUBATOMICTCROW(this.getInitTC()));

		if(mParent.getClassOfMon() == 2 || mParent.getClassOfMon() == 3|| this.getInitTC().contains("Champ")|| this.getInitTC().contains("Super")|| this.getInitTC().contains("Unique") || ((String)getLastRow(miscTCS).getTC().get(0)).contains("Uitem")){
			miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(0))));
		}
		
		for(int x = 0;x<getLastRow(miscTCS).getTC().size();x++){
			if(((String)getLastRow(miscTCS).getTC().get(x)).contains("Good")){
//				System.out.println(x);
				keyRow = x;
				
			}
		}
		if(keyRow == -1){
			System.err.println("ERROR OCCURING KEYROW1? - " + mParent.getRealName());
			
			calcdM = nPlayers+","+nplayersParty+","+input;
			return;
		}
		
		miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(keyRow))));

//		if(getLastRow(miscTCS).getTC().size()==2){
//			miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(0))));
//		}else{
//			miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(3))));
//		}
		
		if(mParent.getMonID().contains("batdemon2")){
			
			if(((String)getLastRow(miscTCS).getTC().get(getLastRow(miscTCS).getTC().size()-1)).contains("Runes") && ((String)getLastRow(miscTCS).getTC().get(getLastRow(miscTCS).getTC().size()-2)).contains("Runes")){
				ArrayList runesArr1 = new ArrayList();
				runesArr1.addAll(miscTCS);
				ArrayList runesArr2 = new ArrayList();
				runesArr2.addAll(miscTCS);
				
				parseRuneTree(runesArr1,getLastRow(miscTCS).getTC().size()-1, keyRow);
				parseRuneTree(runesArr2,getLastRow(miscTCS).getTC().size()-2, keyRow);
			}
		}


		

		this.setFinalTrueMiscTCs(miscTCS);
		/**
		 * APPLY PICKS...
		 * 
		 */
		

//		if(picks < 0 ||picks >1){
//			if(input != 0){
//			if(mParent.getClassOfMon()!= 4 && mParent.getClassOfMon()!=5){
//			System.out.println("RWAR");
//			
//			}
//			
//			if(mParent.getClassOfMon() == 1 ||mParent.getClassOfMon() == 4 ||mParent.getClassOfMon() == 5 ){
//				
//				input = input * DC.getQuality(item, getLevel(), MF,this, item.getiNUS() - 1);
//			}
//			
//			applyMPicks(input);
//			}else{
//				applyMPicks();
//			}
//			}

		calcdM = nPlayers+","+nplayersParty+","+input;
		
	}

	private void parseRuneTree(ArrayList runesArr, int key, int keyRow) {
		
		
		double miscCounter = 1;

		runesArr.add(lookupTCReturnSUBATOMICTCROW((String) getLastRow(runesArr).getTC().get(key)));
		
		if(((String)getLastRow(runesArr).getTC().get(getLastRow(runesArr).getTC().size() - 1)).indexOf("Runes") !=-1){

			do{
				runesArr.add(lookupTCReturnSUBATOMICTCROW((String) getLastRow(runesArr).getTC().get(getLastRow(runesArr).getTC().size() - 1)));
			}while(((String)getLastRow(runesArr).getTC().get(getLastRow(runesArr).getTC().size() - 1)).indexOf("Runes") !=-1);

		}

		System.out.println((String) getLastRow(runesArr).getTC().get(0));

		int f = 0;
		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));
		mPicks  = picks;
		if(picks < 0){

			if(mParent.getClassOfMon()==2 || mParent.getClassOfMon()==3|| mParent.getClassOfMon()==4){
				constructTCPairs((ProbTCRow)runesArr.get(1));
			miscCounter =  (((Double)((ProbTCRow)runesArr.get(1)).getProb().get(keyRow)).doubleValue());
			}
			f=2;
		}else{

			int noDrop = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class", this.getInitTC()).get("NoDrop"));
//			if(nPlayers>1){
//			double realnP = Math.floor(1 + ((double)(nPlayers -1)/(double)2) + ((double)(nplayersParty -1)/(double)2));
//			int probSum = ((ProbTCRow)allTCS.get(0)).getTotProb();
//			if(noDrop != 0){
//			noDrop = ( (int)Math.floor(((double)probSum)/(BigDecimal.valueOf(((((double)1)/(Math.pow((((double)noDrop)/((double)(noDrop + probSum))),realnP))))).subtract ((BigDecimal.valueOf(1)))).doubleValue()));
//			}
//			}
			((ProbTCRow)runesArr.get(0)).setTotProb(((ProbTCRow)runesArr.get(0)).getTotProb() + noDrop) ;

			constructTCPairs((ProbTCRow)runesArr.get(0));


			if(((ProbTCRow)runesArr.get(0)).getTC().size()==2){
				miscCounter =  (((Double)((ProbTCRow)runesArr.get(0)).getProb().get(0)).doubleValue());
			}else{

				miscCounter =  (((Double)((ProbTCRow)runesArr.get(0)).getProb().get(keyRow)).doubleValue());
			}
			
			if(mParent.getClassOfMon()==5 && ((Boss)mParent).getQuest()){
				miscCounter =  (((Double)((ProbTCRow)runesArr.get(0)).getProb().get(keyRow)).doubleValue());
			}

			f=1;
		}


		
		
//		if()
		
		for(int x = f; x< runesArr.size();x=x+1){

			

		}
		
		
	}

	public void lookupBASETCReturnMiscTCS(int nPlayers, int nplayersParty, double input, Item item, DCNew DC, int MF) {

		if(calcdM.equals(nPlayers+","+nplayersParty+","+input)){
			return;
		}
		

		mParent.clearFinalM(this);

		double miscCounter = 1;
		int keyRow = -1;
		ArrayList miscTCS = new ArrayList();

		if(mParent.getID().equals("duriel")){
			if(!getInitTC().contains("Base")){
				miscTCS.add(lookupTCReturnSUBATOMICTCROW(this.getInitTC()));
				setInitTC((String)getLastRow(miscTCS).getTC().get(1));
				miscTCS.clear();
			}
		}
		miscTCS.add(lookupTCReturnSUBATOMICTCROW(this.getInitTC()));

		if(mParent.getClassOfMon() == 2 || mParent.getClassOfMon() == 3|| this.getInitTC().contains("Champ")|| this.getInitTC().contains("Super")|| this.getInitTC().contains("Unique") || ((String)getLastRow(miscTCS).getTC().get(0)).contains("Uitem")){
			miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(0))));
		}
		
		for(int x = 0;x<getLastRow(miscTCS).getTC().size();x++){
			if(((String)getLastRow(miscTCS).getTC().get(x)).contains("Good")){
//				System.out.println(x);
				keyRow = x;
				
			}
		}
		if(keyRow == -1){
			System.err.println("ERROR OCCURING KEYROW1? - " + mParent.getRealName());
			
			calcdM = nPlayers+","+nplayersParty+","+input;
			return;
		}
		
		miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(keyRow))));

//		if(getLastRow(miscTCS).getTC().size()==2){
//			miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(0))));
//		}else{
//			miscTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(miscTCS).getTC().get(3))));
//		}


		if(((String)getLastRow(miscTCS).getTC().get(0)).indexOf("Jewelry") !=-1){

			miscTCS.add(lookupTCReturnSUBATOMICTCROW((String) getLastRow(miscTCS).getTC().get(0)));

		}else{
			System.err.println("Err?");
		}

//		System.out.println((String) getLastRow(miscTCS).getTC().get(0));

		int f = 0;
		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));
		mPicks  = picks;
		if(picks < 0){

//			((ProbTCRow)miscTCS.get(1)).setTotProb(((ProbTCRow)miscTCS.get(1)).getTotProb()) ;
//			constructTCPairs((ProbTCRow)miscTCS.get(1));
//			if(mParent.getClassOfMon()==2 || mParent.getClassOfMon()==1 ){
//				miscCounter =  (((Double)((ProbTCRow)miscTCS.get(1)).getProb().get(1)).doubleValue());
//			}else{
//				miscCounter =  (((Double)((ProbTCRow)miscTCS.get(1)).getProb().get(0)).doubleValue());
//			}
			if(mParent.getClassOfMon()==2 || mParent.getClassOfMon()==3|| mParent.getClassOfMon()==4){
//				((ProbTCRow)miscTCS.get(1)).setTotProb(((ProbTCRow)miscTCS.get(1)).getTotProb()) ;
				constructTCPairs((ProbTCRow)miscTCS.get(1));
			miscCounter =  (((Double)((ProbTCRow)miscTCS.get(1)).getProb().get(keyRow)).doubleValue());
			}
			f=2;
		}else{

			int noDrop = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class", this.getInitTC()).get("NoDrop"));
//			if(nPlayers>1){
//			double realnP = Math.floor(1 + ((double)(nPlayers -1)/(double)2) + ((double)(nplayersParty -1)/(double)2));
//			int probSum = ((ProbTCRow)allTCS.get(0)).getTotProb();
//			if(noDrop != 0){
//			noDrop = ( (int)Math.floor(((double)probSum)/(BigDecimal.valueOf(((((double)1)/(Math.pow((((double)noDrop)/((double)(noDrop + probSum))),realnP))))).subtract ((BigDecimal.valueOf(1)))).doubleValue()));
//			}
//			}
			((ProbTCRow)miscTCS.get(0)).setTotProb(((ProbTCRow)miscTCS.get(0)).getTotProb() + noDrop) ;

			constructTCPairs((ProbTCRow)miscTCS.get(0));


			if(((ProbTCRow)miscTCS.get(0)).getTC().size()==2){
				miscCounter =  (((Double)((ProbTCRow)miscTCS.get(0)).getProb().get(0)).doubleValue());
			}else{

				miscCounter =  (((Double)((ProbTCRow)miscTCS.get(0)).getProb().get(keyRow)).doubleValue());
			}
			
			if(mParent.getClassOfMon()==5 && ((Boss)mParent).getQuest()){
				miscCounter =  (((Double)((ProbTCRow)miscTCS.get(0)).getProb().get(keyRow)).doubleValue());
			}

			f=1;
		}

		for(int x = f; x< miscTCS.size();x=x+1){

			if(!((String)((ProbTCRow)miscTCS.get(x)).getTC().get(0)).equals("gld")){
				constructTCPairs((ProbTCRow)miscTCS.get(x));
				multiplyOut((ProbTCRow)miscTCS.get(x), miscCounter);
//				if(((String)getLastRow(miscTCS).getTC().get(0)).indexOf("Jewelry") !=-1){
				if(((String)((ProbTCRow)miscTCS.get(x)).getTC().get(0)).contains("Equip")){
					miscCounter =  ((((Double)((ProbTCRow)miscTCS.get(x)).getProb().get(1))).doubleValue());
//				System.out.print(((ProbTCRow)miscTCS.get(x)).getTC().get(1) + " , ");
				}else{
				miscCounter =  ((((Double)((ProbTCRow)miscTCS.get(x)).getProb().get(0))).doubleValue());
//				System.out.print(((ProbTCRow)miscTCS.get(x)).getTC().get(0) + " , ");
				}
//				miscTCS.add(lookupTCReturnSUBATOMICTCROW((String) getLastRow(miscTCS).getTC().get(0)));
//				}
			}
		}

		this.setFinalMiscTCs(miscTCS);
		/**
		 * APPLY PICKS...
		 * 
		 */
		

		if(picks < 0 ||picks >1){
		if(input != 0){
		if(mParent.getClassOfMon()!= 4 && mParent.getClassOfMon()!=5){
		System.out.println("RWAR");
		
		}
		
		if(mParent.getClassOfMon() == 1 ||mParent.getClassOfMon() == 4 ||mParent.getClassOfMon() == 5 ){
			
			input = input * DC.getQuality(item, getLevel(), MF,this, item.getiNUS() - 1);
		}
		
		applyMPicks(input);
		}else{
			applyMPicks();
		}
		}

		calcdM = nPlayers+","+nplayersParty+","+input;
		
	}

	public void lookupBASETCReturnATOMICTCS(int nPlayers, int nplayersParty, double input) {


		if(calcd.equals(nPlayers+","+nplayersParty+","+input)){
//			return;
		}

		mParent.clearFinal(this);

		double counter = 1;
		ArrayList allTCS = new ArrayList();			

		if(mParent.getID().equals("duriel")){
			if(!getInitTC().contains("Base")){
				allTCS.add(lookupTCReturnSUBATOMICTCROW(this.getInitTC()));
				setInitTC((String)getLastRow(allTCS).getTC().get(1));
				allTCS.clear();
			}
		}
		allTCS.add(lookupTCReturnSUBATOMICTCROW(this.getInitTC()));
		if(mParent.getClassOfMon()==2 || mParent.getClassOfMon()==3|| mParent.getClassOfMon()==4 || mParent.getID().equals("griswold")){
			allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(allTCS).getTC().get(0))));
			//With SOME SUs, the last TC is empty for some reason. So remove it if it is!
			if(getLastRow(allTCS).getTC().isEmpty()){
				allTCS.remove(allTCS.size()-1);
			}
		}

		if(mParent.getClassOfMon()==2 || mParent.getClassOfMon()==0 ||mParent.getClassOfMon()==5 || mParent.getClassOfMon()==4 || mParent.getClassOfMon()==1){
			if(!mParent.getID().equals("griswold") && mParent.getClassOfMon()!=4){
				//Quest drops use a different row.
				if(mParent.getClassOfMon()==5 && ((Boss)mParent).getQuest()){
					allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(allTCS).getTC().get(0))));
				}else
				{
					allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(allTCS).getTC().get(1))));
				}
			}
			else{
				if(((String)getLastRow(allTCS).getTC().get(0)).contains("gld")){
					allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(allTCS).getTC().get(1))));
				}else{
					allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(allTCS).getTC().get(0))));
				}
			}

		}else{
			allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)getLastRow(allTCS).getTC().get(0))));
		}

		if(((String)getLastRow(allTCS).getTC().get(getLastRow(allTCS).getTC().size() - 1)).indexOf("Equip") !=-1){

			do{
				allTCS.add(lookupTCReturnSUBATOMICTCROW((String) getLastRow(allTCS).getTC().get(getLastRow(allTCS).getTC().size() - 1)));
			}while(((String)getLastRow(allTCS).getTC().get(getLastRow(allTCS).getTC().size() - 1)).indexOf("Equip") !=-1);

		}

		int f = 0;
		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));
		mPicks  = picks;
		if(picks < 0){

			((ProbTCRow)allTCS.get(1)).setTotProb(((ProbTCRow)allTCS.get(1)).getTotProb()) ;
			constructTCPairs((ProbTCRow)allTCS.get(1));
			if(mParent.getClassOfMon()==2 || mParent.getClassOfMon()==1 ){
				counter =  (((Double)((ProbTCRow)allTCS.get(1)).getProb().get(1)).doubleValue());
			}else{
				counter =  (((Double)((ProbTCRow)allTCS.get(1)).getProb().get(0)).doubleValue());
			}

			f=2;
		}else{

			int noDrop = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class", this.getInitTC()).get("NoDrop"));
			if(nPlayers>1){
				double realnP = Math.floor(1 + ((double)(nPlayers -1)/(double)2) + ((double)(nplayersParty -1)/(double)2));
				int probSum = ((ProbTCRow)allTCS.get(0)).getTotProb();
				if(noDrop != 0){
					noDrop = ( (int)Math.floor(((double)probSum)/(BigDecimal.valueOf(((((double)1)/(Math.pow((((double)noDrop)/((double)(noDrop + probSum))),realnP))))).subtract ((BigDecimal.valueOf(1)))).doubleValue()));
				}
			}
			((ProbTCRow)allTCS.get(0)).setTotProb(((ProbTCRow)allTCS.get(0)).getTotProb() + noDrop) ;
			constructTCPairs((ProbTCRow)allTCS.get(0));

			//BOSS QUEST DROPS TAKE FROM 0th ENTRY
			if(mParent.getClassOfMon()==5 && ((Boss)mParent).getQuest()){
				counter =  (((Double)((ProbTCRow)allTCS.get(0)).getProb().get(0)).doubleValue());
			}else{
				counter =  (((Double)((ProbTCRow)allTCS.get(0)).getProb().get(1)).doubleValue());
			}
			f=1;
		}
		for(int x = f; x< allTCS.size();x=x+1){
			if(!((String)((ProbTCRow)allTCS.get(x)).getTC().get(0)).equals("gld")){
				constructTCPairs((ProbTCRow)allTCS.get(x));
				multiplyOut((ProbTCRow)allTCS.get(x), counter);
				if(((String)((ProbTCRow)allTCS.get(x)).getTC().get(((ProbTCRow)allTCS.get(x)).getTC().size() -1 )).indexOf("Equip") != -1){
					counter =  ((((Double)((ProbTCRow)allTCS.get(x)).getProb().get(((ProbTCRow)allTCS.get(x)).getProb().size() -1 ))).doubleValue());
				}
			}
		}

		this.setFinalTCs(allTCS);
		/**
		 * APPLY PICKS...
		 * 
		 */

//		if(picks < 0 ||picks >1){
		if(input != 0){
			if(mParent.getClassOfMon()!= 4 && mParent.getClassOfMon()!=5){
				System.out.println("RWAR");
			}
			applyPicks(input);
		}else{
			applyPicks();
		}

		calcd = nPlayers+","+nplayersParty+","+input;
	}


	public void applyPicks(){

		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));


		if(picks > 1){
			if(picks > 6){
				picks = picks -1;
			}
			Iterator pickIt = this.getFinalTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalTCs().put(pickItStr,new Double(1-(Math.pow((1- ((Double)this.getFinalTCs().get(pickItStr)).doubleValue()), picks))));

			}
		}
		if(picks == -4 && mParent.getClassOfMon()==4){
			int pow = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Prob1"));
			Iterator pickIt = this.getFinalTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalTCs().put(pickItStr,new Double(1-(Math.pow((1- ((Double)this.getFinalTCs().get(pickItStr)).doubleValue()), pow))));
			}
		}
	}

	
	public void applyMPicks(){

		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));


		if(picks > 1){
			if(picks > 6){
				picks = picks -1;
			}
			Iterator pickIt = this.getFinalMiscTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalMiscTCs().put(pickItStr,new Double(1-(Math.pow((1- ((Double)this.getFinalMiscTCs().get(pickItStr)).doubleValue()), picks))));

			}
		}
		if(picks == -4 && mParent.getClassOfMon()==4){
			int pow = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Prob1"));
			Iterator pickIt = this.getFinalMiscTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalMiscTCs().put(pickItStr,new Double(1-(Math.pow((1- ((Double)this.getFinalMiscTCs().get(pickItStr)).doubleValue()), pow))));
			}
		}
	}
	
	public void applyMPicks(double input){

		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));


		if(picks > 1){
			if(picks > 6){
				picks = picks -1;
			}
			Iterator pickIt = this.getFinalMiscTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
//				if(mParent.getName().contains("The Cow King")){
//					this.getFinalMiscTCs().put(pickItStr,new Double((1-(Math.pow((1- (((Double)this.getFinalMiscTCs().get(pickItStr)).doubleValue())), picks))) *input));

//				}else{
				
				this.getFinalMiscTCs().put(pickItStr,new Double((1-(Math.pow((1- (((Double)this.getFinalMiscTCs().get(pickItStr)).doubleValue() *input)), picks)))));
//				}
			}
		}else if(picks == -4 && mParent.getClassOfMon()==4){
			int pow = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Prob1"));
			Iterator pickIt = this.getFinalMiscTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalMiscTCs().put(pickItStr,new Double(1-(Math.pow((1- (((Double)this.getFinalMiscTCs().get(pickItStr)).doubleValue()*input)), pow))));
			}
		}else{

			Iterator pickIt = this.getFinalMiscTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalMiscTCs().put(pickItStr,new Double(((Double)this.getFinalMiscTCs().get(pickItStr)).doubleValue()*input));
			}
			
		}
	}
	
	public void applyPicks(double input){

		int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));


		if(picks > 1){
			if(picks > 6){
				picks = picks -1;
			}
			Iterator pickIt = this.getFinalTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalTCs().put(pickItStr,new Double(1-(Math.pow((1- (((Double)this.getFinalTCs().get(pickItStr)).doubleValue()*input)), picks))));

			}
		}
		if(picks == -4 && mParent.getClassOfMon()==4){
			int pow = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Prob1"));
			Iterator pickIt = this.getFinalTCs().keySet().iterator();
			while(pickIt.hasNext()){
				String pickItStr = (String) pickIt.next();
				this.getFinalTCs().put(pickItStr,new Double(1-(Math.pow((1- (((Double)this.getFinalTCs().get(pickItStr)).doubleValue()*input)), pow))));
			}
//		}else{
//			Iterator pickIt = this.getFinalTCs().keySet().iterator();
//			while(pickIt.hasNext()){
//				String pickItStr = (String) pickIt.next();
//				this.getFinalTCs().put(pickItStr,new Double(((Double)this.getFinalTCs().get(pickItStr)).doubleValue()*input));
//			}
		}
	}


	private void setInitTC(String string) {
		this.initTC = string;

	}

	public Monster getParent() {
		return this.mParent;
	}

	public String getArLvlName(){
		return this.RealAreaName + " (mlvl " + this.Level + ")";
	}

	public int getSqual() {
		return mSqual;
	}

	public int getLevel() {
		return Level;
	}

	public HashMap getFinalMiscTCs() {
		return finalMiscTCs;
	}
}
