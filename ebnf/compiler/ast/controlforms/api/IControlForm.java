package ebnf.compiler.ast.controlforms.api;

import java.util.Map;
import java.util.Set;

import ebnf.compiler.ast.api.IRuleSet;

public interface IControlForm {

	/**
	 * Matches a part (startIndex to end) of the expression against some rules
	 * 
	 * @param expression
	 * @param startIndex
	 * @param rules The universe
	 * @param recursionDepth The current depth of the recursion of a control form
	 * @return The length of the string that has been matched by this control form
	 */
	Set<Integer> match(String expression, int startIndex, IRuleSet rules, Map<IControlForm, Integer> recursionDepth);
}
