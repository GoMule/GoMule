package gomule.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class D2PropCollection {

	
	
	public ArrayList pArr = new ArrayList();
	private HashMap modMap;
	
	
	//Stage 1: Remove useless mods
	//Stage 2: Combine mods such as resistance etc
	//Stage 3: Populate modMap to display what properties are being modified. 
	
	
	private void cleanProps(){
		
		for(int x = 0;x<pArr.size();x++){
			
			//Throw damage of some form.
			if(((D2Prop)pArr.get(x)).getPNum() == 160 || ((D2Prop)pArr.get(x)).getPNum() == 159){
				pArr.remove(x);
				x--;
			}
			
		}
		
	}
	
	
	private void generateMods() {
		
		

		
		modMap = new HashMap();
		
		
		
		
	}
	
	public void add(D2Prop prop) {
		
		pArr.add(prop);
		
	}


	public ArrayList generateDisplay() {
		
		ArrayList arrOut = new ArrayList();
		
		if(modMap == null){
			generateMods();
		}
		
		
		for(int x = 0;x<pArr.size();x++){
			arrOut.add(((D2Prop)pArr.get(x)).generateDisplay());		
		}
		
		return arrOut;
	}
	
	
	
	

}
