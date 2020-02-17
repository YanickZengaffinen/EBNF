package ebnf.compiler.ast.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ebnf.compiler.ast.api.IRule;
import ebnf.compiler.ast.api.IRuleSet;

public final class RuleSet implements IRuleSet {
	
	private Map<String, IRule> rules;
	
	public RuleSet(List<IRule> rules)
	{
		this.rules = rules.stream()
				.collect(Collectors.toUnmodifiableMap(r -> r.getName(), r -> r));
	}
	
	@Override
	public Map<String, IRule> getRules() {
		return rules;
	}
	
	@Override
	public boolean isValid(String expression) {
		for(IRule rule : rules.values())
		{
			if(rule.isValid(expression, this))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.join(System.lineSeparator(), rules.values().stream().map(x -> x.toString()).collect(Collectors.toList()));
	}
}
