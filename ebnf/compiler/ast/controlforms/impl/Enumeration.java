package ebnf.compiler.ast.controlforms.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import ebnf.compiler.ast.api.IRuleSet;
import ebnf.compiler.ast.controlforms.api.IControlForm;
import ebnf.compiler.ast.controlforms.api.IEnumeration;

public final class Enumeration implements IEnumeration {

	private final List<IControlForm> elements;
	
	public Enumeration(final List<IControlForm> elements)
	{
		this.elements = Collections.unmodifiableList(elements);
	}
	
	@Override
	public List<IControlForm> getElements() {
		return elements;
	}
	
	@Override
	public Set<Integer> match(String expression, int startIndex, IRuleSet rules, Map<IControlForm, Integer> recursionDepth) {
		Set<Integer> matched = new HashSet<Integer>();
		Set<Long> handled = new HashSet<>();
		
		Stack<MatchState> matchStates = new Stack<>(); //prefer stack over queue as then queues
		
		//initialize
		MatchState zeroState = new MatchState(0, 0);
		matchStates.add(zeroState);
		handled.add(zeroState.getIdentifier());
		
		//execute until tested all options
		while(!matchStates.isEmpty())
		{
			MatchState state = matchStates.pop();
			IControlForm element = elements.get(state.element);
		
			Set<Integer> subMatches = element.match(expression, startIndex + state.startIndex, rules, recursionDepth);
			if(state.element < elements.size() - 1)
			{
				//not last element
				for(Integer matchLength : subMatches)
				{
					MatchState subState = new MatchState(state.element+1, state.startIndex + matchLength);
					
					if(!handled.contains(subState.getIdentifier()))
					{
						matchStates.push(subState);
						handled.add(subState.getIdentifier());
					}
				}	
			}
			else
			{
				//last element
				for(Integer matchLength : subMatches)
				{
					matched.add(state.startIndex + matchLength);
				}
			}
		}
		
		return matched;
	}
	
	@Override
	public String toString() {
		return "( " + String.join(" ", elements.stream().map(x -> x.toString()).collect(Collectors.toList())) + " )";
	}
	
	private static final class MatchState
	{
		private int startIndex;
		private int element;
		
		public MatchState(int element, int startIndex)
		{
			this.element = element;
			this.startIndex = startIndex;
		}
		
		public long getIdentifier()
		{
			return (((long)element) << 32) | (startIndex & 0xffffffffL);
		}
	}
}
