package org.argeo.slc.client.rap;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
//import org.eclipse.rap.rwt.service.IServiceHandler;

/**
 * Basic Default service handler that retrieves a file from a NT_FILE JCR node
 * and launch the download.
 */
public class OpenJcrFileService {//implements IServiceHandler {

	/* DEPENDENCY INJECTION */
	final private Node fileNode;

	public OpenJcrFileService(Node fileNode) {
		this.fileNode = fileNode;
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Get the file content
		byte[] download = getFileAsByteArray();

		// Send the file in the response
		//HttpServletResponse response = RWT.getResponse();
		response.setContentType("application/octet-stream");
		response.setContentLength(download.length);
		String contentDisposition = null;
		try {
			contentDisposition = "attachment; filename=\""
					+ JcrUtils.lastPathElement(fileNode.getPath()) + "\"";
		} catch (RepositoryException e) {
			throw new SlcException("Error while getting file Path " + fileNode,
					e);
		}
		response.setHeader("Content-Disposition", contentDisposition);

		try {
			response.getOutputStream().write(download);
		} catch (IOException ioe) {
			throw new SlcException("Error while writing the file " + fileNode
					+ " to the servlet response", ioe);
		}
	}

	protected byte[] getFileAsByteArray() {

		Session businessSession = null;
		try {
			boolean isValid = true;
			Node child = null;
			if (!fileNode.isNodeType(NodeType.NT_FILE))
				isValid = false;
			else {
				child = fileNode.getNode(Property.JCR_CONTENT);
				if (!(child.isNodeType(NodeType.NT_RESOURCE) || child
						.hasProperty(Property.JCR_DATA)))
					isValid = false;
			}

			if (!isValid)
				return null;

			byte[] ba = null;
			InputStream fis = null;
			try {
				fis = (InputStream) child.getProperty(Property.JCR_DATA)
						.getBinary().getStream();
				ba = IOUtils.toByteArray(fis);
			} catch (Exception e) {
				throw new SlcException("Stream error while opening file "
						+ fileNode, e);
			} finally {
				IOUtils.closeQuietly(fis);
			}
			return ba;

		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while "
					+ "opening file node " + fileNode, e);
		} finally {
			JcrUtils.logoutQuietly(businessSession);
		}
	}
}