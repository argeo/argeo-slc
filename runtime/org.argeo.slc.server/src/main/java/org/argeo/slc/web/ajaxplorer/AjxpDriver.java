package org.argeo.slc.web.ajaxplorer;

import javax.servlet.http.HttpServletRequest;

public interface AjxpDriver {
	public AjxpAnswer executeAction(HttpServletRequest request); 
}
