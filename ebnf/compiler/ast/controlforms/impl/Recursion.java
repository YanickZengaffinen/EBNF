package ebnf.compiler.ast.controlforms.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ebnf.compiler.ast.api.IRule;
import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;
import ebnf.compiler.ast.controlforms.api.IRecursion;

public final class Recursion implements IRecursion {

	private final String ruleName;
	
	public Recursion(final String ruleName)
	{
		this.ruleName = ruleName;
	}
	
	@Override
	public String getRuleName() {
		return ruleName;
	}
	
	@Override
	public Set<Integer> match(String expression, int startIndex, IRuleSet rules, Map<IControlForm, Integer> recursionDepth) {
		IRule rule = rules.getRule(ruleName);

		//update recursion depth
		Map<IControlForm, Integer> recursionDepthCopy = new HashMap<IControlForm, Integer>(recursionDepth);
		int currentRecursionDepth = recursionDepthCopy.getOrDefault(this, 0) + 1;
		recursionDepthCopy.put(this, currentRecursionDepth);
		
		//check if the max recursion depth has been reached
		if(currentRecursionDepth > expression.length())
		{
			return new HashSet<>();
		}
		else
		{
			return rule.getRHS().match(expression, startIndex, rules, recursionDepthCopy);
		}
	}
	
	@Override
	public String toString() {
		return "<" + ruleName + ">";
	}
}
