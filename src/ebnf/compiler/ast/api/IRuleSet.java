package ebnf.compiler.ast.api;

import java.util.Map;

public interface IRuleSet {

	/**
	 * Gets all the {@link IRule}'s of this {@link IRuleSet} by their name
	 */
	Map<String, IRule> getRules();

	/**
	 * Gets an {@link IRule} by its name
	 */
	public default IRule getRule(String name)
	{
		return getRules().getOrDefault(name, null);
	}
	
	boolean isValid(String expression);
}
