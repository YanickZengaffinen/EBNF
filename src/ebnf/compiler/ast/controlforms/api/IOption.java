package ebnf.compiler.ast.controlforms.api;

import java.util.List;

public interface IOption extends IControlForm {

	List<IControlForm> getOptions();
}
