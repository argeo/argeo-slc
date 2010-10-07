package org.argeo.slc.web.ajaxplorer.svn;

import java.io.File;

import org.argeo.slc.web.ajaxplorer.AjxpDriverException;
import org.argeo.slc.web.ajaxplorer.file.FileUploadAction;
import org.tmatesoft.svn.core.SVNException;

public class SvnUploadAction extends FileUploadAction<SvnDriver> {
	@Override
	protected void postProcess(SvnDriver driver, File file) {
		try {
			driver.beginWriteAction(file.getParentFile());

			log.debug("SVN Add: " + file);
			driver.getManager().getWCClient().doAdd(file, true,
					file.isDirectory(), true, true);

			driver.commitAll("Commit file " + file.getName());
			driver.completeWriteAction(file.getParentFile());
		} catch (SVNException e) {
			throw new AjxpDriverException("Cannot commit file " + file, e);
		} finally {
			driver.rollbackWriteAction(file.getParentFile());
		}
	}

}
