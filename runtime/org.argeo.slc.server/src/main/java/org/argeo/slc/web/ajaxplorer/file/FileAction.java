package org.argeo.slc.web.ajaxplorer.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.web.ajaxplorer.AjxpAction;

public abstract class FileAction implements AjxpAction<FileDriver> {
	protected final Log log = LogFactory.getLog(getClass());
}
