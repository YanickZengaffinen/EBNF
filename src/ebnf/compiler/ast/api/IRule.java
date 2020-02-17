package ebnf.compiler.ast.api;

import ebnf.compiler.ast.controlforms.api.IControlForm;

public interface IRule {
	
	String getName();
	
	IControlForm getRHS();
	
	boolean isValid(String expression, IRuleSet rules);
}
