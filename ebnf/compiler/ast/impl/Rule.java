package ebnf.compiler.ast.impl;

import java.util.HashMap;

import ebnf.compiler.ast.api.IRule;
import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;

public class Rule implements IRule {
	
	private final String name;
	private final IControlForm rhs;
	
	public Rule(final String name, final IControlForm rhs)
	{
		this.name = name;
		this.rhs = rhs;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public IControlForm getRHS() {
		return rhs;
	}
	
	@Override
	public boolean isValid(String expression, IRuleSet rules) {
		return rhs.match(expression, 0, rules, new HashMap<IControlForm, Integer>()).contains(expression.length());
	}

	@Override
	public String toString() {
		return "<" + name + ">" + " <= " + rhs.toString();
	}
}
