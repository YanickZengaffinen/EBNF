package ebnf.compiler.ast.controlforms.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;
import ebnf.compiler.ast.controlforms.api.IRepetition;

public final class Repetition implements IRepetition {
	
	private final IControlForm element;
	
	public Repetition(final IControlForm element)
	{
		this.element = element;
	}
	
	@Override
	public IControlForm getElement() {
		return element;
	}
	
	@Override
	public Set<Integer> match(String expression, int startIndex, IRuleSet rules, Map<IControlForm, Integer> recursionDepth) {
		Set<Integer> matched = new HashSet<Integer>();
		
		Stack<Integer> currentMatches = new Stack<Integer>();
		currentMatches.add(0);
		matched.add(0);
		
		while(!currentMatches.isEmpty())
		{
			Integer subStartIndex = currentMatches.pop();
			Set<Integer> subMatches = element.match(expression, startIndex + subStartIndex, rules, recursionDepth);
			for(Integer matchLength : subMatches)
			{
				if(!matched.contains(subStartIndex + matchLength))
				{
					matched.add(subStartIndex + matchLength);
					currentMatches.push(subStartIndex + matchLength);
				}
				//else we have already checked from the given index
			}
		}

		return matched;
	}

	@Override
	public String toString() {
		return "{ " + element.toString() + " }"; 
	}
}
