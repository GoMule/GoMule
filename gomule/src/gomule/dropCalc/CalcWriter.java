package gomule.dropCalc;

import java.io.BufferedWriter;
import java.io.IOException;


public class CalcWriter {

	String fileName;
	BufferedWriter Bw;
	
	
	public CalcWriter(String fileName){
//		this.fileName = fileName;
//		try {
//			Bw = new BufferedWriter(new FileWriter(fileName));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}
	
	public boolean writeData(String dataToWrite){



		try {
			Bw.append(dataToWrite);
			Bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
		
	}
	
	
}
