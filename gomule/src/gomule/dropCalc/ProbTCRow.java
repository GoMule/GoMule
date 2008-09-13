package gomule.dropCalc;

import java.util.ArrayList;

public class ProbTCRow {

	ArrayList TC;
	ArrayList Prob;
	int totProb;
	
	public ProbTCRow(ArrayList TC, ArrayList Prob){
		this.Prob = Prob;
		this.TC = TC;
		this.totProb = sum(Prob);
		
	}
	
	public ArrayList getTC(){
		return this.TC;
	}
	
	public ArrayList getProb(){
		return this.Prob;
	}
	
	public int getTotProb(){
		return this.totProb;
	}
	
	public void setTotProb(int totProb){
		this.totProb = totProb;
	}
		
		
	private int sum(ArrayList prob) {

		int out = 0;
		for(int x= 0;x<prob.size();x=x+1){
			out = out + (((Double)prob.get(x)).intValue());
		}

		return out;
		
	}

	public void setProb(double d, int x) {
		this.Prob.set(x,new Double(d));
		
	}
	
}
