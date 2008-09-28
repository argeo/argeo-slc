package org.argeo.slc.autoui.rmi;

import java.io.Serializable;

public interface AutoUiTask extends Serializable {
	public Object execute(AutoUiContext context) throws Exception;
}
