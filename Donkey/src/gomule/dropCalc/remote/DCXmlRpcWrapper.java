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
		
		switch(monKey){
		case 0:
			
			Monster mSelected = (Monster)(DCXmlRpcServer.DC.getMainRegMonArray().get(mIndex));
			System.out.println(mSelected.getMonName() + " " + mSelected.getMonID() + " " + mSelected.getMonDiff());
			mSelected.lookupBASETCReturnATOMICTCS(nPlayers, nGroup,0,sevenPicks);
			return arrlistToArr(mSelected.getmTuples());
			
		}		
		return null;
		
	}
	
	public Object[] getMonsters(int monKey){
		switch(monKey){
		case 0:
			return extractName(DCXmlRpcServer.DC.getMainRegMonArray());
		}
		return null;
	}
	
	private Object[] extractName(ArrayList arrIn) {
		
		ArrayList arrOut = new ArrayList();
		
		for(int x = 0;x <arrIn.size();x++){
			arrOut.add(((Monster)arrIn.get(x)).getName());
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
	
}
