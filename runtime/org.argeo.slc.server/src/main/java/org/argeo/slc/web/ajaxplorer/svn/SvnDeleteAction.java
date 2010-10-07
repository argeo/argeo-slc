package org.argeo.slc.web.ajaxplorer.svn;

import java.io.File;

import org.argeo.slc.web.ajaxplorer.AjxpDriverException;
import org.argeo.slc.web.ajaxplorer.file.FileDeleteAction;
import org.tmatesoft.svn.core.SVNException;

public class SvnDeleteAction extends FileDeleteAction<SvnDriver> {
	@Override
	protected void executeDelete(SvnDriver driver, File file) {
		try {
			driver.beginWriteAction(file.getParentFile());

			log.debug("SVN Delete: " + file);
			driver.getManager().getWCClient().doDelete(file, true, false);

			driver.commitAll("Commit delete of " + file.getName());
			driver.completeWriteAction(file.getParentFile());
		} catch (SVNException e) {
			throw new AjxpDriverException("Cannot delete file " + file, e);
		} finally {
			driver.rollbackWriteAction(file.getParentFile());
		}
	}

}
