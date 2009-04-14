package gomule.dropCalc.remote;

import gomule.dropCalc.DCNew;
import gomule.dropCalc.gui.OutputRow;
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
	
	public Object[] performLookup(int nType, int monKey, int mIndex, int nPlayers, int nGroup, boolean sevenPicks){
		
//		getMonsters(0);
		
		switch(monKey){
		case 0:
			
			Monster mSelected = (Monster)(DCXmlRpcServer.DC.getMainRegMonArray().get(mIndex));
			System.out.println(mSelected.getMonName() + " " + mSelected.getMonID() + " " + mSelected.getMonDiff());
			mSelected.lookupBASETCReturnATOMICTCS(nPlayers, nGroup,0,sevenPicks);
			return arrlistToArr(mSelected.getmTuples());
			
		}		
		return null;
		
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
	
	public String test(int boss){
		
		return ((Monster)(DCXmlRpcServer.DC.getMainBossArray().get(boss))).getMonID();
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
	
}
