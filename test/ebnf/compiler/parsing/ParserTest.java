package ebnf.compiler.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ParserTest {

	private static Parser parser;
	
	@BeforeAll
	static void setup()
	{
		parser = new Parser();
	}
	
	@Test
	void testSanitizer() 
	{
		//double whitespace
//		assertEquals(" ", parser.sanitize("  "));
//		
//		//option
//		assertEquals(" | ", parser.sanitize("|"));
//		assertEquals(" | ", parser.sanitize("| "));
//		assertEquals(" | ", parser.sanitize(" | "));
//		assertEquals(" | ", parser.sanitize(" |"));
		
		assertEquals("\\| ", parser.sanitize("\\|"));
	}
	
	@Test
	void testExpressions()
	{
		//enumeration
		assertEquals("<abc> <= ( a ( b c ) )", parser.parse("<abc> <= a b c").toString());
		
		//option
		assertEquals("<abc> <= ( a | b )", parser.parse("<abc> <= a | b").toString());
		assertEquals("<abc> <= ( a | ( b | c ) )", parser.parse("<abc> <= a | b | c").toString());
		assertEquals("<abc> <= ( a | { ( b | c ) } )", parser.parse("<abc> <= a|{b|c}").toString());
		
		//grouping
		assertEquals("<abc> <= ( a | ( b c ) )", parser.parse("<abc> <= a | (b c)").toString());
		assertEquals("<abc> <= ( ( a d ) | ( b c ) )", parser.parse("<abc> <= ( a d ) | (b c)").toString());
		assertEquals("<abc> <= ( a | ( b | c ) )", parser.parse("<abc> <= ( a | ( b | c ) )").toString());
		assertEquals("<abc> <= ( a | b | c )", parser.parse("<abc> <= ( ( a | b ) | c )").toString());
		
		//recursion
		assertEquals("<abc> <= <de>", parser.parse("<abc> <= <de>").toString());
		
		//optional
		assertEquals("<abc> <= [ a ]", parser.parse("<abc> <= [a]").toString());
		assertEquals("<abc> <= [ [ a ] ]", parser.parse("<abc> <= [[a]]").toString());
		
		//repetition
		assertEquals("<abc> <= { a }", parser.parse("<abc> <= {a}").toString());
		assertEquals("<abc> <= { <a> }", parser.parse("<abc> <= {<a>}").toString());
		assertEquals("<abc> <= { ( a | b ) }", parser.parse("<abc> <= {a|b}").toString());
	}
	
	private Scanner sanitizedScannerOf(String s)
	{
		return new Scanner(parser.sanitize(s));
	}
}
