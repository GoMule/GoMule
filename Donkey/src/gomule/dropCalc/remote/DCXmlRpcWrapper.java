package gomule.dropCalc.remote;

import gomule.dropCalc.DCNew;
import gomule.dropCalc.gui.OutputRow;
import gomule.dropCalc.items.Item;
import gomule.dropCalc.monsters.Monster;
import gomule.dropCalc.monsters.MonsterTuple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DCXmlRpcWrapper {

	DecimalFormat DM = new DecimalFormat("#.###############");
	
	public Object[] arrlistToArr(ArrayList inArr){
	

		ArrayList outArr = new ArrayList();	
		
		for(int x = 0;x<inArr.size();x++){
			MonsterTuple tSelected = ((MonsterTuple)inArr.get(x));
			Iterator TCIt = tSelected.getFinalTCs().keySet().iterator();

			while(TCIt.hasNext()){

				String tcArr = (String) TCIt.next();				
				outArr.add(new Object[]{tcArr,tSelected.getArLvlName(),getStrC2(true,(Double)tSelected.getFinalTCs().get(tcArr))});

			}
		}
		
		return outArr.toArray();
		
		
	}
	
	public Object[] performLookup(int nType, int monKey, int mIndex, int nPlayers, int nGroup, int diff, boolean sevenPicks){
		
			
			Monster mSelected = (Monster) getMonster(monKey, diff).get(mIndex);
			System.out.println(mSelected.getMonName() + " " + mSelected.getMonID() + " " + mSelected.getMonDiff());
			mSelected.lookupBASETCReturnATOMICTCS(nPlayers, nGroup,0,sevenPicks);
			return arrlistToArr(mSelected.getmTuples());
		
	}
	
//	public Object[] getMonsters(int monKey){
//		switch(monKey){
//		case 0:
//			return extractName(DCXmlRpcServer.DC.getMainRegMonArray());
//		}
//		return null;
//	}
	
	private Object[] extractName(ArrayList arrIn) {
		
		ArrayList arrOut = new ArrayList();
		
		for(int x = 0;x <arrIn.size();x++){
			System.out.println(((Monster)arrIn.get(x)).getName());
		}
		
		return arrOut.toArray();
	}

	public String getStrC2(boolean dec, Double c2) {
		if(dec){
		return DM.format(c2);
	
		}
		else{
			return "1:" + (int)Math.floor(1/(c2.doubleValue()));
			}
		
	}
	
	
		public Object[] refreshMonsterField(int nClass, int nDiff){
			ArrayList nMonster = new ArrayList();
			
		switch(nClass){
		case 0:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getRealName() + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff() + ")");
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff()).equals("N")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getRealName() + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff()).equals("NM")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getRealName() + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff()).equals("H")){		
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getRealName() + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			}
			break;
		case 1:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getRealName() + " - " + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff() + ")");
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff()).equals("N")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getRealName() + " - " + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff()).equals("NM")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getRealName() + " - " + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff()).equals("H")){	
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getRealName() + " - " + " (" + (String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			}
			break;
		case 2:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff() + ")");
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff()).equals("N")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff()).equals("NM")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff()).equals("H")){		
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			}
			break;
		case 3:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff() + ")");
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff()).equals("N")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff()).equals("NM")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff()).equals("H")){		
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			}
			break;
		case 4:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff() + ")");
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff()).equals("N")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff()).equals("NM")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff()).equals("H")){			
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			}
			break;
		case 5:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff() + ")");
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff()).equals("N")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff()).equals("NM")){
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff()).equals("H")){		
						nMonster.add((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getRealName()+ " (" + (String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff() + ")");
					}
				}
				break;
			}
			break;
		}

		return nMonster.toArray();
		
	}
		
	private ArrayList getMonster(int nClass, int nDiff){


		ArrayList nMonsterKey = new ArrayList();
		
		switch(nClass){
		case 0:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					nMonsterKey.add(DCXmlRpcServer.DC.getMainRegMonArray().get(x));
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff()).equals("N")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainRegMonArray().get(x));
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff()).equals("NM")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainRegMonArray().get(x));
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainRegMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainRegMonArray().get(x)).getMonDiff()).equals("H")){		
						nMonsterKey.add(DCXmlRpcServer.DC.getMainRegMonArray().get(x));
					}
				}
				break;
			}
			break;
		case 1:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					nMonsterKey.add(DCXmlRpcServer.DC.getMainMinMonArray().get(x));
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff()).equals("N")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainMinMonArray().get(x));
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff()).equals("NM")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainMinMonArray().get(x));
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainMinMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainMinMonArray().get(x)).getMonDiff()).equals("H")){	
						nMonsterKey.add(DCXmlRpcServer.DC.getMainMinMonArray().get(x));
					}
				}
				break;
			}
			break;
		case 2:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					nMonsterKey.add(DCXmlRpcServer.DC.getMainChampMonArray().get(x));
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff()).equals("N")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainChampMonArray().get(x));
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff()).equals("NM")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainChampMonArray().get(x));
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainChampMonArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainChampMonArray().get(x)).getMonDiff()).equals("H")){		
						nMonsterKey.add(DCXmlRpcServer.DC.getMainChampMonArray().get(x));
					}
				}
				break;
			}
			break;
		case 3:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					nMonsterKey.add(DCXmlRpcServer.DC.getMainUniqArray().get(x));
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff()).equals("N")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainUniqArray().get(x));
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff()).equals("NM")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainUniqArray().get(x));
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainUniqArray().get(x)).getMonDiff()).equals("H")){		
						nMonsterKey.add(DCXmlRpcServer.DC.getMainUniqArray().get(x));
					}
				}
				break;
			}
			break;
		case 4:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					nMonsterKey.add(DCXmlRpcServer.DC.getMainSupUniqArray().get(x));
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff()).equals("N")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainSupUniqArray().get(x));
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff()).equals("NM")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainSupUniqArray().get(x));
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainSupUniqArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainSupUniqArray().get(x)).getMonDiff()).equals("H")){			
						nMonsterKey.add(DCXmlRpcServer.DC.getMainSupUniqArray().get(x));
					}
				}
				break;
			}
			break;
		case 5:
			switch(nDiff){
			case 0:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					nMonsterKey.add(DCXmlRpcServer.DC.getMainBossArray().get(x));
				}
				break;
			case 1:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff()).equals("N")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainBossArray().get(x));
					}
				}
				break;
			case 2:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff()).equals("NM")){
						nMonsterKey.add(DCXmlRpcServer.DC.getMainBossArray().get(x));
					}
				}
				break;
			case 3:
				for(int x = 0; x< DCXmlRpcServer.DC.getMainBossArray().size();x=x+1){
					if(((String)((Monster)DCXmlRpcServer.DC.getMainBossArray().get(x)).getMonDiff()).equals("H")){		
						nMonsterKey.add(DCXmlRpcServer.DC.getMainBossArray().get(x));
					}
				}
				break;
			}
			break;
		}
		return nMonsterKey;
	}
	
	public ArrayList refreshItems(int nMItemQual, int nMItemQual2){
		
		ArrayList nMItem = new ArrayList();
		
		switch(nMItemQual){
		case 0:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
				}
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getMiscItemArray().get(x)).getRealName());

				}


				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 0){
						nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 1){
						nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 2){
						nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getMiscItemArray().get(x)).getRealName());

				}
				break;

			}
			break;
		case 2:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getRealName());

				}
				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 0){
						nMItem.add(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getRealName());
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 1){
						nMItem.add(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getRealName());
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 2){
						nMItem.add(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getRealName());
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 3){
						nMItem.add(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getRealName());
					}
				}
				break;
			}
			break;
		case 1:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getRealName());

				}
				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 0){
						nMItem.add(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getRealName());
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 1){
						nMItem.add(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getRealName());
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 2){
						nMItem.add(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getRealName());
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 3){
						nMItem.add(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getRealName());
					}
				}
				break;
			}
			break;
		default:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
				}
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getMiscItemArray().get(x)).getRealName());

				}


				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 0){
						nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 1){
						nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 2){
						nMItem.add(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getRealName());
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItem.add(((Item)DCXmlRpcServer.DC.getMiscItemArray().get(x)).getRealName());

				}
				break;

			}
			break;
		}
		return nMItem;
	}
	
	private ArrayList getItems(int nMItemQual, int nMItemQual2){
		
		ArrayList nMItemKey = new ArrayList();
		
		switch(nMItemQual){
		case 0:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
				}
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getMiscItemArray().get(x));

				}


				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 0){
						nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 1){
						nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 2){
						nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getMiscItemArray().get(x));

				}
				break;

			}
			break;
		case 2:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getSetItemArray().get(x));

				}
				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 0){
						nMItemKey.add(DCXmlRpcServer.DC.getSetItemArray().get(x));
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 1){
						nMItemKey.add(DCXmlRpcServer.DC.getSetItemArray().get(x));
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 2){
						nMItemKey.add(DCXmlRpcServer.DC.getSetItemArray().get(x));
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getSetItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getSetItemArray().get(x)).getItemQual() == 3){
						nMItemKey.add(DCXmlRpcServer.DC.getSetItemArray().get(x));
					}
				}
				break;
			}
			break;
		case 1:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getUniqItemArray().get(x));

				}
				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 0){
						nMItemKey.add(DCXmlRpcServer.DC.getUniqItemArray().get(x));
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 1){
						nMItemKey.add(DCXmlRpcServer.DC.getUniqItemArray().get(x));
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 2){
						nMItemKey.add(DCXmlRpcServer.DC.getUniqItemArray().get(x));
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getUniqItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getUniqItemArray().get(x)).getItemQual() == 3){
						nMItemKey.add(DCXmlRpcServer.DC.getUniqItemArray().get(x));
					}
				}
				break;
			}
			break;
		default:
			switch(nMItemQual2){

			case 0:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
				}
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getMiscItemArray().get(x));

				}


				break;
			case 1:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 0){
						nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
					}
				}
				break;
			case 2:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 1){
						nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
					}
				}
				break;
			case 3:
				for(int x  = 0;x<DCXmlRpcServer.DC.getRegItemArray().size();x++){
					if(((Item)DCXmlRpcServer.DC.getRegItemArray().get(x)).getItemQual() == 2){
						nMItemKey.add(DCXmlRpcServer.DC.getRegItemArray().get(x));
					}
				}
				break;
			case 4:
				for(int x  = 0;x<DCXmlRpcServer.DC.getMiscItemArray().size();x++){
					nMItemKey.add(DCXmlRpcServer.DC.getMiscItemArray().get(x));

				}
				break;

			}
			break;
		}
		return nMItemKey;
	}
	
	
}
