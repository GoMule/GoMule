package gomule.dropCalc;

public class ProbTCPair {

	String TC;
	int Prob;
	
	public ProbTCPair(String TC, int Prob){
		this.Prob = Prob;
		this.TC = TC;
		
	}
	
	public String getTC(){
		return this.TC;
	}
	
	public int getProb(){
		return this.Prob;
	}
	
}
