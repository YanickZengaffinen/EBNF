package ebnf.compiler.ast.controlforms.impl;

import java.util.Map;
import java.util.Set;

import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;
import ebnf.compiler.ast.controlforms.api.IOptional;

public class Optional implements IOptional {

	IControlForm optional;
	
	public Optional(IControlForm optional)
	{
		this.optional = optional;
	}
	
	@Override
	public IControlForm getControlForm() {
		return optional;
	}
	
	@Override
	public Set<Integer> match(String expression, int startIndex, IRuleSet rules, Map<IControlForm, Integer> recursionDepth) {
		Set<Integer> matched = optional.match(expression, startIndex, rules, recursionDepth);
		matched.add(0);
		return matched;
	}
	
	@Override
	public String toString() {
		return "[ " + optional.toString() + " ]"; 
	}
}
