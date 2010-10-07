package org.argeo.slc.web.ajaxplorer;

import javax.servlet.http.HttpServletRequest;

public interface AjxpAction<T extends AjxpDriver>{
	public AjxpAnswer execute(T driver, HttpServletRequest request);
}
