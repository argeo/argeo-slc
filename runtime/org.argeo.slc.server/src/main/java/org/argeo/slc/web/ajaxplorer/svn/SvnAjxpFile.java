package org.argeo.slc.web.ajaxplorer.svn;

import org.argeo.slc.web.ajaxplorer.file.AjxpFile;
import org.argeo.slc.web.ajaxplorer.file.LsMode;
import org.tmatesoft.svn.core.wc.SVNInfo;

public class SvnAjxpFile extends AjxpFile {

	protected final SVNInfo info;

	public SvnAjxpFile(SVNInfo info, String parentPath) {
		super(info.getFile(), parentPath);
		this.info = info;
	}

	@Override
	protected void addAdditionalAttrs(StringBuffer buf, LsMode mode,
			String encoding) {
		addAttr("author", info.getAuthor(), buf);
		addAttr("revision", Long.toString(info.getRevision().getNumber()), buf);
	}

}
