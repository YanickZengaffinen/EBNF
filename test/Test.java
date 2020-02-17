package test;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.parsing.Parser;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		
		File file = new File("ruleset.txt");
		
		Parser parser = new Parser();
		IRuleSet ruleSet = parser.parse(new Scanner(file));
		
		Scanner s = new Scanner(System.in);
		String expression;
		while(!(expression = s.nextLine()).equals("/stop"))
		{
			System.out.println(ruleSet.isValid(expression));
		}
		
		s.close();
	}

}
