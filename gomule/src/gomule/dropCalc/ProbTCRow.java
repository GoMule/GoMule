package gomule.dropCalc;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;

public class ProbTCRow {

	ArrayList TC;
	ArrayList Prob;
	int totProb;
	
	public ProbTCRow(ArrayList TC, ArrayList Prob){
		this.Prob = Prob;
		this.TC = TC;
		this.totProb = sum(Prob);
		
	}
	
	public ProbTCRow(ProbTCRow ptcR){
		this.Prob = new ArrayList();
		this.TC = new ArrayList();
		
		for(int x = 0;x<ptcR.getProb().size();x++){
			Prob.add(new Double(((Double)ptcR.getProb().get(x)).doubleValue()));
			TC.add(new String(((String)ptcR.getTC().get(x))));
		}
		this.totProb = ptcR.totProb;
		
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
