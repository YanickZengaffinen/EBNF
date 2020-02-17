package ebnf.compiler.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import ebnf.compiler.ast.api.IRule;
import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;
import ebnf.compiler.ast.controlforms.impl.Enumeration;
import ebnf.compiler.ast.controlforms.impl.Option;
import ebnf.compiler.ast.controlforms.impl.Optional;
import ebnf.compiler.ast.controlforms.impl.Recursion;
import ebnf.compiler.ast.controlforms.impl.Repetition;
import ebnf.compiler.ast.controlforms.impl.Symbol;
import ebnf.compiler.ast.impl.Rule;
import ebnf.compiler.ast.impl.RuleSet;

/**
 * Integer Test
	<digit> <= 0|1|2|3|4|5|6|7|8|9
	<integer> <= [+|-]<digit>{<digit>}
 *
 */
public final class Parser {

	private static final Pattern WhiteSpacePattern = Pattern.compile("\\s");
	private static final Pattern RuleNamePattern = Pattern.compile("<[a-z_]*>");
	private static final Pattern SymbolPattern = Pattern.compile("([a-z]|[A-Z]|[0-9])*|\\\\.*");
	
	//internal representation of parenthesis
	private static final String ParenthesisOpener = "("; 
	private static final String ParenthesisCloser = ")";
	
	private static final String Option = "|";

	private static final String RepetitionOpener = "{";
	private static final String RepetitionCloser = "}";
	
	private static final String OptionalOpener = "[";
	private static final String OptionalCloser = "]";
	
	//how parenthesis appear before sanitizing
	private static final Pattern ParenthesisOpenerPattern = Pattern.compile("\\" + ParenthesisOpener);
	private static final Pattern ParenthesisCloserPattern = Pattern.compile("\\" + ParenthesisCloser);

	private static final Pattern OptionPattern = Pattern.compile("\\" + Option);
	
	private static final Pattern RepetitionOpenerPattern = Pattern.compile("\\" + RepetitionOpener);
	private static final Pattern RepetitionCloserPattern = Pattern.compile("\\" + RepetitionCloser);
	
	private static final Pattern OptionalOpenerPattern = Pattern.compile("\\" + OptionalOpener);
	private static final Pattern OptionalCloserPattern = Pattern.compile("\\" + OptionalCloser);
	
	public IRuleSet parse(String s)
	{
		Scanner scanner = new Scanner(s);

		IRuleSet ruleSet = parse(scanner);
		
		scanner.close();
		return ruleSet;
	}
	
	public IRuleSet parse(Scanner scanner)
	{
		List<IRule> rules = new ArrayList<IRule>();
		
		while(scanner.hasNextLine())
		{
			String line = sanitize(scanner.nextLine());
			rules.add(parseRule(new Scanner(line)));
		}
		
		return new RuleSet(rules);	
	}
	
	private IRule parseRule(Scanner s)
	{
		String name = parseName(s);
		
		s.next(Pattern.compile("\\s?<="));
		if(s.hasNext(WhiteSpacePattern))
		{
			s.next(WhiteSpacePattern);
		}
		
		IControlForm rhs = parseControlForm(s);
		
		return new Rule(name, rhs);
	}
	
	private IControlForm parseControlForm(Scanner s)
	{
		IControlForm current = null;
		
		//initial term
		
		//-- PARENTHESIS
		if(s.hasNext(ParenthesisOpenerPattern))
		{
			current = parseBlock(s, ParenthesisOpener, ParenthesisOpenerPattern, ParenthesisCloser, ParenthesisCloserPattern);
		}
		//-- REPETITION
		else if(s.hasNext(RepetitionOpenerPattern))
		{
			current = new Repetition(parseBlock(s, RepetitionOpener, RepetitionOpenerPattern, RepetitionCloser, RepetitionCloserPattern));
		}
		//-- OPTIONAL
		else if(s.hasNext(OptionalOpenerPattern))
		{
			current = new Optional(parseBlock(s, OptionalOpener, OptionalOpenerPattern, OptionalCloser, OptionalCloserPattern));
		}
		//-- RECURSION
		else if(s.hasNext(RuleNamePattern))
		{
			//.<abc>
			String ruleName = s.next(RuleNamePattern);
			current = new Recursion(ruleName.substring(1, ruleName.length() - 1));
		}
		//-- SYMBOL
		else if(s.hasNext(SymbolPattern))
		{
			current = new Symbol(s.next(SymbolPattern));
		}
		
		//further terms
		
		while(s.hasNext())
		{
			//-- OPTION
			if(s.hasNext(OptionPattern))
			{
				if(current instanceof Option)
				{
					// a | b .| c
					List<IControlForm> options = new ArrayList<>(((Option) current).getOptions());
					options.add(parseOption(s));
					current = new Option(options);
				}
				else
				{
					// a .| b
					current = new Option(List.of(current, parseOption(s)));
				}
			}
			//-- ENUMERATION
			else
			{
				if(current instanceof Enumeration)
				{
					// a b .c
					List<IControlForm> elements = new ArrayList<IControlForm>(((Enumeration) current).getElements());
					elements.add(parseControlForm(s)); //TODO: actually this should only parse the next term
					current = new Enumeration(elements);
				}
				else
				{
					// a .b
					current = new Enumeration(List.of(current, parseControlForm(s)));
				}
			}
		}
		
		return current;
	}
	
	private String parseName(Scanner s)
	{
		String term = s.next(RuleNamePattern);
		return term.substring(1, term.length() - 1);
	}
	
	private IControlForm parseBlock(Scanner s, String opener, Pattern openerPattern, String closer, Pattern closerPattern)
	{
		String content = "";
		int bracketCnt = 1;
		s.next(openerPattern);
		
		while(bracketCnt != 0)
		{
			if(s.hasNext(openerPattern))
			{
				s.next(openerPattern);
				bracketCnt++;
				if(bracketCnt > 1)
				{
					content += opener + " ";
				}
			}
			else if(s.hasNext(closerPattern))
			{
				s.next(closerPattern);
				bracketCnt--;
				if(bracketCnt == 0)
				{
					return parseControlForm(new Scanner(content));
				}
				else
				{
					content += closer + " ";
				}
			}
			else
			{
				content += s.next() + " ";
			}
		}
		
		//TODO: throw new ParseException("Couldn't parse" + content);
		return null;
	}
	
	//Parses .| abc
	private IControlForm parseOption(Scanner s)
	{
		if(s.hasNext(OptionPattern))
		{
			s.next(OptionPattern);
			return parseControlForm(s);
		}
		
		return null;
	}
	
	String sanitize(String s)
	{
		s = s.replaceAll(ParenthesisOpenerPattern.pattern(), " " + ParenthesisOpener + " ");
		s = s.replaceAll(ParenthesisCloserPattern.pattern(), " " + ParenthesisCloser + " ");

		s = s.replaceAll(OptionPattern.pattern(), " " + Option + " "); 
		
		s = s.replaceAll(RepetitionOpenerPattern.pattern(), " " + RepetitionOpener + " ");
		s = s.replaceAll(RepetitionCloserPattern.pattern(), " " + RepetitionCloser + " ");
		
		s = s.replaceAll(OptionalOpenerPattern.pattern(), " " + OptionalOpener + " ");
		s = s.replaceAll(OptionalCloserPattern.pattern(), " " + OptionalCloser + " ");
		
		//remove double whitespaces until the length of the string doesn't change
		int length = s.length();
		while((s = s.replace("  ", " ")).length() != length)
		{
			length = s.length();
		}
		
		//merge escaped: "\ {" to "\{"
		String preceedingBackslash = "(\\\\)\\s"; // "\ "
		s = s.replaceAll(preceedingBackslash + ParenthesisOpenerPattern.pattern(), "\\\\" + ParenthesisOpener);
		s = s.replaceAll(preceedingBackslash + ParenthesisCloserPattern.pattern(), "\\\\" + ParenthesisCloser);
		
		s = s.replaceAll(preceedingBackslash + OptionPattern.pattern(), "\\\\" + Option);
		
		s = s.replaceAll(preceedingBackslash + RepetitionOpenerPattern.pattern(), "\\\\" + RepetitionOpener);
		s = s.replaceAll(preceedingBackslash + RepetitionCloserPattern.pattern(), "\\\\" + RepetitionCloser);
		
		s = s.replaceAll(preceedingBackslash + OptionalOpenerPattern.pattern(), "\\\\" + OptionalOpener);
		s = s.replaceAll(preceedingBackslash + OptionalCloserPattern.pattern(), "\\\\" + OptionalCloser);
		
		return s;
	}
}
