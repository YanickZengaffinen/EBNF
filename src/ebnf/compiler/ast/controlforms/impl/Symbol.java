package ebnf.compiler.ast.controlforms.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;
import ebnf.compiler.ast.controlforms.api.ISymbol;

public final class Symbol implements ISymbol{

	private final String symbol;
	
	public Symbol(final String symbol)
	{
		if(symbol.startsWith("\\"))
		{
			this.symbol = symbol.substring(1);
		}
		else
		{
			this.symbol = symbol;
		}
	}

	@Override
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public Set<Integer> match(String expression, int startIndex, IRuleSet rules, Map<IControlForm, Integer> recursionDepth) {
		Set<Integer> matched = new HashSet<Integer>();
		
		if(expression.startsWith(symbol, startIndex))
		{
			matched.add(symbol.length());
		}
		
		return matched;
	}
	
	@Override
	public String toString() {
		return symbol;
	}
}
