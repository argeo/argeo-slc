/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.jcr;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;

/**
 * Writes arbitrary metadata into a child node of a given node (or the node
 * itself if metadata node name is set to null)
 */
public class JcrMetadataWriter implements Runnable {
	private final static Log log = LogFactory.getLog(JcrMetadataWriter.class);

	private Node baseNode;
	private String metadataNodeName = SlcNames.SLC_METADATA;

	private Map<String, String> metadata = new HashMap<String, String>();

	public void run() {
		try {
			Node metadataNode;
			if (metadataNodeName != null)
				metadataNode = baseNode.hasNode(metadataNodeName) ? baseNode
						.getNode(metadataNodeName) : baseNode
						.addNode(metadataNodeName);
			else
				metadataNode = baseNode;

			for (String key : metadata.keySet())
				metadataNode.setProperty(key, metadata.get(key));

			baseNode.getSession().save();

			if (log.isDebugEnabled())
				log.debug("Wrote " + metadata.size() + " metadata entries to "
						+ metadataNode);
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
