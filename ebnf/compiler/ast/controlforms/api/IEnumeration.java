package ebnf.compiler.ast.controlforms.api;

import java.util.List;

public interface IEnumeration extends IControlForm {

	/**
	 * Gets the {@link IControlForm} elements of this enumeration
	 */
	List<IControlForm> getElements();
}
