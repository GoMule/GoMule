package gomule.dropCalc.gui;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class OutputRow {

	DecimalFormat DM = new DecimalFormat("#.###############");
	String c0;
	String c1;
	double c2;
	
	public OutputRow(String c0, String c1, Object c2){
		this.c0 = c0;
		this.c1 = c1;
		this.c2 = ((Double)c2).doubleValue();
	}
	
	public String getC0(){
		
		return c0;
		
	}
	
	public String getC1(){
		
		return c1;
		
	}
	
	public double getC2(){
		
		return c2;
		
	}
	
	public Double getObjC2(){
		
		return new Double(c2);	
//		return new Double(roundDouble(c2, 5));		
	}
	
    public double roundDouble(double d, int places) {
        return Math.round(d * Math.pow(10, (double) places)) / Math.pow(10,
            (double) places);
    }

	public String getStrC2() {
		return DM.format(c2);	
	}
	
}
