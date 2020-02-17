package ebnf.compiler.ast.controlforms.api;

public interface IRepetition extends IControlForm {

	/**
	 * Gets the element that is repeated
	 */
	IControlForm getElement();
}
