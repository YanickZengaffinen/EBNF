package ebnf.compiler.ast.controlforms.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;
import ebnf.compiler.ast.controlforms.api.IOption;

public final class Option implements IOption {
	
	private final List<IControlForm> options;
	
	public Option(final List<IControlForm> options)
	{
		this.options = Collections.unmodifiableList(options);
	}
	
	@Override
	public List<IControlForm> getOptions() {
		return options;
	}
	
	@Override
	public Set<Integer> match(String expression, int startIndex, IRuleSet rules, Map<IControlForm, Integer> recursionDepth) {
		Set<Integer> matched = new HashSet<Integer>();
		
		for(IControlForm option : options)
		{
			matched.addAll(option.match(expression, startIndex, rules, recursionDepth));
		}
		
		return matched;
	}
	
	@Override
	public String toString() {
		return "( " + String.join(" | ", options.stream().map(x -> x.toString()).collect(Collectors.toList())) + " )";
	}
}
