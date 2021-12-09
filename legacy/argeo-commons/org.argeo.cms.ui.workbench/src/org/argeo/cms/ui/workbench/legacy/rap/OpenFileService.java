package org.argeo.cms.ui.workbench.legacy.rap;

import static org.argeo.cms.ui.workbench.legacy.rap.SingleSourcingConstants.FILE_SCHEME;
import static org.argeo.cms.ui.workbench.legacy.rap.SingleSourcingConstants.SCHEME_HOST_SEPARATOR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;

/**
 * RWT specific Basic Default service handler that retrieves a file on the
 * server file system using its absolute path and forwards it to the end user
 * browser.
 * 
 * Clients might extend to provide context specific services
 */
public class OpenFileService implements ServiceHandler {
	public OpenFileService() {
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String fileName = request.getParameter(SingleSourcingConstants.PARAM_FILE_NAME);
		String uri = request.getParameter(SingleSourcingConstants.PARAM_FILE_URI);

		// Use buffered array to directly write the stream?
		if (!uri.startsWith(SingleSourcingConstants.FILE_SCHEME))
			throw new IllegalArgumentException(
					"Open file service can only handle files that are on the server file system");

		// Set the Metadata
		response.setContentLength((int) getFileSize(uri));
		if (OpenFile.isEmpty(fileName))
			fileName = getFileName(uri);
		response.setContentType(getMimeType(uri, fileName));
		String contentDisposition = "attachment; filename=\"" + fileName + "\"";
		response.setHeader("Content-Disposition", contentDisposition);

		// Useless for current use
		// response.setHeader("Content-Transfer-Encoding", "binary");
		// response.setHeader("Pragma", "no-cache");
		// response.setHeader("Cache-Control", "no-cache, must-revalidate");

		Path path = Paths.get(getAbsPathFromUri(uri));
		Files.copy(path, response.getOutputStream());

		// FIXME we always use temporary files for the time being.
		// the deleteOnClose file only works when the JVM is closed so we
		// explicitly delete to avoid overloading the server
		if (path.startsWith("/tmp"))
			path.toFile().delete();
	}

	protected long getFileSize(String uri) throws IOException {
		if (uri.startsWith(SingleSourcingConstants.FILE_SCHEME)) {
			Path path = Paths.get(getAbsPathFromUri(uri));
			return Files.size(path);
		}
		return -1l;
	}

	protected String getFileName(String uri) {
		if (uri.startsWith(SingleSourcingConstants.FILE_SCHEME)) {
			Path path = Paths.get(getAbsPathFromUri(uri));
			return path.getFileName().toString();
		}
		return null;
	}

	private String getAbsPathFromUri(String uri) {
		if (uri.startsWith(FILE_SCHEME))
			return uri.substring((FILE_SCHEME + SCHEME_HOST_SEPARATOR).length());
		// else if (uri.startsWith(JCR_SCHEME))
		// return uri.substring((JCR_SCHEME + SCHEME_HOST_SEPARATOR).length());
		else
			throw new IllegalArgumentException("Unknown URI prefix for" + uri);
	}

	protected String getMimeType(String uri, String fileName) throws IOException {
		if (uri.startsWith(FILE_SCHEME)) {
			Path path = Paths.get(getAbsPathFromUri(uri));
			String mimeType = Files.probeContentType(path);
			if (OpenFile.notEmpty(mimeType))
				return mimeType;
		}
		return "application/octet-stream";
	}
}
