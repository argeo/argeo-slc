package org.argeo.slc.jcr;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;

/**
 * Writes arbitrary metadata into a child node of a given node (or the node
 * itself if metadata node name is set to null)
 */
public class JcrMetadataWriter implements Runnable {
	private final static CmsLog log = CmsLog.getLog(JcrMetadataWriter.class);

	private Node baseNode;
	private String metadataNodeName = SlcNames.SLC_METADATA;

	private Map<String, String> metadata = new HashMap<String, String>();

	public void run() {
		try {
			Node metadataNode;
			if (metadataNodeName != null)
				metadataNode = baseNode.hasNode(metadataNodeName) ? baseNode.getNode(metadataNodeName)
						: baseNode.addNode(metadataNodeName);
			else
				metadataNode = baseNode;

			for (String key : metadata.keySet())
				metadataNode.setProperty(key, metadata.get(key));

			baseNode.getSession().save();

			if (log.isDebugEnabled())
				log.debug("Wrote " + metadata.size() + " metadata entries to " + metadataNode);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot write metadata to " + baseNode, e);
		} finally {
			JcrUtils.discardUnderlyingSessionQuietly(baseNode);
		}

	}

	public void setBaseNode(Node baseNode) {
		this.baseNode = baseNode;
	}

	public void setMetadataNodeName(String metadataNodeName) {
		this.metadataNodeName = metadataNodeName;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

}
