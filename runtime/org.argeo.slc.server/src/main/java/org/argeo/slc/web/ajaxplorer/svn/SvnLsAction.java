package org.argeo.slc.web.ajaxplorer.svn;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Vector;

import org.argeo.slc.web.ajaxplorer.AjxpDriverException;
import org.argeo.slc.web.ajaxplorer.file.FileLsAction;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

public class SvnLsAction extends FileLsAction<SvnDriver, SvnAjxpFile> {

	@Override
	protected List<SvnAjxpFile> listFiles(SvnDriver driver, final String path,
			final boolean dirOnly) {
		try {
			File dir = driver.getFile(path);
			SVNWCClient client = driver.getManager().getWCClient();

			final List<SvnAjxpFile> res = new Vector<SvnAjxpFile>();
			FileFilter filter = createFileFilter(dir);
			File[] files = dir.listFiles(filter);
			for (File file : files) {
				//SVNStatus status = driver.getManager().getStatusClient().doStatus(file, false);
				
				SVNInfo info = client.doInfo(file, SVNRevision.WORKING);
				if (dirOnly) {
					if (file.isDirectory())
						res.add(new SvnAjxpFile(info, path));
				} else {
					res.add(new SvnAjxpFile(info, path));
				}
			}
			return res;
		} catch (SVNException e) {
			throw new AjxpDriverException("Cannot list svn dir " + path, e);
		}
	}

	@Override
	protected FileFilter createFileFilter(File dir) {
		return new FileFilter() {

			public boolean accept(File pathname) {
				if (pathname.getName().equals(".svn")) {
					return false;
				} else {
					return true;
				}
			}

		};
	}

}