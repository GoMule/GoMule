package gomule.item;

import java.util.ArrayList;
import java.util.Collection;

public class D2PropCollection {

	
	
	public ArrayList pArr = new ArrayList();
	
	
	public void add(D2Prop prop) {
		
		pArr.add(prop);
		
	}


	public ArrayList generateDisplay() {
		
		ArrayList arrOut = new ArrayList();
		
		
		for(int x = 0;x<pArr.size();x++){
			arrOut.add(((D2Prop)pArr.get(x)).generateDisplay());		
		}
		
		return arrOut;
	}
	
	
	
	

}
