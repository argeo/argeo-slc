package org.argeo.slc.web.ajaxplorer.svn;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.web.ajaxplorer.AjxpAction;
import org.argeo.slc.web.ajaxplorer.AjxpAnswer;
import org.argeo.slc.web.ajaxplorer.AjxpDriverException;
import org.argeo.slc.web.ajaxplorer.file.FileDownloadAction;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnDownloadAction implements AjxpAction<SvnDriver> {

	public AjxpAnswer execute(SvnDriver driver, HttpServletRequest request) {
		String path = request.getParameter("file");
		if (path.charAt(path.length() - 1) == '/') {
			// probably a directory
			return AjxpAnswer.DO_NOTHING;
		}

		String revStr = request.getParameter("rev");
		Long rev = Long.parseLong(revStr);
		return new SvnDownloadAnswer(driver, path, rev);
	}

	public class SvnDownloadAnswer implements AjxpAnswer {
		private final SvnDriver driver;
		private final String path;
		private final Long rev;

		public SvnDownloadAnswer(SvnDriver driver, String path, Long rev) {
			this.driver = driver;
			this.path = path;
			this.rev = rev;
		}

		public void updateResponse(HttpServletResponse response) {
			ServletOutputStream out = null;
			try {
				FileDownloadAction.setDefaultDownloadHeaders(response,
						getFileName(), null);
				response.setHeader("AjaXplorer-SvnFileName", getFileName());

				SVNRepository repository = driver.getRepository();
				out = response.getOutputStream();
				repository.getFile(path, rev, null, out);
			} catch (Exception e) {
				throw new AjxpDriverException("Cannot download revision " + rev
						+ " of path " + path, e);
			} finally {
				IOUtils.closeQuietly(out);
			}
		}

		protected String getFileName() {
			int lastIndexSlash = path.lastIndexOf('/');
			final String origFileName;
			if (lastIndexSlash != -1) {
				origFileName = path.substring(lastIndexSlash + 1);
			} else {
				origFileName = path;
			}

			int lastIndexPoint = origFileName.lastIndexOf('.');
			String prefix = origFileName.substring(0, lastIndexPoint);
			String ext = origFileName.substring(lastIndexPoint);
			return prefix + "-" + rev + ext;
		}
	}
}
