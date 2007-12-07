package DropCalcRefactored;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import randall.d2files.D2TxtFile;
import randall.d2files.D2TxtFileItemProperties;

public class MonsterTuple {

	String AreaName;
	int Level;
	String initTC;
	
	//SO FOR EVERY INIT TC THERE IS A SETOF FINAL TCS MAPPED TO A SET OF FINAL TC PROBs
	HashMap finalTCs = new HashMap();
	
	public MonsterTuple(String AreaName, Integer level, String initTC){
		this.AreaName = AreaName;
		this.Level = level.intValue();
		this.initTC = initTC;
		
	}

	public String getInitTC() {
		// TODO Auto-generated method stub
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
	
	
	public void lookupBASETCReturnATOMICTCS(Monster mon, int nPlayers, int nplayersParty) {



//		nPlayers = 1;

			double counter = 1;
			ArrayList allTCS = new ArrayList();			

//			System.out.println(tcQuery+" , "+TCROW.getTC());
			allTCS.add(lookupTCReturnSUBATOMICTCROW(this.getInitTC()));
			System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
			if(mon.getType().equals("CHAMP") || mon.getType().equals("UNIQ")|| mon.getType().equals("SUPUNIQ") || mon.getID().equals("griswold")){
				allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0))));
				System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
			}else if(mon.getID().equals("duriel")){

//				allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(1))));
//				System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
//				setInitTC(t, ((String)((ProbTCRow)allTCS.get(0)).getTC().get(1)));
//				allTCS.remove(0);



			}

			if(mon.getType().equals("CHAMP") || mon.getType().equals("REG") ||mon.getType().equals("BOSS") || mon.getType().equals("SUPUNIQ") || mon.getType().equals("MIN")){
				if(!mon.getID().equals("griswold") && !mon.getType().equals("SUPUNIQ")){
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
				if(mon.getID().equals("duriel")){
//					allTCS.remove(0);
//					allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(1))));
//					System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
				}




			}else{
				allTCS.add(lookupTCReturnSUBATOMICTCROW(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(0))));
				System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
			}

//			System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1));
			if(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip") !=-1){

				do{
					allTCS.add(lookupTCReturnSUBATOMICTCROW((String) ((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)));
					System.out.println(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC());
//					System.out.println(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip"));
				}while(((String)((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().get(((ProbTCRow)allTCS.get(allTCS.size()-1)).getTC().size() - 1)).indexOf("Equip") !=-1);

			}
//			for(int x = 0;x<thisSubTC.size();x=x+1){
//			if(((String)thisSubTC.get(x)).indexOf("Act")!=-1){
//			thisSubTC.addAll(lookupTCReturnSUBATOMICTCS((String)thisSubTC.get(x)));
////			TCROW.add(new ProbTCRow(thisSubTC, thisSubTCProb));

//			thisSubTC.remove(x);
//			x = x-1;
//			0
//			}
//			}

//			thisSubTC.addAll(thatSubTC);

			if(nPlayers != 1){

			}
			int f = 0;
			int picks = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Picks"));


			if(picks < 0){





				((ProbTCRow)allTCS.get(1)).setTotProb(((ProbTCRow)allTCS.get(1)).getTotProb()) ;
				constructTCPairs((ProbTCRow)allTCS.get(1));
				if(mon.getType().equals("CHAMP") || mon.getType().equals("REG") ){
					counter =  (((Double)((ProbTCRow)allTCS.get(1)).getProb().get(1)).doubleValue());
				}else{
					counter =  (((Double)((ProbTCRow)allTCS.get(1)).getProb().get(0)).doubleValue());
				}

				f=2;
			}else{
				
				nPlayers = (int) Math.round(nPlayers/(double)2);
				int noDrop = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class", this.getInitTC()).get("NoDrop"));
				int MPNDrop =(int) Math.round(((1+((double)nPlayers/(double)2))+((double)nplayersParty/(double)2)));
				
				
				
				System.out.println(noDrop);
				if(nPlayers>1){
					
//					int newNoDrop= (int)Math.floor( ((ProbTCRow)allTCS.get(0)).getTotProb()/(1/(Math.pow((noDrop/(noDrop+((ProbTCRow)allTCS.get(0)).getTotProb())),MPNDrop))-1) );
					noDrop = (int) Math.round(((double)(((ProbTCRow)allTCS.get(0)).getTotProb())/(double)((double)1/(Math.pow(((double)100/((double)100+60)),4))-1)));
					System.out.println(noDrop);
				}
				((ProbTCRow)allTCS.get(0)).setTotProb(((ProbTCRow)allTCS.get(0)).getTotProb() + noDrop) ;
				
				constructTCPairs((ProbTCRow)allTCS.get(0));
				
				counter =  (((Double)((ProbTCRow)allTCS.get(0)).getProb().get(1)).doubleValue());
				f=1;
			}

			/**
			 * MODIFY FOR NODROP
			 */



			for(int x = f; x< allTCS.size();x=x+1){
//				System.out.println(((ProbTCRow)allTCS.get(x)).getProb());
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





//			if(((String)allTCS.get(x)).indexOf("armo") != -1){
//			atomicTCSArm.add(allTCS.get(x));
//			}
//			}

//			System.out.println(atomicTCSWep.size()+atomicTCSArm.size());
//			atomicTCSWep=removeDuplicates(atomicTCSWep);
//			atomicTCSArm=removeDuplicates(atomicTCSArm);
//			System.out.println(atomicTCSWep.size()+atomicTCSArm.size());
//			atomicTCSWep.addAll(atomicTCSArm);

			this.setFinalTCs(allTCS);

//			selectedMon.printFinals();

			/**
			 * APPLY PICKS...
			 * 
			 */



			if(picks > 1){
				if(picks > 6){
					picks = picks -1;
				}
				Iterator pickIt = this.getFinalTCs().keySet().iterator();
				while(pickIt.hasNext()){
					String pickItStr = (String) pickIt.next();
					this.getFinalTCs().put(pickItStr,new Double(1-(Math.pow((1- ((Double)this.getFinalTCs().get(pickItStr)).doubleValue()), picks))));
					
				}
//				}
//				System.out.println(((ArrayList)selectedMon.getFinalProbs().get(t)).get(y));
//				System.out.println((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()));
//				System.out.println(1-(Math.pow((1- ((Double)((ArrayList)selectedMon.getFinalProbs().get(t)).get(y)).doubleValue()), picks -1)));

			}

			if(picks == -4 && mon.getType().equals("SUPUNIQ")){
				int pow = Integer.parseInt(D2TxtFile.TCS.searchColumns("Treasure Class",this.getInitTC()).get("Prob1"));
				Iterator pickIt = this.getFinalTCs().keySet().iterator();
				while(pickIt.hasNext()){
					String pickItStr = (String) pickIt.next();
					this.getFinalTCs().put(pickItStr,new Double(1-(Math.pow((1- ((Double)this.getFinalTCs().get(pickItStr)).doubleValue()), pow))));
					
				}
			}
			
		}

	
	
	

	
}
