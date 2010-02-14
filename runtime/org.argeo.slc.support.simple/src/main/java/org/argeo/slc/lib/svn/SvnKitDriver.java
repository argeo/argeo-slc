package org.argeo.slc.lib.svn;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.VersioningDriver;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.admin.ISVNChangeEntryHandler;
import org.tmatesoft.svn.core.wc.admin.SVNChangeEntry;

public class SvnKitDriver implements VersioningDriver {
	private final SVNClientManager manager;

	public SvnKitDriver() {
		 DAVRepositoryFactory.setup();
		 FSRepositoryFactory.setup();
		manager = SVNClientManager.newInstance();
	}

	public void updateToHead(File fileOrDir) {
		try {
			manager.getUpdateClient().doUpdate(fileOrDir, SVNRevision.HEAD,
					true);
		} catch (Exception e) {
			throw new SlcException("Cannot update " + fileOrDir, e);
		}
	}

	public void importFileOrDir(String repositoryUrl, File fileOrDir) {
		try {
			manager.getCommitClient().doImport(fileOrDir,
					SVNURL.parseURIDecoded(repositoryUrl),
					"Import " + fileOrDir, true);
		} catch (Exception e) {
			throw new SlcException("Cannot import " + repositoryUrl + " to "
					+ fileOrDir, e);
		}
	}

	public void checkout(String repositoryUrl, File destDir, Boolean recursive) {
		try {
			manager.getUpdateClient().doCheckout(
					SVNURL.parseURIDecoded(repositoryUrl), destDir,
					SVNRevision.UNDEFINED, SVNRevision.HEAD, recursive);
		} catch (Exception e) {
			throw new SlcException("Cannot checkout " + repositoryUrl + " to "
					+ destDir, e);
		}
	}

	public void getFileFromRepository(String repositoryBaseUrl,
			String location, OutputStream out) {
		try {
			SVNURL url = SVNURL.parseURIDecoded(repositoryBaseUrl);
			SVNRepository repo = manager.createRepository(url, true);
			repo.getFile(location, -1, null, out);
		} catch (Exception e) {
			throw new SlcException("Cannot retrieve file " + location
					+ " from " + repositoryBaseUrl, e);
		}

	}

	public String getRelativePath(String repositoryUrl) {
		try {
			SVNURL url = SVNURL.parseURIDecoded(repositoryUrl);
			SVNRepository repo = manager.createRepository(url, true);
			return repo.getRepositoryPath("");
		} catch (Exception e) {
			throw new SlcException("Cannot get relative path for "
					+ repositoryUrl, e);
		}
	}

	public String getRepositoryRoot(String repositoryUrl) {
		try {
			SVNURL url = SVNURL.parseURIDecoded(repositoryUrl);
			SVNRepository repo = manager.createRepository(url, true);
			return repo.getRepositoryRoot(true).toDecodedString();
		} catch (Exception e) {
			throw new SlcException("Cannot get repository root for "
					+ repositoryUrl, e);
		}
	}

	public List<String> getChangedPaths(File repositoryRoot, Long revision) {
		try {
			final List<String> paths = new Vector<String>();
			ISVNChangeEntryHandler handler = new ISVNChangeEntryHandler() {
				public void handleEntry(SVNChangeEntry entry)
						throws SVNException {
					paths.add(entry.getPath());
				}
			};
			manager.getLookClient().doGetChanged(repositoryRoot,
					SVNRevision.create(revision), handler, false);
			return paths;
		} catch (Exception e) {
			throw new SlcException("Cannot get changed paths at "
					+ repositoryRoot + " for revision " + revision, e);
		}
	}

	public void createRepository(String filePath) {
		try {
			manager.getAdminClient().doCreateRepository(new File(filePath),
					null, false, false);
		} catch (Exception e) {
			throw new SlcException("Cannot create repository " + filePath, e);
		}
	}

	public void commit(File fileOrDir, String commitMessage) {
		try {
			manager.getCommitClient().doCommit(new File[] { fileOrDir }, true,
					commitMessage, false, true);
		} catch (Exception e) {
			throw new SlcException("Cannot commit " + fileOrDir, e);
		}
	}

}
