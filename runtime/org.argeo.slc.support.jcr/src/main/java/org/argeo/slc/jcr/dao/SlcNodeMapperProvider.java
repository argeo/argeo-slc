/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.jcr.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.jcr.NodeMapper;
import org.argeo.jcr.NodeMapperProvider;

public class SlcNodeMapperProvider implements NodeMapperProvider {
//	private final static Log log = LogFactory
//			.getLog(SlcNodeMapperProvider.class);

	private NodeMapper defaultNodeMapper;
	private NodeMapper treeTestResultNodeMapper;

	public void init() {
		defaultNodeMapper.setNodeMapperProvider(this);
		treeTestResultNodeMapper.setNodeMapperProvider(this);
	}

	public NodeMapper findNodeMapper(Node node) {
		try {
			// useful to initialize recursivity
			if (node == null)
				return defaultNodeMapper;

			if ("testresult".equals(node.getName()))
				return treeTestResultNodeMapper;
			else
				return defaultNodeMapper;

		} catch (RepositoryException re) {
			throw new ArgeoException("Cannot find NodeMapper for node " + node,
					re);
		}
	}

	public void setTreeTestResultNodeMapper(NodeMapper treeTestResultNodeMapper) {
		this.treeTestResultNodeMapper = treeTestResultNodeMapper;
	}

	public void setDefaultNodeMapper(NodeMapper defaultNodeMapper) {
		this.defaultNodeMapper = defaultNodeMapper;
	}

}
