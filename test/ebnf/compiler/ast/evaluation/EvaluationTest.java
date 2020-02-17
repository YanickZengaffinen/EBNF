package ebnf.compiler.ast.evaluation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.parsing.Parser;

class EvaluationTest {

	@Test
	public void testSymbol()
	{
		Parser p = new Parser();
		
		IRuleSet simple = p.parse("<a> <= a");
		assertTrue(simple.isValid("a"));
		assertFalse(simple.isValid("b"));
		
		IRuleSet multiple = p.parse("<a> <= ab");
		assertTrue(multiple.isValid("ab"));
		assertFalse(multiple.isValid("ba"));
	}
	
	@Test
	public void testEscaping()
	{
		Parser p = new Parser();
		
		assertTrue(p.parse("<a> <= \\{ a \\}").isValid("{a}"));
	}
	
	@Test
	public void testEnumeration()
	{
		Parser p = new Parser();

		assertTrue(p.parse("<a> <= a b").isValid("ab"));
		assertTrue(p.parse("<a> <= a b c").isValid("abc"));
		
		assertFalse(p.parse("<a> <= a b").isValid("ba"));
		
		//spacing
		assertFalse(p.parse("<a> <= a b").isValid("a b"));
		assertFalse(p.parse("<a> <= a b c").isValid("ab c"));
		assertFalse(p.parse("<a> <= a b c").isValid("a bc"));
	}
	
	@Test
	public void testOptional()
	{
		Parser p = new Parser(); 
	
		IRuleSet simple = p.parse("<a> <= [a]");
		assertTrue(simple.isValid("a"));
		assertTrue(simple.isValid(""));
		assertFalse(simple.isValid("b"));
		
		IRuleSet nested = p.parse("<a> <= [[a] | b]");
		assertTrue(nested.isValid("a"));
		assertTrue(nested.isValid(""));
		assertFalse(nested.isValid("[]"));
	}
	
	@Test
	public void testOptions() 
	{
		Parser p = new Parser();
		
		assertTrue(p.parse("<a> <= a | b").isValid("a"));
		assertTrue(p.parse("<a> <= a | b").isValid("b"));
		
		assertFalse(p.parse("<a> <= a | b").isValid("c"));
	}
	
	@Test
	public void testRepetition()
	{
		Parser p = new Parser();
		
		IRuleSet simple = p.parse("<a> <= {a}");
		assertTrue(simple.isValid(""));
		assertTrue(simple.isValid("a"));
		assertTrue(simple.isValid("aa"));
		assertTrue(simple.isValid("aaa"));
		
		assertFalse(simple.isValid("aa a"));
		assertFalse(simple.isValid("b"));
		assertFalse(simple.isValid("ab"));
		
		
		
		IRuleSet multiple = p.parse("<a> <= {ab}");
		assertTrue(multiple.isValid(""));
		assertTrue(multiple.isValid("ab"));
		
		assertFalse(multiple.isValid("abba"));
	}
	
	@Test
	public void testRecursion()
	{
		Parser p = new Parser();
		
		IRuleSet simple = p.parse("<a> <= (<a> b) | b");
		assertTrue(simple.isValid("b"));
		assertTrue(simple.isValid("bb"));
		assertTrue(simple.isValid("bbb"));
		
		assertFalse(simple.isValid(""));
		assertFalse(simple.isValid("a"));
	}
}
