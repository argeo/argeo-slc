package org.argeo.cms.ui.workbench.legacy.rap;

import org.argeo.api.cms.CmsLog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;

/**
 * RWT specific object to open a file retrieved from the server. It forwards the
 * request to the correct service after encoding file name and path in the
 * request URI.
 * 
 * <p>
 * The parameter "URI" is used to determine the correct file service, the path
 * and the file name. An optional file name can be added to present the end user
 * with a different file name as the one used to retrieve it.
 * </p>
 * 
 * 
 * <p>
 * The instance specific service is called by its ID and must have been
 * externally created
 * </p>
 */
public class OpenFile extends AbstractHandler {
	private final static CmsLog log = CmsLog.getLog(OpenFile.class);

	public final static String ID = SingleSourcingConstants.OPEN_FILE_CMD_ID;
	public final static String PARAM_FILE_NAME = SingleSourcingConstants.PARAM_FILE_NAME;
	public final static String PARAM_FILE_URI = SingleSourcingConstants.PARAM_FILE_URI;;

	/* DEPENDENCY INJECTION */
	private String openFileServiceId;

	public Object execute(ExecutionEvent event) {
		String fileName = event.getParameter(PARAM_FILE_NAME);
		String fileUri = event.getParameter(PARAM_FILE_URI);
		// Sanity check
		if (fileUri == null || "".equals(fileUri.trim()) || openFileServiceId == null
				|| "".equals(openFileServiceId.trim()))
			return null;

		org.argeo.cms.ui.workbench.legacy.rap.OpenFile openFileClient = new org.argeo.cms.ui.workbench.legacy.rap.OpenFile();
		openFileClient.execute(openFileServiceId, fileUri, fileName);
		return null;
	}

	public Object execute(String openFileServiceId, String fileUri, String fileName) {
		StringBuilder url = new StringBuilder();
		url.append(RWT.getServiceManager().getServiceHandlerUrl(openFileServiceId));

		if (notEmpty(fileName))
			url.append("&").append(SingleSourcingConstants.PARAM_FILE_NAME).append("=").append(fileName);
		url.append("&").append(SingleSourcingConstants.PARAM_FILE_URI).append("=").append(fileUri);

		String downloadUrl = url.toString();
		if (log.isTraceEnabled())
			log.trace("Calling OpenFileService with ID: " + openFileServiceId + " , with download URL: " + downloadUrl);

		UrlLauncher launcher = RWT.getClient().getService(UrlLauncher.class);
		launcher.openURL(downloadUrl);
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setOpenFileServiceId(String openFileServiceId) {
		this.openFileServiceId = openFileServiceId;
	}

	/** Simply checks if a string is not null nor empty */
	public static boolean notEmpty(String stringToTest) {
		return !(stringToTest == null || "".equals(stringToTest.trim()));
	}

	/** Simply checks if a string is null or empty */
	public static boolean isEmpty(String stringToTest) {
		return stringToTest == null || "".equals(stringToTest.trim());
	}

}
