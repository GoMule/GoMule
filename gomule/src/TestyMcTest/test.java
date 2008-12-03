package TestyMcTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String test = "200% Enhanced Defense";
		
//		Pattern pat = Pattern.compile(".*[\\d+].*");
		Pattern pat = Pattern.compile("\\d+");
		Matcher mat = pat.matcher(test);
		
		while(mat.find()){
			System.out.println(mat.group());
		}
		

	}

}
